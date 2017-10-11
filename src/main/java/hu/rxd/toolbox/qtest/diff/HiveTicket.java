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

package hu.rxd.toolbox.qtest.diff;

import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.junit.Test;

import net.rcarz.jiraclient.Comment;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;

public class HiveTicket {

  static JiraClient jira = new JiraClient("https://issues.apache.org/jira");
  private Issue i;

  public HiveTicket(String ticketId) throws Exception {
    i = jira.getIssue("HIVE-16827");
  }

  @Test
  public void u1() throws Exception {
    System.out.println(i);
    List<Comment> comments = i.getComments();
    Comment c0 = comments.get(comments.size() - 1);
    System.out.println(c0.getBody());

  }

  public List<Comment> getCommentsByName(String name) {
    List<Comment> ret = Lists.newArrayList();
    for (Comment comment : i.getComments()) {
      if (name.equals(comment.getAuthor().getName())) {
        ret.add(comment);
      }
    }
    return ret;
  }

  public Comment getLastQAComment() {
    List<Comment> comments = getCommentsByName("hiveqa");
    if (comments.size() == 0) {
      throw new RuntimeException("theres no last qa comment");
    }
    return comments.get(comments.size() - 1);
  }

}
