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
VER=1.3
NAME=$PRJ-$DATE-$VER
TARGET=android-16

ant clean

cd ../../pulltorefresh-and-loadmore/
android update project -p . -t $TARGET
cd -

cd ../../android-menudrawer/library
android update project -p . -t $TARGET
cd -

cd ../../abs
android update project -p . -t $TARGET
cd -

cd ../../WeiboSDK
android update project -p . -t $TARGET
cd -

android update project --path . --name $NAME -t $TARGET

ant $1

