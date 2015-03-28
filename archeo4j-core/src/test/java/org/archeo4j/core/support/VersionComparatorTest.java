package org.archeo4j.core.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.archeo4j.core.model.Version;
import org.junit.Test;


public class VersionComparatorTest {
  VersionComparator comparator = new VersionComparator();

  @Test
  public void shouldDetecEqualVersion() throws Exception {
    assertThat(comparator.compare(new Version("1.2"), new Version("1.2"))).isEqualTo(0);
  }

  @Test
  public void shouldDetecLowerVersion() throws Exception {
    assertThat(comparator.compare(new Version("1.2"), new Version("2.1"))).isEqualTo(-1);
    assertThat(comparator.compare(new Version("1.2"), new Version("1.3"))).isEqualTo(-1);
    assertThat(comparator.compare(new Version("1.2"), new Version("2"))).isEqualTo(-1);
    assertThat(comparator.compare(new Version("1.2.1"), new Version("1.2"))).isEqualTo(-1);
    assertThat(comparator.compare(new Version("1.2-SNAPSHOT"), new Version("1.2"))).isEqualTo(-1);
    assertThat(comparator.compare(new Version("1.2"), new Version("1.2-SNAPSHOT"))).isEqualTo(1);
  }


  @Test
  public void shouldSortVersion() throws Exception {

    List<String> sortedVersions =
        Arrays.asList("1.2", "1.3", "1.9", "1.9-SNAPSHOT", "1.56", "3.1.6", "3.2", null).stream()
            .map(v -> v == null ? null : new Version(v)).sorted(new VersionComparator())
            .map(v -> v == null ? null : v.asString()).collect(Collectors.toList());

    assertThat(sortedVersions).isEqualTo(
        Arrays.asList("1.2", "1.3", "1.9-SNAPSHOT", "1.9", "1.56", "3.1.6", "3.2", null));

  }


}
