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

  public DiffReport diff(List<AnalyzedArtefact> rawBefore, List<AnalyzedArtefact> rawAfter) {
    System.out.println("rawBefore : "
        + rawBefore.stream().map(a -> a.getDisplayName()).sorted().collect(Collectors.joining()));
    System.out.println("rawAfter : "
        + rawAfter.stream().map(a -> a.getDisplayName()).sorted().collect(Collectors.joining()));
    DiffReport report = new DiffReport();
    Set<AnalyzedArtefact> before = new HashSet<AnalyzedArtefact>(rawBefore);

    Set<AnalyzedArtefact> after = new HashSet<AnalyzedArtefact>(rawAfter);


    SetView<AnalyzedArtefact> unmodified = Sets.intersection(before, after);
    VersionComparator versionComparator = new VersionComparator();
    unmodified
        .stream()
        .map(
            unmodifedArtefact -> new DiffEntry(
                unmodifedArtefact,
                unmodifedArtefact,
                DiffStatus.UNMODIFIED))
        .forEach(removedArtefact -> report.addDiffEntry(removedArtefact));
    Map<String, List<AnalyzedArtefact>> afterByGA = group(after);

    Map<String, List<AnalyzedArtefact>> beforeByGA = group(before);


    MapDifference<String, List<AnalyzedArtefact>> diff = Maps.difference(beforeByGA, afterByGA);
    ArtefactComparator artefactComparator = new ArtefactComparator();
    diff
        .entriesDiffering()
        .values()
        .stream()
        .forEach(
            valueDiff -> {
              Collections.sort(valueDiff.leftValue(), artefactComparator);
              Collections.sort(valueDiff.rightValue(), artefactComparator);

              for (int i = 0; i < valueDiff.leftValue().size(); i++) {
                AnalyzedArtefact left = valueDiff.leftValue().get(i);
                Optional<AnalyzedArtefact> right =
                    valueDiff
                        .rightValue()
                        .stream()
                        .filter(
                            a -> a
                                .getArtefactVersion()
                                .getMajor()
                                .equals(left.getArtefactVersion().getMajor()))
                        .findFirst();
                if (right.isPresent()) {
                  int compare =
                      versionComparator.compare(left.getArtefactVersion(), right
                          .get()
                          .getArtefactVersion());
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


    diff
        .entriesOnlyOnLeft()
        .values()
        .stream()
        .flatMap(l -> l.stream())
        .map(removedArtefact -> new DiffEntry(removedArtefact, null, DiffStatus.REMOVED))
        .forEach(entry -> report.addDiffEntry(entry));

    diff
        .entriesOnlyOnRight()
        .values()
        .stream()
        .flatMap(l -> l.stream())
        .map(addedArtefact -> new DiffEntry(null, addedArtefact, DiffStatus.ADDED))
        .forEach(entry -> report.addDiffEntry(entry));



    System.out.println(report);
    return report;
  }

  private Map<String, List<AnalyzedArtefact>> group(Set<AnalyzedArtefact> after) {
    return after.stream().collect(
        Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtefactId()));
  }
}
