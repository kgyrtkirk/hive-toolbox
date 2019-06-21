#!/bin/bash

set -e

echo "Executing@`hostname`: $0 $*"
if [ "$1" != "client" ] ; then
	[ "$1" == "" ] && echo "usage: $0 <host>" && exit 1

	scp "$0" "$1:"
	ssh -t "$1" "$0 client"
	exit 0
fi

mkdir -p ~/.m2

cat >~/.m2/settings.xml <<EOL
<settings>
<localRepository/>
 <mirrors>
   <mirror>
     <id>public</id>
     <mirrorOf>*</mirrorOf>
     <url>http://nexus-private.hortonworks.com/nexus/content/groups/public</url>
   </mirror>
  </mirrors>
</settings>
EOL

cd /tools

[ ! -d hive ] && git clone https://github.infra.cloudera.com/CDH/hive
cd hive
git checkout cdpd-master
yum install -y jq

VERSION="`find /opt/cloudera/parcels/ -name hive-exec-*core.jar -printf "%f\n"|sort|uniq|cut -d- -f 3-4|cut -d. -f 4-`"

echo "VERSION: $VERSION"
patch_url="`curl -s "http://release.infra.cloudera.com/hwre-api/latestcompiledbuild?stack=CDH&release=${VERSION}&os=centos7" | jq -r .centos7.patch_url`"
wget $patch_url/hive-source.patch

git apply -3 -p1 hive-source.patch
git commit -m 'POM-PATCH' -a

MO="-Denforcer.skip -Pitests,hadoop-2"
mvn $MO install source:jar install -pl ql -DskipTests -am
mvn $MO eclipse:eclipse -pl ql -DskipTests

