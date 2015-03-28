package org.archeo4j.core.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalyzedArtefact implements Serializable {

  private static final long serialVersionUID = 7241361862032495600L;

  private String name;

  private String groupId;
  private String artefactId;
  private String version;

  private ArtefactType packaging;

  private Map<String, AnalyzedArtefact> bundledAterfacts = new HashMap<String, AnalyzedArtefact>();

  private Map<String, AnalyzedClass> classes = new HashMap<String, AnalyzedClass>();

  private AnalyzedArtefact bundledJar;
  private ScmInfos scm;

  public AnalyzedArtefact(String name) {
    this.name = name;
    if (name.endsWith(".jar")) {
      packaging = ArtefactType.JAR;
    } else {
      packaging = ArtefactType.WAR;
    }
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtefactId() {
    return artefactId;
  }

  public void setArtefactId(String artefactId) {
    this.artefactId = artefactId;
  }

  public String getVersion() {
    return version;
  }

  public Version getArtefactVersion() {
    return version == null ? null : new Version(version);
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Map<String, AnalyzedArtefact> getBundledAterfacts() {
    return bundledAterfacts;
  }

  public void setBundledAterfacts(Map<String, AnalyzedArtefact> bundledAterfacts) {
    this.bundledAterfacts = bundledAterfacts;
  }

  public String getName() {
    return name;
  }

  public ArtefactType getPackaging() {
    return packaging;
  }

  public void addClass(AnalyzedClass analyzedClass) {
    if (analyzedClass == null) {
      return;
    }
    analyzedClass.setAnalyzedArtefact(this);
    classes.put(analyzedClass.getName(), analyzedClass);
  }

  public void addBundleJar(AnalyzedArtefact bundledJar) {
    bundledJar.setBundledBy(this);
    bundledAterfacts.put(bundledJar.getName(), bundledJar);
  }

  public Optional<AnalyzedArtefact> getBundledBy() {
    return Optional.ofNullable(this.bundledJar);
  }

  private void setBundledBy(AnalyzedArtefact bundledJar) {
    this.bundledJar = bundledJar;
  }

  public Optional<ScmInfos> getScm() {
    return Optional.ofNullable(scm);
  }

  public void setScm(ScmInfos scm) {
    this.scm = scm;
  }

  public Optional<String> getGAV() {
    return groupId == null ? Optional.empty() : Optional.of(groupId + ":" + artefactId + ":"
        + version);
  }

  public String getDisplayName() {
    return getGAV().orElse(getName()) + " "
        + getScm().map(scm -> scm.getDisplayConnection()).orElse("");
  }

  @Override
  public String toString() {
    return "AnalyzedArtefact [name=" + name + ", groupId=" + groupId + ", artefactId=" + artefactId
        + ", version=" + version + ", packaging=" + packaging + ", scm="
        + getScm().map(scm -> scm.getDisplayConnection()).orElse("") + "]";
  }

  public List<AnalyzedMethod> getMethods() {
    return Stream.concat(
        classes.values().stream().flatMap(clazz -> clazz.getDeclaredMethods().stream()),
        bundledAterfacts.values().stream().flatMap(artefact -> artefact.getMethods().stream()))
        .collect(Collectors.toList());

  }

  public Collection<AnalyzedClass> getClasses() {
    return Stream.concat(classes.values().stream(),
        bundledAterfacts.values().stream().flatMap(artefact -> artefact.getClasses().stream()))
        .collect(Collectors.toList());
  }
}
