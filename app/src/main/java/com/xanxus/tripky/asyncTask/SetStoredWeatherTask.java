package com.xanxus.tripky.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonWriter;

import com.xanxus.tripky.activity.MainActivity;
import com.xanxus.tripky.model.Weather;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SetStoredWeatherTask extends AsyncTask<String, Void, String> {

    private FileOutputStream out;
    private List<Weather> weathers;
    private Context context;

    public SetStoredWeatherTask(Context context, List<Weather> weathers) throws FileNotFoundException {
        super();
        this.context = context;
        this.out = context.openFileOutput("weather.json", MODE_PRIVATE);
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

    }

    public void writeJsonStream(OutputStream out, List<Weather> weathers) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("");
        writeMessagesObject(writer, weathers);
        writer.close();
    }

    public void writeMessagesObject(JsonWriter writer, List<Weather> weathers) throws IOException {

        int i = 0;

        writer.beginObject();
        for (Weather w : weathers) {
            writer.name("weather" + i);
            writer.beginObject();
            writeWeather(writer, w);
            writer.endObject();
            i++;
        }
        writer.endObject();
    }


    public void writeWeather(JsonWriter writer, Weather weather) throws IOException {

        writer.name("city").value(((MainActivity) context).getSupportActionBar().getTitle().toString());
        writer.name("description").value(weather.getDescription());
        writer.name("temperature").value(weather.getTemperature());
        writer.name("icon").value(weather.getIcon());
        writer.name("humidity").value(weather.getHumidity());
        writer.name("pressure").value(weather.getPressure());
        writer.name("wind").value(weather.getWind());
        writer.name("precips").value(weather.getPrecipitations());
        writer.name("date").value(weather.getDate().getTime());
        writer.name("sunrise").value(weather.getSunrise());
        writer.name("sunset").value(weather.getSunset());
    }
}
