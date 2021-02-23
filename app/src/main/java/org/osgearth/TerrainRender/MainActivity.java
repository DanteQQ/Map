package org.osgearth.TerrainRender;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.SimpleTimeZone;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks{

    private final int REQUEST_LOCATION_CAMERA_STORAGE_PERMISSION = 1;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1;

    @Override protected void onCreate(Bundle icicle) {
    	
    	//load  native lib
        System.loadLibrary("osgNativeLib");
        super.onCreate(icicle);
        //request all permissions
        requestLocationCameraStoragePermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //finish activity when GPS activity returned success and start NativeViewer activity
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent nativeActivity= new Intent(this, NativeViewer.class);
                finish();
                startActivity(nativeActivity);
            }
        }
    }
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //set init variables to false on phone rotation to prevent calling uninitialized variables
        NativeViewer.eglInit = false;
        NativeViewer.headingInit = false;
    }

    @AfterPermissionGranted(REQUEST_LOCATION_CAMERA_STORAGE_PERMISSION)
    private void requestLocationCameraStoragePermission() {
        //request all permissions
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //start GPS activity if permissions granted
            Intent GPSactivity= new Intent(this, GPS.class);
            startActivityForResult(GPSactivity, SECOND_ACTIVITY_REQUEST_CODE);
        } else {
            //request permissions again after rejection
            EasyPermissions.requestPermissions(this, "Application can't run properly without location and camera permissions",
                    REQUEST_LOCATION_CAMERA_STORAGE_PERMISSION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!(EasyPermissions.hasPermissions(this, perms)))
        {
            //request permissions again after rejection
            requestLocationCameraStoragePermission();
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //start GPS activity if permissions granted
        Intent GPSactivity= new Intent(this, GPS.class);
        startActivityForResult(GPSactivity, SECOND_ACTIVITY_REQUEST_CODE);
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //get user to app settings to manually change permissions after permanent rejection
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}