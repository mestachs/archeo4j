package org.archeo4j.core.support;

import java.util.Comparator;

import org.archeo4j.core.model.AnalyzedArtefact;

public class ArtefactComparator implements Comparator<AnalyzedArtefact> {
  VersionComparator versionComparator = new VersionComparator();

  @Override
  public int compare(AnalyzedArtefact a1, AnalyzedArtefact a2) {
    if (a1.getGroupId() == null || a2.getGroupId() == null) {
      return a1.getDisplayName().compareTo(a2.getDisplayName());
    }

    int groupIdCompare = a1.getGroupId().compareTo(a2.getGroupId());
    if (groupIdCompare != 0)
      return groupIdCompare;
    int artefactIdCompare = a1.getArtefactId().compareTo(a2.getArtefactId());
    if (artefactIdCompare != 0)
      return artefactIdCompare;
    int versionCompare =
        versionComparator.compare(a1.getArtefactVersion(), a2.getArtefactVersion());
    return versionCompare;
  }
}
