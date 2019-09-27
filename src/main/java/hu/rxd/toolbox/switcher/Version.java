package hu.rxd.toolbox.switcher;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.rxd.toolbox.HiveDevBoxSwitcher;
import hu.rxd.toolbox.qtest.diff.CachedURL;

public class Version {
  public enum Type {
    APACHE, HDP, DEV, XXX;
  }

  Version.Type type;
  private String versionStr;
  String stackVersion;

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

  static Logger LOG = LoggerFactory.getLogger(Version.class);

  // FIXME this logic seems to be mirror specific; move there!
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