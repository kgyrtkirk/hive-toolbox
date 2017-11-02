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

public class StatsOnlyChangeClassifier implements Classifier {

    @Override
    public String getName() {
      return "statsOnly";
    }

    @Override
    public boolean accept(DiffObject dio) {
      if(dio.getL().size()!=dio.getR().size()) {
        return false;
      }

      Iterator<String> lIter = dio.getL().iterator();
      Iterator<String> rIter = dio.getR().iterator();

      while(lIter.hasNext()){
        String strL = lIter.next();
        String strR = rIter.next();

        if(classifyLine(strL).equals(classifyLine(strR))) {
          continue;
        }

        return false;
      }
      return true;
    }

    private String classifyLine(String string) {
      if (string.contains("aggregations: compute_stats")) {
        return "__AGGR_COMPUTE_STATS";
      }
      if (string.contains("Statistics: Num rows:")) {
        return "__STATISTICS";
      }
      if ((string.contains("expressions: ") || string.contains("columns.types")) && (string
          .contains(
              "struct<columntype:string,maxlength:bigint,sumlength:bigint,count:bigint,countnulls:bigint,bitvector")
          || string.contains("struct<columntype:string,min:bigint,max:bigint,")
          || string.contains("struct<columntype:string,min:double,max:double,")
          || string.contains(
              "struct<columntype:string,maxlength:bigint,avglength:double,countnulls:bigint,numdistinctvalues:bigint"))) {
        return "__TYPES";
      }
      if (string.contains("Stage-") && string.contains("depends on stages")) {
        return "__STAGE_DEPS";
      }

      if (string.matches("^ *(Map|Reducer) [0-9]+ <- (Map|Reducer) [0-9]+ \\(.*\\)$")) {
        return "__STAGE_DEPS_TEZ";
      }
//      Reducer 2 <- Map 1 (GROUP, 4)


      return string;
    }
  }