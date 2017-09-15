#!/bin/bash

set -e
#cat e2.p | patch -f  -s --dry-run  -R ql/src/test/results/clientpositive/spark/union_lateralview.q.out ;echo $?

ACCEPT_CAT="$1"
function process(){
	CAT="$1"
	FILE="$2"
	PATCH_OPTS="$3"
	DIFF_FILE="$4"
	

	if [ "$CAT" == "$ACCEPT_CAT" ];then
		echo " * checking if it applies... $FILE"
		if  patch -f  -s --dry-run  $PATCH_OPTS "$FILE" < "$DIFF_FILE" ;then
#		(echo "SHOWING: $FILE" ; cat "$DIFF_FILE") | less
#		echo "patch? (y to apply):"
#		read
#		if [ "$REPLY" = "y" ];then
				patch -f $PATCH_OPTS "$FILE" < "$DIFF_FILE"
#		fi
		else
			echo "WWW skipping"
		fi
	else
		echo "--- skip $FILE ($CAT)"
	fi
}

K=
function rerun(){
	if [ "$K" != "" ];then
		K="$K,$1"
	else
		K="$1"
	fi
	echo "time mvn install -Dmaven.surefire.plugin.version=2.19.1 -Pitests -q '-Dtest=$K'"
}


#===
##





