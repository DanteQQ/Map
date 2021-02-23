package org.osgearth.TerrainRender;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SRTM extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ProgressDialog.show(this, "Downloading heightmap", "Wait for download to finish");

        //receiver to find out when download is completed
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //delete previous SRTM file
        File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "SRTM.tif");

        if (myFile.exists()) {
            myFile.delete();
        }

        //delete previous earthfile
        File myEarthFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "readymap.earth");

        if (myEarthFile.exists()) {
            myEarthFile.delete();
        }

        //body of earthfile to create
        String body = "<map name=\"readymap.org\" type=\"geocentric\" version=\"2\">\n" +
                "\n" +
                "    <image name=\"ReadyMap.org - Imagery\" driver=\"tms\">\n" +
                "        <url>http://readymap.org/readymap/tiles/1.0.0/7/</url>\n" +
                "    </image>\n" +
                "        \n" +
                "    <elevation name=\"ReadyMap.org - Elevation\" driver=\"gdal\">\n" +
                "        <url>"+Environment.getExternalStorageDirectory().getAbsolutePath()+"/SRTM.tif</url>\n" +
                "    </elevation>\n" +
                "    \n" +
                "</map>\n";

        //create earthfile
        generateEarthFile(getApplicationContext(),"readymap.earth",body);

        downloadFile(GPS.latitude, GPS.longitude);

    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(onComplete);
        super.onStop();
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            finishedDownload();
        }
    };

    void downloadFile(double lat, double lon) {
        double south = lat - 0.5;
        double north = lat + 0.5;
        double west = lon - 0.5;
        double east = lon + 0.5;
        int demType = 3;

        //download heightmap according to location
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("https://portal.opentopography.org/API/globaldem?demtype=SRTMGL" + demType + "&south=" + south + "&north=" + north + "&west=" + west + "&east=" + east + "&outputFormat=GTiff");

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("SRTM");
        request.setDescription("Downloading");
        request.setVisibleInDownloadsUi(false);
        File destFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "SRTM.tif");
        request.setDestinationUri(Uri.fromFile(destFile));
        downloadmanager.enqueue(request);
    }

    void finishedDownload() {
        setResult(RESULT_OK);
        finish();
    }

    public void generateEarthFile(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}