#!/bin/bash

[ "$1" == "" ] && echo "err" && exit 1
set -e
wget -np -c -O /tmp/tr-$1.tgz http://104.198.109.242/logs/PreCommit-HIVE-Build-$1/test-results.tar.gz
D=/tmp/tr-$1/
mkdir -p "$D"
tar -xzf /tmp/tr-$1.tgz -C $D

~/projects/toolbox/build/install/toolbox/bin/toolbox $D/test-results/TEST-*xml
