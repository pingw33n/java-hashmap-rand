#!/bin/sh

set -e
rm -rf out/*
javac -d out java/util/*.java --patch-module java.base=.

echo "To use add this to the java command:\n    --patch-module java.base=$PWD/out"
