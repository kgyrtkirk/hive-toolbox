package hu.rxd.toolbox.switcher;

public class HdpMirrors2 implements Mirrors {

  @Override
  public String getComponentVersion(Version version, Component c) throws Exception {
    String artifacts =
        String.format("http://public-repo-1.hortonworks.com/HDP/centos7/3.x/updates/%s/artifacts.txt",
            version.stackVersion);
    return XXXMirrors2.determineComponentVerFromArtifactsTxt(artifacts, version, c);
  }


}
