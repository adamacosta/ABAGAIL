#! /usr/bin/env bash

export CLASSPATH=$HOME/ABAGAIL/ABAGAIL.jar:$CLASSPATH

touch crxResults.csv

function crxTest {
	java opt.test.CrxTest $1 >> crxResults.csv
}
export -f crxTest

parallel crxTest ::: 10000 20000 30000 40000 50000 60000 70000 80000 90000 100000

touch rbtResults.csv

function rbtTest {
	java opt.test.RbtTest $1 >> rbtResults.csv
}
export -f rbtTest

parallel rbtTest ::: 10000 20000 30000 40000 50000 60000 70000 80000 90000 100000
