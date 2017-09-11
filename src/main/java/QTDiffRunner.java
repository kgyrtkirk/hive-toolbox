import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import com.google.common.io.ByteStreams;

import hu.rxd.model.junit.JunitReader;
import hu.rxd.model.junit.JunitReport;
import hu.rxd.model.junit.JunitReport.TestCase;

public class QTDiffRunner {

  private static PrintStream output;
  static int idx = 0;

  public static void main(String[] args) throws FileNotFoundException, Exception {
    try (PrintStream output0 = new PrintStream("/tmp/_qd")) {
      output = output0;
      ByteStreams.copy(QTestDiffExtractor.class.getResourceAsStream("/qdr.bash"), output);
      for (String string : args) {
        File f = new File(string);
        JunitReport jr = JunitReader.parse(new FileInputStream(f));
        processTestCases(jr.testcase);
      }
    }
  }

  private static void processTestCases(List<TestCase> testcase) throws Exception {
    for (TestCase tc : testcase) {
      if (tc.systemOut != null) {
        QTestDiffExtractor qde = new QTestDiffExtractor(tc.systemOut);
        File file = new File("/tmp/__qde" + (idx++));
        try (PrintStream patchFile = new PrintStream(file)) {
          qde.writePatch(patchFile);
        }
        output.printf("process \"%s\" \"%s\"\n", qde.getQFile(), file.getAbsolutePath());
      }
    }
  }

}
