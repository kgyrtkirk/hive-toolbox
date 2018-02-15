/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.rxd.model.jenkins;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;

import hu.rxd.toolbox.jira.ToolboxSettings;

public class HiveJenkinsX {

  public static void main(String[] args) throws IOException {
    //    add("17934");
    add("XY-18448");
  }


  public static void add0(String string) throws IOException {
    ToolboxSettings ts = ToolboxSettings.instance();
    JenkinsServer js = new JenkinsServer(URI.create("https://builds.apache.org/"), ts.getJenkinsUser(), ts.getJenkinsPass());
    //    JenkinsServer js = new JenkinsServer(URI.create("https://builds.apache.org/"));

    JobWithDetails j = js.getJob("PreCommit-HIVE-Build");
    Map map = new HashMap<>();
    map.put("ISSUE_NUM", string);

    j.build(map, true);
  }

  static class JenkinsHandler {
    private JenkinsHttpClient jc;
    private JenkinsServer js;
    private URI serverUri;

    public JenkinsHandler(URI serverUri, String user, String pass) {
      this.serverUri = serverUri;
      jc = new JenkinsHttpClient(serverUri, user, pass);
      js = new JenkinsServer(jc);
    }

    public Job getJob(String string) {
      Job j0 = new Job(string, serverUri.toString() + "/job/" + string + "/");
      j0.setClient(jc);
      return j0;
    }
  }

  public static void add(String string) throws IOException {
    URI serverUri = URI.create("https://localhost1.apache.org:8443/");
    //    ToolboxSettings ts = ToolboxSettings.instance();
    //    JenkinsServer js = new JenkinsServer(URI.create("https://builds.apache.org/"), ts.getJenkinsUser(), ts.getJenkinsPass());
    //    JenkinsServer js = new JenkinsServer(URI.create("https://builds.apache.org/"));
    JenkinsHandler jh = new JenkinsHandler(serverUri, "xxxkgyrtkirk", "__fix_the_token__");
    Job j0 = jh.getJob("PreCommit-HIVE-Build");

    long t0 = System.currentTimeMillis();
    //    JobWithDetails j = js.getJob("hive-check");
    //    Job j0 = new Job("hive-check", "http://sust-j3.duckdns.org:8080/job/hive-check/");

    //    j0.build();
    //    JobWithDetails j2 = j0.details();
    Map map = new HashMap<>();
    map.put("REVISION", string);
    map.put("ISSUE_NUM", string);


    j0.build(map, true);
    long t1 = System.currentTimeMillis();
    System.out.println(t1 - t0);
  }

}
