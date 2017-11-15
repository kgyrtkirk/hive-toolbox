import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.Test;

import hu.rxd.model.junit.JunitReader;
import hu.rxd.model.junit.JunitReport;
import hu.rxd.model.junit.JunitReport.TestCase;
import hu.rxd.toolbox.qtest.QTestDiffExtractor;

public class ExtractDiffFX1Test {

  private TestCase testCase;

  public ExtractDiffFX1Test() throws Exception {
    InputStream resourceAsStream =
        getClass().getResourceAsStream("/hu/rxd/model/junit/junitWithDiff.xml");
    JunitReport jr = JunitReader.parse(resourceAsStream);

    for (TestCase tc : jr.testcase) {
      if (tc.systemOut != null)
        testCase = tc;
    }
    assertNotNull("failed to parse a failing testcase", testCase);
  }

  @Test
  public void asdf2() throws Exception {
    QTestDiffExtractor qde = new QTestDiffExtractor(testCase.systemOut);
    
  }
}
