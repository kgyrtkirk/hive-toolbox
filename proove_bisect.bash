#!/bin/bash
set -e
set -x

C=b8aa16ff6c2ec0185cd953a7854d6abda2306df7

M_OPTS+=" -Dorg.slf4j.simpleLogger.log.org.apache.maven.plugin.surefire.SurefirePlugin=INFO"
M_OPTS+=" -q -Dmaven.surefire.plugin.version=2.20.1"
M_OPTS+=" -Pitests -DskipSparkTests"
M_OPTS+=" -pl itests/qtest -am"
M_OPTS+=" -Dtest=TestCliDriver#testCliDriver[vector_complex_join]"
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
