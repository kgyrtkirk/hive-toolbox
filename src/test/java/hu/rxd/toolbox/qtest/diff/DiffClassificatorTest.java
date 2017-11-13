
package hu.rxd.toolbox.qtest.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)

public class DiffClassificatorTest {

  private String fileName;
  private String expected;

  @Parameters(name = "{0}")
  public static List<Object[]> getParameters() {
    List<Object[]> ret = new ArrayList<>();
    ret.add(new Object[] { "statsOnly1", "statsOnly" });
    ret.add(new Object[] { "statsOnly2", "statsOnly" });
    ret.add(new Object[] { "statsOnly3", "statsOnly" });
    ret.add(new Object[] { "statsOnly4", "statsOnly" });
    ret.add(new Object[] { "statsOnly5", "statsOnly" });
    ret.add(new Object[] { "statsOnly_stages", "statsOnly" });
    ret.add(new Object[] { "statsOnly_stages2", "statsOnly" });
    ret.add(new Object[] { "statsOnly_order", "statsOnly" });
    ret.add(new Object[] { "statTaskOnly_1", "statTaskOnly" });
    ret.add(new Object[] { "empty_1", "empty" });
    ret.add(new Object[] { "zStats_1", "zStats" });
    ret.add(new Object[] { "zStatsEst_1", "zStatsEst" });
    //    ret.add(new Object[] { "zStatsEst_2", "zStatsEst" });
    ret.add(new Object[] { "taskName_1", "taskName" });
    //    ret.add(new Object[] { "statsPC_1", "statsPC" });
    return ret;
  }

  public DiffClassificatorTest(String fileName, String expected) {
    this.fileName = fileName;
    this.expected = expected;

  }

  @Test
  public void test() throws Exception {
    DiffClassificator dc = new DiffClassificator();
    List<String> lines = readLines(getClass().getResourceAsStream(fileName));
    String cat = dc.classify(lines);
    assertEquals(expected, cat);
  }

  private List<String> readLines(InputStream is) throws IOException {

    assertNotNull("invalid input", is);
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
      String line;
      List<String> ret = new LinkedList<>();

      while ((line = br.readLine()) != null) {
        ret.add(line);
      }
      return ret;

    }
    // throw new RuntimeException("fail!");
  }
}
