package org.archeo4j.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyzedCatalog implements Serializable {

  private static final long serialVersionUID = -5346383553431940923L;

  public AnalyzedCatalog(List<AnalyzedArtefact> artefacts) {
    this.artefacts = artefacts.stream().filter(a -> a != null).collect(Collectors.toList());
  }

  private List<AnalyzedArtefact> artefacts;

  public List<AnalyzedMethod> analyzedMethods() {
    return artefacts.stream().flatMap(artefact -> artefact.getMethods().stream())
        .collect(Collectors.toList());
  }

  public List<AnalyzedClass> analyzedClasses() {
    return artefacts.stream().flatMap(artefact -> artefact.getClasses().stream())
        .collect(Collectors.toList());
  }
  
  public List<AnalyzedArtefact> analyzedArtefacts() {
    return artefacts;
  }
}
