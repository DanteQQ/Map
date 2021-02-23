#!/bin/bash

BASE_PATH=/home/dante/StudioProjects/OSGEarthAndroidDemo

/home/dante/Android/Sdk/ndk/android-ndk-r9d/ndk-build -j8
echo "Removing old shared libraries from Map project folder..."
rm -rf $BASE_PATH/Map/app/src/main/jniLibs/*
echo "Copying new shared libraries to Map project folder..."
cp -r ../libs/* $BASE_PATH/Map/app/src/main/jniLibs
