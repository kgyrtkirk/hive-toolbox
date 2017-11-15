package hu.rxd.toolbox.qtest;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;

import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileFilterSelector;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import hu.rxd.toolbox.jira.HiveTicket;

public class LastQAReportInputStreamDispatcher2 implements IInputStreamDispatcher {

  private HiveTicket t;
  private URI qaLogs;

  public LastQAReportInputStreamDispatcher2(String string) throws Exception {
    t = new HiveTicket(string);
    qaLogs = t.getLastQATestLogsURI();
  }

  @Override
  public void visit(Function<InputStream, Void> function) throws Exception {
    FileSystemManager fsManager = VFS.getManager();

    System.out.println(qaLogs);
    String tgz = "tgz:" + qaLogs.toString();
    FileObject jarFile = fsManager.resolveFile(tgz);
    // List the children of the Jar file
    System.out.println(jarFile);

    FileFilter fileFilter = new FileFilter() {

      @Override
      public boolean accept(FileSelectInfo arg0) {
        FileName a = arg0.getFile().getName();
        String bn = a.getBaseName();
        String ext = a.getExtension();
        return bn.startsWith("TEST-") && "xml".equals(ext);
      }
    };

    FileObject[] children = jarFile.getChildren();
    System.out.println("Children of " + jarFile.getName().getURI());
    for (int i = 0; i < children.length; i++) {
      System.out.println(children[i].getName().getBaseName());
    }

    FileObject trRoot = jarFile.getChild("test-results");
    if (trRoot == null) {
      throw new RuntimeException("expected a test-results");
    }
    int cnt = 0;
    for (FileObject a : trRoot.findFiles(new FileFilterSelector(fileFilter))) {
      System.out.println("X" + a);
      function.apply(a.getContent().getInputStream());
      cnt++;
    }
    System.out.println("c:" + cnt);

  }

}
