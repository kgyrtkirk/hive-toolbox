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

package hu.rxd.toolbox.jira;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.utils.Lists;
import net.rcarz.jiraclient.Attachment;
import net.rcarz.jiraclient.Comment;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.Issue.SearchResult;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

public class HiveTicket {

  private static final String HIVEQA = "hiveqa";
  public static JiraClient jira = new JiraClient("https://issues.apache.org/jira");
  private Issue i;

  public HiveTicket(Issue i0) throws Exception {
    //    i = i0;
    this(i0.getKey());
  }

  public HiveTicket(String ticketId) throws Exception {
    //    i = jira.getIssue(ticketId);
    i = jira.getIssue(ticketId, "*all", "schema,editmeta");
  }

  //  @Test
  //  public void u1() throws Exception {
  //    System.out.println(i);
  //    List<Comment> comments = i.getComments();
  //    Comment c0 = comments.get(comments.size() - 1);
  //    System.out.println(c0.getBody());
  //
  //  }

  public List<Comment> getCommentsByName(String name) {
    List<Comment> ret = Lists.newArrayList();
    for (Comment comment : i.getComments()) {
      if (name.equals(comment.getAuthor().getName())) {
        ret.add(comment);
      }
    }
    return ret;
  }

  public List<Comment> getCommentsContaining(String substr) {
    List<Comment> ret = Lists.newArrayList();
    for (Comment comment : i.getComments()) {
      if (comment.getBody().contains(substr)) {
        ret.add(comment);
      }
    }
    return ret;
  }

  public List<Comment> getReviewComments() {
    List<Comment> comments = getCommentsContaining("+1");
    for (Iterator<Comment> iterator = comments.iterator(); iterator.hasNext();) {
      Comment comment = iterator.next();
      if (comment.getAuthor().getName().equals(HIVEQA)) {
        iterator.remove();
      }
    }
    return comments;
  }

  public Comment getLastQAComment() {
    List<Comment> comments = getCommentsByName(HIVEQA);
    if (comments.size() == 0) {
      throw new RuntimeException("theres no last qa comment");
    }
    return comments.get(comments.size() - 1);
  }

  public Attachment getLastAttachment() throws JiraException {
    Attachment ret = null;
    //    i.refresh("*all");
    for (Attachment a : i.getAttachments()) {
      if (ret == null || ret.getCreatedDate().before(a.getCreatedDate())) {
        ret = a;
      }
    }
    if (ret == null) {
      throw new RuntimeException("theres no last attachment!");
    }
    return ret;
  }

  public URI getLastQATestLogsURI() throws Exception {
    Comment lastQAComment = getLastQAComment();

    String b = lastQAComment.getBody();
    Pattern p = Pattern.compile("/.*Test logs: ([^ ]+).*/", Pattern.MULTILINE | Pattern.DOTALL);
    Matcher m = p.matcher(b);
    if (m.find()) {
      return new URI(m.group(1) + "/test-results.tar.gz");
    }
    throw new RuntimeException("can't extract qa uri from input");
  }

  public static List<HiveTicket> getMatchingTickets(String jqlSearchStr) throws Exception {
    List<HiveTicket> ret=new ArrayList<>();
    SearchResult issues = jira.searchIssues(jqlSearchStr);
    for (Issue i0 : issues.issues) {
      ret.add(new HiveTicket(i0));
    }
    return ret;
  }

  @Override
  public String toString() {
    return String.format("%s: %s", i.getKey(), i.getSummary());
  }

  public Issue getIssue() {
    return i;
  }

  public boolean canBeSubmitted() {
    // FIXME: needs check for latest QA run
    return (getReviewComments().size() > 0);
  }

}
