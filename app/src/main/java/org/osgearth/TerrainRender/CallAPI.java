package org.osgearth.TerrainRender;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;

public class CallAPI extends AsyncTask<Void, Void, Void> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        double northOverpass = round(GPS.latitude, 2) + 0.1;
        double southOverpass = round(GPS.latitude, 2) - 0.1;
        double eastOverpass = round(GPS.longitude, 2) + 0.1;
        double westOverpass = round(GPS.longitude, 2) - 0.1;

        try {
            //create request to Overpass API and save response to inputstring
            URLConnection connection = new URL("https://overpass-api.de/api/interpreter?data=%0A[out%3Ajson][timeout%3A25]%3B%0A(%09%09%0A%20%20node%0A%20%20%09[%22place%22%3D%22city%22]%0A%20%20%09("+southOverpass+"%2C"+westOverpass+"%2C"+northOverpass+"%2C"+eastOverpass+")%3B%0A%0A%20%20node%20%20%09[%22place%22%3D%22town%22]%0A%20%20%09("+southOverpass+"%2C"+westOverpass+"%2C"+northOverpass+"%2C"+eastOverpass+")%3B%0A%20%0A%20%20node%20%20%09[%22place%22%3D%22village%22]%0A%20%20%09("+southOverpass+"%2C"+westOverpass+"%2C"+northOverpass+"%2C"+eastOverpass+")%3B%0A%0A)%3B%0Aout%20body%3B%0A%3E%3B%0Aout%20skel%20qt%3B&fbclid=IwAR3pbA0DgUb8bnOtZMErAxIH8AnyXvm-ilCZdGhu_w_iVMNHOHIns_93R8E").openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");
            InputStream response = connection.getInputStream();

            //parse JSON response
            JSONObject jsonObject = new JSONObject(inputStreamToString(response));
            JSONArray elementArray = jsonObject.getJSONArray("elements");
            //set geodata variables in JNI
            for (int i = 0; i < elementArray.length(); i++) {
                JSONObject cityCoords = elementArray.getJSONObject(i);
                JSONObject cityName =  cityCoords.getJSONObject("tags");
                //normalize special characters
                String normalizedName = Normalizer.normalize(cityName.getString("name"), Normalizer.Form.NFD);
                osgNativeLib.setCitiesData(i, cityCoords.getDouble("lat"), cityCoords.getDouble("lon"), normalizedName);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        return sb.toString();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
