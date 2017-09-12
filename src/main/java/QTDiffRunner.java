import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import com.google.common.io.ByteStreams;

import hu.rxd.model.junit.JunitReader;
import hu.rxd.model.junit.JunitReport;
import hu.rxd.model.junit.JunitReport.TestCase;
import hu.rxd.toolbox.qtest.diff.DiffClassificator;

public class QTDiffRunner {

  private static PrintStream output;
  static int idx = 0;

  public static void main(String[] args) throws FileNotFoundException, Exception {
    try (PrintStream output0 = new PrintStream("/tmp/_qd")) {
      output = output0;
      ByteStreams.copy(QTestDiffExtractor.class.getResourceAsStream("/qdr.bash"), output);
      for (String string : args) {
        try {
          File f = new File(string);
          JunitReport jr = JunitReader.parse(new FileInputStream(f));
          processTestCases(jr.testcase);
        } catch (Exception e) {
          throw new RuntimeException("Error processing file: " + string, e);
        }
      }
    }
  }

  private static void processTestCases(List<TestCase> testcase) throws Exception {
    DiffClassificator diffClassificator = new DiffClassificator();
 
    for (TestCase tc : testcase) {
      if (tc.failure==null )
        continue;
      if (tc.systemOut == null) {
        continue;
      }
      if(tc.failure.message.startsWith("Client Execution succeeded but contained differences") || tc.failure.message.contains("QTestUtil.failedDiff(")){
        try {
          QTestDiffExtractor qde = new QTestDiffExtractor(tc.systemOut);
          File file = new File("/tmp/__qde" + (idx++));
          try (PrintStream patchFile = new PrintStream(file)) {
            qde.writePatch(patchFile);
          }
          String category=diffClassificator.classify(qde.getDiffIterable());
          output.printf("process \"%s\" \"%s\" \"%s\"\n", category, qde.getQFile(), file.getAbsolutePath());
        } catch (Exception e) {
          throw new RuntimeException("Error processing testcase", e);
        }
    }}
  }

}
