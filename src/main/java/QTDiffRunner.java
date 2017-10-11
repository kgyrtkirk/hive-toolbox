import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.common.base.Function;
import com.google.common.io.ByteStreams;

import hu.rxd.model.junit.JunitReader;
import hu.rxd.model.junit.JunitReport;
import hu.rxd.model.junit.JunitReport.TestCase;
import hu.rxd.toolbox.qtest.diff.DiffClassificator;

public class QTDiffRunner {

  private static PrintStream output;
  static int idx = 0;
  static Map<String, Integer> catCnt = new HashMap<String, Integer>();

  public static void main(String[] args) throws FileNotFoundException, Exception {
    try (PrintStream output0 = new PrintStream("/tmp/_qd")) {
      output = output0;
      ByteStreams.copy(QTestDiffExtractor.class.getResourceAsStream("/qdr.bash"), output);

      new FileInputStreamDispatcher(args).visit(new Function<InputStream, Void>() {

        @Override
        public Void apply(InputStream a) {
          JunitReport jr;
          try {
            jr = JunitReader.parse(a);
            processTestCases(jr.testcase);
            return null;
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }

      });

      for (Entry<String, Integer> string : catCnt.entrySet()) {
        System.out.println(string.getKey() + ": " + string.getValue());

      }
      System.out.println();
    }
  }

  private static void processTestCases(List<TestCase> testcase) throws Exception {
    DiffClassificator diffClassificator = new DiffClassificator();

    for (TestCase tc : testcase) {
      if (tc.failure == null) {
        continue;
      }
      if (tc.systemOut == null) {
        continue;
      }
      if (tc.failure.message.startsWith("Client Execution succeeded but contained differences") || tc.failure.message.contains("QTestUtil.failedDiff(")
          || tc.failure.message.startsWith("Client result comparison failed with error code")) {
        try {
          QTestDiffExtractor qde = new QTestDiffExtractor(tc.systemOut);
          File file = new File("/tmp/__qde" + (idx++));
          try (PrintStream patchFile = new PrintStream(file)) {
            qde.writePatch(patchFile);
          }
          String category = diffClassificator.classify(qde.getDiffIterable());
          catCnt.put(category, catCnt.getOrDefault(category, 0) + 1);
          if (qde.canPatch()) {
            output.printf("process \"%s\" \"%s\" \"%s\" \"%s\"\n", category, qde.getQFile(), qde.isReverse() ? "-R" : "", file.getAbsolutePath());
          } else {
            output.printf("rerun '%s#%s'\n", tc.classname.replaceAll(".*\\.", ""), tc.name);
          }
        } catch (Exception e) {
          throw new RuntimeException("Error processing testcase", e);
        }
      }
    }
  }

}
