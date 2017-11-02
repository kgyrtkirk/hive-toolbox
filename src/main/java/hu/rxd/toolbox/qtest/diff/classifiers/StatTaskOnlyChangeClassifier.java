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

public class StatTaskOnlyChangeClassifier implements Classifier {

  @Override
  public String getName() {
    return "statTaskOnly";
  }

  @Override
  public boolean accept(DiffObject dio) {

    Iterator<String> lIter = dio.l.iterator();
    Iterator<String> rIter = dio.r.iterator();

    while(lIter.hasNext() && rIter.hasNext()) {
      if(!lIter.next().trim().equals("Stats Work")) {
        return false;
      }
      if(!rIter.next().trim().equals("Stats-Aggr Operator")) {
        return false;
      }
      if(!lIter.hasNext()) {
        return false;
      }
      if(!lIter.next().trim().equals("Basic Stats Work:")) {
        return false;
      }
    }
    return !lIter.hasNext() && !rIter.hasNext();
  }
}