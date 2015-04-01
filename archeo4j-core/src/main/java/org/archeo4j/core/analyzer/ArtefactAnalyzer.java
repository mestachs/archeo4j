package org.archeo4j.core.analyzer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.AnalyzedClass;
import org.archeo4j.core.model.ScmInfos;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

public class ArtefactAnalyzer {
  private static final Pattern maven = Pattern.compile("(.+?)-(\\d.+?(-SNAPSHOT)?)(-(.+?))?\\.(jar|war)");
  private AnalyzisConfig analyzisConfig;

  public ArtefactAnalyzer(AnalyzisConfig analyzisConfig) {
    this.analyzisConfig = analyzisConfig;
  }

  public AnalyzedArtefact analyzeJar(JarFile jarFile) {
    ClassAnalyzer classAnalyzer = new ClassAnalyzer(analyzisConfig);
    AnalyzedArtefact analyzedArtefact = new AnalyzedArtefact(jarFile.getName());
    try {
      for (Enumeration<JarEntry> list = jarFile.entries(); list.hasMoreElements();) {
        JarEntry zipEntry = list.nextElement();
        String className = getClassNameForEntry(zipEntry);
        if (className != null) {

          AnalyzedClass analyzedClass =
              classAnalyzer.analyzeCallsForClass(
                  className,
                  toBytes(jarFile, zipEntry),
                  jarFile.getName());
          analyzedArtefact.addClass(analyzedClass);
        }

        if (isJar(zipEntry)) {
          analyzeInnerJar(analyzedArtefact, jarFile, zipEntry);
        }
        if (isMavenPom(zipEntry)) {
          assignMavenScm(analyzedArtefact, new String(toBytes(jarFile, zipEntry)));
        }
        if (isMavenProperties(zipEntry)) {
          assignMavenProperties(
              new ByteArrayInputStream(toBytes(jarFile, zipEntry)),
              analyzedArtefact);
        }
      }
      if (analyzedArtefact.getArtefactId() == null) {
        String fileName = new File(jarFile.getName()).getName();
        System.out.println("missing artefacts : " + fileName + " "
            + maven.matcher(fileName).matches());
        Matcher matcher = maven.matcher(fileName);
        if (matcher.matches()) {
          analyzedArtefact.setArtefactId(matcher.group(1));
          analyzedArtefact.setGroupId(matcher.group(1));
          analyzedArtefact.setVersion(matcher.group(2));
        }
      }
    } finally {
      closeQuietly(jarFile);
    }
    if (analyzedArtefact.getArtefactId() == null) {
      try {
        System.out.println(jarFile.getManifest().getMainAttributes());
      } catch (IOException | IllegalStateException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    // System.out.println("Analyzed " + analyzedArtefact);
    return analyzedArtefact;
  }

  private void assignMavenScm(AnalyzedArtefact analyzedArtefact, String pomxml) {
    // System.out.println(analyzedArtefact + " " + pomxml);

    List<String> extractionPatterns =
        Arrays.asList(
            "<connection>(.*)</connection>",
            "<developerConnection>(.*)</developerConnection>",
            "<url>(.*)</url>",
            "<tag>(.*)</tag>");

    List<String> scminfos = extractionPatterns.stream().map(regexp -> {
      Matcher matcher = Pattern.compile(regexp).matcher(pomxml);

      return matcher.find() ? matcher.group(1) : null;
    }).collect(Collectors.toList());
    ScmInfos scm = new ScmInfos();
    scm.setConnection(scminfos.get(0));
    scm.setDeveloperConnection(scminfos.get(1));
    scm.setUrl(scminfos.get(2));
    scm.setTag(scminfos.get(3));
    analyzedArtefact.setScm(scm);
  }

  private void closeQuietly(JarFile jarFile) {
    try {
      jarFile.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void assignMavenProperties(InputStream is, AnalyzedArtefact analyzedArtefact) {
    Properties properties = new Properties();
    try {
      properties.load(is);
      analyzedArtefact.setVersion(properties.getProperty("version"));
      analyzedArtefact.setGroupId(properties.getProperty("groupId"));
      analyzedArtefact.setArtefactId(properties.getProperty("artifactId"));
    } catch (IOException ignored) {

    }
  }

  private boolean isMavenPom(JarEntry zipEntry) {
    return zipEntry != null && zipEntry.getName().startsWith("META-INF")
        && zipEntry.getName().endsWith("pom.xml");
  }

  private boolean isMavenProperties(JarEntry zipEntry) {
    return zipEntry != null && zipEntry.getName().startsWith("META-INF")
        && zipEntry.getName().endsWith("pom.properties");
  }

  private byte[] toBytes(JarFile jarFile, JarEntry zipEntry) {
    try {
      return ByteStreams.toByteArray(jarFile.getInputStream(zipEntry));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }



  private void
      analyzeInnerJar(AnalyzedArtefact analyzedArtefact, JarFile jarFile, JarEntry zipEntry) {

    AnalyzedArtefact bundledJar = new AnalyzedArtefact(zipEntry.getName());
    analyzedArtefact.addBundleJar(bundledJar);
    ClassAnalyzer classAnalyzer = new ClassAnalyzer(analyzisConfig);
    System.out.println(bundledJar);
    JarInputStream jarIS;
    try {
      jarIS = new JarInputStream(jarFile.getInputStream(zipEntry));

      JarEntry innerEntry = jarIS.getNextJarEntry();

      while (innerEntry != null) {
        String subClassName = getClassNameForEntry(innerEntry);
        if (subClassName != null) {
          byte[] classBytes = ByteStreams.toByteArray(jarIS);

          AnalyzedClass analyzedClass =
              classAnalyzer.analyzeCallsForClass(subClassName, classBytes, zipEntry.getName());
          bundledJar.addClass(analyzedClass);
        }
        innerEntry = jarIS.getNextJarEntry();
        if (isMavenProperties(innerEntry)) {
          assignMavenProperties(jarIS, bundledJar);
        }
        if (isMavenPom(innerEntry)) {
          assignMavenScm(bundledJar, CharStreams.toString(new InputStreamReader(jarIS)));
        }
      }
      if (bundledJar.getArtefactId() == null) {
        String fileName = new File(zipEntry.getName()).getName();

        Matcher matcher = maven.matcher(fileName);
        if (matcher.matches()) {
          bundledJar.setArtefactId(matcher.group(1));
          bundledJar.setGroupId(matcher.group(1));
          bundledJar.setVersion(matcher.group(2));
        }
      }
      jarIS.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


    System.out.println("Analyzed " + bundledJar);
  }

  private boolean isJar(JarEntry zipEntry) {
    return zipEntry.getName().endsWith(".jar");
  }

  private String getClassNameForEntry(JarEntry zipEntry) {
    if (zipEntry.getName().endsWith(".class")) {
      return zipEntry
          .getName()
          .replace("WEB-INF/classes/", "")
          .replace(".class", "")
          .replace("/", ".");
    } else {
      return null;
    }
  }

  public AnalyzedArtefact analyzeJar(String jarLocation) {
    try {
      return analyzeJar(new JarFile(jarLocation));
    } catch (IOException e) {
      throw new RuntimeException("failed to analyze " + jarLocation, e);
    }
  }
}
