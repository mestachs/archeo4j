package org.archeo4j.core.analyzer;

import java.util.stream.Collectors;

import org.archeo4j.core.model.AnalyzedCatalog;

public class CatalogService {

  private AnalyzedCatalog catalog;

  public CatalogService(AnalyzisConfig analyzisConfig) {
    super();
    this.catalog = new CatalogBuilder(analyzisConfig).loadOrLoadCatalog();
  }

  public void whoIsDeclaring(String string) {
    System.out.println(" ******* how is declaring " + string);
    catalog
        .analyzedClasses()
        .stream()
        .filter(analyzedClass -> analyzedClass.getName().contains(string))
        .forEach(
            ac -> System.out.println(" " + ac.getArtefact().getDisplayName() + "\n\t"
                + ac.getDeclaredMethods()));
  }

  public void whoIsUsing(String string, String from) {
    System.out.println(" ******* how is using " + string + " from " + from);
    catalog
        .analyzedMethods()
        .stream()
        .filter(
            m -> (m.getClassName() + "." + m.getMethodName()).matches(from)
                && m.getCalledMethods().stream()
                    .anyMatch(called -> called.getFullyQualifiedMethodName().contains(string)))
        .forEach(
            m -> System.out.println(m.getFullyQualifiedMethodName()
                + " "
                + m.getDeclaringClass().getArtefact().getDisplayName()
                + "\n calling :\n\t"
                + m.getCalledMethods().stream().map(cm -> cm.getFullyQualifiedMethodName())
                    .collect(Collectors.joining("\n\t"))));

  }
}