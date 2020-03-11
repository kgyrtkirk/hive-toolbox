#!/bin/bash

set -e
[ "$#" == "0" ] && echo -e "usage: $0 <component> [version]\n example:\n  $0 hive\n  $0 hive 7.0.0.0-251" && exit 1

echo "@@@ $0 $*"


LATEST_VERSION="`curl -s 'http://release.infra.cloudera.com/hwre-api/getreleaseversion?stack=CDH&releaseline=cdpd-master'|jq -r .version`"
COMPONENT="${1}"
VERSION="${2:-7.1.0.0}"
VERSION="${2:-${LATEST_VERSION}}"

tmp=`mktemp`
trap "unlink $tmp" EXIT

wget -nv -O $tmp "http://release.infra.cloudera.com/hwre-api/latestcompiledbuild?stack=CDH&release=${VERSION}&os=centos7"
build="`cat $tmp|jq -r .build`"
echo "@@@ build: $build"
patch_url="`cat $tmp | jq -r .centos7.patch_url`"
wget -nv -O $tmp "${patch_url}/${COMPONENT}-source.patch" ||
wget -nv -O $tmp "${patch_url}/dag_build/${COMPONENT}-source.patch"

git apply -p1 $tmp
sed "s/pig.version>0.16.0.*</pig.version>0.16.0.${build}</"  << EOF > pom.xml
$(cat pom.xml)
EOF

echo "@@@ patched"
