package org.archeo4j.core.model.report;

import java.util.List;
import java.util.stream.Collectors;

import org.archeo4j.core.model.AnalyzedClass;

public class ConflictingMethod {
  private String methodName;
  private List<AnalyzedClass> missingMethodInClasses;


  public ConflictingMethod(String methodName, List<AnalyzedClass> missingMethodInClasses) {
    super();
    this.methodName = methodName;
    this.missingMethodInClasses = missingMethodInClasses;
  }

  @Override
  public String toString() {
    return methodName
        + " not defined in : "
        + missingMethodInClasses.stream().map(ac -> ac.getArtefact().getDisplayName())
            .collect(Collectors.toList());
  }
}
