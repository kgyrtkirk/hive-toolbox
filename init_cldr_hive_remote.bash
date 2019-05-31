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
<!-- insert CLDR specific maven settings here ###TODO###" -->
</settings>
EOL

cd /tools
# clone cldr repo here ###TODO###

# cd hive

# checkout your favorite branch here ###TODO###

# reset to your favorite revision here ###TODO###

# patch or whatever you need here ###TODO###

# build hive, one example is below (legacy hwx) ###TODO###
#../apache-maven-3.6.0/bin/mvn clean install source:jar eclipse:eclipse -DskipTests -Pitests,hadoop-2 -Denforcer.skip=true