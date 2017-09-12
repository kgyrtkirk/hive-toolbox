import java.util.ArrayList;
import java.util.List;

public class DiffClassificator {
  public static interface Classifier {

    String getName();

    boolean accept(Iterable<String> diffLines);
  }

  List<Classifier> classifiers = new ArrayList<Classifier>();

  public static class StatsOnlyChangeClassifier implements Classifier {

    @Override
    public String getName() {
      return "statsOnly";
    }

    @Override
    public boolean accept(Iterable<String> diff) {
      for (String string : diff) {
        if(string.equals("---"))
          continue;
          
        if (string.startsWith("+") || string.startsWith("-") || string.startsWith("<")
            || string.startsWith(">")) {

          if (string.contains("aggregations: compute_stats")) {
            continue;
          }
          if (string.contains("Statistics: Num rows:")) {
            continue;
          }
          if (string.contains("value expressions: ") && string.contains(
              "struct<columntype:string,maxlength:bigint,sumlength:bigint,count:bigint,countnulls:bigint,bitvector")) {
            continue;
          }
          return false;
          // - aggregations: compute_stats(key, 16), compute_stats(value, 16)
          // + aggregations: compute_stats(key, 'hll'), compute_stats(value, 'hll')
          // mode: hash
          // outputColumnNames: _col0, _col1
          // - Statistics: Num rows: 1 Data size: 968 Basic stats: COMPLETE Column stats: NONE
          // + Statistics: Num rows: 1 Data size: 864 Basic stats: COMPLETE Column stats: NONE

        }
      }
      return true;
    }
  }

  public DiffClassificator() {
    classifiers.add(new StatsOnlyChangeClassifier());
  }

  public String classify(Iterable<String> diffLines) {
    for (Classifier classifier : classifiers) {
      if (classifier.accept(diffLines)) {
        return classifier.getName();
      }
    }
    return "UNCLASSIFIED";
  }

}
