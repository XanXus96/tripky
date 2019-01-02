package com.xanxus.tripky.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xanxus.tripky.R;
import com.xanxus.tripky.adapter.PredictionsAdapter;
import com.xanxus.tripky.asyncTask.GetPredictionsTask;
import com.xanxus.tripky.model.Prediction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class FormActivity extends AppCompatActivity {

    private EditText searchDepLocView, searchArrLocView;
    private ListView predsListView, predsList2View;
    private  TextView departLocView, arrivalLocView;
    private Spinner travelModeSpinner, daySpinner, monthSpinner, yearSpinner, hourSpinner, minutesSpinner;
    private Button nextButton;

    private ArrayList<Prediction> predictions;
    private PredictionsAdapter adapter, adapter2;

    private Prediction depart, arrival;
    private Long departAt;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        setTitle("Plan a Trip");

        //retrieving the views
        searchDepLocView = findViewById(R.id.searchDepLoc);
        predsListView = findViewById(R.id.predsList);
        departLocView = findViewById(R.id.departLoc);

        searchArrLocView = findViewById(R.id.searchArrLoc);
        predsList2View = findViewById(R.id.predsList2);
        arrivalLocView = findViewById(R.id.arrivaLoc);

        travelModeSpinner = findViewById(R.id.travelMode);
        daySpinner = findViewById(R.id.day);
        monthSpinner = findViewById(R.id.month);
        yearSpinner = findViewById(R.id.year);
        hourSpinner = findViewById(R.id.hour);
        minutesSpinner = findViewById(R.id.minutes);

        nextButton = findViewById(R.id.nextButton);

        //used to control the keyboard
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        //to search for a depart location
        searchDepLocView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //at first the predictions list is hidden then when first text change takes place we switch it to be visible
                if (predsListView.getVisibility() == View.INVISIBLE) predsListView.setVisibility(View.VISIBLE);
                try {
                    //call the async task on every text change on the search input and storing the result
                    predictions = new GetPredictionsTask(getBaseContext()).execute(s.toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //initiate the adapter on every text change with the retrieved predictions
                adapter = new PredictionsAdapter(getBaseContext(), R.layout.list_preds_item, predictions);
                //set the adapter to the view
                predsListView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //to choose a depart location
        predsListView.setOnItemClickListener((parent, view, position, id) -> {
            //store the data associated with the specified prediction in the list
            depart = (Prediction) parent.getItemAtPosition(position);
            //hide the keyboard
            imm.hideSoftInputFromWindow(searchDepLocView.getWindowToken(), 0);
            //hide the list
            predsListView.setVisibility(View.GONE);
            //set the search field text to the full address description for further edit
            searchDepLocView.setText(depart.getAddress());
            //hide the search field
            searchDepLocView.setVisibility(View.GONE);
            //set the text view content to the selected address
            departLocView.setText(depart.getAddress());
            //set the text view to visible
            departLocView.setVisibility(View.VISIBLE);
            //if both depart and arrival locations are set the set "next" button to clickable and change its color
            if (arrivalLocView.getVisibility() == View.VISIBLE) {
                nextButton.setBackgroundResource(R.color.colorPrimary);
                nextButton.setClickable(true);
            }
        });

        //to edit the depart location
        departLocView.setOnTouchListener((v, event) -> {
            //switch the serch field to visible
            searchDepLocView.setVisibility(View.VISIBLE);
            //set the cursor to the end of the text
            searchDepLocView.setSelection(searchDepLocView.getText().length());
            //switch the predictions list to visible
            predsListView.setVisibility(View.VISIBLE);
            //hide the text view
            departLocView.setVisibility(View.GONE);
            return true;
        });


        //to search for an arrival location (same way as the depart one)
        searchArrLocView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (predsList2View.getVisibility() == View.INVISIBLE) predsList2View.setVisibility(View.VISIBLE);
                try {
                    predictions = new GetPredictionsTask(getBaseContext()).execute(s.toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                adapter2 = new PredictionsAdapter(getBaseContext(), R.layout.list_preds_item, predictions);
                predsList2View.setAdapter(adapter2);

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //to choose an arrival location (same way as the depart one)
        predsList2View.setOnItemClickListener((parent, view, position, id) -> {
            arrival = (Prediction) parent.getItemAtPosition(position);
            imm.hideSoftInputFromWindow(searchArrLocView.getWindowToken(), 0);
            predsList2View.setVisibility(View.GONE);
            searchArrLocView.setText(arrival.getAddress());
            searchArrLocView.setVisibility(View.GONE);
            arrivalLocView.setText(arrival.getAddress());
            arrivalLocView.setVisibility(View.VISIBLE);
            if (departLocView.getVisibility() == View.VISIBLE) {
                nextButton.setBackgroundResource(R.color.colorPrimary);
                nextButton.setClickable(true);
            }
        });

        //to edit the arrival location (same way as the depart one)
        arrivalLocView.setOnTouchListener((v, event) -> {
            searchArrLocView.setVisibility(View.VISIBLE);
            searchArrLocView.setSelection(searchArrLocView.getText().length());
            predsList2View.setVisibility(View.VISIBLE);
            arrivalLocView.setVisibility(View.GONE);
            return true;
        });


        nextButton.setOnClickListener(v -> {
            //retrieve the depart datetime and parse it to Date object
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            StringBuilder sb = new StringBuilder(yearSpinner.getSelectedItem().toString() + "-" +
                    monthSpinner.getSelectedItem().toString() + "-" +
                    daySpinner.getSelectedItem().toString() + " " +
                    hourSpinner.getSelectedItem().toString() + ":" +
                    minutesSpinner.getSelectedItem().toString());
            try {
                departAt = format.parse(sb.toString()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long currentDate = new Date().getTime();

            //check if the specified date time is in the future
            if (currentDate < departAt) {
                Intent i = new Intent(getBaseContext(), RouteActivity.class);
                //put the depart location the arrival location the travel mode and the depart datetime
                //as an extra to the intent bundle
                i.putExtra("depart", depart);
                i.putExtra("arrival", arrival);
                i.putExtra("travelMode", travelModeSpinner.getSelectedItem().toString());
                i.putExtra("departAt", departAt);
                startActivity(i);
            } else {
                Toast.makeText(this, "The depart date should be in the future", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
