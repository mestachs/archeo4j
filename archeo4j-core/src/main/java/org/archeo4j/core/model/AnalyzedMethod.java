package org.archeo4j.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnalyzedMethod implements Serializable {

  private static final long serialVersionUID = 3555284400729724970L;

  private String className;
  private String methodName;
  private String signature;
  private List<AnalyzedMethod> calledMethods = new ArrayList<AnalyzedMethod>();

  private AnalyzedClass declaringClass;

  public AnalyzedMethod(String className, String methodName, String signature) {
    super();
    this.className = className;
    this.methodName = methodName;
    this.signature = signature;
  }

  public void setCalledMethods(List<AnalyzedMethod> methodsCalled) {
    this.calledMethods = methodsCalled;
  }

  public List<AnalyzedMethod> getCalledMethods() {
    return calledMethods;
  }

  public AnalyzedClass getDeclaringClass() {
    return declaringClass;
  }

  public void setDeclaringClass(AnalyzedClass declaringClass) {
    this.declaringClass = declaringClass;
  }

  public String getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getFullyQualifiedMethodName() {
    return this.getClassName() + "." + this.getMethodName();
  }

  public String getSignature() {
    return signature;
  }

  @Override
  public String toString() {
    return "AnalyzedMethod [" + className + "." + methodName + ", signature=" + signature
        + "calledMethodSize" + calledMethods.size() + "]";
  }

  public boolean match(AnalyzedMethod calledMethod) {

    boolean matchMethodName =
        calledMethod.getMethodName() == null
            || calledMethod.getMethodName().equals(getMethodName());
    boolean matchClassName = calledMethod.getClassName().equals(getClassName());
    return matchMethodName && matchClassName;
  }
}
