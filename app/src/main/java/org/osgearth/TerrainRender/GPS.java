package org.osgearth.TerrainRender;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class GPS extends Activity implements LocationListener {
    LocationManager mLocationManager;
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    public static double altitude = 0.0;
    private static final int THIRD_ACTIVITY_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ProgressDialog.show(this, "Getting GPS", "Wait for GPS to connect");

        //check permissions
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission problem", Toast.LENGTH_SHORT).show();
            return;
        }
        //request location from GPS
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    //gps connected
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
            //stop GPS requests for further updates
            mLocationManager.removeUpdates(this);
            Toast.makeText(this, "GPS connected", Toast.LENGTH_SHORT).show();

            new CallAPI().execute();

            //set location variables in JNI
            osgNativeLib.setCoords(GPS.latitude, GPS.longitude, GPS. altitude);

            //start SRTM activity for result
            Intent SRTMactivity= new Intent(this, SRTM.class);
            startActivityForResult(SRTMactivity, THIRD_ACTIVITY_REQUEST_CODE);
        }
    }

    // Required functions
    public void onProviderDisabled(String arg0) {
    }

    public void onProviderEnabled(String arg0) {
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //finish activity when SRTM activity returned success
        if (requestCode == THIRD_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
