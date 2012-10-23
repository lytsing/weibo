#!/bin/bash


cd ../pulltorefresh-and-loadmore/
android update project -p . -t android-8
cd -
cd ../actionbar/
android update project -p . -t android-16
cd -

cd ../com_weibo_android
android update project -p . -t android-16
cd -

android update project -p . --name Weibo -t android-16
ant debug

