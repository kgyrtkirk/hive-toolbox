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

package hu.rxd.toolbox;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;

import hu.rxd.toolbox.jenkins.TestEntries;
import hu.rxd.toolbox.qtest.IInputStreamDispatcher;
import hu.rxd.toolbox.qtest.LastQAReportInputStreamDispatcher;
import hu.rxd.toolbox.qtest.LocalizedZipDispatcher;
import hu.rxd.toolbox.qtest.QTDiffRunner;

public class Toolbox {

  public static void main(String[] args) throws FileNotFoundException, Exception {

    if (args[0].startsWith("http")) {
      IInputStreamDispatcher isd = new LocalizedZipDispatcher(new URL(args[0]));
      QTDiffRunner.processTestXmls(isd);

      return;
    }

    if (args[0].startsWith("HIVE")) {
      IInputStreamDispatcher isd = new LastQAReportInputStreamDispatcher(args[0]);
      QTDiffRunner.processTestXmls(isd);

      return;
    }
    if (args[0].startsWith("U")) {
      JenkinBumper jb = new JenkinBumper();
      jb.bump();

      return;
    }

    if (args[0].startsWith("RERUN")) {
      String url = args[2];
      TestEntries res = TestEntries.fromJenkinsBuild(url);
      TestEntries res2 = res.filterFailed().limit(400);
      System.out.println(res2);
      String pat = res2.getSimpleMavenTestPattern();
      System.out.println("pat len:" + pat.length());
      try (PrintStream ps = new PrintStream(args[1])) {
        ps.println(pat);
      }
      return;
    }

    throw new RuntimeException("don't know what to do!?");
  }

}
