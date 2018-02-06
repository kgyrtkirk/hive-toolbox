#!/bin/bash
set -e
#set -x

git clone github.com/apache/hive
cd hive

C=09ce6cdf3fd3682c2501ca0b5664cfc2adf6b765


M_OPTS="
-Dorg.slf4j.simpleLogger.log.org.apache.maven.plugin.surefire.SurefirePlugin=INFO
-q -Pitests
-DskipSparkTests
-Dtest=TestHiveCli
-pl beeline -am
install
"
export M_OPTS

git log -n1 $C

echo "@@@ check good"
git clean -dfx
git checkout ${C}^
time mvn $M_OPTS

echo "@@@ check bad"
git clean -dfx
git checkout ${C}
time mvn $M_OPTS

echo "@@@ I was wrong..."
