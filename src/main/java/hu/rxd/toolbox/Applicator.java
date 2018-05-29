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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import hu.rxd.toolbox.jira.HiveTicket;
import hu.rxd.toolbox.jira.ToolboxSettings;
import hu.rxd.toolbox.qtest.diff.CachedURL;
import net.rcarz.jiraclient.Attachment;
import net.rcarz.jiraclient.Comment;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.User;

public class Applicator {

  Logger LOG = LoggerFactory.getLogger(Applicator.class);

  // these are fields?
  private HiveTicket ticket;
  private Attachment attachment;

  private File patchFile;

  private String message;

  public Applicator(HiveTicket hiveTicket) throws Exception {
    ticket = hiveTicket;
    attachment = ticket.getLastAttachment();
    URL patchURL = new CachedURL(new URL(attachment.getContentUrl())).getURL();
    patchFile = new File(attachment.getFileName());
    //    patchFile = new File("/tmp/" + attachment.getFileName());
    FileUtils.copyURLToFile(patchURL, patchFile);

    message = buildMessage();

    LOG.info("message: {}", message);
    //    Git git = Git.open(new File("."));
    //    git.status().call();
    //    authorIdent=
    String authorIdent = getAuthorIdent();
    System.out.printf("git apply -p0 -3 %s\n", patchFile.getName());
    System.out.printf("git commit --author '%s' -m '%s' --signoff\n", authorIdent, message);
  }

  private String getAuthorIdent() {
    //    "${assignee.displayName} <${assignee.emailAddress.replaceAll(' at ','@').replaceAll(' dot ','.')}>"
    User author = ticket.getIssue().getAssignee();
    String name = author.getDisplayName();
    String email = author.getEmail().replaceAll(" at ", "@").replaceAll(" dot ", ".");
    return String.format("%s <%s>", name, email);

  }

  private String buildMessage() {
    Issue issue = ticket.getIssue();

    String people = issue.getAssignee().getDisplayName() + " ";
    List<String> reviewPeople = new ArrayList<>();
    for (Comment c : ticket.getReviewComments()) {
      reviewPeople.add(c.getAuthor().getDisplayName());
    }
    LOG.info("assignee: {}", issue.getAssignee().getName());
    if (ToolboxSettings.instance().getJiraUserId().equals(issue.getAssignee().getName())) {
      people += "reviewed by ";
    } else {
      people += "via ";
    }
    people += Joiner.on(", ").join(reviewPeople);

    return String.format("%s: %s (%s)", issue.getKey(), issue.getSummary().trim(), people);
    //    twitter.get( path: 'search', query: ['jql':"key=${jiraKey}"] ) { resp, reader ->
    //    def issue=reader.issues[0]
    //    def key=issue.key
    //    def summary= issue.fields.summary
    //    def assignee=reader.issues[0].fields.assignee
    //
    //    def author="${assignee.displayName}"
    //    def authorIdent="${assignee.displayName} <${assignee.emailAddress.replaceAll(' at ','@').replaceAll(' dot ','.')}>"
    //
    //    println reader.issues[0].keySet()
    //    println reader.issues[0].fields.keySet()
    //    println reader.issues[0].fields.assignee
    //    msg=("${key}: ${summary} (${author} via Zoltan Haindrich)")
    //    println("${msg}")
    //    println(authorIdent)
    //
    //    println ""
    //    println "git commit --author '${authorIdent}' -m '${msg}' --signoff"

  }

  public void apply(File repoDir) throws Exception {
    Git repo = Git.open(repoDir);

    ensureStateClean(repo);

    //    repo.apply().setPatch(new FileInputStream(patchFile)).call();

  }

  private void ensureStateClean(Git repo) throws Exception {
    Status status = repo.status().call();
    if (status.hasUncommittedChanges()) {
      throw new RuntimeException("hasUncommittedChanges!");
    }
  }
}
