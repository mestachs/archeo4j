package org.archeo4j.core.model;

import java.io.Serializable;

public class Version implements Serializable {

  private static final long serialVersionUID = -4029275702979051270L;

  private String rawVersion;

  private String major = "";
  private String minor = "";
  private String incremental = "";
  private String qualifier = "";
  private boolean snapshot;

  /**
   * 
   * @param rawVersion <major version>.<minor version>.<incremental version>-<qualifier>
   */
  public Version(String rawVersion) {
    this.rawVersion = rawVersion;
    this.snapshot = rawVersion.endsWith("-SNAPSHOT");

    String[] versions = rawVersion.split("\\.|-");

    major = versions[0];
    int qualifierIndex = major.length();

    if (versions.length > 1) {
      minor = versions[1];
      qualifierIndex += minor.length() + 1;
    }
    if (versions.length > 2) {
      incremental = versions[2];
      qualifierIndex += incremental.length() + 1;
    }

    if (rawVersion.length() >= qualifierIndex + 1) {
      this.qualifier = rawVersion.substring(qualifierIndex + 1);
    }
    // ".*-[alpha|beta|milestone|rc[cr|sp]-(.*)"
  }



  public String getMajor() {
    return major;
  }

  public String getMinor() {
    return minor;
  }

  public String getIncremental() {
    return incremental;
  }

  public String getQualifier() {
    return qualifier;
  }

  public boolean isSnapshot() {
    return snapshot;
  }

  public String asString() {
    return rawVersion;
  }


  @Override
  public String toString() {
    return "Version [rawVersion=" + rawVersion + ", major=" + major + ", minor=" + minor
        + ", incremental=" + incremental + ", qualifier=" + qualifier + ", snapshot=" + snapshot
        + "]";
  }

}
