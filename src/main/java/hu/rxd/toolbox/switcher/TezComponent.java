package hu.rxd.toolbox.switcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;

class TezComponent extends GenericComponent {

  @Override
  String getApacheMirrorPath(Version ver) throws Exception {
    String v = ver.getComponentVersion(Component.tez);
    return String.format("tez/%s/apache-tez-%s-bin.tar.gz", v, v);
  }

  @Override
  public Component getComponentType() {
    return Component.tez;
  }

  @Override
  protected void provideComponent(File targetPath, Version ver) throws Exception {
    switch (ver.type) {
    case HDP:
    case CDP:
      LOG.info("downloading: {}", ver);
      File f = downloadArtifact(getCandidateUrls(ver));
      File expandPath = new File(baseDir, targetPath.getName() + ".tmp");
      FileUtils.deleteDirectory(expandPath);
      File targetTgz = new File(expandPath, "/share/tez.tar.gz");
      FileUtils.forceMkdir(targetTgz.getParentFile());
      FileUtils.copyFile(f, targetTgz);
      expandPath.renameTo(targetPath);
      return;
    default:
      expand1DirReleaseArtifact(targetPath, downloadArtifact(getCandidateUrls(ver)));
    }
  }

  @Override
  protected File provideDevPath(Version ver) throws Exception {
    File targetPath = new File("/home/dev/hadoop");
    File packageDir = getMatchingPathForGlob(targetPath,
        "packaging/target/apache-invalid-not-yet-checked-*-SNAPSHOT-bin/apache-invalid-*-SNAPSHOT-bin");
    return packageDir;
  }

  public void postActivation() throws IOException {
    try {
      Files.copy(new File(linkDir + "/tez/share/tez.tar.gz").toPath(), new File("/apps/tez/tez.tar.gz").toPath(),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      throw new IOException("cant copy tez.tar.gz", e);
    }
  }

}