package org.archeo4j.core.analyzer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.AnalyzedClass;

import com.google.common.io.ByteStreams;

public class ArtefactAnalyzer {
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
              classAnalyzer.analyzeCallsForClass(className, toBytes(jarFile, zipEntry),
                  jarFile.getName());
          analyzedArtefact.addClass(analyzedClass);
        }

        if (isJar(zipEntry)) {
          analyzeInnerJar(analyzedArtefact, jarFile, zipEntry);
        }
        if (isMavenProperties(zipEntry)) {
          assignMavenProperties(new ByteArrayInputStream(toBytes(jarFile, zipEntry)),
              analyzedArtefact);
        }
      }
    } finally {
      closeQuietly(jarFile);
    }
    // System.out.println("Analyzed " + analyzedArtefact);
    return analyzedArtefact;
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

  private byte[] toBytes(JarFile jarFile, JarEntry zipEntry) {
    try {
      return ByteStreams.toByteArray(jarFile.getInputStream(zipEntry));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isMavenProperties(JarEntry zipEntry) {
    if (zipEntry != null && zipEntry.getName().startsWith("META-INF")
        && zipEntry.getName().endsWith("pom.properties")) {
      return true;
    }
    return false;
  }

  private void analyzeInnerJar(AnalyzedArtefact analyzedArtefact, JarFile jarFile, JarEntry zipEntry) {

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
      return zipEntry.getName().replace("WEB-INF/classes/", "").replace(".class", "")
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
