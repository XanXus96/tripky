package com.xanxus.tripky.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.xanxus.tripky.R;
import com.xanxus.tripky.adapter.PredictionsAdapter;
import com.xanxus.tripky.asyncTask.GetPredictionsTask;
import com.xanxus.tripky.model.Prediction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FormActivity extends AppCompatActivity {

    private PredictionsAdapter adapter, adapter2;
    private Prediction depart, arrival;
    private Long departAt;
    private ArrayList<Prediction> predictions;
    private EditText searchDepLocView, searchArrLocView;
    private ListView predsListView, predsList2View;
    private  TextView departLocView, arrivalLocView;
    private Spinner travelModeSpinner, daySpinner, monthSpinner, yearSpinner, hourSpinner, minutesSpinner;
    private Button nextButton;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        setTitle("Plan a Trip");

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        nextButton = (Button) findViewById(R.id.nextButton);

        searchDepLocView = (EditText) findViewById(R.id.searchDepLoc);
        predsListView = (ListView) findViewById(R.id.predsList);
        departLocView = (TextView) findViewById(R.id.departLoc);

        searchDepLocView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (predsListView.getVisibility() == View.INVISIBLE) predsListView.setVisibility(View.VISIBLE);
                try {
                    predictions = new GetPredictionsTask(getBaseContext()).execute(s.toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                adapter = new PredictionsAdapter(getBaseContext(), R.layout.list_preds_item, predictions);
                predsListView.setAdapter(adapter);

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        predsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                depart = (Prediction) parent.getItemAtPosition(position);
                searchDepLocView.setText(depart.getAddress());
                imm.hideSoftInputFromWindow(searchDepLocView.getWindowToken(), 0);
                predsListView.setVisibility(View.GONE);
                searchDepLocView.setVisibility(View.GONE);
                departLocView.setText(depart.getAddress());
                departLocView.setVisibility(View.VISIBLE);
                if (arrivalLocView.getVisibility() == View.VISIBLE){
                    nextButton.setBackgroundResource(R.color.colorPrimary);
                    nextButton.setClickable(true);
                }
            }
        });

        departLocView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchDepLocView.setVisibility(View.VISIBLE);
                searchDepLocView.setSelection(searchDepLocView.getText().length());
                predsListView.setVisibility(View.VISIBLE);
                departLocView.setVisibility(View.GONE);
                return true;
            }
        });


        searchArrLocView = (EditText) findViewById(R.id.searchArrLoc);
        predsList2View = (ListView) findViewById(R.id.predsList2);
        arrivalLocView = (TextView) findViewById(R.id.arrivaLoc);

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

        predsList2View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                arrival = (Prediction) parent.getItemAtPosition(position);
                searchArrLocView.setText(arrival.getAddress());
                imm.hideSoftInputFromWindow(searchArrLocView.getWindowToken(), 0);
                predsList2View.setVisibility(View.GONE);
                searchArrLocView.setVisibility(View.GONE);
                arrivalLocView.setText(arrival.getAddress());
                arrivalLocView.setVisibility(View.VISIBLE);
                if (departLocView.getVisibility() == View.VISIBLE){
                    nextButton.setBackgroundResource(R.color.colorPrimary);
                    nextButton.setClickable(true);
                }
            }
        });

        arrivalLocView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchArrLocView.setVisibility(View.VISIBLE);
                searchArrLocView.setSelection(searchArrLocView.getText().length());
                predsList2View.setVisibility(View.VISIBLE);
                arrivalLocView.setVisibility(View.GONE);
                return true;
            }
        });

        travelModeSpinner = (Spinner) findViewById(R.id.travelMode);
        daySpinner = (Spinner) findViewById(R.id.day);
        monthSpinner = (Spinner) findViewById(R.id.month);
        yearSpinner = (Spinner) findViewById(R.id.year);
        hourSpinner = (Spinner) findViewById(R.id.hour);
        minutesSpinner = (Spinner) findViewById(R.id.minutes);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Intent i = new Intent(getBaseContext(), RouteActivity.class);
                i.putExtra("depart", depart);
                i.putExtra("arrival", arrival);
                i.putExtra("travelMode", travelModeSpinner.getSelectedItem().toString());
                i.putExtra("departAt", departAt);
                startActivity(i);
            }
        });

    }
}
