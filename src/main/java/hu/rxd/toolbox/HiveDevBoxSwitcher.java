package hu.rxd.toolbox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import hu.rxd.toolbox.qtest.diff.CachedURL;

public class HiveDevBoxSwitcher {

  private static List<? extends URL> getApacheMirrorPaths(String path) throws Exception {
    String apache_mirror = "http://xenia.sote.hu/ftp/mirrors/www.apache.org/";
    String archive_mirror = "https://archive.apache.org/dist/";
    return Lists.newArrayList(
        new URL(apache_mirror + path),
        new URL(archive_mirror + path));
  }

  static class Version {
    enum Type {
      APACHE,
    }

    Type type;
    private String versionStr;

    public Version(String versionStr) {
      this.versionStr = versionStr;
      this.type = Type.APACHE;
    }

    // supposed to be the actual version like 3.1.0.7.0.0.0 or something...
    public String getVersion() {
      return versionStr;
    }

    // supposed to be HDP-3.1 
    public String getVerStr() {
      return versionStr;
    }

    @Override
    public String toString() {
      return versionStr;
    }
  }

  static interface IC {
    void switchTo(Version version) throws Exception;

    String getComponentName();
  }

  static Logger LOG = LoggerFactory.getLogger(HiveDevBoxSwitcher.class);

  static abstract class GenericComponent implements IC {
    File baseDir = new File("/var/tmp/");

    @Override
    public void switchTo(Version ver) throws Exception {
      String componentTargetDir = String.format("%s-%s", getComponentName(), ver.getVerStr());
      File targetPath = new File(baseDir, componentTargetDir);
      ensurePresence(ver, componentTargetDir, targetPath);

      File link = new File(baseDir, getComponentName());
      if (link.exists()) {
        link.delete();
      }
      Files.createSymbolicLink(link.toPath(), targetPath.toPath());
      postActivation();
      LOG.info("activated {} for {}", targetPath, getComponentName());
    }

    public void postActivation() throws Exception {
    }

    private void ensurePresence(Version ver, String componentTargetDir, File targetPath) throws IOException, Exception {
      if (!targetPath.exists()) {
        LOG.info("downloading: {}", ver);
        File f = tryDownload(getCandidateUrls(ver));
        File expandPath = new File(baseDir, componentTargetDir + ".tmp");
        FileUtils.deleteDirectory(expandPath);
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
        LOG.info("extracting: {}", componentTargetDir);
        archiver.extract(f, expandPath);

        File[] files = expandPath.listFiles();
        if (files.length != 1) {
          throw new RuntimeException("expected to have only one directory in the archive... " + Arrays.toString(files));
        }
        LOG.info("renaming {} to {}", files[0], targetPath);
        files[0].renameTo(targetPath);
        FileUtils.deleteDirectory(expandPath);
        LOG.info("finished: {}", componentTargetDir);
      } else {
        LOG.info("{} is already present", componentTargetDir);
      }
    }

    private File tryDownload(List<URL> candidateUrls) throws IOException {
      for (URL url : candidateUrls) {
        try {
          return new CachedURL(url).getFile();
        } catch (Exception e) {
          LOG.info("failed to download: " + url);
        }
      }
      throw new IOException("Cant find a valid url; tried: " + candidateUrls);
    }

    String apache_mirror = "http://xenia.sote.hu/ftp/mirrors/www.apache.org/";
    String archive_mirror = "https://archive.apache.org/dist/";

    private List<URL> getCandidateUrls(Version ver) throws Exception {
      List<URL> ret = new ArrayList<>();

      switch (ver.type) {
      case APACHE:
        ret.add(new URL(apache_mirror + getApacheMirrorPath(ver)));
        ret.add(new URL(archive_mirror + getApacheMirrorPath(ver)));
        break;
      default:
        throw new RuntimeException("?");
      }
      return ret;
    }

    abstract String getApacheMirrorPath(Version version);
  }

  static class HiveComponent extends GenericComponent {

    @Override
    String getApacheMirrorPath(Version ver) {
      return String.format("hive/hive-%s/apache-hive-%s-bin.tar.gz", ver.getVersion(),
          ver.getVersion());
    }

    @Override
    public String getComponentName() {
      return "hive";
    }

  }

  static class HadoopComponent extends GenericComponent {

    @Override
    String getApacheMirrorPath(Version ver) {
      return String.format("hadoop/common/hadoop-%s/hadoop-%s.tar.gz", ver.getVersion(),
          ver.getVersion());
    }

    @Override
    public String getComponentName() {
      return "hadoop";
    }

  }

  static class TezComponent extends GenericComponent {

    @Override
    String getApacheMirrorPath(Version ver) {
      return String.format("tez/%s/apache-tez-%s-bin.tar.gz", ver.getVersion(),
          ver.getVersion());
    }

    @Override
    public String getComponentName() {
      return "tez";
    }

    public void postActivation() throws IOException {
      try {
        Files.copy(new File(baseDir + "/tez/share/tez.tar.gz").toPath(), new File("/apps/tez/").toPath(),
            StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
        throw new IOException("cant copy tez.tar.gz", e);
      }
    }

  }

  enum Component {
    hive(new HiveComponent()), hadoop(new HadoopComponent()), tez(new TezComponent()),;

    private IC component;

    private Component(IC component) {
      this.component = component;
    }

    public static Component valueOf1(String string) {
      try {
        return valueOf(string);
      } catch (Exception e) {
        throw new RuntimeException(string + " is not a valid component name; try: " + Arrays.toString(values()), e);
      }
    }

    IC get() {
      return component;
    }
  }

  public static void main(String[] args) throws Exception {
    Component c = Component.valueOf1(args[0]);
    Version version = new Version(args[1]);

    c.get().switchTo(version);
  }

}
