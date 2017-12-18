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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import hu.rxd.toolbox.jira.HiveTicket;

public class AwaitCommit {
  static Logger LOG = LoggerFactory.getLogger(AwaitCommit.class);

  public static void main(String[] args) throws Exception {
    long t0 = System.currentTimeMillis();
    List<HiveTicket> candidates =
        //        HiveTicket.getMatchingTickets("project = HIVE AND status = 'Patch Available' and updatedDate > -7d ORDER BY updatedDate DESC");
        HiveTicket.getMatchingTickets(
            "project = HIVE AND status = 'Patch Available' and updatedDate > -7d ORDER BY updatedDate DESC");

    long t1 = System.currentTimeMillis();
    System.out.println(candidates.size());

    List<String> filterParts = new ArrayList<>();
    for (HiveTicket hiveTicket : candidates) {
      //      System.out.println(hiveTicket);
      if (hiveTicket.canBeSubmitted()) {
        filterParts.add(hiveTicket.getIssue().getKey());
        //        System.out.println(hiveTicket);
      } else {
        LOG.info("not ready for submission: {}", hiveTicket);
      }
    }

    System.out.println("Submission candidates:");
    System.out.println("key in " + Joiner.on(",").join(filterParts));

  }

}
