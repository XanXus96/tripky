package com.xanxus.tripky.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ChangeCityActivity extends AppCompatActivity {

    private PredictionsAdapter adapter;
    private Prediction pred;
    private ArrayList<Prediction> predictions;
    private EditText searchLocView;
    private ListView predsList3View;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_city);
        setTitle("Change City");

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        searchLocView = (EditText) findViewById(R.id.searchLoc);
        predsList3View = (ListView) findViewById(R.id.predsList3);

        searchLocView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    predictions = new GetPredictionsTask(getBaseContext()).execute(s.toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                adapter = new PredictionsAdapter(getBaseContext(), R.layout.list_preds_item, predictions);
                predsList3View.setAdapter(adapter);

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        predsList3View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                pred = (Prediction) parent.getItemAtPosition(position);
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("pred", pred);
                startActivity(i);
            }
        });

    }
}
