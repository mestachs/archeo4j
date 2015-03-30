package org.archeo4j.core.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.archeo4j.core.model.AnalyzedAnnotation;
import org.archeo4j.core.model.AnalyzedClass;
import org.archeo4j.core.model.AnalyzedMethod;

public class ClassAnalyzer {

  private AnalyzisConfig analyzisConfig;

  public ClassAnalyzer(AnalyzisConfig analyzisConfig) {
    this.analyzisConfig = analyzisConfig;
  }

  public AnalyzedClass analyzeCallsForClass(String className, byte[] classBytes, String location) {
    if (!analyzisConfig.classFilter().test(className))
      return null;

    AnalyzedClass analyzedClass = new AnalyzedClass(className);
    ClassPool cp = new ClassPool();
    CtClass ctClass = parseClassByteCode(className, classBytes, cp);
    analyzeClassAnnotations(analyzedClass, ctClass);
    analyzeInterfaces(analyzedClass, ctClass);

    try {
      CtMethod[] methods = ctClass.getDeclaredMethods();

      for (CtMethod ctMethod : methods) {

        AnalyzedMethod method = analyzeMethodCalls(ctMethod);
        analyzeMethodAnnotations(method, ctMethod);

        analyzedClass.addAnalyzedMethod(method);
      }
    } catch (RuntimeException e) {
      System.out.println("WARN !! failed to analyze " + className + " " + e.getMessage());

    }
    return analyzedClass;
  }

  private void analyzeInterfaces(AnalyzedClass analyzedClass, CtClass ctClass) {
    analyzedClass.setInterfaceNames(Arrays.asList(ctClass.getClassFile2().getInterfaces()));
    analyzedClass.setSuperClassName(ctClass.getClassFile2().getSuperclass());
  }


  private void analyzeClassAnnotations(AnalyzedClass analyzedClass, CtClass ctClass) {
    List<AnalyzedAnnotation> annotations = new ArrayList<>();
    for (Object o : ctClass.getClassFile2().getAttributes()) {
      if (o instanceof AnnotationsAttribute) {
        AnnotationsAttribute attribute = (AnnotationsAttribute) o;
        for (Annotation analyzedAnnotation : attribute.getAnnotations()) {
          annotations.add(new AnalyzedAnnotation(analyzedAnnotation.toString()));
        }
      }
    }
    analyzedClass.setAnnotations(annotations);
  }

  private void analyzeMethodAnnotations(AnalyzedMethod method, CtMethod ctMethod) {
    List<AnalyzedAnnotation> annotations = new ArrayList<>();
    for (Object o : ctMethod.getMethodInfo().getAttributes()) {
      if (o instanceof AnnotationsAttribute) {
        AnnotationsAttribute attribute = (AnnotationsAttribute) o;
        annotations.add(new AnalyzedAnnotation(attribute.toString()));
      }
    }
    method.setAnnotations(annotations);
  }

  private CtClass parseClassByteCode(String className, byte[] classBytes, ClassPool cp) {
    cp.appendClassPath(new ByteArrayClassPath(className, classBytes));
    CtClass ctClass;
    try {
      ctClass = cp.get(className);
    } catch (NotFoundException e1) {
      throw new RuntimeException(e1);
    }
    return ctClass;
  }

  private AnalyzedMethod analyzeMethodCalls(CtMethod ctMethod) {

    final List<AnalyzedMethod> methodsCalled = new ArrayList<AnalyzedMethod>();
    try {
      ctMethod.instrument(new ExprEditor() {
        @Override
        public void edit(MethodCall m) throws CannotCompileException {
          if (analyzisConfig.classFilter().test(m.getClassName())) {
            methodsCalled.add(asAnalyzedMethod(m));
          }
        }
      });
    } catch (CannotCompileException e) {
      throw new RuntimeException(e);
    }
    AnalyzedMethod method = asAnalyzedMethod(ctMethod);
    method.setCalledMethods(methodsCalled);
    return method;
  }

  private static AnalyzedMethod asAnalyzedMethod(CtMethod ctMethod) {
    String params =
        (ctMethod.getLongName().replace(ctMethod.getDeclaringClass().getName(), "").replace("."
            + ctMethod.getName(), ""));

    return new AnalyzedMethod(ctMethod.getDeclaringClass().getName(), ctMethod.getName(),
        ctMethod.getGenericSignature() != null ? ctMethod.getGenericSignature()
            : ctMethod.getSignature(), params, ctMethod.getModifiers());
  }

  private static AnalyzedMethod asAnalyzedMethod(MethodCall m) {
    return new AnalyzedMethod(m.getClassName(), m.getMethodName(), m.getSignature());
  }

}
