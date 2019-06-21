package hu.rxd.toolbox.qtest.diff;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedURL {

  Logger LOG = LoggerFactory.getLogger(CachedURL.class);

  private URL remoteUrl;
  private File cachedFile;
  private File tmpFile;

  public CachedURL(URL url) throws IOException {
    remoteUrl = url;
    
    String sha1 = DigestUtils.sha1Hex(remoteUrl.toString());
    String baseName = new File(url.getPath()).getName();
    String localFileName = "hu.rxd.tmp-" + sha1 + "." + baseName;
    cachedFile = new File(new File(System.getProperty(("java.io.tmpdir"))), localFileName);
    tmpFile = new File(new File(System.getProperty(("java.io.tmpdir"))), localFileName + ".tmp");
  }

  public URL getURL() throws Exception {
    if (!cachedFile.exists()) {
      download();
    }
    LOG.info("serving: {} for {}", cachedFile, remoteUrl);
    return cachedFile.toURI().toURL();
  }

  private void download() throws IOException {
    if (tmpFile.exists()) {
      tmpFile.delete();
    }

    LOG.info("downloading: {}", remoteUrl);
    try (OutputStream output = new FileOutputStream(tmpFile)) {
      try (InputStream input = remoteUrl.openStream()) {
        IOUtils.copy(input, output);
      }
    }
    LOG.info("downloaded: {}", remoteUrl);
    tmpFile.renameTo(cachedFile);
  }

}
