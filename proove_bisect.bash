#!/bin/bash
set -e
set -x

C=4ec47a6e6ffb6a070295348308e3a8bcbc246190

M_OPTS+=" -Dorg.slf4j.simpleLogger.log.org.apache.maven.plugin.surefire.SurefirePlugin=INFO"
M_OPTS+=" -q -Dmaven.surefire.plugin.version=2.20.1"
M_OPTS+=" -Pitests -DskipSparkTests"
M_OPTS+=" -Dtest=TestRestrictedList"
M_OPTS+=" install"
export M_OPTS

git log -n1 $C

echo "* check good"
git clean -dfx
git checkout ${C}^
time mvn $M_OPTS

echo "* check bad"
git clean -dfx
git checkout ${C}
time mvn $M_OPTS

echo "*** I was wrong..."
