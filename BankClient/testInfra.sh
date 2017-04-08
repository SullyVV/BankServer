#!/bin/bash
echo "build start..."

BIN_PATH=bin
SRC_PATH=src
SRC_FILE_LIST_PATH=src/sources.list

rm -f $SRC_PATH/sources
find $SRC_PATH/ -name *.java > $SRC_FILE_LIST_PATH

rm -rf $BIN_PATH/
mkdir $BIN_PATH/

javac -d $BIN_PATH/ @$SRC_FILE_LIST_PATH
java -cp .:$BIN_PATH ece590.bankclient.TestInfra $1 $2
