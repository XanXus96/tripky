package com.xanxus.tripky.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xanxus.tripky.R;
import com.xanxus.tripky.asyncTask.GetWeatherTask;
import com.xanxus.tripky.asyncTask.SetTripWeatherTask;
import com.xanxus.tripky.model.Prediction;
import com.xanxus.tripky.model.Weather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class TripWeatherActivity extends AppCompatActivity {

    private Prediction depart, arrival;
    private Long departAt, arrivalAt;
    private Weather depWeather, arrWeather;
    private ImageView widgetIcon;
    private ImageView widgetIcon2;
    private TextView widgetCity;
    private TextView widgetCity2;
    private TextView widgetDescription;
    private TextView widgetDescription2;
    private TextView widgetTemperature;
    private TextView widgetTemperature2;
    private TextView widgetHumidity;
    private TextView widgetHumidity2;
    private TextView widgetPressure;
    private TextView widgetPressure2;
    private TextView widgetWind;
    private TextView widgetWind2;
    private TextView widgetTime;
    private TextView widgetTime2;
    private TextView widgetPrecip;
    private TextView widgetPrecip2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_weather);

        Intent i = getIntent();
        depart = (Prediction) i.getSerializableExtra("depart");
        arrival = (Prediction) i.getSerializableExtra("arrival");
        departAt = i.getExtras().getLong("departAt");
        arrivalAt = i.getExtras().getLong("arrivalAt");
        try {
            depWeather = new GetWeatherTask(this, ",daily").execute(String.valueOf(depart.getLat()), String.valueOf(depart.getLon()), String.valueOf(departAt/1000)).get().get(0);
            depWeather.setCity(depart.getAddress());
            depWeather.setDate(departAt);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            arrWeather = new GetWeatherTask(this, ",daily").execute(String.valueOf(arrival.getLat()), String.valueOf(arrival.getLon()), String.valueOf(arrivalAt/1000)).get().get(0);
            arrWeather.setCity(arrival.getAddress());
            arrWeather.setDate(arrivalAt);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        widgetCity = (TextView) findViewById(R.id.widgetCity);
        widgetCity2 = (TextView) findViewById(R.id.widgetCity2);
        widgetDescription = (TextView) findViewById(R.id.widgetDescription);
        widgetDescription2 = (TextView) findViewById(R.id.widgetDescription2);
        widgetTemperature = (TextView) findViewById(R.id.widgetTemperature);
        widgetTemperature2 = (TextView) findViewById(R.id.widgetTemperature2);
        widgetHumidity = (TextView) findViewById(R.id.widgetHumidity);
        widgetHumidity2 = (TextView) findViewById(R.id.widgetHumidity2);
        widgetPressure = (TextView) findViewById(R.id.widgetPressure);
        widgetPressure2 = (TextView) findViewById(R.id.widgetPressure2);
        widgetTime = (TextView) findViewById(R.id.widgetTime);
        widgetTime2 = (TextView) findViewById(R.id.widgetTime2);
        widgetWind = (TextView) findViewById(R.id.widgetWind);
        widgetWind2 = (TextView) findViewById(R.id.widgetWind2);
        widgetPrecip = (TextView) findViewById(R.id.widgetPrecip);
        widgetPrecip2 = (TextView) findViewById(R.id.widgetPrecip2);
        widgetIcon = (ImageView) findViewById(R.id.widgetIcon);
        widgetIcon2 = (ImageView) findViewById(R.id.widgetIcon2);

        SimpleDateFormat f = new SimpleDateFormat("EEE d MMM yyyy HH:mm");

        widgetCity.setText(depWeather.getCity());
        widgetDescription.setText(depWeather.getDescription());
        widgetTemperature.setText(depWeather.getTemperature() + " \u2103");
        widgetHumidity.setText(getString(R.string.humidity) + " " + depWeather.getHumidity() + " %");
        widgetPressure.setText(getString(R.string.pressure) + " " + depWeather.getPressure() + " hPa");
        widgetWind.setText(getString(R.string.wind) + " " + depWeather.getWind() + " km/h");
        widgetPrecip.setText(getString(R.string.precip) + " " + depWeather.getPrecipitations());
        widgetTime.setText(f.format(depWeather.getDate()));
        try {
            widgetIcon.setImageBitmap(getBitmapFromAssets(depWeather.getIcon()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        widgetCity2.setText(arrWeather.getCity());
        widgetDescription2.setText(arrWeather.getDescription());
        widgetTemperature2.setText(arrWeather.getTemperature() + " \u2103");
        widgetHumidity2.setText(getString(R.string.humidity) + " " + arrWeather.getHumidity() + " %");
        widgetPressure2.setText(getString(R.string.pressure) + " " + arrWeather.getPressure() + " hPa");
        widgetWind2.setText(getString(R.string.wind) + " " + arrWeather.getWind() + " km/h");
        widgetPrecip2.setText(getString(R.string.precip) + " " + arrWeather.getPrecipitations());
        widgetTime2.setText(f.format(arrWeather.getDate()));
        try {
            widgetIcon2.setImageBitmap(getBitmapFromAssets(arrWeather.getIcon()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                try {
                    new SetTripWeatherTask(this, new ArrayList<Weather>() {{
                        add(depWeather);
                        add(arrWeather);
                    }}).execute();
                    Toast.makeText(this, "saved successfully", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap getBitmapFromAssets(String fileName) throws IOException {
        AssetManager assetManager = getAssets();

        InputStream istr = assetManager.open(fileName+".png");
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        istr.close();

        return bitmap;
    }
}
