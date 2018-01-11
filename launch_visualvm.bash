#!/bin/bash

set -e

yum install -y xterm fluxbox tigervnc-server icewm xclock nano 

vncserver -kill :1 || echo ok
mkdir -p $HOME/.vnc
echo 'exec fluxbox' > $HOME/.vnc/xstartup
#xinitrc
vncpasswd -f > $HOME/.vnc/passwd <<< alskdj
vncserver :1 -depth 24 -geometry 1920x1080

export DISPLAY=:1
DSIAPLY=:1 xhost +localhost

mkdir -p /tools
cd /tools
if [ ! -d visualvm_14 ];then
	wget -c https://github.com/visualvm/visualvm.src/releases/download/1.4/visualvm_14.zip
	unzip visualvm_14.zip
fi

chown hive:hive /var/lib/hive/
DISPLAY=:1 sudo -u hive visualvm_14/bin/visualvm &
