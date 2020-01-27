#!/bin/bash

grep '+++' |grep q.out$|sed 's|.*/||;s|.q.out$||'|sed -r 's|(.*)|Test*CliDriver#*[\1]|'|sort -u|paste -s -d,

