package hu.rxd.toolbox.qtest.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiffClassificator {
  public static interface Classifier {

    String getName();

    boolean accept(DiffObject dio);
  }

  List<Classifier> classifiers = new ArrayList<Classifier>();

  public static class StatsOnlyChangeClassifier implements Classifier {

    @Override
    public String getName() {
      return "statsOnly";
    }

    @Override
    public boolean accept(DiffObject dio) {
      if(dio.getL().size()!=dio.getR().size())
        return false;
      
      Iterator<String> lIter = dio.getL().iterator();
      Iterator<String> rIter = dio.getR().iterator();

      while(lIter.hasNext()){
        String strL = lIter.next();
        String strR = rIter.next();
        
        if(classifyLine(strL).equals(classifyLine(strR)))
          continue;
        
        return false;
      }
      return true;
    }

    private String classifyLine(String string) {
      if (string.contains("aggregations: compute_stats")) {
        return "__AGGR_COMPUTE_STATS";
      }
      if (string.contains("Statistics: Num rows:")) {
        return "__STATISTICS";
      }
      if ((string.contains("expressions: ") || string.contains("columns.types")) && (string
          .contains(
              "struct<columntype:string,maxlength:bigint,sumlength:bigint,count:bigint,countnulls:bigint,bitvector")
          || string.contains("struct<columntype:string,min:bigint,max:bigint,")
          || string.contains("struct<columntype:string,min:double,max:double,")
          || string.contains(
              "struct<columntype:string,maxlength:bigint,avglength:double,countnulls:bigint,numdistinctvalues:bigint"))) {
        return "__TYPES";
      }
      if (string.contains("Stage-") && string.contains("depends on stages")) {
        return "__STAGE_DEPS";
      }
      
      if (string.matches("^ *(Map|Reducer) [0-9]+ <- (Map|Reducer) [0-9]+ \\(.*\\)$")) {
        return "__STAGE_DEPS_TEZ";
      }
//      Reducer 2 <- Map 1 (GROUP, 4)


      return string;
    }
  }

  public DiffClassificator() {
    classifiers.add(new StatsOnlyChangeClassifier());
  }

  public static class DiffObject {

    private List<String> l = new ArrayList<>();
    private List<String> r = new ArrayList<>();

    public DiffObject(Iterable<String> diffLines) {
      for (String string : diffLines) {
        if (string.equals("---"))
          continue;
        if (string.startsWith("-") || string.startsWith("<")) {
          l.add(string.substring(1));

        }
        if (string.startsWith("+") || string.startsWith(">")) {
          r.add(string.substring(1));
        }
      }
    }

    List<String> getL() {
      return l;
    }

    List<String> getR() {
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
