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

package hu.rxd.toolbox.git;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import hu.rxd.toolbox.qtest.diff.CachedURL;

public class Digger implements AutoCloseable {

  private String lBranch;
  private String rBranch;
  private Git git;

  public Digger(String lBranch, String rBranch) throws Exception {

    git = Git.open(new File("."));

    this.lBranch = lBranch;
    this.rBranch = rBranch;

    walk(lBranch, rBranch);
    walk(rBranch, lBranch);

    System.out.println("start:" + new HashSet(mm.values()).size());

    enhanceWithAliases();

    fuseSameLabels();

    System.out.println(mm.keySet().size());
    identifySimple();
    System.out.println(mm.keySet().size());
    removeNoise();
    System.out.println(mm.keySet().size());

    processUpstreamOnly(lBranch, "HIVE");
    System.out.println(mm.keySet().size());
    System.out.println("fin:" + new HashSet(mm.values()).size());

    show(10);
  }

  private void fuseSameLabels() {
    int cnt = 0;
    Set<PCommit> toRemove = new HashSet<>();
    for (Label l : mm.keySet()) {
      if (l.type != LabelType.ticket) {
        continue;
      }
      Collection<PCommit> commits0 = mm.get(l);

      if (commits0.size() < 2) {
        continue;
      }

      List<PCommit> li = new ArrayList<>(commits0);
      for (int i = 0; i < li.size() - 1; i++) {
        PCommit ci = li.get(i);
        if (toRemove.contains(ci)) {
          continue;
        }
        for (int j = i + 1; j < li.size(); j++) {
          PCommit cj = li.get(j);
          if (toRemove.contains(cj)) {
            continue;
          }
          if (ci.get(LabelType.branch).equals(cj.get(LabelType.branch))
              && ci.get(LabelType.ticket).equals(cj.get(LabelType.ticket))) {
            ci.fuse(cj);
            toRemove.add(cj);
            cnt++;
          }
        }
      }
    }
    removeAll(toRemove);

    System.out.println("fused: " + cnt);
  }

  private void enhanceWithAliases() throws Exception {
    HashSet<PCommit> all = new HashSet<>(mm.values());
    for (PCommit c : all) {
      for (Label t : c.get(LabelType.ticket)) {
        Set<Label> aliases = getTicketAliases(t);
        for (Label a : aliases) {
          if (c.labels.contains(a)) {
            continue;
          }
          c.labels.add(a);
          mm.put(a, c);
        }
      }
    }

  }

  private Set<Label> getTicketAliases(Label t) throws Exception {
    File file =
        new CachedURL(new URL("http://datahub.eng.hortonworks.com:8668/gittleman/aliases/" + t.value)).getFile();

    ObjectMapper objectMapper = new ObjectMapper();
    List<String> aliases = objectMapper.readValue(file, List.class);

    Set<Label> ret = new HashSet<Digger.Label>();
    for (String a : aliases) {
      ret.add(new Label(LabelType.ticket, a));
    }
    return ret;

    //

  }

  private void processUpstreamOnly(String branchName, String ticketPrefix) {

    Set<PCommit> toRemove = new HashSet<>();
    for (Label l : mm.keySet()) {
      if (l.type != LabelType.ticket) {
        continue;
      }
      if (!l.value.toUpperCase().startsWith(ticketPrefix.toUpperCase())) {
        continue;
      }
      Collection<PCommit> commits0 = mm.get(l);
      if (commits0.size() != 1) {
        continue;
      }
      PCommit c = commits0.iterator().next();
      if (!c.branch.equals(branchName)) {
        continue;
      }
      toRemove.add(c);
    }
    System.out.println("upstreamOnly: " + toRemove.size());
    removeAll(toRemove);

  }

  private void removeNoise() {
    Set<PCommit> toRemove = new HashSet<>();
    toRemove.addAll(mm.get(new Label(LabelType.author, "Jenkins")));
    toRemove.addAll(mm.get(new Label(LabelType.author, "Jenkins User")));

    System.out.println("noise: " + toRemove.size());
    removeAll(toRemove);

  }

  private void removeAll(Set<PCommit> toRemove) {
    for (PCommit pCommit : toRemove) {
      remove(pCommit);
    }
  }

  private void show(int i) {

    for (Label l : mm.keySet()) {
      if (l.type != LabelType.ticket) {
        continue;
      }
      if (i <= 0) {
        break;
      }

      System.out.printf("%d %s\n", mm.get(l).size(), l);
      //      System.out.println(l);
      //      //      System.out.println(mm.get(l));
      //      System.out.println();
      i--;
    }

  }

