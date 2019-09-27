package hu.rxd.toolbox.switcher;

import java.io.File;
import java.io.IOException;

import hu.rxd.toolbox.HiveDevBoxSwitcher;

class HiveComponent extends GenericComponent {

  @Override
  String getApacheMirrorPath(Version ver) throws Exception {
    String v = ver.getComponentVersion(Component.hive);
    return String.format("hive/hive-%s/apache-hive-%s-bin.tar.gz", v, v);
  }

  @Override
  public Component getComponentType() {
    return Component.hive;
  }

  protected File ensurePresence(Version ver, String componentTargetDir) throws Exception {
    File targetPath = new File(baseDir, componentTargetDir);
    switch (ver.type) {
    case DEV:
      if (!targetPath.exists()) {
        throw new IOException(targetPath + " doesn't exists");
      }
      File packageDir = getMatchingPathForGlob(targetPath,
          "packaging/target/apache-hive-*-SNAPSHOT-bin/apache-hive-*-SNAPSHOT-bin");
      return packageDir;
    case APACHE:
    case HDP:
      if (!targetPath.exists()) {
        expand1DirReleaseArtifact(targetPath, downloadArtifact(getCandidateUrls(ver)));
      } else {
        LOG.info("{} is already present", componentTargetDir);
      }
      return targetPath;
    default:
      throw new RuntimeException("not handled case: " + ver.type);
    }
  }

  @Override
  protected void provideComponent(File targetPath, Version ver) throws Exception {
    expand1DirReleaseArtifact(targetPath, downloadArtifact(getCandidateUrls(ver)));
  }

  @Override
  protected File provideDevPath(Version ver) throws Exception {
    File targetPath = new File("/home/dev/hive");
    File packageDir = getMatchingPathForGlob(targetPath,
        "packaging/target/apache-hive-*-SNAPSHOT-bin/apache-hive-*-SNAPSHOT-bin");
    return packageDir;
  }

}