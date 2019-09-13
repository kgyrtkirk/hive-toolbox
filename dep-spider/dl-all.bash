time bash spider3.bash | sort -R |xargs -P32 -n1 wget -nv -O/dev/null

