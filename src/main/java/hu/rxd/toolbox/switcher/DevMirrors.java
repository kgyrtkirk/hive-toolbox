package hu.rxd.toolbox.switcher;

import java.util.Collection;
import java.util.Collections;

public class DevMirrors implements Mirrors {

  @Override
  public String getComponentVersion(Version version, Component c) {
    return version.getVerStr();
  }

  @Override
  public String decodeStackVersion(String version) {
    throw new RuntimeException("unimpl");
  }

  @Override
  public Collection<Mirror> of0(Version ver) {
    return Collections.EMPTY_LIST;
  }

}
