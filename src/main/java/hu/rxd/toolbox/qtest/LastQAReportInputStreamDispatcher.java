package hu.rxd.toolbox.qtest;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.function.Function;

import hu.rxd.toolbox.jira.HiveTicket;
import hu.rxd.toolbox.qtest.diff.CachedURL;

public class LastQAReportInputStreamDispatcher implements IInputStreamDispatcher {

  private HiveTicket t;
  private URI qaLogs;

  public LastQAReportInputStreamDispatcher(String string) throws Exception {
    t = new HiveTicket(string);
    qaLogs = t.getLastQATestLogsURI();
  }

  @Override
  public void visit(Function<InputStream, Void> function) throws Exception {
    URL url = null;
    try {
      url = new CachedURL(qaLogs.toURL()).getURL();
    } catch (FileNotFoundException fe) {
      System.out.println("not found at QA");
      String p = qaLogs.getPath();
      String id = p.replaceAll("[^0-9]", "");
//      URI u2 = new URI(String.format("http://localhost:8080/ptest-results/test-results.%s.tar.gz", id));
      URI u2 = new URI(String.format("http://demeter/ptest-results/test-results.%s.tar.gz", id));
      url = new CachedURL(u2.toURL()).getURL();
    }
    new TarGzXL(url).visit(function);
  }

}
