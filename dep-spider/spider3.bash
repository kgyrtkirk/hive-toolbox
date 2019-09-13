#!/bin/bash

BASE="http://savara:3200/artifactory/wonder/"
V1=3.1.0.0-31
V2=3.1.0.0-50

grep $V1 access.log |
grep "ACCEPTED DEPLOY"|
grep hwx-cache:|cut -d : -f4-|cut -d ' ' -f 1|
sed "s/$V1/$V2/g;s|^|$BASE|"
