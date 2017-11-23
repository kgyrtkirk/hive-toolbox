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

package hu.rxd.toolbox.qtest;

import java.io.InputStream;
import java.net.URL;
import java.util.function.Function;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public class ZipXL implements IInputStreamDispatcher {

  final URL url;

  public ZipXL(URL url) {
    this.url = url;
  }

  @Override
  public void visit(Function<InputStream, Void> function) throws Exception {
    try (InputStream gzStream = url.openStream()) {
      try (ZipArchiveInputStream archive = new ZipArchiveInputStream(gzStream)) {
        ArchiveEntry entry;
        while ((entry = archive.getNextEntry()) != null) {
          if (entry.isDirectory()) {
            continue;
          }
          String fileName = entry.getName();
          System.out.println(fileName);
          function.apply(archive);
        }
      }
    }
  }

}
