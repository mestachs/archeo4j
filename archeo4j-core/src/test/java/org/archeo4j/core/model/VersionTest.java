package org.archeo4j.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class VersionTest {

  @Test
  public void itShouldHandleMajorMinorIncQualifier() throws Exception {
    Version majMinInc = new Version("1.2.3-alpha-10");
    assertThat(majMinInc.getMajor()).isEqualTo("1");
    assertThat(majMinInc.getMinor()).isEqualTo("2");
    assertThat(majMinInc.getIncremental()).isEqualTo("3");
    assertThat(majMinInc.getQualifier()).isEqualTo("alpha-10");
  }
  
  @Test
  public void itShouldHandleMajorMinorInc() throws Exception {
    Version majMinInc = new Version("1.2.3");
    assertThat(majMinInc.getMajor()).isEqualTo("1");
    assertThat(majMinInc.getMinor()).isEqualTo("2");
    assertThat(majMinInc.getIncremental()).isEqualTo("3");
    assertThat(majMinInc.getQualifier()).isEqualTo("");
  }

  @Test
  public void itShouldHandleMajorMinor() throws Exception {
    Version majMinInc = new Version("1.2");
    assertThat(majMinInc.getMajor()).isEqualTo("1");
    assertThat(majMinInc.getMinor()).isEqualTo("2");
    assertThat(majMinInc.getIncremental()).isEqualTo("");
    assertThat(majMinInc.getQualifier()).isEqualTo("");
  }

  @Test
  public void itShouldHandleMajor() throws Exception {
    Version majMinInc = new Version("2");
    assertThat(majMinInc.getMajor()).isEqualTo("2");
    assertThat(majMinInc.getMinor()).isEqualTo("");
    assertThat(majMinInc.getIncremental()).isEqualTo("");
    assertThat(majMinInc.getQualifier()).isEqualTo("");
  }

  @Test
  public void itShouldDetectSnapshotVersion() throws Exception {
    Version majMinInc = new Version("2");
    assertThat(majMinInc.isSnapshot()).isFalse();
    majMinInc = new Version("2-SNAPSHOT");
    assertThat(majMinInc.isSnapshot()).isTrue();
  }

  @Test
  public void itKeepTheOrignalVersionAsString() throws Exception {
    Version majMinInc = new Version("unparsable");
    assertThat(majMinInc.asString()).isEqualTo("unparsable");

  }
}
