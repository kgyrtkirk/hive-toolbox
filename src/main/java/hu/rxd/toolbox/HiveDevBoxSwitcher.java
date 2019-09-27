package hu.rxd.toolbox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.DirectoryScanner;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import hu.rxd.toolbox.HiveDevBoxSwitcher.Version.Type;
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
      APACHE, HDP, DEV, XXX;
    }

    Type type;
    private String versionStr;
    private String stackVersion;

    public Version(String versionStr) {
      this.versionStr = versionStr;
      if (versionStr.startsWith("DEV")) {
        this.type = Type.DEV;
      } else if (versionStr.startsWith("HDP")) {
        this.type = Type.HDP;
        this.stackVersion = versionStr.substring(4);
      } else if (versionStr.startsWith("XXX")) {
        this.type = Type.XXX;
        this.stackVersion = versionStr.substring(4);
      } else {
        this.type = Type.APACHE;
      }
    }

    /** supposed to be the actual version like 3.1.0.7.0.0.0 or something...
     * @throws Exception */
    public String getComponentVersion(Component c) throws Exception {
      return getComponentVersion(versionStr, c);
      //      return versionStr;
    }

    public String getComponentVersion(String versionStr, Component c) throws Exception {
      if (type == Type.HDP || type == Type.XXX) {

        String artifacts;
        if (type == Type.HDP) {
          artifacts = String.format("http://public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/%s/artifacts.txt",
              stackVersion);
        } else {
          artifacts =
              String.format(
                  "http://cloudera-build-us-west-1.vpc.cloudera.com/s3/build/%s/cdh/7.x/redhat7/yum/artifacts.txt",
                  stackVersion);
        }
        Path path = new CachedURL(new URL(artifacts)).getFile().toPath();
        String versionMatchingPattern = String.format("tars/%s/%s-(.*)-source.tar.gz", c, c);
        Set<String> matches = Files.lines(path).filter(
            //tars/hive/hive-3.1.0.3.0.0.0-1634-source.tar.gz
            s -> s.matches(versionMatchingPattern)
        ).collect(Collectors.toSet());

        if (matches.size() != 1) {
          throw new RuntimeException("Expected to match 1 file; found: " + matches.toString());
        }
        String m = matches.iterator().next();
        Matcher match = Pattern.compile(versionMatchingPattern).matcher(m);
        if(!match.find()) { 
          throw new RuntimeException("no match?!");
        }
        String version = match.group(1);
        LOG.info("Version of " + c + " for " + versionStr + " is " + version);
        return version;

      }
      // TODO Auto-generated method stub
      return versionStr;
    }

    /** Supposed to be the qualified version string
     * 
     * probably something like HDP-3.1
     */
    public String getVerStr() {
      return versionStr;
    }

    @Override
    public String toString() {
      return versionStr;
    }
  }

  static interface IComponent {
    void switchTo(Version version) throws Exception;

    String getComponentName();
  }

  static Logger LOG = LoggerFactory.getLogger(HiveDevBoxSwitcher.class);

  static abstract class GenericComponent implements IComponent {
    File baseDir = new File("/work/");
    File downloadDir = new File(baseDir, ".downloads");
    File linkDir = new File("/active/");
    //    File baseDir = new File("/var/tmp/");

    @Override
    public void switchTo(Version ver) throws Exception {
      File targetPath = ensurePresence(ver);

      File link = new File(linkDir, getComponentName());
      if (Files.isSymbolicLink(link.toPath())) {
        link.delete();
      }
      Files.createSymbolicLink(link.toPath(), targetPath.toPath());
      postActivation();
      LOG.info("activated {} for {}", targetPath, getComponentName());
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
      String componentTargetDir = String.format("%s-%s", getComponentName(), ver.getVerStr());
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

    interface Mirror {

      URL getFor(Component tez, String componentVersion) throws Exception;

    }

    // FIXME s?
    static class HdpMirrors implements Mirror {

      
      private static final List<String> MIRROR_ROOTS =
          Lists.newArrayList(("http://public-repo-1.hortonworks.com/HDP"));
      private final String baseUrl;

      public HdpMirrors(String string) {
        baseUrl = string;
        assert !baseUrl.endsWith("/");
      }

      //"      centos7/3.x/updates/%s/artifacts.txt",stackVersion)"
      public static Collection<Mirror> of(Version ver) {
        List<Mirror> ret = new ArrayList<>();
        for (String root : MIRROR_ROOTS) {
          String versionRoot =
              String.format("%s/centos7/%s.x/updates/%s/", root, ver.stackVersion.substring(0, 1), ver.stackVersion);
          ret.add(new HdpMirrors(versionRoot));
        }
        return ret;
      }

      @Override
      public URL getFor(Component tez, String componentVersion) throws Exception {
        //        tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz
        String tarPart = String.format("tars/%s/%s-%s.tar.gz", tez, tez, componentVersion);
        //        String tarPart = String.format("tars/%s/apache-%s-%s-bin.tar.gz", tez, tez, componentVersion);
        return new URL(baseUrl + tarPart);
      }

    }

    // FIXME s?
    static class XXXMirrors implements Mirror {

      private static final List<String> MIRROR_ROOTS =
          Lists.newArrayList(("http://cloudera-build-us-west-1.vpc.cloudera.com/s3/build"));
      private final String baseUrl;

      public XXXMirrors(String string) {
        baseUrl = string;
        assert !baseUrl.endsWith("/");
      }

      //"      centos7/3.x/updates/%s/artifacts.txt",stackVersion)"
      public static Collection<Mirror> of(Version ver) {
        List<Mirror> ret = new ArrayList<>();
        for (String root : MIRROR_ROOTS) {
          String versionRoot =
              String.format("%s/%s/cdh/7.x/redhat7/yum/", root, ver.stackVersion);
          ret.add(new XXXMirrors(versionRoot));
        }
        return ret;
      }

      @Override
      public URL getFor(Component tez, String componentVersion) throws Exception {
        String tarPart;
        if (tez == Component.hive) {
          tarPart = String.format("tars/%s/apache-%s-%s-bin.tar.gz", tez, tez, componentVersion);
        } else {
          //        tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz
          tarPart = String.format("tars/%s/%s-%s.tar.gz", tez, tez, componentVersion);

        }
        //        String tarPart = String.format("tars/%s/apache-%s-%s-bin.tar.gz", tez, tez, componentVersion);
        return new URL(baseUrl + tarPart);
      }

    }

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
      case XXX: {
        // FIXME: can be moved?!
        String componentVersion = ver.getComponentVersion(getComponentType());
        for (Mirror m : XXXMirrors.of(ver)) {
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

    //FIXME
    protected abstract Component getComponentType();

    //         public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/3.0.0.0/tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz
    //    http://public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/3.0.0.0/tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz]
    //      at hu.rxd.toolbox.HiveDevBoxSwitcher$GenericComponent.tryDownload(Hive

    abstract String getApacheMirrorPath(Version version) throws Exception;
  }

  static class HiveComponent extends GenericComponent {

    @Override
    String getApacheMirrorPath(Version ver) throws Exception {
      String v = ver.getComponentVersion(Component.hive);
      return String.format("hive/hive-%s/apache-hive-%s-bin.tar.gz", v, v);
    }

    // FIXME: probably change to Component?
    // FIXME: rename Component to ComponentKind ?
    @Override
    public String getComponentName() {
      return "hive";
    }

    @Override
    protected Component getComponentType() {
      return Component.hive;
    }

    protected File ensurePresence(Version ver, String componentTargetDir) throws Exception {
      File targetPath = new File(baseDir, componentTargetDir);
      switch (ver.type) {
      case DEV:
        if (!targetPath.exists()) {
          throw new IOException(targetPath + " doesn't exists");
        }
        File packageDir = getMatchingPathForGlob(targetPath,
            "packaging/target/apache-hive-*-SNAPSHOT-bin/apache-hive-*-SNAPSHOT-bin");
        return packageDir;
      case APACHE:
      case HDP:
        if (!targetPath.exists()) {
          expand1DirReleaseArtifact(targetPath, downloadArtifact(getCandidateUrls(ver)));
        } else {
          LOG.info("{} is already present", componentTargetDir);
        }
        return targetPath;
      default:
        throw new RuntimeException("not handled case: " + ver.type);
      }
    }

    @Override
    protected void provideComponent(File targetPath, Version ver) throws Exception {
      expand1DirReleaseArtifact(targetPath, downloadArtifact(getCandidateUrls(ver)));
    }

    @Override
    protected File provideDevPath(Version ver) throws Exception {
      File targetPath = new File("/home/dev/hive");
      File packageDir = getMatchingPathForGlob(targetPath,
          "packaging/target/apache-hive-*-SNAPSHOT-bin/apache-hive-*-SNAPSHOT-bin");
      return packageDir;
    }

  }

  static class HadoopComponent extends GenericComponent {

    @Override
    String getApacheMirrorPath(Version ver) throws Exception {
      String v = ver.getComponentVersion(Component.hadoop);
      return String.format("hadoop/common/hadoop-%s/hadoop-%s.tar.gz", v, v);
    }

    @Override
    public String getComponentName() {
      return "hadoop";
    }

    @Override
    protected Component getComponentType() {
      return Component.hadoop;
    }


    @Override
    protected void provideComponent(File targetPath, Version ver) throws Exception {
      expand1DirReleaseArtifact(targetPath, downloadArtifact(getCandidateUrls(ver)));
    }

    @Override
    protected File provideDevPath(Version ver) throws Exception {
      File targetPath = new File("/home/dev/hadoop");
      File packageDir = getMatchingPathForGlob(targetPath,
          "packaging/target/apache-invalid-not-yet-checked-*-SNAPSHOT-bin/apache-invalid-*-SNAPSHOT-bin");
      return packageDir;
    }

  }

  static class TezComponent extends GenericComponent {

    @Override
    String getApacheMirrorPath(Version ver) throws Exception {
      String v = ver.getComponentVersion(Component.tez);
      return String.format("tez/%s/apache-tez-%s-bin.tar.gz", v, v);
    }

    @Override
    public String getComponentName() {
      return "tez";
    }

    @Override
    protected Component getComponentType() {
      return Component.tez;
    }

    @Override
    protected void provideComponent(File targetPath, Version ver) throws Exception {
      switch (ver.type) {
      case HDP:
      case XXX:
        LOG.info("downloading: {}", ver);
        File f = downloadArtifact(getCandidateUrls(ver));
        File expandPath = new File(baseDir, targetPath.getName() + ".tmp");
        FileUtils.deleteDirectory(expandPath);
        File targetTgz = new File(expandPath, "/share/tez.tar.gz");
        FileUtils.forceMkdir(targetTgz.getParentFile());
        FileUtils.copyFile(f, targetTgz);
        expandPath.renameTo(targetPath);
        return;
      default:
        expand1DirReleaseArtifact(targetPath, downloadArtifact(getCandidateUrls(ver)));
      }
    }

    @Override
    protected File provideDevPath(Version ver) throws Exception {
      File targetPath = new File("/home/dev/hadoop");
      File packageDir = getMatchingPathForGlob(targetPath,
          "packaging/target/apache-invalid-not-yet-checked-*-SNAPSHOT-bin/apache-invalid-*-SNAPSHOT-bin");
      return packageDir;
    }

    public void postActivation() throws IOException {
      try {
        Files.copy(new File(linkDir + "/tez/share/tez.tar.gz").toPath(), new File("/apps/tez/tez.tar.gz").toPath(),
            StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
        throw new IOException("cant copy tez.tar.gz", e);
      }
    }

  }

  enum Component {
    hive(new HiveComponent()), hadoop(new HadoopComponent()), tez(new TezComponent()),;

    private IComponent component;

    private Component(IComponent component) {
      this.component = component;
    }

    public static Component valueOf1(String string) {
      try {
        return valueOf(string);
      } catch (Exception e) {
        throw new RuntimeException(string + " is not a valid component name; try: " + Arrays.toString(values()), e);
      }
    }

    IComponent get() {
      return component;
    }
  }

  public static void main(String[] args) throws Exception {
    Component c = Component.valueOf1(args[0]);
    Version version = new Version(args[1]);

    c.get().switchTo(version);
  }

}
