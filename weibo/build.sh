#!/bin/bash
#
# Android Development on the Command Line, use Ant.
# Create by : Lytsing Huang
# Date: 2011-12-22
#

if [ $# != 1 ]; then
	echo "Usage: $0 [debug|release]";
	exit;
fi

PRJ=Weibo
DATE=`date +%Y%m%d`
VER=1.2
NAME=$PRJ-$DATE-$VER

cd ../../pulltorefresh-and-loadmore/
android update project -p . -t android-8
cd -

cd ../../android-menudrawer/library
android update project -p . -t android-16
cd -

cd ../../abs
android update project -p . -t android-16
cd -

android update project --path . --name $NAME -t android-16

ant $1

