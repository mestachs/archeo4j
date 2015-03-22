package org.archeo4j.core.analyzer;

import java.util.function.Predicate;

public interface AnalyzisConfig {
  Predicate<String> classFilter();

  String toCatalogPath();
}
