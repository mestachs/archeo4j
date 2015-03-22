package org.archeo4j.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyzedClass implements Serializable {

  private static final long serialVersionUID = -456764755917967320L;

  private AnalyzedArtefact artefact;
  private String name;

  private List<AnalyzedMethod> declaredMethods = new ArrayList<AnalyzedMethod>();

  public AnalyzedClass(String className) {
    this.name = className;
  }

  public void addAnalyzedMethod(AnalyzedMethod analyzedMethod) {
    analyzedMethod.setDeclaringClass(this);
    declaredMethods.add(analyzedMethod);
  }

  public String getName() {
    return name;
  }



  @Override
  public String toString() {
    return "AnalyzedClass [name=" + name + ", declaredMethodsSize=" + declaredMethods.size() + "]";
  }

  public void setAnalyzedArtefact(AnalyzedArtefact analyzedArtefact) {
    this.artefact = analyzedArtefact;
  }

  public List<AnalyzedMethod> findMethodCall(AnalyzedMethod calledMethod) {
    return declaredMethods.stream().filter(method -> method.match(calledMethod))
        .collect(Collectors.toList());
  }

  public List<AnalyzedMethod> getDeclaredMethods() {
    return declaredMethods;
  }

  public AnalyzedArtefact getArtefact() {
    return artefact;
  }

}
