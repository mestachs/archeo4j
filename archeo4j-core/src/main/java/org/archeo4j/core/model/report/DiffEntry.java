package org.archeo4j.core.model.report;

import java.util.ArrayList;
import java.util.Collections;

import org.archeo4j.core.analyzer.ArtefactsDiffer;
import org.archeo4j.core.model.AnalyzedArtefact;

public class DiffEntry {

  private AnalyzedArtefact before;
  private AnalyzedArtefact after;
  private DiffStatus status;

  public DiffEntry(AnalyzedArtefact before, AnalyzedArtefact after, DiffStatus status) {
    super();
    this.before = before;
    this.after = after;
    this.status = status;
  }

  public AnalyzedArtefact getBefore() {
    return before;
  }

  public AnalyzedArtefact getAfter() {
    return after;
  }

  public DiffStatus getStatus() {
    return status;
  }

  public DiffReport getBundleArtefactDiff() {
    return new ArtefactsDiffer().diff(
        (before == null ? Collections.emptyList() : new ArrayList<AnalyzedArtefact>(before
            .getBundledAterfacts()
            .values())),
        (after == null ? Collections.emptyList() : new ArrayList<AnalyzedArtefact>(after
            .getBundledAterfacts()
            .values())));
  }

  @Override
  public String toString() {
    return "DiffEntry [status=" + status + ", before="
        + (before == null ? "" : before.getDisplayName()) + ", after="
        + (after == null ? "" : after.getDisplayName()) + "]";
  }


}
