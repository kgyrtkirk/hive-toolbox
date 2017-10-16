import java.io.InputStream;
import java.net.URL;
import java.util.function.Function;

import org.junit.Test;

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

public class fdsgfsd {

  @Test
  public void tC() throws Exception {
    new CachedURL(new URL("http://104.198.109.242/logs/PreCommit-HIVE-Build-7247/test-results.tar.gz"));
  }

  @Test
  public void aa() throws Exception {
    TarGzXL aa = new TarGzXL(new URL("file:///tmp/a.tar.gz"));
    //    TarGzXL aa = new TarGzXL();
    aa.visit(new Fn());

  }

  class Fn implements Function<InputStream, Void> {

    @Override
    public Void apply(InputStream t) {
      //      System.out.println(t);
      return null;
    }

  }
}
