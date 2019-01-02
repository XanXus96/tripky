package com.xanxus.tripky.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.xanxus.tripky.R;
import com.xanxus.tripky.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetWeatherTask extends AsyncTask<String, Void, ArrayList<Weather>> {


    private Weather todayWeather = new Weather();
    private ArrayList<Weather> listWeather = new ArrayList<Weather>();

    private static final String WEATHER_API_BASE = "https://api.darksky.net/";
    private static final String WEATHER_API_TYPE = "forecast/";
    private static final String WEATHER_UNITS = "ca";
    private static final String RESPONSE_EXCLUDES = "currently,flags,alerts,minutely";

    private static String DARK_SKY_API_KEY;

    private String excludes;


    public GetWeatherTask(Context context, String excludes) {

        DARK_SKY_API_KEY = context.getString(R.string.dark_sky_key);
        this.excludes = excludes;
    }

    @Override
    protected ArrayList<Weather> doInBackground(String... strings) {
        try {
            StringBuilder sb;
            if (strings[2] != null) {
                sb = new StringBuilder(WEATHER_API_BASE + WEATHER_API_TYPE + DARK_SKY_API_KEY + "/"
                                                + strings[0] + "," + strings[1] + "," + strings[2] + "/");
            }else{
                sb = new StringBuilder(WEATHER_API_BASE + WEATHER_API_TYPE + DARK_SKY_API_KEY + "/"
                        + strings[0] + "," + strings[1] + "/");
            }
            sb.append("?units=" + WEATHER_UNITS);
            sb.append("&exclude=" + RESPONSE_EXCLUDES + excludes);
            URL url = new URL(sb.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }
            if (strings[2] != null) {
                try {
                    JSONObject reader = new JSONObject(builder.toString());
                    JSONObject hourly = reader.getJSONObject("hourly");
                    JSONArray data = hourly.getJSONArray("data");
                    JSONObject d = new JSONObject();
                    for (int i = 0; i < data.length(); i++) {
                        d = data.getJSONObject(i);
                        if (Math.abs(d.get("time").toString().compareTo(strings[2])) <= 1800) break;
                    }
                    todayWeather.setDescription(d.getString("summary"));
                    todayWeather.setDate(Long.parseLong(strings[2]) * 1000);
                    todayWeather.setIcon(d.getString("icon"));
                    todayWeather.setTemperature(String.valueOf(d.getLong("temperature")));
                    todayWeather.setHumidity(String.valueOf(d.getDouble("humidity")*100));
                    todayWeather.setPressure(String.valueOf(d.getDouble("pressure")));
                    todayWeather.setWind(String.valueOf(d.getDouble("windSpeed")));
                    if (d.has("precipType")) {
                        todayWeather.setPrecipitations(String.valueOf(d.getDouble("precipProbability") * 100) + "% " + d.getString("precipType"));
                    } else {
                        todayWeather.setPrecipitations(String.valueOf(d.getDouble("precipProbability") * 100) + "% ");
                    }
                    listWeather.add(todayWeather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    JSONObject reader = new JSONObject(builder.toString());
                    JSONObject daily = reader.getJSONObject("daily");
                    JSONArray data = daily.getJSONArray("data");
                    JSONObject d;
                    for (int i = 0; i < data.length(); i++) {
                        d = data.getJSONObject(i);
                        todayWeather = new Weather();
                        todayWeather.setDescription(d.getString("summary"));
                        todayWeather.setDate(d.getLong("time")*1000);
                        todayWeather.setIcon(d.getString("icon"));
                        todayWeather.setTemperature(String.valueOf(d.getLong("temperatureMin")) + "~" + String.valueOf(d.getLong("temperatureMax")));
                        todayWeather.setHumidity(String.valueOf(d.getDouble("humidity")*100));
                        todayWeather.setPressure(String.valueOf(d.getDouble("pressure")));
                        todayWeather.setWind(String.valueOf(d.getDouble("windSpeed")));
                        if (d.has("precipType")) {
                            todayWeather.setPrecipitations(String.valueOf(d.getDouble("precipProbability") * 100) + "% " + d.getString("precipType"));
                        } else {
                            todayWeather.setPrecipitations(String.valueOf(d.getDouble("precipProbability") * 100) + "% ");
                        }
                        todayWeather.setSunrise(d.getLong("sunriseTime")*1000);
                        todayWeather.setSunset(d.getLong("sunsetTime")*1000);
                        listWeather.add(todayWeather);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listWeather;
    }

    @Override
    protected void onPostExecute(ArrayList<Weather> temp) {
    }
}
