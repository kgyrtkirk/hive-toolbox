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
import java.util.function.Predicate;
import java.util.regex.Pattern;

import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class ZeroStatsDisappearClassifier implements Classifier {

  private String c = "";

  @Override
  public String getName() {
    return "zStats" + c;
  }

  @Override
  public boolean accept(DiffObject dio) {
    //      if (dio.getL().size() != 0) {
    //        return false;
    //      }
    if (dio.getL().size() != dio.getR().size()) {
      return false;
    }
    Predicate<String> p = Pattern.compile("^\\s*(numRows|rawDataSize)\\s+0\\s*$").asPredicate();
    Iterator<String> itL = dio.getL().iterator();
    for (String r : dio.getR()) {
      if (p.test(r)) {
        continue;
      }
      return false;
    }
    return true;
  }
}