package hu.rxd.toolbox.qtest.diff.classifiers;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class StringOccurrenceClassifier implements Classifier {
  private String classifiedString;

  public StringOccurrenceClassifier(String classifiedString) {
    this.classifiedString = classifiedString;
  }

  @Override
  public String getName() {
    return "stringOccurrence_" + classifiedString;
  }

  @Override
  public boolean accept(DiffObject dio) {

    for (List<String> input : Lists.newArrayList(dio.l, dio.r)) {
      Iterator<String> lIter = input.iterator();
      while (lIter.hasNext()) {
        String line = lIter.next();
        if (line.trim().contains(classifiedString)) {
          return true;
        }
      }
    }
    return false;
  }
}
