package org.archeo4j.core.model.report;

import java.util.ArrayList;

import org.archeo4j.core.analyzer.ArtefactsDiffer;
import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.support.VersionComparator;

public class DiffEntry {

  private static final VersionComparator versionComparator = new VersionComparator();
  
  private AnalyzedArtefact before;
  private AnalyzedArtefact after;
  private DiffStatus status;

  public DiffEntry(AnalyzedArtefact before, AnalyzedArtefact after) {
    super();
    this.before = before;
    this.after = after;

    if (before == null)
      this.status = DiffStatus.REMOVED;
    else if (after == null)
      this.status = DiffStatus.ADDED;
    else {

      int compare =
          versionComparator.compare(before.getArtefactVersion(), after.getArtefactVersion());
      if (compare > 0) {
        this.status = DiffStatus.DOWNGRADED;
      } else if (compare < 0) {
        this.status = DiffStatus.UPGRADED;
      } else {
        this.status = DiffStatus.UNMODIFIED;        
      }
    }

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
    if (status.equals(DiffStatus.REMOVED) || status.equals(DiffStatus.ADDED)) {
      return new DiffReport();
    }
    return new ArtefactsDiffer().diff(new ArrayList<AnalyzedArtefact>(before
        .getBundledAterfacts()
        .values()), new ArrayList<AnalyzedArtefact>(after.getBundledAterfacts().values()));
  }

  @Override
  public String toString() {
    return "DiffEntry [status=" + status + ", before="
        + (before == null ? "" : before.getDisplayName()) + ", after="
        + (after == null ? "" : after.getDisplayName()) + "]";
  }


}
