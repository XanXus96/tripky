package com.xanxus.tripky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.xanxus.tripky.R;
import com.xanxus.tripky.adapter.PredictionsAdapter;
import com.xanxus.tripky.asyncTask.GetPredictionsTask;
import com.xanxus.tripky.model.Prediction;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ChangeCityActivity extends AppCompatActivity {

    private PredictionsAdapter adapter;
    private Prediction pred;
    private ArrayList<Prediction> predictions;
    private EditText searchLocView;
    private ListView predsList3View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_city);
        setTitle("Change City");

        //retrieving the views
        searchLocView = findViewById(R.id.searchLoc);
        predsList3View = findViewById(R.id.predsList3);

        searchLocView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                predsList3View.setAdapter(adapter);

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        predsList3View.setOnItemClickListener((parent, view, position, id) -> {
            //get data associated with the specified prediction in the list
            pred = (Prediction) parent.getItemAtPosition(position);
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            //put the prediction as an extra to the intent bundle
            i.putExtra("pred", pred);
            //start the new activity
            startActivity(i);
        });

    }
}
