package hu.rxd.toolbox.jenkins;

import java.util.Comparator;

/**
 * Represents 1 test's execution
 */
public class TestEntry {

  public static final Comparator<? super TestEntry> LABEL_COMPARATOR = new Comparator<TestEntry>() {

    @Override
    public int compare(TestEntry o1, TestEntry o2) {
      return o1.label.compareTo(o2.label);
    }
  };
  private String label;
  private double duration;
  private String className;
  private String methodName;
  private String status;

  public TestEntry(String className, String methodName, double duration, String status) {
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

  /**
   * maven compatible test pattern.
   *
   * can be passed as -Dtest=
   */
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