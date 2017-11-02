package hu.rxd.toolbox.qtest.diff;

import java.util.ArrayList;
import java.util.List;

import hu.rxd.toolbox.qtest.diff.classifiers.EmptyLineRemovalClassifier;
import hu.rxd.toolbox.qtest.diff.classifiers.PostHookChangeClassifier;
import hu.rxd.toolbox.qtest.diff.classifiers.StatTaskOnlyChangeClassifier;
import hu.rxd.toolbox.qtest.diff.classifiers.StatsDisappearClassifier;
import hu.rxd.toolbox.qtest.diff.classifiers.StatsNPChangeClassifier;
import hu.rxd.toolbox.qtest.diff.classifiers.StatsOnlyChangeClassifier;
import hu.rxd.toolbox.qtest.diff.classifiers.StatsPCChangeClassifier;
import hu.rxd.toolbox.qtest.diff.classifiers.StatsTaskRenameClassifier;
import hu.rxd.toolbox.qtest.diff.classifiers.ZeroStatsDisappearClassifier;

public class DiffClassificator {
  public static interface Classifier {

    String getName();

    boolean accept(DiffObject dio);
  }

  List<Classifier> classifiers = new ArrayList<Classifier>();

  public DiffClassificator() {
    classifiers.add(new StatsNPChangeClassifier());
    classifiers.add(new StatsPCChangeClassifier());
    classifiers.add(new StatsOnlyChangeClassifier());
    classifiers.add(new StatTaskOnlyChangeClassifier());
    classifiers.add(new PostHookChangeClassifier());
    classifiers.add(new EmptyLineRemovalClassifier());
    classifiers.add(new ZeroStatsDisappearClassifier());
    classifiers.add(new StatsDisappearClassifier());
    classifiers.add(new StatsTaskRenameClassifier());
  }

  public static class DiffObject {

    public List<String> l = new ArrayList<>();
    public List<String> r = new ArrayList<>();

    public DiffObject(Iterable<String> diffLines) {
      for (String string : diffLines) {
        if (string.equals("---")) {
          continue;
        }
        if (string.startsWith("-") || string.startsWith("<")) {
          l.add(string.substring(1));

        }
        if (string.startsWith("+") || string.startsWith(">")) {
          r.add(string.substring(1));
        }
      }
    }

    // these are in reverse order; fix upstream? or just fix for myself?
    // however...beware: L is the modified/new; R is the etalon

    @Deprecated
    public List<String> getL() {
      return l;
    }

    @Deprecated
    public List<String> getR() {
      return r;
    }

  }

  public String classify(Iterable<String> diffLines) {

    DiffObject dio = new DiffObject(diffLines);
    for (Classifier classifier : classifiers) {
      if (classifier.accept(dio)) {
        return classifier.getName();
      }
    }
    return "UNCLASSIFIED";
  }

}
