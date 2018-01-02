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

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;

import hu.rxd.toolbox.jira.HiveTicket;
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
  public void as2() throws Exception {
    //    HiveTicket t = new HiveTicket("HIVE-16827");
    //    Attachment u = t.getLastAttachment();
    //    Issue ii = HiveTicket.jira.getIssue("SLING-2720");
    Issue ii = HiveTicket.jira.getIssue("HIVE-13567");
    System.out.println(ii.getAttachments().size());

    //    Comment lastQAComment = t.getLastQAComment();
    //    String b = lastQAComment.getBody();
    //    System.out.println(b);

  }

}
