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
    APACHE(new ApacheMirrors()), HDP(new HdpMirrors()), DEV(new DevMirrors()), XXX(new CDPMirrors());

    public Mirrors mirrors;

    private Type(Mirrors m) {
      mirrors = m;
    }

    public Mirrors getMirrors() {
      return mirrors;
    }
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
    return type.getMirrors().getComponentVersion(this, c);
    //      return versionStr;
  }

  static Logger LOG = LoggerFactory.getLogger(Version.class);

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