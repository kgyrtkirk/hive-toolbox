import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class Banya {

  static class Q {

    private Date ed;
    private Date st;
    private Date last;
    private String queryID;
    private String threadId;
    private QTypes type;
    private Engines engine;

    public Q(String queryID) {
      this.queryID = queryID;
      type = QTypes.UNK;
      engine = Engines.UNK;
    }

    public void setStart(LogLine decomposeLine) {
      st = decomposeLine.ts;
      last = st;

    }

    public void setEnd(LogLine decomposeLine) {
      ed = decomposeLine.ts;
      last = ed;
    }

    @Override
    public String toString() {
      return type + "@" + engine + " TI:" + threadId + " " + queryID + " "
          + (tfformat(st) + " ~[" + last + "]> " + tfformat(ed));
    }

    private String tfformat(Date ed2) {
      if (ed2 == null) {
        return null;
      }
      SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss,S");
      return tf.format(ed2);
    }

    public void setThreadId(String thread) {
      threadId = thread;
    }

    enum Engines {
      UNK, TEZ
    }
    enum QTypes {
      UNK, META
    }

    public void process(LogLine ll) {
      last = ll.ts;
      boolean endCandidate = ll.line.contains("</PERFLOG method=Driver.run ");
      if (endCandidate && ed == null) {
        type = QTypes.META;
        setEnd(ll);
      }
      if (ll.line.contains("client.TezClient")) {
        engine = Engines.TEZ;
      }

    }

    public void recordMetaTime(String string) {
      long mt = extractMetaTime(string);
    }

    private static long extractMetaTime(String string) {
      Long total = 0l;
      String[] parts = string.split("[=,}]");
      for (String string2 : parts) {
        Long val = Long.valueOf(string2);
        total = val;
      }
      return total;
    }

  }

  static class LogLine {

    private Date ts;
    private String line;
    private String threadName;
//    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S");
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,S");

    public LogLine(String line) throws Exception {
      this.line = line;

//      Pattern pat_line = Pattern.compile("(.{23}) ([^ ]+) \\[([^]]+)\\]: (.*)");
      Pattern pat_line = Pattern.compile("(.{23}) +([^ ]+) +\\[(.+)\\] (.*)");
      Matcher m = pat_line.matcher(line);
      if (!m.matches()) {
        throw new RuntimeException(line);
      }
      ts = format.parse(m.group(1));//line.substring(0, 23)
      threadName = m.group(3);


    }

    @Override
    public String toString() {
      return "ts: " + ts + " ; " + line;
    }

    public static LogLine of(String line) throws Exception {
      return new LogLine(line);

    }

    public String getThread() {
      return threadName;
    }

  }

  static class QP {

    private Pattern pat_accepted;
    private Pattern pat_postHook;
    private Map<String, Q> queryMap = new LinkedHashMap<>();
    private Map<String, Q> qByThread = new LinkedHashMap<>();
    private Pattern pat_exec;
    private Pattern pat_MTime;

    public QP() {
      //      pat_accepted = Pattern.compile(".*Hive query accepted: hive_([^ ]+).*");
      pat_accepted = Pattern.compile(".*ql.Driver: Compiling command\\(queryId=hive_([^ ]+)\\).*");

      pat_exec = Pattern.compile(".*ql.Driver: Executing command\\(queryId=hive_([^ ]+)\\).*");

      pat_postHook = Pattern.compile(".*Received post-hook notification for :hive_([^ ]+).*");

      pat_MTime = Pattern.compile(".*metadata.Hive: Total time spent in each metastore function (ms):(.*)");
    }

    public void process(String line) throws Exception {
      Matcher m_acc = pat_accepted.matcher(line);
      String queryID;
      if (m_acc.matches()) {
        LogLine decomposeLine = decomposeLine(line);
        queryID = m_acc.group(1);
        Q q = getQ(queryID);
        q.setThreadId(decomposeLine.getThread());
        q.setStart(decomposeLine);
        qByThread.put(decomposeLine.getThread(), q);
        System.out.println(m_acc.group(1) + "=1>" + q.threadId);
      }
      Matcher m_exec = pat_exec.matcher(line);
      if (m_exec.matches()) {
        LogLine decomposeLine = decomposeLine(line);
        queryID = m_exec.group(1);
        Q q = getQ(queryID);
        qByThread.remove(q.threadId);
        qByThread.put(decomposeLine.getThread(), q);
        System.out.println(m_exec.group(1) + "=2>" + q.threadId);
      }
      Matcher m_pos = pat_postHook.matcher(line);
      if (m_pos.matches()) {
        LogLine decomposeLine = decomposeLine(line);
        queryID = m_pos.group(1);
        getQ(queryID).setEnd(decomposeLine);
      }

      if (line.startsWith("201")) {
        LogLine ll = LogLine.of(line);
        String t = ll.getThread();

        Q q = qByThread.get(t);
        if (q != null) {
          Matcher pm = pat_MTime.matcher(line);
          if (pm.matches()) {
            q.recordMetaTime(pm.group(1));
          }

          q.process(ll);
        }
      }

      //      if(line.matches(regex))
      //      accepted = "Hive query accepted";
      //      post = "Received post-hook notification for";

    }

    private long extractMetaTime(String group) {
      throw new RuntimeException();
      // TODO Auto-generated method stub
      // return null;
    }

    // FIXME: LL.of
    @Deprecated
    private LogLine decomposeLine(String line) throws Exception {
      return LogLine.of(line);
    }

    private Q getQ(String queryID) {
      Q q = queryMap.get(queryID);
      if (q == null) {
        q = new Q(queryID);
        queryMap.put(queryID, q);
      }
      return q;

    }

  }

  public static void main(String[] args) throws Exception {
    String input = "/mnt/work/hwx/ear/ear-9636/aa/hiveserver2_11829.log";
    String ec = "/mnt/work/hwx/ear/ear-8827/exacrap/";
    input = ec + "hiveserver2Interactive.log.2019-02-05_47";
    input = ec + "hiveserver2Interactive.log.2019-02-05_185";

    LineIterator it = FileUtils.lineIterator(new File(input), "UTF-8");
    try {
      int cnt = 0;
      QP qp = new QP();
      while (it.hasNext()) {
        String line = it.nextLine();
        // do something with line

        qp.process(line);
        //                if (cnt > 3000) {
        //                  break;
        //                }
        cnt++;
      }

      for (Entry<String, Q> string : qp.queryMap.entrySet()) {
        Q v = string.getValue();
        //        if (v.ed == null) {
          System.out.println(v);
        //        }
      }

    } finally {
      it.close();
    }
  }
}
