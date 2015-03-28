package org.archeo4j.core.support;

import java.util.Comparator;

import org.archeo4j.core.model.Version;

public class VersionComparator implements Comparator<Version> {

  @Override
  public int compare(Version va, Version vb) {
    if (va == null && vb == null)
      return 0;
    if (va == null && vb != null)
      return 1;
    if (vb == null && va != null)
      return -1;

    if (va.asString().equals(vb.asString())) {
      return 0;
    }

    int compareMajor = compareAsNumber(va.getMajor(), vb.getMajor());
    if (compareMajor != 0) {
      return compareMajor;
    }
    int compareMinor = compareAsNumber(va.getMinor(), vb.getMinor());
    if (compareMinor != 0) {
      return compareMinor;
    }

    int compareIncrement = compareAsNumber(va.getIncremental(), vb.getIncremental());
    if (compareIncrement != 0) {
      return compareIncrement;
    }

    // TODO handle qualifier and snapshot

    return 0;
  }

  private int compareAsNumber(String valuea, String valueb) {
    if (valuea.isEmpty() && valueb.isEmpty()) {
      return 0;
    }
    if (valuea.isEmpty() && !valueb.isEmpty()) {
      return 1;
    }
    if (!valuea.isEmpty() && valueb.isEmpty()) {
      return -1;
    }
    return Long.valueOf(valuea).compareTo(Long.valueOf(valueb));
  }
}
