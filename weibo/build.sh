#!/bin/bash
#
# Android Development on the Command Line, use Ant.
# Create by : Lytsing Huang
# Date: 2011-12-22
#

cd ../../pulltorefresh-and-loadmore/
android update project -p . -t android-8
cd -

cd ../../android-menudrawer/library
android update project -p . -t android-16
cd -

cd ../../abs
android update project -p . -t android-16
cd -

android update project -p . --name Weibo -t android-16
ant debug

# Install 
adb install -r bin/Weibo-debug.apk 

