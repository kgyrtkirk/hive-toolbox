
package hu.rxd.toolbox.jenkins;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestResults {

  public static class Suite {
    public static class Case {
      public String className;
      public String name;
      public String status;
      public double duration;
    }

    public List<Case> cases;
  };

  public String _class;
  public List<Suite> suites;

  public static class Entry {

    public static final Comparator<? super Entry> LABEL_COMPARATOR = new Comparator<Entry>() {

      @Override
      public int compare(Entry o1, Entry o2) {
        return o1.label.compareTo(o2.label);
      }
    };
    private String label;
    private double duration;
    private String className;
    private String methodName;
    private String status;

    public Entry(String className, String methodName, double duration, String status) {
      this.duration = duration;
      this.methodName = methodName;
      this.status = status;
      this.className = className.replace('.', '/') + ".class";
      // label = className.replaceAll(".*\\.", "") + "#" + methodName;
      label = className + "#" + methodName;
    }

    @Override
    public String toString() {
      return String.format("%s  %d", label, duration);
    }

    public String getLabel() {
      return label;
    }

    public double getDuration() {
      return duration;
    }

    public boolean isFailed() {
      return "FAILED".equals(status);
    }

    public boolean isPassed() {
      return "PASSED".equals(status);
    }

  }

  public static List<Entry> testEntries(TestResults results) {
    List<Entry> entries = new ArrayList<Entry>();

    for (Suite s : results.suites) {
      for (Suite.Case c : s.cases) {
        entries.add(new Entry(c.className, c.name, c.duration, (c.status)));
      }
    }
    return entries;
  }

}
