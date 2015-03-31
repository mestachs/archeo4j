package org.archeo4j.core.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.report.DiffEntry;
import org.archeo4j.core.model.report.DiffReport;
import org.archeo4j.core.model.report.DiffStatus;
import org.junit.Test;


public class ArtefactsDifferTest {

  AnalyzedArtefact ab_12 = createArtefact("a", "b", "1.2");
  AnalyzedArtefact ab_13 = createArtefact("a", "b", "1.3");
  AnalyzedArtefact gh_13 = createArtefact("g", "h", "1.3");
  AnalyzedArtefact cd_14 = createArtefact("c", "d", "1.4");
  AnalyzedArtefact ef_11 = createArtefact("e", "f", "1.1");
  AnalyzedArtefact xy_21 = createArtefact("x", "y", "2.1");
  AnalyzedArtefact xy_11 = createArtefact("x", "y", "1.1");

  AnalyzedArtefact ij_21 = createArtefact("i", "j", "2.1");
  AnalyzedArtefact ij_22 = createArtefact("i", "j", "2.2");
  AnalyzedArtefact ij_11 = createArtefact("i", "j", "1.1");
  AnalyzedArtefact ij_12 = createArtefact("i", "j", "1.2");

  @Test
  public void itShouldFindUnmodified() throws Exception {
    DiffReport diff =
        createDiff(
            Arrays.asList(ab_12, gh_13, cd_14, xy_21),
            Arrays.asList(ef_11, gh_13, ab_13, xy_11));
    DiffEntry unmodifiedEntry =
        diff
            .getEntries()
            .stream()
            .filter(e -> e.getStatus().equals(DiffStatus.UNMODIFIED))
            .findFirst()
            .get();
    assertThat(unmodifiedEntry.getBefore()).isEqualTo(gh_13);
    assertThat(unmodifiedEntry.getAfter()).isEqualTo(gh_13);
  }

  @Test
  public void itShouldFindModifiedMatchedByMajorFirst() throws Exception {
    System.out.println("------------------ ");
    DiffReport diff = createDiff(Arrays.asList(ij_11, ij_22), Arrays.asList(ij_12, ij_21));

    System.out.println("------------------ ");
    diff = createDiff(Arrays.asList(ij_11, ij_22), Arrays.asList(ij_12));
    System.out.println("------------------ ");
    diff = createDiff(Arrays.asList(ij_11, ij_22), Arrays.asList(ij_22));
    System.out.println("------------------ ");
  }

  DiffReport createDiff(List<AnalyzedArtefact> rawBefore, List<AnalyzedArtefact> rawAfter) {
    return new ArtefactsDiffer().diff(rawBefore, rawAfter);
  }

  AnalyzedArtefact createArtefact(String g, String a, String v) {
    AnalyzedArtefact artefact = new AnalyzedArtefact(a);
    artefact.setArtefactId(a);
    artefact.setGroupId(g);
    artefact.setVersion(v);
    return artefact;
  }
}
