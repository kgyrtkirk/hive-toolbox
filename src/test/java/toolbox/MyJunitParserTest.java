package toolbox;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.Test;

import toolbox.JunitReport.TestCase;

public class MyJunitParserTest {

  @Test
  public void asdf() throws Exception {
    InputStream resourceAsStream = getClass().getResourceAsStream("/junitEmpty.xml");
    JunitReport jr=JunitReader.parse(resourceAsStream);
    assertNotNull(jr);
    System.out.println(jr.properties.size());
    System.out.println(jr.testcase.size());
  }
  @Test
  public void asdf2() throws Exception {
    InputStream resourceAsStream = getClass().getResourceAsStream("/junitWithDiff.xml");
    JunitReport jr=JunitReader.parse(resourceAsStream);
    System.out.println(jr.properties.size());
    System.out.println(jr.testcase.size());
    for (TestCase tc : jr.testcase) {
      System.out.println(tc.classname);
      System.out.println(tc.systemOut);
    }
    assertNotNull(jr);
  }
}
