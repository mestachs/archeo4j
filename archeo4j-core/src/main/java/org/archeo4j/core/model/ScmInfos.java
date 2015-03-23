package org.archeo4j.core.model;

import java.io.Serializable;
import java.util.Arrays;

public class ScmInfos implements Serializable {

  private static final long serialVersionUID = 7911918454235720960L;

  private String connection;
  private String developerConnection;
  private String tag;
  private String url;

  public String getConnection() {
    return connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }

  public String getDeveloperConnection() {
    return developerConnection;
  }

  public void setDeveloperConnection(String developerConnection) {
    this.developerConnection = developerConnection;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDisplayConnection() {
    return Arrays.asList(developerConnection, connection, url).stream().filter(m -> m != null)
        .findFirst().orElse("");
  }

}
