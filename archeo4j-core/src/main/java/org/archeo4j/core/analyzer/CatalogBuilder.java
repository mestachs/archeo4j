package org.archeo4j.core.analyzer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.AnalyzedCatalog;

public class CatalogBuilder {
  private AnalyzisConfig analyzisConfig;
  private String catalogPath;

  public CatalogBuilder(AnalyzisConfig analyzisConfig) {
    this.analyzisConfig = analyzisConfig;
    try {
      this.catalogPath =
          new File(analyzisConfig.toCatalogPath() + File.separator + "catalog.ser").getCanonicalFile().getAbsolutePath();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }



  public AnalyzedCatalog loadOrLoadCatalog() {
    AnalyzedCatalog catalog = null;
    try {
      catalog = loadCatalog();
      throw new Exception();
    } catch (Exception e) {
      catalog = buildCatalog();
      storeCatalog(catalog);
    }

    return catalog;
  }

  private AnalyzedCatalog buildCatalog() {

    File directory = new File(analyzisConfig.toCatalogPath());
    List<String> warLocations = Arrays.asList(directory.list());

    warLocations =
        warLocations.stream().map(warLocation -> directory.getAbsolutePath() + "/" + warLocation)
            .collect(Collectors.toList());

    List<AnalyzedArtefact> wars =
        warLocations.stream().parallel()
            .filter(warLocation -> warLocation.endsWith(".war") || warLocation.endsWith(".jar"))
            .map(warLocation -> new ArtefactAnalyzer(analyzisConfig).analyzeJar(warLocation))
            .collect(Collectors.toList());
    return new AnalyzedCatalog(wars);

  }

  private AnalyzedCatalog loadCatalog() {
    try (InputStream file = new FileInputStream(catalogPath);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);) {
      return (AnalyzedCatalog) input.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("failed to load Catalog from " + catalogPath, e);
    }
  }

  private void storeCatalog(AnalyzedCatalog catalog) {
    try (OutputStream file = new FileOutputStream(catalogPath);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);) {
      output.writeObject(catalog);
    } catch (IOException e) {
      throw new RuntimeException("failed to save Catalog from " + catalogPath, e);
    }
  }

}
