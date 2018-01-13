package hu.rxd.toolbox.qtest.diff.classifiers;

import java.util.Iterator;

import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class TextMatchClassifier implements Classifier {
  
  private String text;

  public TextMatchClassifier(String text) {
    this.text = text;
  }
  
  @Override
  public String getName() {
    return String.format("textMatch_%s", normalize(text));
  }

  private String normalize(String textToNormalize) {
    return textToNormalize.toLowerCase().replaceAll("\\s+","_");
  }

  @Override
  public boolean accept(DiffObject dio) {
    Iterator<String> rIter = dio.r.iterator();

    while (rIter.hasNext()) {
      String rLine = rIter.next();

      if (!rLine.contains(text)) {
        return true;
      }
    }
    return false;
  }
}
