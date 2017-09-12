
package hu.rxd.toolbox.qtest.diff;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class DiffClassificatorTest {

  @Test
  public void statsOnly1() throws Exception {
    DiffClassificator dc = new DiffClassificator();
    List<String> lines = readLines(getClass().getResourceAsStream("statsOnly1"));
    String cat = dc.classify(lines);
    assertEquals("statsOnly", cat);
  }
  @Test
  public void statsOnly2() throws Exception {
    DiffClassificator dc = new DiffClassificator();
    List<String> lines = readLines(getClass().getResourceAsStream("statsOnly2"));
    String cat = dc.classify(lines);
    assertEquals("statsOnly", cat);
  }
  @Test
  public void statsOnly3() throws Exception {
    DiffClassificator dc = new DiffClassificator();
    List<String> lines = readLines(getClass().getResourceAsStream("statsOnly3"));
    String cat = dc.classify(lines);
    assertEquals("statsOnly", cat);
  }
  @Test
  public void statsOnly4() throws Exception {
    DiffClassificator dc = new DiffClassificator();
    List<String> lines = readLines(getClass().getResourceAsStream("statsOnly4"));
    String cat = dc.classify(lines);
    assertEquals("statsOnly", cat);
  }
  @Test
  public void statsOnly5() throws Exception {
    DiffClassificator dc = new DiffClassificator();
    List<String> lines = readLines(getClass().getResourceAsStream("statsOnly5"));
    String cat = dc.classify(lines);
    assertEquals("statsOnly", cat);
  }
  @Test
  public void statsOnlyStaege() throws Exception {
    DiffClassificator dc = new DiffClassificator();
    List<String> lines = readLines(getClass().getResourceAsStream("statsOnly_stages"));
    String cat = dc.classify(lines);
    assertEquals("statsOnly", cat);
  }
  @Test
  public void statsOnlyOrder() throws Exception {
    DiffClassificator dc = new DiffClassificator();
    List<String> lines = readLines(getClass().getResourceAsStream("statsOnly_order"));
    String cat = dc.classify(lines);
    assertEquals("statsOnly", cat);
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
