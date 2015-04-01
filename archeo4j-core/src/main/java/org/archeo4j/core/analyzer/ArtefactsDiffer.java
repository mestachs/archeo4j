package org.archeo4j.core.analyzer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.archeo4j.core.model.AnalyzedArtefact;
import org.archeo4j.core.model.report.DiffEntry;
import org.archeo4j.core.model.report.DiffReport;
import org.archeo4j.core.model.report.DiffStatus;
import org.archeo4j.core.support.ArtefactComparator;
import org.archeo4j.core.support.VersionComparator;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class ArtefactsDiffer {
  VersionComparator versionComparator = new VersionComparator();
  ArtefactComparator artefactComparator = new ArtefactComparator();

  public DiffReport diff(List<AnalyzedArtefact> rawBefore, List<AnalyzedArtefact> rawAfter) {
  /*  System.out.println("rawBefore : " + toString(rawBefore));
    System.out.println("rawAfter : " + toString(rawAfter));
    */
    DiffReport report = new DiffReport();
    Set<AnalyzedArtefact> before = new HashSet<AnalyzedArtefact>(rawBefore);
    Set<AnalyzedArtefact> after = new HashSet<AnalyzedArtefact>(rawAfter);

    SetView<AnalyzedArtefact> unmodified = Sets.intersection(before, after);
    unmodified.stream().map(unmodifedArtefact -> new DiffEntry(unmodifedArtefact, unmodifedArtefact, DiffStatus.UNMODIFIED))
        .forEach(removedArtefact -> report.addDiffEntry(removedArtefact));

    // after.removeAll(unmodified);
    // before.removeAll(unmodified);

    MapDifference<String, List<AnalyzedArtefact>> diff = Maps.difference(group(before), group(after));

    diff.entriesDiffering().values().stream().forEach(valueDiff -> {
      Collections.sort(valueDiff.leftValue(), artefactComparator);
      Collections.sort(valueDiff.rightValue(), artefactComparator);
      for (AnalyzedArtefact left : valueDiff.leftValue()) {
        Optional<AnalyzedArtefact> right = valueDiff.rightValue().stream().filter(a -> a.getArtefactVersion().getMajor().equals(left.getArtefactVersion().getMajor())).findFirst();

        if (!right.isPresent() && valueDiff.rightValue().size() == 1) {
          right = Optional.of(valueDiff.rightValue().get(0));
        }

        if (right.isPresent()) {
          int compare = versionComparator.compare(left.getArtefactVersion(), right.get().getArtefactVersion());
          if (compare > 0) {
            report.addDiffEntry(new DiffEntry(left, right.get(), DiffStatus.DOWNGRADED));
          } else if (compare < 0) {
            report.addDiffEntry(new DiffEntry(left, right.get(), DiffStatus.UPGRADED));
          }
        } else {
          report.addDiffEntry(new DiffEntry(left, null, DiffStatus.REMOVED));
        }
      }

    });


    diff.entriesOnlyOnLeft().values().stream().flatMap(l -> l.stream()).map(removedArtefact -> new DiffEntry(removedArtefact, null, DiffStatus.REMOVED))
        .forEach(entry -> report.addDiffEntry(entry));

    diff.entriesOnlyOnRight().values().stream().flatMap(l -> l.stream()).map(addedArtefact -> new DiffEntry(null, addedArtefact, DiffStatus.ADDED))
        .forEach(entry -> report.addDiffEntry(entry));



    return report;
  }

  private String toString(List<AnalyzedArtefact> rawBefore) {
    return rawBefore.stream().map(a -> a.getDisplayName()).sorted().collect(Collectors.joining(" "));
  }

  private Map<String, List<AnalyzedArtefact>> group(Set<AnalyzedArtefact> after) {
    return after.stream().collect(Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtefactId()));
  }
}
