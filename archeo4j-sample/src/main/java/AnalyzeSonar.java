import java.util.ArrayList;
import java.util.function.Predicate;

import org.archeo4j.core.analyzer.AnalyzisConfig;
import org.archeo4j.core.analyzer.ArtefactsDiffer;
import org.archeo4j.core.analyzer.CatalogService;
import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.report.DiffReport;


public class AnalyzeSonar {


  public static void main(String[] args) {
    CatalogService catalogServiceProd = new CatalogService(new AnalyzisConfig() {

      @Override
      public String toCatalogPath() {
        return "../../repo/prod";
      }

      @Override
      public Predicate<String> classFilter() {
        return s -> s.startsWith("org");
      }
    });

    CatalogService catalogServicePreProd = new CatalogService(new AnalyzisConfig() {

      @Override
      public String toCatalogPath() {
        return "../../repo/preprod";
      }

      @Override
      public Predicate<String> classFilter() {
        return s -> s.startsWith("org");
      }
    });

    catalogServiceProd.whoIsDeclaring("TendencyAnalyser");

    catalogServiceProd.whoIsUsing("TendencyAnalyser", ".*");

    catalogServiceProd.methodsAnnotatedWith("@javax.persistence.PreUpdate", ".*");

    catalogServiceProd.classAnnotatedWith("@javax.persistence.Entity", ".*");
    catalogServiceProd.classAnnotatedWith("org.hibernate", ".*");
    catalogServiceProd.classAnnotatedWith("Deprecated", ".*");

    catalogServiceProd.duplicatedClasses();

    catalogServiceProd.sortedArtefacts();
    DiffReport report =
        new ArtefactsDiffer().diff(new ArrayList<AnalyzedArtefact>(catalogServiceProd
            .getCatalog()
            .analyzedArtefacts()), new ArrayList<AnalyzedArtefact>(catalogServicePreProd
            .getCatalog()
            .analyzedArtefacts()));
    System.out.println("------------ diff between prod and preprod");
    System.out.println(report);

    report.getEntries().forEach(
        e -> System.out.println("------------ " + e + "\n" + e.getBundleArtefactDiff()));
  }
}
