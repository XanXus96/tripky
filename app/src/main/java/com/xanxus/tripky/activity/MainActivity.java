package com.xanxus.tripky.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomtom.online.sdk.search.OnlineSearchApi;
import com.tomtom.online.sdk.search.SearchApi;
import com.tomtom.online.sdk.search.api.SearchError;
import com.tomtom.online.sdk.search.api.revgeo.RevGeoSearchResultListener;
import com.tomtom.online.sdk.search.data.common.Address;
import com.tomtom.online.sdk.search.data.reversegeocoder.ReverseGeocoderSearchQueryBuilder;
import com.tomtom.online.sdk.search.data.reversegeocoder.ReverseGeocoderSearchResponse;
import com.xanxus.tripky.R;
import com.xanxus.tripky.adapter.ListWeatherAdapter;
import com.xanxus.tripky.asyncTask.GetWeatherTask;
import com.xanxus.tripky.model.Prediction;
import com.xanxus.tripky.model.Weather;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Prediction pred;
    private ArrayList<Weather> listWeather;

    private TextView tempTextView, cityTextView, descTextView, humiTextView, presTextView, windTextView, precipTextView, sRiseTextView,
            sSetTextView;
    private ImageView iconImageView;
    private RecyclerView recyclerView;
    private ListWeatherAdapter weatherAdapter;
    private Weather today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        @SuppressLint("MissingPermission")
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (getIntent().getExtras() != null) {
            pred = (Prediction) getIntent().getExtras().getSerializable("pred");
            setTitle(pred.getMcc());
        }else{
            pred = new Prediction("Tripky", location.getLongitude(),location.getLatitude());

            SearchApi searchApi = OnlineSearchApi.create(this);
            searchApi.reverseGeocoding(new ReverseGeocoderSearchQueryBuilder(location.getLatitude(), location.getLongitude()).build(),
                    new RevGeoSearchResultListener() {
                        @Override
                        public void onSearchResult(ReverseGeocoderSearchResponse reverseGeocoderSearchResponse) {

                            Address a = reverseGeocoderSearchResponse.getAddresses().get(0).getAddress();
                            String address = a.getMunicipality() + " " + a.getCountrySubdivision() + ", " + a.getCountryCode();
                            setTitle(address);
                        }
                        @Override
                        public void onSearchError(SearchError searchError) {
                            //TODO
                        }
                    });
        }



        try {
            listWeather = new GetWeatherTask(this, ",hourly").execute(String.valueOf(pred.getLat()), String.valueOf(pred.getLon()), null).get();
            today = listWeather.get(0);
            listWeather.remove(0);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tempTextView = (TextView) findViewById(R.id.widgetTemperature);
        cityTextView = (TextView) findViewById(R.id.widgetCity);
        descTextView = (TextView) findViewById(R.id.widgetDescription);
        humiTextView = (TextView) findViewById(R.id.widgetHumidity);
        presTextView = (TextView) findViewById(R.id.widgetPressure);
        windTextView = (TextView) findViewById(R.id.widgetWind);
        iconImageView = (ImageView) findViewById(R.id.widgetIcon);
        precipTextView = (TextView) findViewById(R.id.widgetPrecip);
        sRiseTextView = (TextView) findViewById(R.id.widgetSunrise);
        sSetTextView = (TextView) findViewById(R.id.widgetSunset);

        tempTextView.setText(today.getTemperature() + " \u2103");
        descTextView.setText(today.getDescription().toUpperCase());
        humiTextView.setText(getString(R.string.humidity) + " " + today.getHumidity() + " %");
        presTextView.setText(getString(R.string.pressure) + " " + today.getPressure()+ " hPa");
        windTextView.setText(getString(R.string.wind) + " " + today.getWind()+ "m/s");
        precipTextView.setText(getString(R.string.precip) + " " + today.getPrecipitations());
        sRiseTextView.setText(getString(R.string.sunrise) + " " + today.getSunrise());
        sSetTextView.setText(getString(R.string.sunset) + " " + today.getSunset());
        try {
            iconImageView.setImageBitmap(getBitmapFromAssets(today.getIcon()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        weatherAdapter = new ListWeatherAdapter(listWeather, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(weatherAdapter);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_city:
                startActivity(new Intent(MainActivity.this, ChangeCityActivity.class));
                return true;

            case R.id.plan_a_trip:
                startActivity(new Intent(MainActivity.this, FormActivity.class));
                return true;

            case R.id.my_trips:
                startActivity(new Intent(MainActivity.this, MyTripsActivity.class));
                return true;

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
