package org.archeo4j.core.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.AnalyzedCatalog;
import org.archeo4j.core.model.AnalyzedClass;
import org.archeo4j.core.model.report.ConflictingMethod;
import org.archeo4j.core.support.ArtefactComparator;

public class CatalogService {

  private AnalyzedCatalog catalog;

  public CatalogService(AnalyzisConfig analyzisConfig) {
    super();
    this.catalog = new CatalogBuilder(analyzisConfig).loadOrLoadCatalog();
  }

  public void duplicatedClasses() {
    System.out.println(" ************** duplicated classes");
    duplicateClassesStream().forEach(
        l -> {
          List<ConflictingMethod> conflictingMethods = conflictingMethodsFor(l);
          System.out.println(l.get(0).getName()
              + " ("
              + l.size()
              + ")"
              + " in "
              + l.stream().map(AnalyzedClass::getArtefact).map(AnalyzedArtefact::getDisplayName)
                  .collect(Collectors.toList()));
          if (!conflictingMethods.isEmpty()) {
            System.out.println("\tconflicting "
                + conflictingMethods.size()
                + " methods "
                + conflictingMethods.stream().map(m -> m.toString())
                    .collect(Collectors.joining("\n\t\t")));
          }
        });



  }

  private List<ConflictingMethod> conflictingMethodsFor(List<AnalyzedClass> sameClasses) {

    Set<Object> methods = sameClasses.stream().flatMap(cl -> {
      return cl.getDeclaredMethods().stream().map(m -> m.getFullyQualifiedMethodName());
    }).collect(Collectors.toSet());
    List<String> methodNames = new ArrayList(methods);
    Collections.sort(methodNames);

    List<ConflictingMethod> conflictingMethods = new ArrayList<>();

    for (String methodName : methodNames) {
      List<AnalyzedClass> missingMethodInClasses =
          sameClasses
              .stream()
              .filter(
                  ac -> ac.getDeclaredMethods().stream()
                      .noneMatch(m -> methodName.equals(m.getFullyQualifiedMethodName())))
              .collect(Collectors.toList());
      if (!missingMethodInClasses.isEmpty()) {
        conflictingMethods.add(new ConflictingMethod(methodName, missingMethodInClasses));
      }
    }


    return conflictingMethods;
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

  public void sortedArtefacts() {
    catalog
        .analyzedArtefacts()
        .stream()
        .sorted(new ArtefactComparator())
        .forEach(
            a -> {
              System.out.println(a.getDisplayName());
              a.getBundledAterfacts().values().stream().sorted(new ArtefactComparator())
                  .forEach(ba -> System.out.println("\t" + ba.getDisplayName()));
            });

  }
}
