
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


LESS=' -R'
LESSOPEN='|pygmentize %s'

