#!/bin/sh
mkdir -p bin
javac -d bin $(find . -name "*.java")
cp src/dictionary.txt bin
mkdir -p dist
cd bin
jar cfe ../dist/wayside.jar Main *