  private void identifySimple() {
    int cnt = 0;
    Set<PCommit> toRemove = new HashSet<>();
    for (Label l : mm.keySet()) {
      if (l.type != LabelType.ticket) {
        continue;
      }
      Collection<PCommit> commits0 = mm.get(l);
      if (commits0.size() != 2) {
        continue;
      }
      List<PCommit> commits = new ArrayList(mm.get(l));
      PCommit c1 = commits.get(0);
      PCommit c2 = commits.get(1);

      if (c1.branch == c2.branch) {
        continue;
      }
      toRemove.add(c1);
      toRemove.add(c2);
      cnt++;

    }
    removeAll(toRemove);

    System.out.println("simple: " + cnt);

  }

  private void remove(PCommit c1) {
    for (Label l : c1.labels) {
      mm.remove(l, c1);
    }
  }

  private RevCommit getMergeBase(String string, String string2) throws Exception {
    Repository repo = git.getRepository();
    try (RevWalk walk = new RevWalk(repo)) {
      RevCommit c1 = walk.parseCommit(repo.resolve(string));
      RevCommit c2 = walk.parseCommit(repo.resolve(string2));

      walk.setRevFilter(RevFilter.MERGE_BASE);
      walk.markStart(c1);
      walk.markStart(c2);
      RevCommit mergeBase = walk.next();
      return mergeBase;
    }
  }

  private void walk(String lBranch, String rBranch) throws Exception {
    RevCommit base = getMergeBase(lBranch, rBranch);
    try (RevWalk revWalk = new RevWalk(git.getRepository())) {
      Repository repo = git.getRepository();
      revWalk.markStart(revWalk.parseCommit(repo.resolve(lBranch)));
      int cnt = 0;
      for (RevCommit revCommit : revWalk) {
        if (revCommit.equals(base)) {
          break;
        }
        processCommit(lBranch, revCommit);
        cnt++;
      }
      System.out.printf("%s -> %s : %d\n", lBranch, rBranch, cnt);
    }
  }

  enum LabelType {
    author, id, ticket, branch;

  }

  static class Label {
    LabelType type;
    String value;

    public Label(LabelType string, String name) {
      type = string;
      value = name;

    }

    @Override
    public boolean equals(Object obj) {
      Label o = (Label) obj;
      return Objects.equals(type, o.type) && Objects.equals(value, o.value);

    }

    @Override
    public int hashCode() {
      return com.google.common.base.Objects.hashCode(type, value);
    }

    @Override
    public String toString() {
      return type + ":" + value;
    }

  }

  static class PCommit {

    private final String message;
    private final PersonIdent author;
    private final ObjectId id;
    private final Set<Label> labels;
    private final String branch;
    private Set<PCommit> fused = new HashSet<>();

    public PCommit(String branch0, RevCommit revCommit) {
      this.branch = branch0;
      message = revCommit.getFullMessage();
      author = revCommit.getAuthorIdent();
      id = revCommit.getId();

      labels = new HashSet<>();
      labels.add(new Label(LabelType.author, author.getName()));
      labels.add(new Label(LabelType.id, id.getName()));
      labels.add(new Label(LabelType.branch, branch));

      extractTickets(message);
      for (String t : extractTickets(message)) {
        labels.add(new Label(LabelType.ticket, t));
      }

    }

    public void fuse(PCommit cj) {
      fused.add(cj);

    }

    Set<Label> get(LabelType t) {
      Set<Label> ret = new HashSet<Digger.Label>();

      for (Label label : labels) {
        if (label.type == t) {
          ret.add(label);
        }
      }
      return ret;
    }

    private Set<String> extractTickets(String str) {
      Set<String> tickets = new HashSet<>();
      Pattern pat = Pattern.compile("[A-Za-z]+\\-[0-9]+");
      Matcher matcher = pat.matcher(str);
      while (matcher.find()) {
        String g = matcher.group();
        tickets.add(g);
      }
      return tickets;
    }
  }

  Multimap<Label, PCommit> mm = MultimapBuilder.hashKeys().arrayListValues().build();

  private void processCommit(String branch, RevCommit revCommit) {
    PCommit p = new PCommit(branch, revCommit);
    for (Label l : p.labels) {
      mm.put(l, p);
    }

  }

  public static void main(String[] args) throws Exception {
    String lBranch = args[0];
    String rBranch = args[1];

    Digger digger = new Digger(lBranch, rBranch);
  }

  @Override
  public void close() throws Exception {
    git.close();
  }

}
