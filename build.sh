#!/bin/bash
#
# Android Development on the Command Line, use gradle
# Create by : Lytsing Huang
# Date: 2017-08-02
#

export JAVA_HOME="/Applications/Android Studio.app/Contents/jre/Contents/Home"
export PATH=$JAVA_HOME/bin:$PATH

./gradlew check
./gradlew clean build

