package hu.rxd.toolbox.switcher;

import java.io.File;

class HadoopComponent extends GenericComponent {

  @Override
  String getApacheMirrorPath(Version ver) throws Exception {
    String v = ver.getComponentVersion(Component.hadoop);
    return String.format("hadoop/common/hadoop-%s/hadoop-%s.tar.gz", v, v);
  }

  @Override
  public String getComponentName() {
    return "hadoop";
  }

  @Override
  protected Component getComponentType() {
    return Component.hadoop;
  }


  @Override
  protected void provideComponent(File targetPath, Version ver) throws Exception {
    expand1DirReleaseArtifact(targetPath, downloadArtifact(getCandidateUrls(ver)));
  }

  @Override
  protected File provideDevPath(Version ver) throws Exception {
    File targetPath = new File("/home/dev/hadoop");
    File packageDir = getMatchingPathForGlob(targetPath,
        "packaging/target/apache-invalid-not-yet-checked-*-SNAPSHOT-bin/apache-invalid-*-SNAPSHOT-bin");
    return packageDir;
  }

}