package hu.rxd.toolbox;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.rxd.toolbox.jira.HiveTicket;
import hu.rxd.toolbox.jira.ToolboxSettings;
import hu.rxd.toolbox.qtest.diff.CachedURL;
import net.rcarz.jiraclient.Attachment;
import net.rcarz.jiraclient.Comment;

public class TicketUtils {
  static Logger LOG = LoggerFactory.getLogger(TicketUtils.class);

  public static void reattach(String ticketKey) throws Exception {
    jiraLogin();
    HiveTicket t = new HiveTicket(ticketKey);
    Optional<Attachment> attachment = t.getLastAttachment();
    if (!attachment.isPresent()) {
      throw new RuntimeException("no attachment for " + ticketKey);
    }
    URL patchURL = new CachedURL(new URL(attachment.get().getContentUrl())).getURL();

    File patchFile = new File(attachment.get().getFileName());
    FileUtils.copyURLToFile(patchURL, patchFile);

    try (Closeable c = t.withAssignedToCurrentUser()) {
      t.getIssue().addAttachment(patchFile);
    }
  }

  private static void jiraLogin() {
    HiveTicket.jiraLogin(
        ToolboxSettings.instance().getJiraUserId(),
        ToolboxSettings.instance().getJiraPassword());
  }

  public static void upload() throws Exception {
    try (Git git = Git.open(new File("."))) {
      Repository repo = git.getRepository();
      String currentBranch = repo.getBranch();
      String ticketKey = extractJiraKey(currentBranch);

      jiraLogin();
      HiveTicket t = new HiveTicket(ticketKey);
      Optional<Attachment> att = t.getLastAttachment();
      int idx = 0;
      if (att.isPresent()) {
        idx = extractFileIndex(att.get().getFileName());
      }
      idx++;

      String patchFileName = String.format("/tmp/%s.%02d.patch", ticketKey, idx);
      File patchFile = new File(patchFileName);

      ObjectId ref = repo.resolve("apache/master");


      try (FileOutputStream fos = new FileOutputStream(patchFile)) {
        git.diff().setOldTree(getMergeBase(repo, "apache/master", "HEAD"))
            .setNewTree(getMergeBase(repo, "HEAD", "HEAD"))
            .setOutputStream(fos)
            .call();
      }

      t.getIssue().addAttachment(patchFile);

    }
  }

  public static AbstractTreeIterator getMergeBase(Repository repo, String string, String string2) throws Exception {
    try (RevWalk walk = new RevWalk(repo)) {
      RevCommit c1 = walk.parseCommit(repo.resolve(string));
      RevCommit c2 = walk.parseCommit(repo.resolve(string2));

      walk.setRevFilter(RevFilter.MERGE_BASE);
      walk.markStart(c1);
      walk.markStart(c2);
      RevCommit mergeBase = walk.next();

      final CanonicalTreeParser p = new CanonicalTreeParser();
      try (ObjectReader or = repo.newObjectReader();
        RevWalk rw = new RevWalk(repo)) {
        p.reset(or, mergeBase.getTree());
        return p;
      }

    }
  }

  static private AbstractTreeIterator getTreeIterator(String name, Repository repo)
      throws IOException {
    final ObjectId id = repo.resolve(name);
    if (id == null) {
      throw new IllegalArgumentException(name);
    }
    final CanonicalTreeParser p = new CanonicalTreeParser();
    try (ObjectReader or = repo.newObjectReader();
      RevWalk rw = new RevWalk(repo)) {
      p.reset(or, rw.parseTree(id));
      return p;
    }
  }

  private static int extractFileIndex(String fileName) {
    Pattern pat = Pattern.compile("^[A-Z]+-[0-9]+\\.([0-9]+)\\.patch$");
    Matcher m = pat.matcher(fileName);
    if (!m.matches()) {
      throw new RuntimeException(fileName + " doesnt match regex..");
    }
    String key = m.group(1);
    LOG.info(fileName + " => " + key);
    return Integer.parseInt(key);
  }

  private static String extractJiraKey(String currentBranch) {
    Pattern pat = Pattern.compile("^([A-Z]+-[0-9]+).*");
    Matcher m = pat.matcher(currentBranch);
    if (!m.matches()) {
      throw new RuntimeException(currentBranch + " doesnt match regex..");
    }
    String key = m.group(1);
    LOG.info(currentBranch + " => " + key);
    return key;
  }

  static class TxProcessor {

    private int nofromat;
    private List<String> patterns = new ArrayList<>();

    public void process(String line) {
      if (line.equals("{noformat}")) {
        nofromat++;
        return;
      }
      if (nofromat == 1) {
        processTestLine(line);
      }

    }

    private void processTestLine(String line) {
      String parts[] = line.split(" ");
      String test = parts[0];
      System.out.println(test);
      //String testM = test.replaceAll("\\.([^\\.]+)$", "#$1");

      String[] p2 = test.split("\\.");
      System.out.println(p2.length);

      String testM = p2[p2.length - 2] + "#" + p2[p2.length - 1];
      patterns.add(testM);
    }

  }

  public static List<String> getFailed(HiveTicket hiveTicket) {
    HiveTicket t = hiveTicket;
    Comment c = t.getLastQAComment();

    String[] lines = c.getBody().split("\n");
    TxProcessor txp = new TxProcessor();
    for (String string : lines) {
      txp.process(string);
    }

    return txp.patterns;
    //
    //      //      TestEntries res2 = res.filterFailed().limit(600);
    //      //      System.out.println(res2);
    //      //      String pat = res2.getSimpleMavenTestPattern();
    //      //      System.out.println("pat len:" + pat.length());
    //      //      try (PrintStream ps = new PrintStream(args[1])) {
    //      //        ps.println(pat);
    //      //      }

  }

}
