package hu.rxd.toolbox.switcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class HdpMirrors implements Mirrors {

  @Override
  public String getComponentVersion(Version version, Component c) throws Exception {
    String artifacts =
        String.format("http://private-repo-1.hortonworks.com/HDP/centos7/3.x/updates/%s/artifacts.txt",
            //              String.format("http://public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/%s/artifacts.txt",
            version.stackVersion);
    return CDPMirrors.determineComponentVerFromArtifactsTxt(artifacts, version, c);
  }

  private static final List<String> MIRROR_ROOTS =
      Lists.newArrayList("http://public-repo-1.hortonworks.com/HDP",
          "http://private-repo-1.hortonworks.com/HDP");

  public static Collection<Mirror> of(Version ver) {
    List<Mirror> ret = new ArrayList<>();
    for (String root : MIRROR_ROOTS) {
      String versionRoot =
          String.format("%s/centos7/%s.x/updates/%s/", root, ver.stackVersion.substring(0, 1), ver.stackVersion);
      ret.add(new HdpMirror(versionRoot));
    }
    return ret;
  }

}
