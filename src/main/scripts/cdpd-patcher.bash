#!/bin/bash

set -e
[ "$#" == "0" ] && echo -e "usage: $0 <component> [version]\n example:\n  $0 hive\n  $0 hive 7.0.0.0-251" && exit 1

echo "@@@ $0 $*"


COMPONENT="${1}"
VERSION="${2//CDH-/}"
case "$VERSION" in
  cdpd-master|"")
    VERSION="`curl -s 'http://release.infra.cloudera.com/hwre-api/getreleaseversion?stack=CDH&releaseline=cdpd-master'|jq -r .version`"
    ;;
  FENG)
    VERSION=7.0.2.1
    ;;
  *)
esac

echo "@@@ version: $VERSION"

tmp=`mktemp`
trap "unlink $tmp" EXIT

wget -nv -O $tmp "http://release.infra.cloudera.com/hwre-api/latestcompiledbuild?stack=CDH&release=${VERSION}&os=centos7"
build="`cat $tmp|jq -r .build`"
echo "@@@ build: $build"
patch_url="`cat $tmp | jq -r .centos7.patch_url`"
wget -nv -O $tmp "${patch_url}/${COMPONENT}-source.patch" ||
wget -nv -O $tmp "${patch_url}/dag_build/${COMPONENT}-source.patch"

git apply -p1 $tmp
if [[ "$build" < "7.2.1" ]];then
	sed -i "s/pig.version>0.16.0.*</pig.version>0.16.0.${build}</" pom.xml
fi

echo "@@@ patched"
