package hu.rxd.model.junit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import com.offbytwo.jenkins.model.QueueItem;

public class JenkinsQueue {

  public String _class;
  public List<String> discoverableItems;
  public List<QueueItem> items;

}
