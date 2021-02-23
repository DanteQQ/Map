// OSG Earth built using scripts from https://github.com/thahemp

#include <string.h>
#include <jni.h>
#include <android/log.h>

#include <iostream>

#include "OsgMainApp.hpp"

OsgMainApp mainApp;

extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    __android_log_write(ANDROID_LOG_ERROR, "OSGANDROID",
            "Entered JNI_OnLoad");
	return JNI_VERSION_1_6;
}

extern "C" {
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_init(JNIEnv * env, jobject obj, jint width, jint height);
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_step(JNIEnv * env, jobject obj);
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_touchZoomEvent(JNIEnv * env, jobject obj, jdouble delta);
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setDataFilePath(JNIEnv * env, jobject obj, jstring dataPath, jstring packagePath, jobject assetManager);
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setCoords(JNIEnv * env, jobject obj, jdouble lat, jdouble lon, jdouble alt);
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setPitchHeading(JNIEnv * env, jobject obj, jdouble pitch, jdouble heading);
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_changePitchHeading(JNIEnv * env, jobject obj, jdouble pitch, jdouble heading);
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setCitiesData(JNIEnv * env, jobject obj, jint position, jdouble lat, jdouble lon, jstring name);
    JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setEarthFilePath(JNIEnv * env, jobject obj, jstring path);
};

JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_init(JNIEnv * env, jobject obj, jint width, jint height){
    mainApp.initOsgWindow(0,0,width,height);
}
JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_step(JNIEnv * env, jobject obj){
    mainApp.draw();
}
JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_touchZoomEvent(JNIEnv * env, jobject obj, jdouble delta){
    mainApp.touchZoomEvent(delta);
}
JNIEXPORT void JNICALL JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setDataFilePath(JNIEnv * env, jobject obj, jstring dataPath, jstring packagePath, jobject assetManager)
{
    //data paths from java
    const char *nativeDataPath = env->GetStringUTFChars(dataPath, JNI_FALSE);
    const char *nativePackagePath = env->GetStringUTFChars(packagePath, JNI_FALSE);

    mainApp.setDataPath(std::string(nativeDataPath), std::string(nativePackagePath));

    env->ReleaseStringUTFChars(packagePath, nativePackagePath);
    env->ReleaseStringUTFChars(dataPath, nativeDataPath);
}
JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setCoords(JNIEnv * env, jobject obj, jdouble lat, jdouble lon, jdouble alt)
{
    mainApp.setCoords(lat, lon, alt);
}
JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setPitchHeading(JNIEnv * env, jobject obj, jdouble pitch, jdouble heading)
{
    mainApp.setPitchHeading(pitch, heading);
}
JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_changePitchHeading(JNIEnv * env, jobject obj, jdouble pitch, jdouble heading)
{
    mainApp.changePitchHeading(pitch, heading);
}
JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setCitiesData(JNIEnv * env, jobject obj, jint position, jdouble lat, jdouble lon, jstring name)
{
    //conversion from java UTF16 to C UTF8
    jboolean isCopy;
    const char *convertedValue = (env)->GetStringUTFChars(name, &isCopy);
    std::string cname = std::string(convertedValue, strlen(convertedValue));

    mainApp.setCitiesData(position, lat, lon, cname);
    env->ReleaseStringUTFChars(name, convertedValue);
}
JNIEXPORT void JNICALL Java_org_osgearth_TerrainRender_osgNativeLib_setEarthFilePath(JNIEnv * env, jobject obj, jstring path)
{
    //earthfile path from java
    const char *mPath = env->GetStringUTFChars(path, JNI_FALSE);
    mainApp.setEarthFilePath(std::string(mPath));
    env->ReleaseStringUTFChars(path, mPath);
}