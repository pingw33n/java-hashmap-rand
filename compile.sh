#!/bin/sh

set -e
rm -rf out/*

rm -rf out-test
mkdir out-test

javac -d out java/util/*.java --patch-module java.base=.
javac HashMapRandTest.java -d out-test

java -ea -cp out-test --patch-module java.base=out HashMapRandTest

echo "To use add this to the java command:\n    --patch-module java.base=$PWD/out"
