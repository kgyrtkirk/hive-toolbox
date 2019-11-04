package hu.rxd.toolbox.switcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.DirectoryScanner;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.rxd.toolbox.HiveDevBoxSwitcher;
import hu.rxd.toolbox.qtest.diff.CachedURL;
import hu.rxd.toolbox.switcher.Version.Type;

abstract class GenericComponent implements IComponent {
  File baseDir = new File("/work/");
  File downloadDir = new File(baseDir, ".downloads");
  File linkDir = new File("/active/");
  //    File baseDir = new File("/var/tmp/");

  static Logger LOG = LoggerFactory.getLogger(GenericComponent.class);

  @Override
  public void switchTo(Version ver) throws Exception {
    File targetPath = ensurePresence(ver);

    File link = new File(linkDir, getComponentType().getLabel());
    if (Files.isSymbolicLink(link.toPath())) {
      link.delete();
    }
    Files.createSymbolicLink(link.toPath(), targetPath.toPath());
    postActivation();
    LOG.info("activated {} for {}", targetPath, getComponentType());
  }

  /**
   * Some components may need to do something after activation.
   * 
   * For example Tez needs to be placed at some predefined hdfs location to work.
   */
  public void postActivation() throws Exception {
  }

  protected File ensurePresence(Version ver) throws IOException, Exception {
    if (ver.type == Type.DEV) {
      return provideDevPath(ver);
    }
    String componentTargetDir = String.format("%s-%s", getComponentType().getLabel(), ver.getVerStr());
    File targetPath = new File(baseDir, componentTargetDir);
    if (!targetPath.exists())
      provideComponent(targetPath, ver);
    return targetPath;
  }

  protected abstract void provideComponent(File targetPath, Version ver) throws Exception;

  protected abstract File provideDevPath(Version ver) throws Exception;


  /**
   * Expands a "standard" release artifact.
   *
   * they contain exactly 1 directory at the top level of the archive
   * exmaple: apache-hive releases or apache-maven releases
   */
  protected void expand1DirReleaseArtifact(File targetPath, File artifactFile) throws IOException {
    File expandPath = new File(baseDir, targetPath.getName() + ".tmp");
    FileUtils.deleteDirectory(expandPath);
    Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
    LOG.info("extracting: {}", artifactFile.getName());
    archiver.extract(artifactFile, expandPath);

    File[] files = expandPath.listFiles();
    if (files.length != 1) {
      throw new RuntimeException("expected to have only one directory in the archive... " + Arrays.toString(files));
    }
    LOG.info("renaming {} to {}", files[0], targetPath);
    files[0].renameTo(targetPath);
    FileUtils.deleteDirectory(expandPath);
    LOG.info("finished: {}", targetPath);
  }

  File getMatchingPathForGlob(File path, String glob) throws IOException {
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setIncludes(new String[] { glob });
    scanner.setBasedir(path.getAbsolutePath());
    scanner.setCaseSensitive(false);
    scanner.scan();

    String[] dirs = scanner.getIncludedDirectories();

    if (dirs.length != 1) {
      throw new IOException(
          "Expected exactly one match to glob:" + glob + " under " + path + ". bot got: " + Arrays.toString(dirs));
    }
    return new File(path, dirs[0]);
  }

  protected File downloadArtifact(List<URL> candidateUrls) throws IOException {
    for (URL url : candidateUrls) {
      try {
        LOG.info("downloading: {}", url);
        return new CachedURL(url, downloadDir).getFile();
      } catch (Exception e) {
        LOG.info("failed to download: " + url);
      }
    }
    throw new IOException("Cant find a valid url; tried: " + candidateUrls);
  }

  String apache_mirror = "http://xenia.sote.hu/ftp/mirrors/www.apache.org/";
  String archive_mirror = "https://archive.apache.org/dist/";

  protected List<URL> getCandidateUrls(Version ver) throws Exception {
    List<URL> ret = new ArrayList<>();

    switch (ver.type) {
    case APACHE:
      ret.add(new URL(apache_mirror + getApacheMirrorPath(ver)));
      ret.add(new URL(archive_mirror + getApacheMirrorPath(ver)));
      break;
    case HDP:
    { // FIXME: can be moved?!
      String componentVersion = ver.getComponentVersion(getComponentType());
      for (Mirror m : HdpMirrors.of(ver)) {
        ret.add(m.getFor(getComponentType(), componentVersion));
      }
      break;
    }
    case CDP: {
      // FIXME: can be moved?!
      String componentVersion = ver.getComponentVersion(getComponentType());
      for (Mirror m : CDPMirrors.of(ver)) {
        ret.add(m.getFor(getComponentType(), componentVersion));
      }
    }
      break;
    default:
      //        http: //public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/3.0.0.0/artifacts.txt
      //        http: //s3.amazonaws.com/dev.hortonworks.com/HDP/centos7/3.x/BUILDS/3.0.0.0-1634/tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz

      throw new RuntimeException("?");
    }
    return ret;
  }

  //         public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/3.0.0.0/tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz
  //    http://public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/3.0.0.0/tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz]
  //      at hu.rxd.toolbox.HiveDevBoxSwitcher$GenericComponent.tryDownload(Hive

  abstract String getApacheMirrorPath(Version version) throws Exception;
}