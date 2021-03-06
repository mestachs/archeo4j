package org.archeo4j.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyzedClass implements Serializable {

  private static final long serialVersionUID = -456764755917967320L;

  private AnalyzedArtefact artefact;
  private String name;
  private String superClassName;

  private List<AnalyzedMethod> declaredMethods = new ArrayList<AnalyzedMethod>();
  private List<AnalyzedAnnotation> annotations = new ArrayList<>();
  private List<String> interfaceNames = new ArrayList<>();
  
  public AnalyzedClass(String className) {
    this.name = className;
  }
  
  public List<String> getInterfaceNames() {
    return interfaceNames;
  }

  public void setInterfaceNames(List<String> interfaceNames) {
    this.interfaceNames = interfaceNames;
  }

  public String getSuperClassName() {
    return superClassName;
  }


  public void setSuperClassName(String superClassName) {
    this.superClassName = superClassName;
  }

  public List<AnalyzedAnnotation> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<AnalyzedAnnotation> annotations) {
    this.annotations = annotations;
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
