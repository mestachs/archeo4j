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

  @Override
  public String toString() {
    return "DiffEntry [status=" + status + ", before=" + before + ", after=" + after + "]";
  }


}
