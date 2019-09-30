package hu.rxd.toolbox.switcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

// FIXME s?
class HdpMirror implements Mirror {
  
  private final String baseUrl;

  public HdpMirror(String string) {
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