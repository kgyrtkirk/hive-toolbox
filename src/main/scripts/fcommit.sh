#!/bin/bash
[ "$1" == "" ] && echo "usage: $0 <msg; like HIVE-13602>" && exit 1

for i in $(git log --pretty=format:'%H' --grep "$1\\b" --all)
do 
	PAGER= git log --pretty=format:"%cn %H %ci %s%n" $i -1
	git branch -r --contains $i
done | less -RS
