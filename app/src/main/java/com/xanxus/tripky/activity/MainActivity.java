package com.xanxus.tripky.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.xanxus.tripky.asyncTask.CheckInternetTask;
import com.xanxus.tripky.asyncTask.GetWeatherTask;
import com.xanxus.tripky.helper.AppHelper;
import com.xanxus.tripky.model.Prediction;
import com.xanxus.tripky.model.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private TextView tempTextView, descTextView, humiTextView, presTextView, windTextView, precipTextView, sRiseTextView,
            sSetTextView;
    private ImageView iconImageView;

    private RecyclerView recyclerView;
    private ListWeatherAdapter weatherAdapter;

    private ArrayList<Weather> listWeather;
    private Weather today;

    public Location location;
    private Prediction pred;

    private AppHelper helper;

    //this activity cannot be reached if you didn't grant the permissions first although some functions in need of
    //permission require this annotation
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new AppHelper(this);

        //retrieving the views
        tempTextView = findViewById(R.id.widgetTemperature);
        descTextView = findViewById(R.id.widgetDescription);
        humiTextView = findViewById(R.id.widgetHumidity);
        presTextView = findViewById(R.id.widgetPressure);
        windTextView = findViewById(R.id.widgetWind);
        iconImageView = findViewById(R.id.widgetIcon);
        precipTextView = findViewById(R.id.widgetPrecip);
        sRiseTextView = findViewById(R.id.widgetSunrise);
        sSetTextView = findViewById(R.id.widgetSunset);

        //if we have a location coming from another activity (change city)
        if (getIntent().getExtras() != null) {
            pred = (Prediction) getIntent().getExtras().getSerializable("pred");
            setTitle(pred.getMcc());
        }else{
            //getting the current location
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //if we get the current location
            if (location != null) {
                pred = new Prediction("Tripky", location.getLongitude(), location.getLatitude());

                SearchApi searchApi = OnlineSearchApi.create(this);
                //getting the address of the current lat,lon using reverseGeocoding tomtom api
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
                                //no need to do anything address already set to tripky from initiation
                            }
                        });
            } else {
                //if we couldn't get the current location for any reason (note that the permission is absolutely granted)
                pred = new Prediction("London, UK", 51.50853, -0.12574);
                pred.setMcc("noLocation");
                setTitle(pred.getAddress());
            }
        }


        try {
            if (new CheckInternetTask(this).execute().get() && !pred.getMcc().equals("noLocation")) {
                //launch the async task and store the result
                listWeather = new GetWeatherTask(this, ",hourly").execute(String.valueOf(pred.getLat()), String.valueOf(pred.getLon()), null).get();
            } else {
                //if there is no internet get the data stored from the latest usage
                listWeather = jsonToWeather(helper.getObjectsFromFile("weather.json", false));
                setTitle(listWeather.get(0).getCity());
            }
            //get the first (current) object and remove it from the list to pass the list to the listView adapter
            today = listWeather.get(0);
            listWeather.remove(0);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        //setting the views content
        tempTextView.setText(today.getTemperature() + " \u2103");
        descTextView.setText(today.getDescription().toUpperCase());
        humiTextView.setText(getString(R.string.humidity) + " " + today.getHumidity() + " %");
        presTextView.setText(getString(R.string.pressure) + " " + today.getPressure()+ " hPa");
        windTextView.setText(getString(R.string.wind) + " " + today.getWind()+ "m/s");
        precipTextView.setText(getString(R.string.precip) + " " + today.getPrecipitations());
        sRiseTextView.setText(getString(R.string.sunrise) + " " + today.getSunrise());
        sSetTextView.setText(getString(R.string.sunset) + " " + today.getSunset());
        try {
            iconImageView.setImageBitmap(helper.getBitmapFromAssets(today.getIcon()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //retrieving the recyclerView (the weather forecast list) and some config
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //instantiating the adapter and setting it to the recyclerView
        weatherAdapter = new ListWeatherAdapter(listWeather, this);
        recyclerView.setAdapter(weatherAdapter);

    }


    //inflating the menu to the activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu buttons tasks
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

    private ArrayList<Weather> jsonToWeather(ArrayList<JSONObject> jsonList) {
        ArrayList<Weather> weatherList = new ArrayList<>();
        JSONObject jo = jsonList.get(0);

        for (int i = 0; i < 7; i++) {
            JSONObject j = null;
            try {
                j = jo.getJSONObject("weather" + i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Weather w = new Weather();
            try {
                w.setCity(j.getString("city"));
                w.setDescription(j.getString("description"));
                w.setTemperature(j.getString("temperature"));
                w.setIcon(j.getString("icon"));
                w.setHumidity(j.getString("humidity"));
                w.setPressure(j.getString("pressure"));
                w.setWind(j.getString("wind"));
                w.setPrecipitations(j.getString("precips"));
                w.setDate(j.getLong("date"));
                w.setSunrise(j.getString("sunrise"));
                w.setSunset(j.getString("sunset"));

                weatherList.add(w);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return weatherList;
    }
}
