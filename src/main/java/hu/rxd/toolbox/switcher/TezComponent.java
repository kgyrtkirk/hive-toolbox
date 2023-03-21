package hu.rxd.toolbox.switcher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

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
    File expandPath = new File(baseDir, targetPath.getName() + ".tmp");
    FileUtils.deleteDirectory(expandPath);
    switch (ver.type) {
    case HDP:
    case CDP:
    case CDWH:
      LOG.info("downloading: {}", ver);
      // simulate the tez "release" process
      // expand "minimal" as root 
      File minimal = downloadArtifact(addMinimalClassifier(getCandidateUrls(ver)));
      Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
      LOG.info("extracting: {}", minimal.getName());
      archiver.extract(minimal, expandPath);

      // place the "full" version under share
      File f = downloadArtifact(getCandidateUrls(ver));
      File targetTgz = new File(expandPath, "/share/tez.tar.gz");
      FileUtils.forceMkdir(targetTgz.getParentFile());
      FileUtils.copyFile(f, targetTgz);
      break;
    default:
      expand1DirReleaseArtifact(expandPath, downloadArtifact(getCandidateUrls(ver)));

    }
    expandPath.renameTo(targetPath);
  }

  private List<URL> addMinimalClassifier(List<URL> candidateUrls) throws Exception {
    List<URL> ret=new ArrayList<URL>();
    for (URL url : candidateUrls) {
      ret.add(new URL(url.toString().replaceAll(".tar.gz", "-minimal.tar.gz")));
    }
    return ret;
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