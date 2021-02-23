package org.osgearth.TerrainRender;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class NativeViewer extends Activity implements SensorEventListener {

    private EGLview mView;
    private Camera mCamera;
    private CameraPreview camView;

    public static SensorManager mSensorManager;
    public static Sensor accelerometer;
    public static Sensor magnetometer;
    public static Sensor gyroscope;

    public static float[] mAccelerometer = null;
    public static float[] mGeomagnetic = null;
    public static float[] mGyroscope = null;

    //variables to find out if EGLview is initialized and phone heading is set
    public static boolean eglInit = false;
    public static boolean headingInit = false;

    @Override protected void onCreate(Bundle icicle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(icicle);
        setContentView(R.layout.activity_native_viewer);

        //set the data directory string for android platform in JNI
        Context context = getApplicationContext();
        String dir = context.getFilesDir().getPath();
        String packageDir = context.getPackageResourcePath();
        osgNativeLib.setDataFilePath(dir, packageDir);
        //set earthfile path in jni
        osgNativeLib.setEarthFilePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/readymap.earth");

        final LinearLayout linLayout = (LinearLayout) findViewById(R.id.layoutEGL);
        TextView tv = (TextView)findViewById(R.id.textView2);
        //show altitude on screen
        tv.setText("Altitude: " + GPS.altitude + "m");

        //create EGLview which calls our native libs init and frame/update functions
        mView = new EGLview(this);
        linLayout.addView(mView,0);

        //create camera preview
        mCamera = getCameraInstance();
        camView = new CameraPreview(this, mCamera);

        //switch between camera preview and EGLview
        final Switch camButton = findViewById(R.id.cameraSwitch);
        camButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mView.getParent() != null) {
                        linLayout.removeView(mView);
                    }
                    linLayout.addView(camView);
                    eglInit = false;
                    headingInit = false;
                } else {
                    if (camView.getParent() != null) {
                        linLayout.removeView(camView);
                    }
                    linLayout.addView(mView);
                }
            }
        });
        Button exitButton = findViewById(R.id.buttonExit);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
        mSensorManager.unregisterListener(this, gyroscope);

    }
    @Override protected void onResume() {
        super.onResume();
        mView.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometer = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mGyroscope = event.values;
            if(eglInit && headingInit) {
                int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
                //use gyroscope output to move with camera according to phone rotation
                switch (rotation) {
                    case Surface.ROTATION_0: //portrait
                            osgNativeLib.changePitchHeading(event.values[0]/50, -event.values[1]/50);
                        break;
                    case Surface.ROTATION_90: //landscape
                            osgNativeLib.changePitchHeading(-event.values[1]/50, -event.values[0]/50);
                        break;
                    case Surface.ROTATION_270: //reverse landscape
                        osgNativeLib.changePitchHeading(event.values[1]/50, event.values[0]/50);
                        break;
                }
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }
        if (mAccelerometer != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                //get azimuth and pitch of phone
                double azimuth = 180 * orientation[0] / Math.PI;
                double pitch = 180 * orientation[1] / Math.PI;

                int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
                //set initial heading of camera according to pitch and azimuth
                switch (rotation) {
                    case Surface.ROTATION_0: //portrait
                        osgNativeLib.setPitchHeading(-pitch - 90.0, azimuth);
                        break;
                    case Surface.ROTATION_90: //landscape
                        if (azimuth + 90.0 > 360.0){
                            osgNativeLib.setPitchHeading(pitch, azimuth + 90.0 - 360.0);
                        }
                        else {
                            osgNativeLib.setPitchHeading(pitch, azimuth + 90.0);
                        }
                        break;
                    case Surface.ROTATION_270: //reverse landscape
                        if (azimuth - 90.0 < 0.0){
                            osgNativeLib.setPitchHeading(pitch, azimuth - 90.0 + 360.0);
                        }
                        else {
                            osgNativeLib.setPitchHeading(pitch, azimuth - 90.0);
                        }
                        break;
                }
                headingInit = true;

            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        eglInit = false;
        headingInit = false;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if activity_hologram is unavailable
    }

}
