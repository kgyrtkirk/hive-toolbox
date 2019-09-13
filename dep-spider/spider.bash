#!/bin/bash

BASE="http://savara:3200/artifactory/wonder/"

cat ${1:-_deptree} | sed 's/.* //'|grep compile$|grep -F 3.0.3.0|sort|uniq|while read l;do
	#org.apache.hadoop:hadoop-yarn-server-applicationhistoryservice:jar:3.1.1.3.0.3.0-211:provided
	IFS=: read -ra parts <<< $l
	echo "$l ${#parts[@]}" >&2
	group=${parts[0]}
	id=${parts[1]}
	type=${parts[2]}
	versionPart=3
	if [ "${#parts[@]}" != 5 ];then
		versionPart=4
	fi
	version="`echo ${parts[versionPart]}|sed 's/3.0.3.0-211/3.1.0.0-31/'`"


	n="$BASE/`echo $group|tr '.' '/'`/$id/$version/$id-$version"
	echo "$n.pom"
	echo "$n.jar"
	echo "$n-tests.jar"
	echo "$n-sources.jar"
#	http://savara:3200/artifactory/wonder/org/apache/hadoop/
#hadoop-yarn-server-applicationhistoryservice/3.1.1.3.0.3.0-200/hadoop-yarn-server-applicationhistoryservice-3.1.1.3.0.3.0-200.pom
done
