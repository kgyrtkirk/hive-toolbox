import java.io.InputStream;
import java.net.URI;
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
    new TarGzXL(new CachedURL(qaLogs.toURL()).getURL()).visit(function);
  }

}
