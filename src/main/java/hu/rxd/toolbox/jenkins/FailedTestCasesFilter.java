package hu.rxd.toolbox.jenkins;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Joiner;

public class FailedTestCasesFilter {

  public List<TestEntry> entries;

  public FailedTestCasesFilter(String buildUrl) throws Exception {
    TestResults results = JenkinsTestResultsReader.fromJenkinsBuild(buildUrl);
    entries = new LinkedList<>(JenkinsTestResultsReader.testEntries(results));

  }

  static FailedTestCasesFilter fromBuildUrl(String buildUrl) throws Exception {
    return new FailedTestCasesFilter(buildUrl);
  }

  FailedTestCasesFilter filterFailed() {
    // Collections2.filter(entries, predicate)
    Iterator<TestEntry> it = entries.iterator();
    while (it.hasNext()) {
      if (it.next().isPassed()) {
        it.remove();
      }
    }
    return this;
  }

  void writeProps(String fileName) throws Exception {
    List<String> testLabels = collectLabels();
    try (FileOutputStream fos = new FileOutputStream(fileName)) {
      Properties props = new Properties();
      props.setProperty("TESTS", Joiner.on(',').join(testLabels));
      // props.setProperty("TESTS",
      // fos.write(Joiner.on(',').join(testLabels).b);
      props.store(fos, "some comment");
    }
  }

  void writeFile(String fileName) throws Exception {
    List<String> testLabels = collectLabels();
    try (FileOutputStream fos = new FileOutputStream(fileName)) {
      fos.write(Joiner.on(',').join(testLabels).getBytes());
    }
  }

  private List<String> collectLabels() {
    System.out.println("|entries| = " + entries.size());
    if (entries.size() > 400) {
      entries = entries.subList(0, 400);
      System.out.println("!!! first 400");
    }

    List<String> testLabels = new LinkedList<>();
    double totalTime = 0.0;
    for (TestEntry entry : entries) {
      totalTime += entry.getDuration();
      testLabels.add(entry.getLabel());
    }

    System.out.println(" expected runtime: " + totalTime);
    return testLabels;
  }

}
