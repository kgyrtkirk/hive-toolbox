package hu.rxd.toolbox.switcher;

public class CDWHMirrors extends CDPMirrors {
  public static final String CDWH_ARTIFACTS = "http://cloudera-build-us-west-1.vpc.cloudera.com/s3/build/%s/cdwh/7.x/redhat7/yum/artifacts.txt";
  public static final String CDWH_RELEASES = "http://release.infra.cloudera.com/hwre-api/latestcompiledbuild?stack=CDWH&release=%s&os=centos7";

  @Override
  protected String getArtifacts() {
    return CDWH_ARTIFACTS;
  }

  @Override
  protected String getReleases() {
    return CDWH_RELEASES;
  }
}
