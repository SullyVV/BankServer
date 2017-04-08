#!/bin/bash
echo "build start..."

JAR_PATH=lib
BIN_PATH=bin
SRC_PATH=src
SRC_FILE_LIST_PATH=src/sources.list

rm -f $SRC_PATH/sources
find $SRC_PATH/ -name *.java > $SRC_FILE_LIST_PATH

rm -rf $BIN_PATH/
mkdir $BIN_PATH/

for file in ${JAR_PATH}/*.jar;
do
    jarfile=${jarfile}:${file}
done
echo "jarfile="$jarfile

javac -d $BIN_PATH/ -cp $jarfile @$SRC_FILE_LIST_PATH

java -cp .:$BIN_PATH$jarfile ece590.bankserver.Bank 8888
