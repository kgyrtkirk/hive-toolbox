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

package hu.rxd.toolbox.qtest.diff.classifiers;

import java.util.Iterator;

import hu.rxd.toolbox.qtest.diff.DiffClassificator;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class StatsPCChangeClassifier implements Classifier {

  @Override
  public String getName() {
    return "statsPC";
  }

  @Override
  public boolean accept(DiffObject dio) {
    if (dio.getL().size() != dio.getR().size()) {
      return false;
    }

    Iterator<String> lIter = dio.getL().iterator();
    Iterator<String> rIter = dio.getR().iterator();

    while (lIter.hasNext()) {
      String strL = lIter.next().trim();
      String strR = rIter.next().trim();

      if (!(strL.startsWith("Statistics: Num rows:") && strR.startsWith("Statistics: Num rows:"))) {
        return false;
      }
      String[] pL = strL.split(":");
      String[] pR = strR.split(":");
      if (!(pL.length == pR.length && pL.length == 6)) {
        return false;
      }
      for (int i = 0; i < pL.length; i++) {
        if (i != 4 && !pL[i].equals(pR[i])) {
          return false;
        }
      }
      if (!(pR[4].trim().startsWith("COMPLETE") && pL[4].trim().startsWith("PARTIAL"))) {
        return false;
      }
    }
    return true;
  }
}