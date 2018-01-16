package hu.rxd.model.jenkins;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.QueueItem;

import hu.rxd.toolbox.jira.ToolboxSettings;

public class MXJenkinQueuePTest {
  @Test
  public void tx19() throws Exception {
    // InputStream is = getClass().getResourceAsStream("queue.json");
    // assertNotNull(is);
    // ObjectMapper om = new ObjectMapper();
    //
    // JenkinsQueue jq = om.readValue(is, JenkinsQueue.class);
  }

  //  @Test
  public void asf() throws Exception {
    ToolboxSettings.instance();

    JenkinsServer js = new JenkinsServer(URI.create("https://builds.apache.org/"));
    List<QueueItem> items = js.getQueue().getItems();

    List<QueueItem> hiveJobs = filterHive(items);

    ImmutableMultimap.Builder<QueueItem, String> im = new ImmutableMultimap.Builder<>();
    for (QueueItem queueItem : hiveJobs) {

      System.out.println(queueItem);
      System.out.println(queueItem.getParams());
      Map<String, String> m = toMap(queueItem.getParams());

      im.put(queueItem, m.get("ISSUE_NUM"));

    }
    ImmutableMultimap<String, QueueItem> m1 = im.build().inverse();
    for (String e : m1.keySet()) {
      ImmutableCollection<QueueItem> vals = m1.get(e);
      System.out.printf("HIVE-%s  %d\n",e,vals.size());
    }

  }

  // @Test
  public void tm2() {
    Map<String, String> m = toMap("\nISSUE_NUM=17530\nATTACHMENT_ID=12887013");

    ImmutableMultimap.Builder<Map, String> im = new ImmutableMultimap.Builder<>();
    im.put(m, m.get("ISSUE_NUM"));
    im.put(m, m.get("ISSUE_NUM"));
    System.out.println(im.build().inverse());
  }

  @Test
  public void tm1() {
    Map<String, String> m = toMap("\nISSUE_NUM=17530\nATTACHMENT_ID=12887013");
    System.out.println(m);
  }

  private Map<String, String> toMap(String params) {
    Map<String, String> ret = new HashMap<>();
    for (String part : params.split("\n")) {
      if (part.length() == 0) {
        continue;
      }
      String[] p = part.split("=");
      if (p.length != 2) {
        throw new RuntimeException();
      }
      ret.put(p[0], p[1]);
    }
    return ret;

  }

  private List<QueueItem> filterHive(List<QueueItem> items) {
    List<QueueItem> ret = new ArrayList<>();
    for (QueueItem queueItem : items) {
      if (queueItem != null && queueItem.getTask() != null && queueItem.getTask().getName() != null) {
        if (queueItem.getTask().getName().equals("PreCommit-HIVE-Build")) {
          ret.add(queueItem);
        }
      }
    }
    return ret;
  }
}
