#!/bin/bash

set -e

HASH=09ce6cdf3fd3682c2501ca0b5664cfc2adf6b765
M_OPTS="-pl beeline -Dtest=TestHiveCli -q"

git clone github.com/apache/hive
cd hive
echo "@@@ BEFORE"
git checkout ${HASH}^
mvn install $M_OPTS

git clean -dfx

echo "@@@ AFTER"
git checkout ${HASH}
mvn install $M_OPTS
