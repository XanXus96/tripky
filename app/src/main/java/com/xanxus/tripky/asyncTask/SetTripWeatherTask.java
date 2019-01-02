package com.xanxus.tripky.asyncTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonWriter;
import android.util.Log;

import com.xanxus.tripky.activity.MyTripsActivity;
import com.xanxus.tripky.model.Weather;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

public class SetTripWeatherTask extends AsyncTask<String, Void, String> {

    private FileOutputStream out;
    private List<Weather> weathers;
    private Context context;

    public SetTripWeatherTask (Context context, List<Weather> weathers) throws FileNotFoundException {
        super();
        this.context = context;
        this.out = context.openFileOutput("trips.json",MODE_PRIVATE | MODE_APPEND);
        this.weathers = weathers;
    }
    @Override
    protected String doInBackground(String... strings) {
        try {
            writeJsonStream(out, weathers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String temp) {
        Log.e("waaaaw", "called");
        context.startActivity(new Intent(context, MyTripsActivity.class));
    }

    public void writeJsonStream(OutputStream out, List<Weather> weathers) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("");
        writeMessagesObject(writer, weathers);
        writer.close();
    }

    public void writeMessagesObject(JsonWriter writer, List<Weather> weathers) throws IOException {
        writer.beginObject();
        writer.name("depart");
        writer.beginObject();
        writeWeather(writer, weathers.get(0));
        writer.endObject();
        writer.name("arrival");
        writer.beginObject();
        writeWeather(writer, weathers.get(1));
        writer.endObject();
        writer.endObject();
    }


    public void writeWeather(JsonWriter writer, Weather weather) throws IOException {
        writer.name("city").value(weather.getCity());
        writer.name("description").value(weather.getDescription());
        writer.name("temperature").value(weather.getTemperature());
        writer.name("icon").value(weather.getIcon());
        writer.name("humidity").value(weather.getHumidity());
        writer.name("pressure").value(weather.getPressure());
        writer.name("wind").value(weather.getWind());
        writer.name("precips").value(weather.getPrecipitations());
        writer.name("date").value(weather.getDate().getTime());
    }
}
