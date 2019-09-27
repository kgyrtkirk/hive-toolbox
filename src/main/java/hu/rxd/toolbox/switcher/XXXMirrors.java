package hu.rxd.toolbox.switcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

// FIXME s?
class XXXMirrors implements Mirror {

  private static final List<String> MIRROR_ROOTS =
      Lists.newArrayList(("http://cloudera-build-us-west-1.vpc.cloudera.com/s3/build"));
  private final String baseUrl;

  public XXXMirrors(String string) {
    baseUrl = string;
    assert !baseUrl.endsWith("/");
  }

  //"      centos7/3.x/updates/%s/artifacts.txt",stackVersion)"
  public static Collection<Mirror> of(Version ver) {
    List<Mirror> ret = new ArrayList<>();
    for (String root : MIRROR_ROOTS) {
      String versionRoot =
          String.format("%s/%s/cdh/7.x/redhat7/yum/", root, ver.stackVersion);
      ret.add(new XXXMirrors(versionRoot));
    }
    return ret;
  }

  @Override
  public URL getFor(Component tez, String componentVersion) throws Exception {
    String tarPart;
    if (tez == Component.hive) {
      tarPart = String.format("tars/%s/apache-%s-%s-bin.tar.gz", tez, tez, componentVersion);
    } else {
      //        tars/tez/tez-0.9.1.3.0.0.0-1634.tar.gz
      tarPart = String.format("tars/%s/%s-%s.tar.gz", tez, tez, componentVersion);

    }
    //        String tarPart = String.format("tars/%s/apache-%s-%s-bin.tar.gz", tez, tez, componentVersion);
    return new URL(baseUrl + tarPart);
  }

}