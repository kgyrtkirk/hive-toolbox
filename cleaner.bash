#!/bin/bash

cat $1 	|
	fgrep -Fvf <(echo -e 'pool.HikariPool:\nTotal time spent in each metastore function (ms)') |
	sed -E 's/[0-9]{4}-[0-9]{,2}-[0-9]{,2}T[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}/TIMESTAMP/g'	|
	sed -E 's/[0-9]{13,15}/EPOCH/g' |
	sed -E 's|tmp/tmp_[0-9]+/|tmp/tmp_XXXX/|g' |
	sed -E 's@([0-9]+.[0-9]+.[0-9]+.[0-9]+|localhost|savara.lan):[0-9]+@IP:PORT@g' |
	sed -E 's|port=[0-9]+|port=PORT|' |
	sed -E 's|[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|GUID|g' |
	sed -E 's@[pP]ort( |: | to: |=)[0-9]+@port: PORT@g' |
	sed -E 's@duration=[0-9]+@duration=DUR@g' |
	sed -E 's/@[0-9a-f]{6,8}/@OBJ_ADDR/g' |
	sed -E 's/[0-9]+.[0-9]+ seconds/X.X seconds/g' |
	sed -E 's/0x[0-9a-f]{15,16}/REF_64/g'
