package hu.rxd.toolbox.switcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version {

  public enum Type {
    APACHE(new ApacheMirrors()),
    HDP(new HdpMirrors()),
    DEV(new DevMirrors()),
    CDP(new CDPMirrors()),
    CDWH(new CDWHMirrors()),
    HTTP(new ExplicitHttpMirrors());

    public Mirrors mirrors;

    private Type(Mirrors m) {
      mirrors = m;
    }

    public Mirrors getMirrors() {
      return mirrors;
    }

  }

  Version.Type type;
  /** Full versionStr; 3.1.2.7.1.2.2-11  */
  private String versionStr;
  /** stackVersion; 7.1.2.2-11 - for 3.1.2.7.1.2.2-11 */
  String stackVersion;
  private String url;

  public Version(String versionStr) {
    this.versionStr = versionStr;
    if (versionStr.startsWith("DEV")) {
      this.type = Type.DEV;
    } else if (versionStr.startsWith("HDP")) {
      this.type = Type.HDP;
      this.stackVersion = type.mirrors.decodeStackVersion(versionStr.substring(4));
    } else if (versionStr.startsWith("CDP")) {
      this.type = Type.CDP;
      this.stackVersion = type.mirrors.decodeStackVersion(versionStr.substring(4));
    } else if (versionStr.startsWith("CDWH")) {
      this.type = Type.CDWH;
      this.stackVersion = type.mirrors.decodeStackVersion(versionStr.substring(5));
    } else if (versionStr.startsWith("http")) {
      this.type = Type.HTTP;
      this.url = versionStr;
      // FIXME: ugly
      this.versionStr = type.mirrors.decodeStackVersion(versionStr);
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

  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return versionStr;
  }
}