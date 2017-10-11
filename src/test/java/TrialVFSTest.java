

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileFilterSelector;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.junit.Test;

import de.schlichtherle.truezip.file.TFile;

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

public class TrialVFSTest {

  @Test
  public void v1() throws Exception {
    // Locate the Jar file
    FileSystemManager fsManager = VFS.getManager();
    //    fsManager
    String tgz = "tgz:file:///tmp/a.tar.gz";
//    String tgz = "tgz:file:///tmp/a.tar.gz";
    //    String tgz = "tgz:http://104.198.109.242/logs/PreCommit-HIVE-Build-7213/test-results.tar.gz";
    FileObject jarFile = fsManager.resolveFile(tgz);
    // List the children of the Jar file
    System.out.println(jarFile);

    FileFilter fileFilter = new FileFilter() {

      @Override
      public boolean accept(FileSelectInfo arg0) {
        FileName a = arg0.getFile().getName();
        String bn = a.getBaseName();
        String ext = a.getExtension();
        return true || bn.startsWith("TEST-") && "xml".equals(ext);
      }
    };

    FileObject[] children = jarFile.getChildren();
    System.out.println("Children of " + jarFile.getName().getURI());
    for (int i = 0; i < children.length; i++) {
      System.out.println(children[i].getName().getBaseName());
    }
    List<FileObject> ll=new ArrayList<>();
    jarFile.findFiles(new FileFilterSelector(fileFilter), true, ll);
    for (FileObject a : ll) {
      System.out.println("X" + a);
    }

    System.out.println("asd");
  }

  @Test
  public void tt1() {
    //    TPath dir = new TPath("archive.zip/dir");
    TFile fd = new TFile("file:///tmp/a.tar.gz");
    String[] a = fd.list();
    System.out.println(a);

    //    File dir = new TFile("archive.zip/dir");
    //    dir.mkdirs(); // don't do this!
  }

}
