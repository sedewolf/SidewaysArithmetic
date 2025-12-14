#!/bin/sh
if [ -e dist/wayside.jar ]
then
    java -jar dist/wayside.jar $1 $2 $3 $4 $5
else
    echo "wayside.jar must be built before you can run it."
fi
