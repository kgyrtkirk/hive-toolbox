#!/bin/bash

[ "$#" -eq 0 ] && echo "usage: $0 <files...>"  && exit 1

set -e
set -x

D=/tmp/FlameGraph
[ ! -d $D ] && git clone https://github.com/brendangregg/FlameGraph $D

#~/tools/FlameGraph/stackcollapse-jstack.pl --help
perl $D/stackcollapse-jstack.pl "$@" > /tmp/_jstack_collapsed

$D/flamegraph.pl /tmp/_jstack_collapsed > /tmp/_flames.svg
x-www-browser /tmp/_flames.svg
echo ok
