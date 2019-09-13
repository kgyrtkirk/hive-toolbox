#!/bin/bash

set -e

echo "Executing@`hostname`: $0 $*"
if [ "$1" != "client" ] ; then
	[ "$1" == "" ] && echo "usage: $0 <host>" && exit 1

	scp "$0" "$1:"
	ssh -t "$1" "$0 client"
	exit 0
fi

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
if [ ! -d hive ]; then git clone https://github.com/hortonworks/hive ; fi
cd hive
git checkout HDP-3.1-maint
git reset --hard HDP-3.1.0.0-78-tag
wget -qO- "http://s3.amazonaws.com/dev.hortonworks.com/HDP/centos7/3.x/PATCH_FILES/3.1.0.0-78/patch_files/hive-source.patch" | git apply -3
../apache-maven-3.6.0/bin/mvn clean install source:jar -DskipTests -Pitests,hadoop-2 -Denforcer.skip=true
../apache-maven-3.6.0/bin/mvn eclipse:eclipse -DskipTests -Pitests,hadoop-2 -Denforcer.skip=true