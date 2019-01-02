package com.xanxus.tripky.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.xanxus.tripky.R;
import com.xanxus.tripky.adapter.ListTripAdapter;
import com.xanxus.tripky.helper.RecyclerItemTouchHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MyTripsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ListTripAdapter adapter;
    private List<JSONObject> jsonItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);
        setTitle("My Trips");

        jsonItems = new ArrayList<JSONObject>();
        //retrieve the saved trips from the local storage
        try {
            FileInputStream fis = openFileInput("trips.json");
            if (fis == null) return;
            InputStream stream = new BufferedInputStream(fis);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }
            StringBuilder json = new StringBuilder();
            int BracketCount = 0;
            for (char c: builder.toString().toCharArray())
            {
                if (c == '{')
                    ++BracketCount;
                else if (c == '}')
                    --BracketCount;
                json.append(c);

                if (BracketCount == 0 && c != ' ')
                {
                    jsonItems.add(new JSONObject(json.toString()));
                    json = new StringBuilder();

                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //retrieving the recyclerView (the weather forecast list) and some config
        recyclerView = findViewById(R.id.recycler_view2);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //instantiating the adapter and setting it to the recyclerView
        adapter = new ListTripAdapter(jsonItems, this, findViewById(R.id.my_trips_activity));
        recyclerView.setAdapter(adapter);

        //attach the touch helper to the recyclerView (for the swipe event)
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


}
