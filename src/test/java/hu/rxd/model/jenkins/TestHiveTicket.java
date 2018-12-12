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

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import org.junit.Ignore;
import org.junit.Test;

import hu.rxd.toolbox.jira.HiveTicket;
import hu.rxd.toolbox.jira.ToolboxSettings;
import hu.rxd.toolbox.qtest.diff.CachedURL;
import net.rcarz.jiraclient.Attachment;
import net.rcarz.jiraclient.Issue;

public class TestHiveTicket {

  @Test
  @Ignore
  public void as() throws Exception {
    HiveTicket t = new HiveTicket("HIVE-16827");
    URI u = t.getLastQATestLogsURI();
    System.out.println(u);

    //    Comment lastQAComment = t.getLastQAComment();
    //    String b = lastQAComment.getBody();
    //    System.out.println(b);

  }

  @Test
  @Ignore
  public void as2() throws Exception {
    Issue ii = HiveTicket.jira.getIssue("HIVE-13567");
    System.out.println(ii.getAttachments().size());

    //    Comment lastQAComment = t.getLastQAComment();
    //    String b = lastQAComment.getBody();
    //    System.out.println(b);

  }

  @Test
  @Ignore
  public void reattach() throws Exception {
    HiveTicket.jiraLogin(
        ToolboxSettings.instance().getJiraUserId(),
        ToolboxSettings.instance().getJiraPassword());
    HiveTicket t = new HiveTicket("HIVE-15078");
    Attachment attachment = t.getLastAttachment();
    URL patchURL = new CachedURL(new URL(attachment.getContentUrl())).getURL();

    File patchFile = new File(attachment.getFileName());
    FileUtils.copyURLToFile(patchURL, patchFile);

    //    System.out.println(t);

    t.getIssue().addAttachment(patchFile);

    //    Object a = t.getIssue().getField(Field.ASSIGNEE);
    //    System.out.println(a);
    //    t.getIssue().update().field(Field.ASSIGNEE, "kgyrtkirk").execute();

  }

}
