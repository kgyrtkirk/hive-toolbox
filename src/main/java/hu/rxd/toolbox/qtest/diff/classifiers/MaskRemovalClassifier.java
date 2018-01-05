package hu.rxd.toolbox.qtest.diff.classifiers;

import java.util.Iterator;

import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class MaskRemovalClassifier implements Classifier {

  private String mask;

  public MaskRemovalClassifier(String mask) {
    this.mask = mask;
  }
  
  @Override
  public String getName() {
    return "maskRemoval";
  }

  @Override
  public boolean accept(DiffObject dio) {
    Iterator<String> rIter = dio.r.iterator();

    while (rIter.hasNext()) {
      String line = rIter.next();

      if (!line.trim().startsWith(mask)) {
        continue;
      }
      return true;
    }
    return false;
  }
}
