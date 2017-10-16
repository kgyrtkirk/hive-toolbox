package hu.rxd.toolbox.qtest.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

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
      if(dio.getL().size()!=dio.getR().size()) {
        return false;
      }

      Iterator<String> lIter = dio.getL().iterator();
      Iterator<String> rIter = dio.getR().iterator();

      while(lIter.hasNext()){
        String strL = lIter.next();
        String strR = rIter.next();

        if(classifyLine(strL).equals(classifyLine(strR))) {
          continue;
        }

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

  public static class StatTaskOnlyChangeClassifier implements Classifier {

    @Override
    public String getName() {
      return "statTaskOnly";
    }

    @Override
    public boolean accept(DiffObject dio) {

      Iterator<String> lIter = dio.l.iterator();
      Iterator<String> rIter = dio.r.iterator();

      while(lIter.hasNext() && rIter.hasNext()) {
        if(!lIter.next().trim().equals("Stats Work")) {
          return false;
        }
        if(!rIter.next().trim().equals("Stats-Aggr Operator")) {
          return false;
        }
        if(!lIter.hasNext()) {
          return false;
        }
        if(!lIter.next().trim().equals("Basic Stats Work:")) {
          return false;
        }
      }
      return !lIter.hasNext() && !rIter.hasNext();
    }
  }

  public static class EmptyLineRemovalClassifier implements Classifier {
    @Override
    public String getName() {
      return "empty";
    }

    @Override
    public boolean accept(DiffObject dio) {
      if (dio.l.size() != 0) {
        return false;
      }
      for (String l : dio.r) {
        if (!l.trim().isEmpty()) {
          return false;
        }
      }
      return true;
    }

  }

  public static class StatsDisappearClassifier implements Classifier {

    private String c = "";

    @Override
    public String getName() {
      return "StatsDisappear" + c;
    }

    @Override
    public boolean accept(DiffObject dio) {
      //      if (dio.getL().size() != 0) {
      //        return false;
      //      }
      Predicate<String> p = Pattern.compile("^\\s*(numRows|rawDataSize)\\s+\\d+\\s*$").asPredicate();
      Iterator<String> itL = dio.getL().iterator();
      for (String r : dio.getR()) {
        if (p.test(r)) {
          continue;
        }
        if (itL.hasNext()) {
          c = "Est";
          String l = itL.next();
          //          Statistics: Num rows: 26 Data size: 2750 Basic stats: COMPLETE Column stats: NONE
          String l1 = l.replaceAll("Num rows:\\s+\\d+", "roes: __ROWS__");
          String r1 = r.replaceAll("Num rows:\\s+\\d+", "roes: __ROWS__");
          if (l1.equals(r1)) {
            continue;
          }
        }

        return false;
      }
      return true;
    }
  }

  public static class ZeroStatsDisappearClassifier implements Classifier {

    private String c = "";

    @Override
    public String getName() {
      return "zStats" + c;
    }

    @Override
    public boolean accept(DiffObject dio) {
      //      if (dio.getL().size() != 0) {
      //        return false;
      //      }
      Predicate<String> p = Pattern.compile("^\\s*(numRows|rawDataSize)\\s+0\\s*$").asPredicate();
      Iterator<String> itL = dio.getL().iterator();
      for (String r : dio.getR()) {
        if (p.test(r)) {
          continue;
        }
        if (itL.hasNext()) {
          c = "Est";
          String l = itL.next();
          //          Statistics: Num rows: 26 Data size: 2750 Basic stats: COMPLETE Column stats: NONE
          String l1 = l.replaceAll("Num rows:\\s+\\d+", "roes: __ROWS__");
          String r1 = r.replaceAll("Num rows:\\s+\\d+", "roes: __ROWS__");
          if (l1.equals(r1)) {
            continue;
          } else {
            int asd = 1;
          }
        }
        return false;
      }
      return true;
    }
  }

  public static class PostHookChangeClassifier implements Classifier {

    @Override
    public String getName() {
      return "postHook";
    }

    @Override
    public boolean accept(DiffObject dio) {

      for (List<String> input : Lists.newArrayList(dio.l,dio.r)) {
        Iterator<String> lIter=input.iterator();
        while(lIter.hasNext()) {
          String line = lIter.next();
          if(line.trim().startsWith("POSTHOOK:") || line.trim().startsWith("PREHOOK:")) {
            continue;
          }
          return false;
        }
      }
      return true;
    }
  }

  public static class StatsTaskRenameClassifier implements Classifier {

    @Override
    public String getName() {
      return "taskName";
    }

    @Override
    public boolean accept(DiffObject dio) {
      for (List<String> input : Lists.newArrayList(dio.l,dio.r)) {
        Iterator<String> lIter=input.iterator();
        while(lIter.hasNext()) {
          String line = lIter.next();
          if (!line.matches("\\s*RUN:\\s+Stage-\\d+:(COLUMN)?STATS\\s*")) {
            return false;
          }

        }
      }
      return true;
    }
  }

  //  > RUN: Stage-3:COLUMNSTATS
  //  < RUN: Stage-3:STATS

  public DiffClassificator() {
    classifiers.add(new StatsOnlyChangeClassifier());
    classifiers.add(new StatTaskOnlyChangeClassifier());
    classifiers.add(new PostHookChangeClassifier());
    classifiers.add(new EmptyLineRemovalClassifier());
    classifiers.add(new ZeroStatsDisappearClassifier());
    classifiers.add(new StatsDisappearClassifier());
    classifiers.add(new StatsTaskRenameClassifier());
  }



  public static class DiffObject {

    private List<String> l = new ArrayList<>();
    private List<String> r = new ArrayList<>();

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
    List<String> getL() {
      return l;
    }

    @Deprecated
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
