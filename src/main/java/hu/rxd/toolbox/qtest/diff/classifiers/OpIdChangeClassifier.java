package hu.rxd.toolbox.qtest.diff.classifiers;

import java.util.Iterator;

import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class OpIdChangeClassifier implements Classifier {

  @Override
  public String getName() {
    return "OPID";
  }

  @Override
  public boolean accept(DiffObject dio) {
    if (dio.getL().size() != dio.getR().size()) {
      return false;
    }

    Iterator<String> lIter = dio.getL().iterator();
    Iterator<String> rIter = dio.getR().iterator();

    while (lIter.hasNext()) {
      String strL = lIter.next().trim();
      String strR = rIter.next().trim();

      String strL2 = opIdCleaner(strL);
      String strR2 = opIdCleaner(strR);
      if (!strL2.equals(strR2)) {
        return false;
      }
    }
    return true;
  }

  private String opIdCleaner(String l) {
    return l.replaceAll("([A-Z]+)\\[[0-9]+\\]", "$1[XXX]").replaceAll("\\[([A-Z]+)_[0-9]+\\]", "[$1_XXX]")
        .replaceAll("(RS|JOIN|SEL)_[0-9]+", "RS_XXX");

  }
}
