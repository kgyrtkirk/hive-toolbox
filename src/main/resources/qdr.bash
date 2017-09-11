#!/bin/bash

set -e
#cat e2.p | patch -f  -s --dry-run  -R ql/src/test/results/clientpositive/spark/union_lateralview.q.out ;echo $?

function process(){

	echo " * checking if it applies... $1"
	if  patch -f  -s --dry-run  -R "$1" < "$2" ;then
		(echo "SHOWING: $1" ; cat "$2") | less
		echo "patch? (y to apply):"
		read
		if [ "$REPLY" = "y" ];then
			patch -f -R "$1" < "$2"
		fi
	else
		echo "WWW skipping"
	fi
}



#===
##





