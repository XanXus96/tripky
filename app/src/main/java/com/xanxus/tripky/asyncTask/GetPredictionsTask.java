package com.xanxus.tripky.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.xanxus.tripky.R;
import com.xanxus.tripky.model.Prediction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GetPredictionsTask extends AsyncTask<String, Void, ArrayList<Prediction>> {

    private static final String TAG = GetPredictionsTask.class.getSimpleName();

    private static final String SEARCH_API_BASE = "https://api.tomtom.com/search";
    private static final String API_VERSION = "/2";
    private static final String SEARCH_API_TYPE = "/geocode";
    private static final String OUT_JSON = ".json";
    private static final String SEARCH_LIMIT = "20";

    private static String TOMTOM_API_KEY;

    private ArrayList<Prediction> predictions = new ArrayList<Prediction>();

    public GetPredictionsTask(Context context) {
        super();
        TOMTOM_API_KEY = context.getString(R.string.tomtom_key);
    }

    @Override
    protected ArrayList<Prediction> doInBackground(String... strings) {

            try {

                StringBuilder sb = new StringBuilder(SEARCH_API_BASE + API_VERSION + SEARCH_API_TYPE);
                sb.append("/" + URLEncoder.encode(strings[0], "utf8"));
                sb.append(OUT_JSON);
                sb.append("?key=" + TOMTOM_API_KEY);
                sb.append("&limite" + SEARCH_LIMIT);
                URL url = new URL(sb.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                try {
                    // Create a JSON object hierarchy from the results
                    JSONObject jsonObj = new JSONObject(builder.toString());
                    JSONArray predsJsonArray = jsonObj.getJSONArray("results");

                    // Extract the Place descriptions from the results
                    String address, mcc;
                    double lon, lat;
                    for (int i = 0; i < predsJsonArray.length(); i++) {
                        address = predsJsonArray.getJSONObject(i).getJSONObject("address").getString("freeformAddress");
                        mcc = predsJsonArray.getJSONObject(i).getJSONObject("address").getString("municipality") + " " +
                                predsJsonArray.getJSONObject(i).getJSONObject("address").getString("countrySubdivision") + ", " +
                                predsJsonArray.getJSONObject(i).getJSONObject("address").getString("countryCode");
                        lon = Double.parseDouble(predsJsonArray.getJSONObject(i).getJSONObject("position").getString("lon"));
                        lat = Double.parseDouble(predsJsonArray.getJSONObject(i).getJSONObject("position").getString("lat"));

                        Prediction pred = new Prediction(address, lon, lat);
                        pred.setMcc(mcc);
                        predictions.add(pred);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Cannot process JSON results", e);
                }
            }catch (MalformedURLException e) {
                Log.e(TAG, "Error processing Places API URL", e);
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to Places API", e);
            }
        return predictions;
    }
}
