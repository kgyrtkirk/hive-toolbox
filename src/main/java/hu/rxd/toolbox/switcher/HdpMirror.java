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
  public URL getFor(Component tez, String componentVersion) throws Exception {
    //        tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz
    String tarPart = String.format("tars/%s/%s-%s.tar.gz", tez, tez, componentVersion);
    //        String tarPart = String.format("tars/%s/apache-%s-%s-bin.tar.gz", tez, tez, componentVersion);
    return new URL(baseUrl + tarPart);
  }

}