package hu.rxd.toolbox.switcher;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import hu.rxd.toolbox.qtest.diff.CachedURL;

public class HdpMirrors implements Mirrors {
  Logger LOG = LoggerFactory.getLogger(HdpMirrors.class);

  @Override
  public String decodeStackVersion(String version) {

    String u =
        String.format(
            "http://release.eng.hortonworks.com/hwre-api/latestcompiledbuild?stack=HDP&release=%s&os=centos7",
            version);
    try {

      Path path = new CachedURL(new URL(u), 600).getFile().toPath();
      ObjectMapper objectMapper = new ObjectMapper();
      HashMap myMap = objectMapper.readValue(path.toFile(), HashMap.class);
      String build = (String) myMap.get("build");
      if (build == null) {
        throw new NullPointerException("no build info in response");
      }
      if (!version.equals(build)) {
        throw new IllegalArgumentException(
            "You are shooting at a moving target! For consistency reasons; please call with the explicit version: "
                + build);
      }
      return build;
    } catch (Exception e) {
      throw new RuntimeException("Error while processing response of " + u, e);
    }
  }

  public static void main(String[] args) throws Exception {
    CDPMirrors mm = new CDPMirrors();
    String ver = mm.decodeStackVersion("3.1.4.8");
    System.out.println(ver);
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
