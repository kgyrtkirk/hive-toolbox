package hu.rxd.toolbox.switcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class HdpMirrors implements Mirrors {
  Logger LOG = LoggerFactory.getLogger(HdpMirrors.class);

  @Override
  public String decodeStackVersion(String version) {
    //    x=
    String u =
        String.format("http://release.infra.cloudera.com/hwre-api/latestcompiledbuild?stack=CDH&release=%s&os=centos7",
        version);

    throw new RuntimeException("unimpl");
  }

  public static void main(String[] args) {
    HdpMirrors mm = new HdpMirrors();
    mm.decodeStackVersion("3.1.4.8");
  }

  @Override
  public String getComponentVersion(Version version, Component c) throws Exception {
    String stackVersion = version.stackVersion;
    String stackChar = stackVersion.substring(0, 1);

    for (String mirrorRoot : MIRROR_ROOTS) {
      try {
        String artifacts =
            String.format("%s/centos7/%s.x/updates/%s/artifacts.txt", mirrorRoot, stackChar, stackVersion);
        return CDPMirrors.determineComponentVerFromArtifactsTxt(artifacts, version, c);
      } catch (Exception e) {
        LOG.warn("unable to download from: " + mirrorRoot, e);
      }
    }

    throw new RuntimeException("unable to deteminecomponentversion for " + version);
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
