#!/bin/bash

set -e

echo "Executing@`hostname`: $0 $*"
if [ "$1" != "client" ] ; then
	[ "$1" == "" ] && echo "usage: $0 <host>" && exit 1
	scp "$0" "$1:"
	ssh "$1" "$0 client"
	echo "** vnc into $1:1 with alskdj"
	exit 0
fi

sed -ir '/alias (rm|cp|mv)=.*/d' ~/.bashrc

yum install -y xterm fluxbox tigervnc-server icewm xclock nano make wget epel-release gcc sysstat tcpdump nmap strace deltarpm xwininfo banner git tree

if yum list installed nux-dextop-release; then
	echo nux-ok
else
	rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro
	rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-5.el7.nux.noarch.rpm
fi

yum install -y ffmpeg ffmpeg-devel mplayer wmctrl

grep -n '^###>' "$0" | while read l;do
	f="${l//*>}"
	n="${l//:*}"
	tail -n +$[ $n + 1 ] $0 > $f
	chmod +x $f
done

mkdir -p /tools
cd /tools
if [ ! -d visualvm_14 ];then
	wget -c -nv https://github.com/visualvm/visualvm.src/releases/download/1.4/visualvm_14.zip
	unzip visualvm_14.zip
fi

if [ ! -d mat ];then
	#wget -c -nv -O mat_17.zip  'http://www.eclipse.org/downloads/download.php?file=/mat/1.7/rcp/MemoryAnalyzer-1.7.0.20170613-linux.gtk.x86_64.zip&r=1' 
	#unzip mat_17.zip
	wget -c -nv -O mat_181.zip  'https://www.eclipse.org/downloads/download.php?file=/mat/1.8.1/rcp/MemoryAnalyzer-1.8.1.20180910-linux.gtk.x86_64.zip&mirror_id=1045'
	unzip mat_181.zip
fi

if [ ! -d maven ];then
	wget -c -nv -O maven.tgz https://www-us.apache.org/dist/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz
	tar xzf maven.tgz
	ln -s apache-maven-3.6.0/bin maven
fi

if [ ! -d eclipse ];then
	wget -c -nv -O eclipse.tgz http://ftp.halifax.rwth-aachen.de/eclipse//technology/epp/downloads/release/2018-09/R/eclipse-java-2018-09-linux-gtk-x86_64.tar.gz
	tar xzf eclipse.tgz
fi

vncserver -kill :1 || echo ok
mkdir -p $HOME/.vnc
cat > $HOME/.vnc/xstartup << EOF
sudo -u hive xterm +sb -bg black -fg white -geometry 140x50 -xrm 'XTerm.vt100.allowTitleOps: false' -T cap &
sleep 1
xterm -geometry 140x50 &
sudo -u hive /tools/visualvm_14/bin/visualvm &
exec fluxbox
EOF
chmod +x $HOME/.vnc/xstartup
#xinitrc
vncpasswd -f > $HOME/.vnc/passwd <<< alskdj
chmod 600 $HOME/.vnc/passwd


vncserver :1 -depth 24 -geometry 1920x1080

export DISPLAY=:1
xhost +localhost

cp -r ~/.ssh ~hive/
chown -R hive:hive ~hive/.ssh
chown hive:hive /var/lib/hive/
#DISPLAY=:1 icewm &
#DISPLAY=:1 sudo -u hive xterm -bg black -fg white &
#DISPLAY=:1 sudo -u hive visualvm_14/bin/visualvm &

exit 0
###>capture.bash
#!/bin/bash
set -e

[ "$1" == "" ] && echo "usage: $0 <prefix>" && exit 1

N="$1_`date +%s`"

eval $(xwininfo -name cap |
    sed -n -e "s/^ \+Absolute upper-left X: \+\([0-9]\+\).*/x=\1/p" \
           -e "s/^ \+Absolute upper-left Y: \+\([0-9]\+\).*/y=\1/p" \
           -e "s/^ \+Width: \+\([0-9]\+\).*/w=\1/p" \
           -e "s/^ \+Height: \+\([0-9]\+\).*/h=\1/p" )


ffmpeg -video_size ${w}x${h} -framerate 1 -f x11grab -i :1+${x},${y} "$N.mkv"

ffmpeg -i "$N.mkv" -filter:v "setpts=0.01*PTS" -r 20 "$N.gif"



