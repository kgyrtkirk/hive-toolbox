import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class DiffClassificatorTest {

  @Test
  public void asd() throws Exception {
    DiffClassificator dc = new DiffClassificator();

    List<String> lines = readLines(getClass().getResourceAsStream("/__qde9"));
    String cat = dc.classify(lines);
    assertEquals("stats", cat);
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
