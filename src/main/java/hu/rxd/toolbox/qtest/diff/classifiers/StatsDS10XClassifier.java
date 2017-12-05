
package hu.rxd.toolbox.qtest.diff.classifiers;

import java.util.Iterator;

import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class StatsDS10XClassifier implements Classifier {

  private String suffix;
  private String suffix2;

  @Override
  public String getName() {
    return "dataser10x" + suffix + suffix2;
  }

  @Override
  public boolean accept(DiffObject dio) {
    if (dio.getL().size() != dio.getR().size()) {
      return false;
    }

    suffix = "";
    suffix2 = "";
    Iterator<String> lIter = dio.getL().iterator();
    Iterator<String> rIter = dio.getR().iterator();

    while (lIter.hasNext()) {
      String strL = lIter.next().trim();
      String strR = rIter.next().trim();

      if (!(strL.startsWith("Statistics: Num rows:") && strR.startsWith("Statistics: Num rows:"))) {
        return false;
      }

      String[] pL = strL.split(" ");
      String[] pR = strR.split(" ");

      if (!(pL.length == pR.length && pL.length == 13)) {
        return false;
      }
      for (int i = 0; i < pL.length; i++) {
        String vL = pL[i];
        String vR = pR[i];
        if (vL.equals(vR)) {
          continue;
        }

        switch (i) {
        case 3:
          suffix = "N";
          continue;
        case 6: {
          long l = Long.parseLong(vL);
          long r = Long.parseLong(vR);
          if (l != 10 * r) {
            suffix2 = "X";
          }
          continue;
        }
        default:
          return false;
        }
      }
    }
    return true;
  }
}