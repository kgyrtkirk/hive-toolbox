package hu.rxd.toolbox.switcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

// FIXME s?
class HdpMirrors implements Mirror {

  
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