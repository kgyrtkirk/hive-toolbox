#!/bin/bash

cat $1 	| tr -cd '[:print:]\n' |
	fgrep -Fvf <(echo -e 'pool.HikariPool:\nTotal time spent in each metastore function (ms)') |
	sed -E 's/[0-9]{4}-[0-9]{,2}-[0-9]{,2}T[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}/TIMESTAMP/g'	|
	sed -E 's/[0-9]{4}-[0-9]{,2}-[0-9]{,2}_[0-9]{2}-[0-9]{2}-[0-9]{2}_[0-9]+_[0-9]+/ALLTIMER/g'	|
	sed -E 's|tmp/tmp_[0-9]+/|tmp/tmp_XXXX/|g' |
	sed -E 's@([0-9]+.[0-9]+.[0-9]+.[0-9]+|localhost|savara.lan):[0-9]+@IP:PORT@g' |
	sed -E 's|port=[0-9]+|port=PORT|' |
	sed -E 's|[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|GUID|g' |
	sed -E 's@[pP]ort( |: | to: |=)[0-9]+@port: PORT@g' |
	sed -E 's@duration=[0-9]+@duration=DUR@g' |
	sed -E 's/@[0-9a-f]{6,10}/@OBJ_ADDR/g' |
	sed -E 's/[0-9]+.[0-9]+ seconds/X.X seconds/g' |
	sed -E 's/IPC Server handler [0-9] on [0-9]+/IPC Server handler X on XXX/g' |
	sed -E 's/Thread-[0-9]+/Thread-XXXX/g' |
	sed -E 's/started at [0-9]+/started at XXXXX/g' |
	sed -E 's/DFSClient_NONMAPREDUCE_[0-9\\-]+_/DFSClient_NONMAPREDUCE_XXXX_/g' |
	sed -E 's/[0-9]{13,15}/EPOCH/g' |
	sed -E 's/[0-9]{10}/EpOCH/g' |
	sed -E 's/\((X|SESSION)ID = [0-9]+\)/(\1ID = XXX)/g' |
	sed -E 's/0x[0-9a-f]{15,16}/REF_64/g'

#TezTR-274858_1_13_1_0_0
