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

package hu.rxd.toolbox.qtest.diff.classifiers;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

import hu.rxd.toolbox.qtest.diff.DiffClassificator.Classifier;
import hu.rxd.toolbox.qtest.diff.DiffClassificator.DiffObject;

public class PatternMatchClassifier implements Classifier {

  private String name;
  private String regexp;

  public PatternMatchClassifier(String name, String regexp) {
    this.name = name;
    this.regexp = regexp;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean accept(DiffObject dio) {
    for (List<String> input : Lists.newArrayList(dio.l, dio.r)) {
      Iterator<String> lIter = input.iterator();
      while (lIter.hasNext()) {
        String line = lIter.next();
        if (!line.matches(regexp)) {
          return false;
        }

      }
    }
    return true;
  }

}
