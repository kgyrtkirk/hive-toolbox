package hu.rxd.toolbox.switcher;

public class ApacheMirrors implements Mirrors {

  @Override
  public String getComponentVersion(Version version, Component c) {
    return version.getVerStr();
  }

  @Override
  public String decodeStackVersion(String version) {
    return version;
  }

}
