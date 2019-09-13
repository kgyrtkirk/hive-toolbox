import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class AX1 {


  private PrintStream dotOut;

  public AX1() throws Exception {

    dotOut = new PrintStream(new FileOutputStream("/tmp/out.dot"));
  }

  public static void main(String[] args) throws Exception {
    String input = "  Stage: Stage-1\n" +
        "    Tez\n" +
        "#### A masked pattern was here ####\n" +
        "      Edges:\n" +
        "        Map 6 <- Reducer 12 (BROADCAST_EDGE), Reducer 14 (BROADCAST_EDGE)\n" +
        "        Reducer 10 <- Reducer 9 (SIMPLE_EDGE)\n" +
        "        Reducer 12 <- Map 11 (CUSTOM_SIMPLE_EDGE)\n" +
        "        Reducer 14 <- Map 13 (CUSTOM_SIMPLE_EDGE)\n" +
        "        Reducer 2 <- Map 1 (SIMPLE_EDGE)\n" +
        "        Reducer 3 <- Reducer 10 (ONE_TO_ONE_EDGE), Reducer 2 (SIMPLE_EDGE)\n" +
        "        Reducer 4 <- Reducer 3 (SIMPLE_EDGE)\n" +
        "        Reducer 5 <- Reducer 4 (SIMPLE_EDGE)\n" +
        "        Reducer 7 <- Map 11 (SIMPLE_EDGE), Map 6 (SIMPLE_EDGE)\n" +
        "        Reducer 8 <- Map 13 (SIMPLE_EDGE), Reducer 7 (SIMPLE_EDGE)\n" +
        "        Reducer 9 <- Map 15 (SIMPLE_EDGE), Reducer 8 (SIMPLE_EDGE)\n" +
        "#### A masked pattern was here ####\n" +
        "      Vertices:\n" +
        "        Map 1 \n" +
        "            Map Operator Tree:\n" +
        "                TableScan\n" +
        "                  alias: item\n" +
        "" +
        "";

    String[] lines = input.split("\\r?\\n");
    List<String> lines2 = Files.readLines(
        //        new File("/mnt/data/tmp/BUG-111272-q7-q49/query7.transposeon.exp"),
        //        new File("/mnt/data/tmp/BUG-111272-q7-q49/query7.transposeoff.exp"),
        //        new File("/home/kirk/projects/hive/ws/ql/src/test/results/clientpositive/perf/tez/query7.q.out"),
//        new File("/home/kirk/projects/hive/ws/ql/src/test/results/clientpositive/perf/tez/query7o.q.out"),
        new File(
            "/home/kirk/projects/hive/HIVE-14431-case-opener/ql/src/test/results/clientpositive/perf/tez/query78.q.out"),
        Charset.defaultCharset());

    AX1 o = new AX1();

    for (String string : lines2) {
      o.visit(string.replaceAll("\\|", ""));
    }


  }
  enum Mode {
    EDGES, VERTICES;
  };

  Mode mode;
  private String currentVertex;

  private void visit(String string) {
    string = string.trim();
    if (string.matches("Edges:")) {
      mode = Mode.EDGES;
      return;
    }
    if (string.matches("Vertices:")) {
      mode = Mode.VERTICES;
      return;
    }
    if (mode == null) {
      return;
    }

    switch (mode) {
    case EDGES:
      visitEdge(string);
      break;
    case VERTICES:
      visitVertex(string);
      break;
    default:
      break;
    }

    System.out.println(mode);
  }

  private void visitEdge(String string) {
    string=string.trim();
    if (string.matches("(Map|Reducer).*")) {
      String[] p0 = string.split("<-");
      System.out.println(string);
      System.out.println(p0[0]);
      String[] p1 = p0[1].split(",");

      for (String sLeft : p1) {
        String[] lP = sLeft.replaceAll("\\)", "").split("\\(");
        lP[1] = lP[1].replaceAll("BROADCAST", "B").replaceAll("SIMPLE", "S").replaceAll("CUSTOM", "C")
            .replaceAll("_EDGE", "E");
        System.out.printf("%s -> %s[label=%s];", p0[0].trim().replaceAll(" ", "_"), lP[0].trim().replaceAll(" ", "_"),
            lP[1]);
        dotOut.printf("%s -> %s[label=%s];\n", lP[0].trim().replaceAll(" ", "_"), p0[0].trim().replaceAll(" ", "_"),
            lP[1]);

      }
    }
  }

  private void visitVertex(String string) {
    string = string.trim();
    if (string.matches("(Map|Reducer) [0-9]+")) {
      currentVertex = string.trim().replaceAll(" ", "_");
      System.out.println("vertex: " + currentVertex);
    }
    Pattern aliasPat = Pattern.compile("alias: (.*)");
    Matcher aliasMatcher = aliasPat.matcher(string);
    if (aliasMatcher.matches()) {
      String alias = aliasMatcher.group(1);
      System.out.println(currentVertex + " scans " + alias);

      dotOut.printf("%s[color=red,label=%s];\n", currentVertex,
          currentVertex + "__" + alias);

    }

  }

}
