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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.rxd.toolbox.qtest.diff.CachedURL;

public class TestEntries {

  /**
   * example buildURL: http://j1:8080/job/tmp_kx_2/lastCompletedBuild/
   *
   * @param buildURL
   * @return
   * @throws Exception
   */
  public static List<TestEntry> fromJenkinsBuild(String buildURL) throws Exception {
    URL u0 = new URL(buildURL + "/testReport/api/json?pretty=true&tree=suites[cases[className,name,duration,status]]");
    URL u = new CachedURL(u0).getURL();
    try (InputStream jsonStream = u.openStream()) {
      return testEntries(parseTestResults(jsonStream));
    }
  }

  private static TestResults parseTestResults(InputStream jsonStream) throws IOException, JsonParseException, JsonMappingException {
    ObjectMapper mapper = new ObjectMapper();
    TestResults results = mapper.readValue(jsonStream, TestResults.class);
    return results;
  }

  public static List<TestEntry> testEntries(TestResults results) {
    List<TestEntry> entries = new ArrayList<TestEntry>();

    for (TestResults.Suite s : results.suites) {
      for (TestResults.Suite.Case c : s.cases) {
        entries.add(new TestEntry(c.className, c.name, c.duration, (c.status)));
      }
    }
    return entries;
  }

  public static void main(String[] args) throws Exception {
    String url = "https://builds.apache.org/job/PreCommit-HIVE-Build/8020/";
    List<TestEntry> res = fromJenkinsBuild(url);
    System.out.println(res.size());
  }

}
