#!/bin/bash

cat > /tmp/__a << EOF
org.apache.hadoop.hive.cli.TestBlobstoreCliDriver.testCliDriver[orc_nonstd_partitions_loc] (batchId=242)
org.apache.hadoop.hive.cli.TestBlobstoreCliDriver.testCliDriver[rcfile_nonstd_partitions_loc] (batchId=242)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[authorization_parts] (batchId=46)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[autoColumnStats_3] (batchId=53)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[autoColumnStats_5] (batchId=40)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[columnstats_infinity] (batchId=73)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[insert_values_orig_table_use_metadata] (batchId=61)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[list_bucket_query_oneskew_1] (batchId=57)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[list_bucket_query_oneskew_2] (batchId=3)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[list_bucket_query_oneskew_3] (batchId=9)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[recursive_dir] (batchId=49)
org.apache.hadoop.hive.cli.TestCliDriver.testCliDriver[stats_noscan_2] (batchId=35)
org.apache.hadoop.hive.cli.TestMiniLlapCliDriver.testCliDriver[external_table_with_space_in_location_path] (batchId=144)
org.apache.hadoop.hive.cli.TestMiniLlapCliDriver.testCliDriver[schemeAuthority] (batchId=143)
org.apache.hadoop.hive.cli.TestMiniLlapLocalCliDriver.testCliDriver[autoColumnStats_1] (batchId=149)
org.apache.hadoop.hive.cli.TestMiniLlapLocalCliDriver.testCliDriver[autoColumnStats_2] (batchId=162)
org.apache.hadoop.hive.cli.TestMiniSparkOnYarnCliDriver.testCliDriver[external_table_with_space_in_location_path] (batchId=171)
org.apache.hadoop.hive.cli.TestMiniSparkOnYarnCliDriver.testCliDriver[schemeAuthority] (batchId=170)
org.apache.hadoop.hive.cli.TestMiniTezCliDriver.testCliDriver[explainanalyze_5] (batchId=99)
org.apache.hadoop.hive.cli.TestPerfCliDriver.testCliDriver[query23] (batchId=234)
org.apache.hadoop.hive.cli.TestSparkCliDriver.testCliDriver[stats_noscan_2] (batchId=116)
org.apache.hadoop.hive.ql.lockmgr.TestDbTxnManager2.testShowLocksAgentInfo (batchId=282)
EOF

F=${1:-/tmp/__a}

cat ${F:-} | grep CliDriver | sed -E 's/.*(Test[^\.]+)\..+\[(.+)\].*/\1	\2.q/' > /tmp/__b
cat /tmp/__b | cut -f 1 | sort | uniq | while read d;do
	q="`grep $d /tmp/__b | cut -f 2 |paste -d, -s`"
	echo " mvn install -Pitests -pl itests/qtest -T9 -q -Dtest=$d -Dqfile=$q -Dtest.output.overwrite"
done
