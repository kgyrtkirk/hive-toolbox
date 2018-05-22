/*
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

package hu.rxd.toolbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;

import com.google.common.collect.Lists;

import hu.rxd.toolbox.jira.HiveTicket;

public class ReviewBoarder {


  public static void main(String[] args) throws Exception {
    new ReviewBoarder(new File("/home/kirk/projects/hive/ws/"));
  }

  public ReviewBoarder(File dir) throws Exception {
    Git repo = Git.open(dir);

    String branchName = getBranchName(repo);
    repo.getRepository().getConfig().getString("extra", "rbt", "x");
    System.out.println(branchName);
    String ticketId = extractTicket(branchName);
    HiveTicket hiveTicket = new HiveTicket(ticketId);
    System.out.println(hiveTicket.getIssue().getSummary());

    String reviewRequestId = repo.getRepository().getConfig().getString("extra", ticketId, "reviewRequestId");

    List<String >cmd =new ArrayList<>();
    cmd.addAll(Lists.newArrayList("rbt", "post", "-g", "no"));
    cmd.addAll(Lists.newArrayList("-d"));
    if (reviewRequestId != null) {
      cmd.addAll(Lists.newArrayList("-r", reviewRequestId));
    }
    cmd.addAll(Lists.newArrayList("--parent", "asf/master"));
    cmd.addAll(Lists.newArrayList("--bugs-closed", ticketId));
    cmd.addAll(Lists.newArrayList("--summary", hiveTicket.toString()));
    cmd.addAll(Lists.newArrayList("--target-people", "ashutoshc"));

    System.out.println("inoking:" + cmd);
    //    repo.getRepository().getConfig().setString("extra", null, "reviewRequestId0", "2");
    //  repo.getRepository().getConfig().save();
    //    System.out.println(reviewRequestId);
    Process p = new ProcessBuilder(cmd).directory(dir).inheritIO().start();
    p.waitFor();
    int exitValue = p.exitValue();
    if (exitValue != 0) {
      throw new RuntimeException("exitValue:" + exitValue);
    }
  }

  private String extractTicket(String branchName) {
    Pattern p = Pattern.compile("^HIVE+-[0-9]+");
    Matcher m = p.matcher(branchName);
    if (!m.find()) {
      throw new RuntimeException();
    }
    return m.group(0);
  }

  private String getBranchName(Git repo) throws Exception {
    ObjectId head = repo.getRepository().resolve(Constants.HEAD);
    Map<ObjectId, String> res = repo.nameRev().add(head).call();
    if (res.size() != 1) {
      throw new RuntimeException();
    }
    return res.values().iterator().next();
  }

}
