#! /usr/bin/env bash

export CLASSPATH=$HOME/ABAGAIL/ABAGAIL.jar:$CLASSPATH

touch tsp.csv

function tspTest {
    java $1 >> tsp.csv
}
export -f tspTest

parallel tspTest ::: opt.test.TSPTest opt.test.TSPTest opt.test.TSPTest opt.test.TSPTest \
                     opt.test.TSPTest opt.test.TSPTest opt.test.TSPTest opt.test.TSPTest

touch nQueens.csv

function queenTest {
    java $1 >> nQueens.csv
}
export -f queenTest

parallel queenTest ::: opt.test.NQueensTest opt.test.NQueensTest opt.test.NQueensTest opt.test.NQueensTest \
                       opt.test.NQueensTest opt.test.NQueensTest opt.test.NQueensTest opt.test.NQueensTest

touch fourPeaks.csv

function peakTest {
    java $1 >> fourPeaks.csv
}
export -f peakTest

parallel peakTest ::: opt.test.FourPeaksTest opt.test.FourPeaksTest opt.test.FourPeaksTest opt.test.FourPeaksTest \
                      opt.test.FourPeaksTest opt.test.FourPeaksTest opt.test.FourPeaksTest opt.test.FourPeaksTest