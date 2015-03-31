package org.archeo4j.core.model.report;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiffReport {
  private List<DiffEntry> entries = new ArrayList<DiffEntry>();

  public List<DiffEntry> getEntries() {
    return entries;
  }

  public void addDiffEntry(DiffEntry entry) {
    this.entries.add(entry);
  }

  @Override
  public String toString() {
    return getEntries().stream().map(e -> e.toString()).collect(Collectors.joining("\n"));
  }

}
