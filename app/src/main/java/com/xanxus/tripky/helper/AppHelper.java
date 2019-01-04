package com.xanxus.tripky.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AppHelper {

    private static Context context;

    public AppHelper(Context context) {
        AppHelper.context = context;
    }

    public Bitmap getBitmapFromAssets(String fileName) throws IOException {
        AssetManager assetManager = context.getAssets();

        InputStream istr = assetManager.open(fileName + ".png");
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        istr.close();

        return bitmap;
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                return urlc.getResponseCode() == 200;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public ArrayList<JSONObject> getObjectsFromFile(String filename, boolean check) {
        ArrayList<JSONObject> jsonItems = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(filename);
            if (fis == null) return jsonItems;
            InputStream stream = new BufferedInputStream(fis);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }
            if (!check) {
                jsonItems.add(new JSONObject(builder.toString()));
                return jsonItems;
            }
            StringBuilder json = new StringBuilder();
            int BracketCount = 0;
            for (char c : builder.toString().toCharArray()) {
                if (c == '{')
                    ++BracketCount;
                else if (c == '}')
                    --BracketCount;
                json.append(c);

                if (BracketCount == 0 && c != ' ') {
                    jsonItems.add(new JSONObject(json.toString()));
                    json = new StringBuilder();

                }
            }
        } catch (java.io.IOException | JSONException e) {
            return jsonItems;
        }
        return jsonItems;
    }
}
