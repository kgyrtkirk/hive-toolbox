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

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class CachedURL {

  Logger LOG = LoggerFactory.getLogger(CachedURL.class);

  private URL remoteUrl;
  private File cachedFile;
  private File tmpFile;

  public CachedURL(URL url) throws IOException {
    remoteUrl = url;
    String sha1 = DigestUtils.sha1Hex(remoteUrl.toString());
    cachedFile = new File(new File(System.getProperty(("java.io.tmpdir"))), "hu.rxd.tmp-" + sha1);
    tmpFile = new File(new File(System.getProperty(("java.io.tmpdir"))), "hu.rxd.tmp-" + sha1 + ".tmp");
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
