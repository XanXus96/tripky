package com.xanxus.tripky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.CameraPosition;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.data.FullRoute;
import com.tomtom.online.sdk.routing.data.RouteQuery;
import com.tomtom.online.sdk.routing.data.RouteQueryBuilder;
import com.tomtom.online.sdk.routing.data.TravelMode;
import com.xanxus.tripky.R;
import com.xanxus.tripky.model.Prediction;

import java.util.Date;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TomtomMap map;
    private LatLng dep,arr;
    private String travelModeString;
    private Long departAt, arrivalAt;
    private Prediction depart, arrival;
    private Button nextButton;

    private HashMap<String, TravelMode> travelMode = new HashMap<String, TravelMode>() {{
        put("PEDESTRIAN", TravelMode.PEDESTRIAN);
        put("BICYCLE", TravelMode.BICYCLE);
        put("BUS", TravelMode.BUS);
        put("CAR", TravelMode.CAR);
        put("MOTORCYCLE", TravelMode.MOTORCYCLE);
        put("TAXI", TravelMode.TAXI);
        put("VAN", TravelMode.VAN);
        put("TRUCK", TravelMode.TRUCK);
        put("OTHER", TravelMode.OTHER);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle("Route Map");

        //get the depart location the arrival location the travel mode and the depart datetime
        //from the intent bundle
        Bundle b = getIntent().getExtras();
        depart = (Prediction) b.getSerializable("depart");
        arrival = (Prediction) b.getSerializable("arrival");
        travelModeString = b.getString("travelMode");
        departAt = b.getLong("departAt");

        dep = new LatLng(depart.getLat(), depart.getLon());
        arr = new LatLng(arrival.getLat(), arrival.getLon());

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        try {
            mapFragment.getAsyncMap(this);
        } catch (Exception e) {
            finish();
            return;
        }

        //retrieving the button
        nextButton = findViewById(R.id.nextButton2);

        nextButton.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), TripWeatherActivity.class);
            //put the depart location the arrival location the depart and the fetched arrival
            //datetime as an extra to the intent bundle
            i.putExtra("depart", depart);
            i.putExtra("arrival", arrival);
            i.putExtra("departAt", departAt);
            i.putExtra("arrivalAt", arrivalAt);
            startActivity(i);
        });
    }


    @Override
    public void onMapReady(@NonNull TomtomMap tomtomMap) {
        this.map = tomtomMap;
        //put a marker on the depart location
        SimpleMarkerBalloon balloon = new SimpleMarkerBalloon(depart.getAddress());
        MarkerBuilder depMarker = new MarkerBuilder(dep).markerBalloon(balloon);
        tomtomMap.addMarker(depMarker);
        //put a marker on the arrival location
        balloon = new SimpleMarkerBalloon(arrival.getAddress());
        MarkerBuilder arrMarker = new MarkerBuilder(arr).markerBalloon(balloon);
        tomtomMap.addMarker(arrMarker);
        //draw the route
        drawRoute();

    }

    public void drawRoute() {
        //search for the route using tomtom routing api
        RoutingApi routingApi = OnlineRoutingApi.create(getApplicationContext());
        RouteQuery routeQuery = new RouteQueryBuilder(dep, arr)
                .withTravelMode(travelMode.get(travelModeString))
                .withDepartAt(new Date(departAt))
                .build();
        routingApi.planRoute(routeQuery)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(routeResult -> {
                    for (FullRoute fullRoute : routeResult.getRoutes()) {
                        RouteBuilder routeBuilder = new RouteBuilder(
                                fullRoute.getCoordinates()).isActive(true);
                        //add the route to the map
                        map.addRoute(routeBuilder);
                        //center the camera to the depart location
                        map.centerOn(CameraPosition.builder(dep).zoom(9).build());
                        //get the arrival time resulting from the api
                        arrivalAt = fullRoute.getSummary().getArrivalTime().getTime();
                    }
                });
    }
}
