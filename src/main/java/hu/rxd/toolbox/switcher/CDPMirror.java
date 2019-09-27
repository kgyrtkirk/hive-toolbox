package hu.rxd.toolbox.switcher;

import java.net.URL;

class CDPMirror implements Mirror {

  private final String baseUrl;

  public CDPMirror(String string) {
    baseUrl = string;
    assert !baseUrl.endsWith("/");
  }

  @Override
  public URL getFor(Component component, String componentVersion) throws Exception {
    String tarPart;
    if (component == Component.hive) {
      tarPart = String.format("tars/%s/apache-%s-%s-bin.tar.gz", component, component, componentVersion);
    } else {
      tarPart = String.format("tars/%s/%s-%s.tar.gz", component, component, componentVersion);
    }
    return new URL(baseUrl + tarPart);
  }

}