package org.archeo4j.core.analyzer;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.AnalyzedCatalog;
import org.archeo4j.core.model.AnalyzedClass;
import org.archeo4j.core.model.AnalyzedMethod;

public class CatalogService {

  private AnalyzedCatalog catalog;

  public CatalogService(AnalyzisConfig analyzisConfig) {
    super();
    this.catalog = new CatalogBuilder(analyzisConfig).loadOrLoadCatalog();
  }

  public void duplicatedClasses() {
    System.out.println(" ************** duplicated classes");
    duplicateClassesStream()
        .forEach(
            l -> System.out.println(l.get(0).getName()
                + " in "
                + l.stream().map(AnalyzedClass::getArtefact).map(AnalyzedArtefact::getDisplayName)
                    .collect(Collectors.toList()) + "\n\tconflicting methods "
                + conflictingMethods(l)));
  }

  private String conflictingMethods(List<AnalyzedClass> classes) {
    Set<String> allmethods =
        classes.stream().flatMap(ac -> ac.getDeclaredMethods().stream())
            .map(m -> m.getFullyQualifiedMethodName() + " - " + m.getSignature())
            .collect(Collectors.toSet());



    return allmethods.toString();
  }

  private Stream<List<AnalyzedClass>> duplicateClassesStream() {
    return catalog.analyzedClasses().stream()
        .collect(Collectors.groupingBy(AnalyzedClass::getName)).values().stream()
        .sorted((a, b) -> a.get(0).getName().compareTo(b.get(0).getName()))
        .filter(l -> l.size() > 1);
  }

  public void whoIsDeclaring(String string) {
    System.out.println(" ******* who is declaring " + string);
    catalog
        .analyzedClasses()
        .stream()
        .filter(analyzedClass -> analyzedClass.getName().contains(string))
        .forEach(
            ac -> System.out.println(" " + ac.getArtefact().getDisplayName() + "\n\t"
                + ac.getDeclaredMethods()));
  }

  public void whoIsUsing(String string, String from) {
    System.out.println(" ******* who is using " + string + " from " + from);
    catalog
        .analyzedMethods()
        .stream()
        .filter(m -> m.getFullyQualifiedMethodName().matches(from))
        .filter(
            m -> m.getCalledMethods().stream()
                .anyMatch(called -> called.getFullyQualifiedMethodName().contains(string)))
        .forEach(
            m -> System.out.println(m.getFullyQualifiedMethodName()
                + " "
                + m.getDeclaringClass().getArtefact().getDisplayName()
                + "\n calling :\n\t"
                + m.getCalledMethods().stream().map(cm -> cm.getFullyQualifiedMethodName())
                    .collect(Collectors.joining("\n\t"))));

  }

  public void methodsAnnotatedWith(String annotation, String from) {
    System.out.println(" ******* method annotated with " + annotation + " from " + from);
    catalog
        .analyzedMethods()
        .stream()
        .filter(
            m -> (m.getClassName() + "." + m.getMethodName()).matches(from)
                && m.getAnnotations().stream().anyMatch(ann -> ann.toString().contains(annotation)))
        .forEach(
            m -> System.out.println(m.getFullyQualifiedMethodName()
                + " "
                + m.getDeclaringClass().getArtefact().getDisplayName()
                + "\n calling :\n\t"
                + m.getAnnotations().stream().map(a -> a.toString())
                    .collect(Collectors.joining("\n\t"))));

  }

  public void classAnnotatedWith(String annotation, String from) {
    System.out.println(" ******* classes annotated with " + annotation + " from " + from);
    catalog
        .analyzedClasses()
        .stream()
        .filter(analyzedClass -> analyzedClass.getAnnotations().toString().contains(annotation))
        .forEach(
            analyzedClass -> System.out.println(analyzedClass.getName() + " "
                + analyzedClass.getAnnotations()));
  }
}
