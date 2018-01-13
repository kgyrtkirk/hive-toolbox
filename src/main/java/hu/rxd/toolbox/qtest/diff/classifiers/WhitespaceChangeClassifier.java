package hu.rxd.toolbox.qtest.diff.classifiers;

import java.util.Iterator;

import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class WhitespaceChangeClassifier implements Classifier {
  @Override
  public String getName() {
    return "whitespace";
  }

  @Override
  public boolean accept(DiffObject dio) {
    Iterator<String> rIter = dio.r.iterator();
    Iterator<String> lIter = dio.l.iterator();

    while (rIter.hasNext() && lIter.hasNext()) {
      String lLine = lIter.next();
      String rLine = rIter.next();

      if (!lLine.trim().equals(rLine.trim())) {
        continue;
      }
      return true;
    }
    return false;
  }
}
