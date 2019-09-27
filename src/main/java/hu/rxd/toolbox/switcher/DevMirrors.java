package hu.rxd.toolbox.switcher;

public class DevMirrors implements Mirrors {

  @Override
  public String getComponentVersion(Version version, Component c) {
    return version.getVerStr();
  }

}
