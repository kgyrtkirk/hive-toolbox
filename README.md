
## hive toolbox

various hive related utilities which can be used during hive development

### current features:

* download and apply ptest results to current worktree
```
build/install/toolbox/bin/toolbox HIVE-XXXXX
build/install/toolbox/bin/toolbox http://..../something.zip
```
  classifies the diffs by some classificators...
  creates an executable script at `/tmp/_qd` to guide diff applications
  This whole thing is most usefull if someone is dealing massive amount of q.out changes...and most of them can be accepted by programming the pattern;
  Adding a classificator for it can be used to remove the "noise" from the other changes; and uncover real problems without just hitting `git commit -a -m ...` for 600 q.out changes...

* ptest-rexecutor (pattern builder)
  analyzes a jenkins testReport; and builds a maven compatible `test` pattern to re-execute failing test cases
```
toolbox/bin/toolbox RERUN patternfile https://builds.apache.org/job/PreCommit-HIVE-Build/8060/
```
  writes the test pattern into `patternfile`

* `toolbox.userscript.js` is a small greasemonkey extension to enable quick lookup of failed tests
* `li-nav` is a tool which enables to "browse" git modified files one-by-one;
  especially usefull: in case of 100s of q.outs which can only be accepted manually
  works by employing the following principle: sets a variable `LI` to the actual file; and provides next/prev features
  so I don't have to cut'n paste the test/q.out name every time...
  it has some other extension like `llq` which shows the `q` file for the actual entry...


```
toolbox applicator HIVE-19097
```
* downloads the latest patch from the jira
* prints out 2 commands:
	* the git apply command to apply the patch
	* and a git commit to submit it with the original author with the jira title.

### also there but not yet working...

* some jenkins related stuff...to peek into its queues and other things; however currently I'm not able to access attachments using the jira api because of [some problem](https://issues.apache.org/jira/browse/INFRA-15541)

### further ideas

* scan for unused q.out-s
* store in annotations:
	* active subprojects
	* remote ticket ref
	* q results
* simplify submission...upload/etc
* favorite test set - asf test queue is around 24 hours;
  a smaller but usefull set to check that the changes are solid?
* archive all ptest executions from apache
* be more connected
  * download & commit patch to current ws
  * upload rbt item to prev rbt; 
  * branch names - prefix scan; closed tickets are not needed anymore
* add jenkins related job-dsl definitions?
* hive-reinit

* rename project to ?


* sort diffs by size; could enable to show similar diffs after each other



### usefull stuff

I keep forgetting stuff I found usefull..so I'll make notes here if I don't forget it :D

* ##### [saboteur](https://github.com/tomakehurst/saboteur)

  Can be used to impose network latency arbitrarily
  ```bash
  sab add --fault_type DELAY -l 3 -p 3306  --direction IN 
  sab reset --fault_type DELAY -l 3 -p 3306  --direction IN
  ```

* ##### colorize less using pygmentize

  ```bash
  LESS=' -R'
  LESSOPEN='|pygmentize %s'
  ```



dot -Tpng ticketDag.dot -o /tmp/asd.png
feh -R1 --scale-down /tmp/asd.png
