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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.crypto.SecretKey;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.xerces.impl.dv.util.Base64;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import hu.rxd.toolbox.jenkins.TestEntries;
import hu.rxd.toolbox.jira.HiveTicket;
import hu.rxd.toolbox.qtest.IInputStreamDispatcher;
import hu.rxd.toolbox.qtest.LastQAReportInputStreamDispatcher;
import hu.rxd.toolbox.qtest.LocalFileDispatcher;
import hu.rxd.toolbox.qtest.LocalizedArchiveDispatcher;
import hu.rxd.toolbox.qtest.QTDiffRunner;

public class Toolbox {

  public static void main(String[] args) throws FileNotFoundException, Exception {

    if (args[0].equals("jceks-decode")) {
      String fileName = args[1];
      char[] password = "none".toCharArray();
      //      String alias = args[2];

      KeyStore ks = KeyStore.getInstance("JCEKS");
      try (FileInputStream fis = new FileInputStream(fileName)) {
        ks.load(fis, password);
        Enumeration<String> aa = ks.aliases();
        for (String alias : (List<String>) EnumerationUtils.toList(aa)) {
          SecretKey secretKey = (SecretKey) ks.getKey(alias, password);
          String secret = new String(secretKey.getEncoded());
          
          System.out.println("alias: " + alias);
          System.out.println("secret: " + secret);
          //          System.out.println(Base64.decode(secret));
//          System.out.println(new BigInteger(1, secretKey.getEncoded()).toString(16));
//          System.out.println(secretKey.getEncoded());
//          Key key = secretKey;
          
        }
      }
      return;
    }

    if (args[0].equals("reattach")) {
      TicketUtils.reattach(args[1]);
      return;
    }
    if (args[0].equals("upload")) {
      TicketUtils.upload();
      return;
    }

    if (args[0].startsWith("http")) {
      IInputStreamDispatcher isd = new LocalizedArchiveDispatcher(new URL(args[0]));
      new QTDiffRunner().withArgs(args).processTestXmls(isd);

      return;
    }

    if (args[0].startsWith("file://")) {
      IInputStreamDispatcher isd = new LocalFileDispatcher(new URL(args[0]));
      new QTDiffRunner().withArgs(args).processTestXmls(isd);

      return;
    }

    if (args[0].startsWith("HIVE")) {
      IInputStreamDispatcher isd = new LastQAReportInputStreamDispatcher(args[0]);
      new QTDiffRunner().withArgs(args).processTestXmls(isd);

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
      TestEntries res2 = res.filterFailed().limit(600);
      System.out.println(res2);
      String pat = res2.getSimpleMavenTestPattern();
      System.out.println("pat len:" + pat.length());
      try (PrintStream ps = new PrintStream(args[1])) {
        ps.println(pat);
      }
      return;
    }
    if (args[0].startsWith("diffapply")) {
      String url = args[1];
      TestEntries res = TestEntries.fromJenkinsBuild(url);
      TestEntries res2 = res.filterFailed().limit(600);
      System.out.println(res2);
      //      String pat = res2.getSimpleMavenTestPattern();
      //      System.out.println("pat len:" + pat.length());
      //      try (PrintStream ps = new PrintStream(args[1])) {
      //        ps.println(pat);
      //      }
      return;
    }
    //    if (args[0].equals("")) 
    if (args[0].startsWith("ticketScan")) {
      List<String> failedPats = TicketUtils.getFailed(new HiveTicket(args[2]));

      try (PrintStream ps = new PrintStream(args[1])) {
        String s = Joiner.on(",").join(failedPats);
        ps.println(s);
      }

    //      String ticket = args[2];
      //      HiveTicket
      //      .getMatchingTickets("assignee = kgyrtkirk and ")
      //          HiveTicket t = new HiveTicket(ticket);
    //      Comment c = t.getLastQAComment();
    //      System.out.println(c);
    //      String[] lines = c.getBody().split("\n");
    //
    //      //      TestEntries res2 = res.filterFailed().limit(600);
    //      //      System.out.println(res2);
    //      //      String pat = res2.getSimpleMavenTestPattern();
    //      //      System.out.println("pat len:" + pat.length());
    //      //      try (PrintStream ps = new PrintStream(args[1])) {
    //      //        ps.println(pat);
    //      //      }
      return;
    }
    //
    if (args[0].equals("applicator")) {
      Applicator applicator = new Applicator(new HiveTicket(args[1]));

      applicator.apply(new File("."));

      //      FileRepositoryBuilder builder = new FileRepositoryBuilder();
      return;
    }

    if (args[0].equals("reviewboard")) {

      new ReviewBoarder(new File("."));
      //      Applicator applicator = new Applicator(new HiveTicket(args[1]));

      //      FileRepositoryBuilder builder = new FileRepositoryBuilder();
      return;
    }

    //      tmp/jobL.properties tmp/jobR.properties "$QFILE_TARGET" "$QFILE_ALL"
    if(args[0].equals("QF_SPLIT")) {
      // FIXME: ugly crap!
      File outFileL = new File(args[1]);
      File outFileR = new File(args[2]);
      String ducks = args[3].trim();
      String victim = args[4].trim();

      String[] parts0 = ducks.split("[ ,\n]+");
      List<String> p = new ArrayList<>();
      p.addAll(Arrays.asList(parts0));
      while (p.contains("")) {
        p.remove("");
      }
      while (p.contains(victim)) {
        p.remove(victim);
      }

      if (p.size() < 2) {
        throw new RuntimeException("I guess we are finished |valid_ducks| < 2 ; " + p);
      }

      Random rnd = new Random(System.currentTimeMillis());
      List<String> outL = new ArrayList<>();
      List<String> outR = new ArrayList<>();
      for (String candidate : p) {
        boolean destL = rnd.nextBoolean();
        if (outL.isEmpty()) {
          destL = true;
        }
        if (outR.isEmpty()) {
          destL = false;
        }
        if (destL) {
          outL.add(candidate);
        } else {
          outR.add(candidate);
        }
      }

      outL.add(victim);
      outR.add(victim);

      try (PrintStream psL = new PrintStream(outFileL)) {
        psL.println(Joiner.on(",").join(outL));
      }
      try (PrintStream psR = new PrintStream(outFileR)) {
        psR.println(Joiner.on(",").join(outR));
      }
      return;
    }

    throw new RuntimeException("don't know what to do!?");
  }

}
