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

package hu.rxd.toolbox;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import hu.rxd.model.jenkins.HiveJenkinsX;
import hu.rxd.toolbox.jira.HiveTicket;
import net.rcarz.jiraclient.Attachment;
import net.rcarz.jiraclient.Comment;
import net.rcarz.jiraclient.JiraException;

public class JenkinBumper {

  Logger LOG = LoggerFactory.getLogger(JenkinBumper.class);

  public static void main(String[] args) throws Exception {
    new JenkinBumper().bump();
  }

  public void bump() throws Exception {
    LOG.info("downloading tickets ");
    // last week; but not in last hour - to avoid interference
    List<HiveTicket> candidates =
        HiveTicket.getMatchingTickets("project = HIVE and assignee = kgyrtkirk AND status = 'Patch Available' and updatedDate > -7d and updatedDate < -1h ORDER BY updatedDate DESC");
    for (HiveTicket t : candidates) {
      LOG.info("processing: " + t);
      if (needsTestRun(t)) {
        submit(t);
        //        LOG.info("check?: ");
      }
    }

  }

  private void submit(HiveTicket t) throws Exception {
    String id = t.getIssue().getKey();
    LOG.info("submit:" + id);
    String jiraNum = id.split("-")[1];
    //    "ISSUE_NUM"

    //    HiveJenkinsX.add(jiraNum);

  }

  private boolean needsTestRun(HiveTicket t) throws JiraException {
    Attachment lastA = null;
    Comment lastQAComment = null;
    try {
      lastA = t.getLastAttachment();
    } catch (RuntimeException e) {
      return false;
    }
    try {
      lastQAComment = t.getLastQAComment();
    } catch (RuntimeException e) {
      return true;
    }
    Date qaDate = lastQAComment.getCreatedDate();
    Date atDate = lastA.getCreatedDate();
    return (qaDate.before(atDate));
  }

}
