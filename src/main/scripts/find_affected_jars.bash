#!/bin/bash -e

[ "$1" == "" ] && echo "usage: $0 <commitish>" && exit 1
c="$1"

find packaging/ -name 'hive*jar' -exec unzip -t "{}" \;  > _contents


git diff "$c"|grep '^+++'|
	cut -c 5-|
	sed 's/java$/class/'|
	grep 'class$'|
	sed -r 's|^(.*/)|/|'  > pats


fgrep -f <(cat pats;echo Archive:) _contents |fgrep -B1 -f pats
echo -e "\n --- \n"

fgrep -f <(cat pats;echo Archive:) _contents |fgrep -B1 -f pats|grep Archive

