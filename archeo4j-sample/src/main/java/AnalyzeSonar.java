import java.util.function.Predicate;

import org.archeo4j.core.analyzer.AnalyzisConfig;
import org.archeo4j.core.analyzer.CatalogService;


public class AnalyzeSonar {


  public static void main(String[] args) {
    CatalogService catalogService = new CatalogService(new AnalyzisConfig() {

      @Override
      public String toCatalogPath() {
        return "../..";
      }

      @Override
      public Predicate<String> classFilter() {
        return s -> s.startsWith("org");
      }
    });

    catalogService.whoIsDeclaring("TendencyAnalyser");

    catalogService.whoIsUsing("TendencyAnalyser", ".*");

    catalogService.methodsAnnotatedWith("@javax.persistence.PreUpdate", ".*");

    catalogService.classAnnotatedWith("@javax.persistence.Entity", ".*");
    catalogService.classAnnotatedWith("org.hibernate", ".*");
    catalogService.classAnnotatedWith("Deprecated", ".*");
    
    catalogService.duplicatedClasses();
  }
}
