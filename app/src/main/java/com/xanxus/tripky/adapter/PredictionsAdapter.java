package com.xanxus.tripky.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.xanxus.tripky.R;
import com.xanxus.tripky.model.Prediction;

import java.util.ArrayList;

public class PredictionsAdapter extends ArrayAdapter<Prediction> implements Filterable {

    private Context context;
    private int resource;

    private ArrayList<Prediction> predictions;

    public PredictionsAdapter(Context context, int resource, ArrayList<Prediction> predictions) {
        super(context, resource, predictions);

        this.context = context;
        this.resource = resource;
        this.predictions = predictions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(resource, parent, false);
        TextView address=(TextView)itemView.findViewById(R.id.predAddress);
        address.setText(predictions.get(position).getAddress());

        return itemView;

    }


}