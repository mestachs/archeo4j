package org.archeo4j.core.model.report;

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

  @Override
  public String toString() {
    return "DiffEntry [status=" + status + ", before="
        + (before == null ? "" : before.getDisplayName()) + ", after="
        + (after == null ? "" : after.getDisplayName()) + "]";
  }


}
