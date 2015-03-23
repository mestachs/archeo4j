package org.archeo4j.core.model;

import java.io.Serializable;

public class AnalyzedAnnotation implements Serializable {

  private static final long serialVersionUID = 2595731495593890975L;

  private String annot;


  public AnalyzedAnnotation(String annot) {
    this.annot = annot;
  }


  @Override
  public String toString() {
    return "AnalyzedAnnotation [annotation=" + annot + "]";
  }

}
