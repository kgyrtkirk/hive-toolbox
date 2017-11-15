
function li_init() {
	git status --porcelain=1|grep '^ M'|cut -c 3- > li
	li_file li
}

function li_file() {
	LI_FILE="$1"
	LI_IDX=0
	LI_N="`cat $LI_FILE|wc -l`"
	li_next
}

function li_sel() {
	LI="`head -n $LI_IDX $LI_FILE|tail -n1`"
	echo "[$LI_IDX/$LI_N]: LI=$LI"
	echo "$LI" > /tmp/_li_item
}

function li_next() {
	if [ "$LI_IDX" == "$LI_N" ]; then
		banner eof
		LI=""
		return 1
	fi
	LI_IDX=$[ $LI_IDX + 1 ]
	li_sel $LI_IDX
}

function li_prev() {
	if [ "$LI_IDX" == "1" ]; then
		banner UNDERFLOW
		LI=""
		return 1
	fi
	LI_IDX=$[ $LI_IDX - 1 ]
	li_sel
}

function llq() {
	local LI="`cat /tmp/_li_item`"
	local q="`basename "$LI" .out`"
	echo "$q"
	ll $q
}
