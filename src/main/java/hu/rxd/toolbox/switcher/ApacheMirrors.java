package hu.rxd.toolbox.switcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApacheMirrors implements Mirrors {

  @Override
  public String getComponentVersion(Version version, Component c) {
    return version.getVerStr();
  }

  @Override
  public String decodeStackVersion(String version) {
    return version;
  }

  @Override
  public Collection<Mirror> of0(Version ver) {
    List<Mirror> ret = new ArrayList<Mirror>();
    ret.add(new ApacheMirror("http://xenia.sote.hu/ftp/mirrors/www.apache.org/"));
    ret.add(new ApacheMirror("https://archive.apache.org/dist/"));
    ret.add(new ApacheMirror("https://rxd.hu/misc/preview/"));
    return ret;
  }

  static class ApacheMirror implements Mirror {

    private String root;

    public ApacheMirror(String root) {
      this.root = root;
    }

    @Override
    public URL getFor(Component c, String componentVersion) throws Exception {
      String p = getPath(c, componentVersion);
      return new URL(root + "" + p);
    }

    private String getPath(Component c, String componentVersion) {
      String v = componentVersion;
      switch (c) {
      case hadoop:
        return String.format("hadoop/common/hadoop-%s/hadoop-%s.tar.gz", v, v);
      case hive:
        return String.format("hive/hive-%s/apache-hive-%s-bin.tar.gz", v, v);
      case tez:
        return String.format("tez/%s/apache-tez-%s-bin.tar.gz", v, v);
      default:
        throw new RuntimeException("unknown:" + c);
      }
    }

  }
}
