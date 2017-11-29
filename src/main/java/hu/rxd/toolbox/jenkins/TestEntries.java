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

package hu.rxd.toolbox.jenkins;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.rxd.toolbox.qtest.diff.CachedURL;

public class TestEntries {

  private List<TestEntry> entries;

  private TestEntries(List<TestEntry> entries) {
    this.entries = entries;
  }


  /**
   * example buildURL: http://j1:8080/job/tmp_kx_2/lastCompletedBuild/
   *
   * @param buildURL
   * @return
   * @throws Exception
   */
  public static TestEntries fromJenkinsBuild(String buildURL) throws Exception {
    URL u0 = new URL(buildURL + "/testReport/api/json?pretty=true&tree=suites[cases[className,name,duration,status]]");
    URL u = new CachedURL(u0).getURL();
    try (InputStream jsonStream = u.openStream()) {
      return new TestEntries(testEntries(parseTestResults(jsonStream)));
    }
  }

  public static void main(String[] args) throws Exception {
    String url = "https://builds.apache.org/job/PreCommit-HIVE-Build/8020/";
    TestEntries res = fromJenkinsBuild(url);
    TestEntries res2 = res.filterFailed().limit(400);
    res2.writeAsSimpleMavenTestPattern(System.out);
    System.out.println(res.entries.size());
    System.out.println(res2.entries.size());
  }

  public TestEntries filterFailed() {
    List<TestEntry> ret = new ArrayList<>();
    for (TestEntry entry : entries) {
      if (!entry.isPassed()) {
        ret.add(entry);
      }
    }
    return new TestEntries(ret);
  }

  public TestEntries limit(int max) {
    List<TestEntry> ret = new ArrayList<>();
    if (entries.size() > max) {
      throw new RuntimeException(String.format("is everything working fine? orig:%d max:%d", entries.size(), max));
//      System.err.printf("limiting test list from %d to contain %d elements", entries.size(), max);
    }
    ret.addAll(entries);
    return new TestEntries(ret);
  }

  public void writeAsSimpleMavenTestPattern(OutputStream os) {
    PrintStream ps = new PrintStream(os);
    for (TestEntry testEntry : entries) {
      ps.println(testEntry.getLabel());
    }
  }

  private static TestResults parseTestResults(InputStream jsonStream) throws IOException, JsonParseException, JsonMappingException {
    ObjectMapper mapper = new ObjectMapper();
    TestResults results = mapper.readValue(jsonStream, TestResults.class);
    return results;
  }

  private static List<TestEntry> testEntries(TestResults results) {
    List<TestEntry> entries = new ArrayList<TestEntry>();

    for (TestResults.Suite s : results.suites) {
      for (TestResults.Suite.Case c : s.cases) {
        entries.add(new TestEntry(c.className, c.name, c.duration, (c.status)));
      }
    }
    return entries;
  }

}
