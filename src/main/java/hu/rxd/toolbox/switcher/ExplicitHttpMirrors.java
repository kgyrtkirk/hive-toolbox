/*
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

package hu.rxd.toolbox.switcher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExplicitHttpMirrors implements Mirrors {

  @Override
  public String getComponentVersion(Version version, Component c) {
    return version.getVerStr();
  }

  @Override
  public String decodeStackVersion(String url) {
    //    http://ci.hive.apache.org/job/hive-nightly/4/artifact/archive/apache-hive-4.0.0-nightly-dd23fa9147-20220210_160351-bin.tar.gz
    //FIXME: possibly generalize later
    Pattern pat = Pattern.compile("http.*/apache-hive-(.*)-bin\\.tar\\.gz");
    Matcher m = pat.matcher(url);
    if (!m.matches()) {
      throw new RuntimeException("unsupported url: " + url);
    }
    return m.group(1);
  }

  @Override
  public Collection<Mirror> of0(Version ver) {
    List<Mirror> ret = new ArrayList<Mirror>();
    ret.add(new ExplicitMirror(ver.getUrl()));
    return ret;
  }

  static class ExplicitMirror implements Mirror {

    private String root;

    public ExplicitMirror(String root) {
      this.root = root;
    }

    @Override
    public URL getFor(Component c, String componentVersion) throws Exception {
      return new URL(root);
    }
  }

}
