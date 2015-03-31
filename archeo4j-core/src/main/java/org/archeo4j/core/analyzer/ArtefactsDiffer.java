package org.archeo4j.core.analyzer;

import java.util.List;

import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.report.DiffReport;

public class ArtefactsDiffer {

  public DiffReport diff(List<AnalyzedArtefact> rawBefore, List<AnalyzedArtefact> rawAfter) {
    return new DiffReport();
  }
}
