import java.util.ArrayList;
import java.util.function.Predicate;

import org.archeo4j.core.analyzer.AnalyzisConfig;
import org.archeo4j.core.analyzer.ArtefactsDiffer;
import org.archeo4j.core.analyzer.CatalogService;
import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.report.DiffReport;


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

    catalogService.sortedArtefacts();
    DiffReport report =
        new ArtefactsDiffer().diff(new ArrayList<AnalyzedArtefact>(catalogService
            .getCatalog()
            .analyzedArtefacts()), new ArrayList<AnalyzedArtefact>(catalogService
            .getCatalog()
            .analyzedArtefacts()));
    System.out.println("------------ bundle artefacts");
    System.out.println(report.getEntries().get(0).getBundleArtefactDiff());
  }
}
