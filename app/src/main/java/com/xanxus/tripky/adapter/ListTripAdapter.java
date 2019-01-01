package com.xanxus.tripky.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xanxus.tripky.R;
import com.xanxus.tripky.model.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ListTripAdapter extends RecyclerView.Adapter<ListTripAdapter.MyViewHolder> {

    private List<JSONObject> tripList;
    private Context context;
    private JSONObject mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private View myTripsView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
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
        private TextView widgetTitle;


        public MyViewHolder(View view) {
            super(view);

            widgetTitle = (TextView) view.findViewById(R.id.widgetTitle);

            widgetCity = (TextView) view.findViewById(R.id.widgetCity);
            widgetTemperature = (TextView) view.findViewById(R.id.widgetTemperature);
            widgetDescription = (TextView) view.findViewById(R.id.widgetDescription);
            widgetHumidity = (TextView) view.findViewById(R.id.widgetHumidity);
            widgetPressure = (TextView) view.findViewById(R.id.widgetPressure);
            widgetWind = (TextView) view.findViewById(R.id.widgetWind);
            widgetTime = (TextView) view.findViewById(R.id.widgetTime);
            widgetPrecip = (TextView) view.findViewById(R.id.widgetPrecip);
            widgetIcon = (ImageView) view.findViewById(R.id.widgetIcon);

            widgetCity2 = (TextView) view.findViewById(R.id.widgetCity2);
            widgetTemperature2 = (TextView) view.findViewById(R.id.widgetTemperature2);
            widgetDescription2 = (TextView) view.findViewById(R.id.widgetDescription2);
            widgetHumidity2 = (TextView) view.findViewById(R.id.widgetHumidity2);
            widgetPressure2 = (TextView) view.findViewById(R.id.widgetPressure2);
            widgetWind2 = (TextView) view.findViewById(R.id.widgetWind2);
            widgetTime2 = (TextView) view.findViewById(R.id.widgetTime2);
            widgetPrecip2 = (TextView) view.findViewById(R.id.widgetPrecip2);
            widgetIcon2 = (ImageView) view.findViewById(R.id.widgetIcon2);

        }
    }

    public ListTripAdapter(List<JSONObject> tripList, Context context, View view) {
        this.tripList = tripList;
        this.context = context;
        myTripsView = view;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_trips_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JSONObject trip = tripList.get(position);
        JSONObject depart, arrival;
        SimpleDateFormat f = new SimpleDateFormat("EEE d MMM yyyy HH:mm");
        try {
            depart = trip.getJSONObject("depart");
            arrival = trip.getJSONObject("arrival");

            holder.widgetTitle.setText("Trip " + (position+1));

            holder.widgetCity.setText("DEPART FROM : " + depart.getString("city"));
            holder.widgetTemperature.setText(depart.getString("temperature") + " \u2103");
            holder.widgetDescription.setText(depart.getString("description").toUpperCase());
            holder.widgetHumidity.setText(context.getResources().getString(R.string.humidity) + " " + depart.getString("humidity") + " %");
            holder.widgetPressure.setText(context.getResources().getString(R.string.pressure) + " " + depart.getString("pressure") + " hPa");
            holder.widgetWind.setText(context.getResources().getString(R.string.wind) + " " + depart.getString("wind") + "m/s");
            holder.widgetPrecip.setText(context.getResources().getString(R.string.precip) + " " + depart.getString("precips"));
            holder.widgetTime.setText(f.format(new Date(depart.getLong("date"))));

            holder.widgetCity2.setText("ARRIVAL IN : " + arrival.getString("city"));
            holder.widgetTemperature2.setText(arrival.getString("temperature") + " \u2103");
            holder.widgetDescription2.setText(arrival.getString("description").toUpperCase());
            holder.widgetHumidity2.setText(context.getResources().getString(R.string.humidity) + " " + arrival.getString("humidity") + " %");
            holder.widgetPressure2.setText(context.getResources().getString(R.string.pressure) + " " + arrival.getString("pressure") + " hPa");
            holder.widgetWind2.setText(context.getResources().getString(R.string.wind) + " " + arrival.getString("wind") + "m/s");
            holder.widgetPrecip2.setText(context.getResources().getString(R.string.precip) + " " + arrival.getString("precips"));
            holder.widgetTime2.setText(f.format(new Date(arrival.getLong("date"))));

            holder.widgetIcon.setImageBitmap(getBitmapFromAssets(depart.getString("icon")));
            holder.widgetIcon2.setImageBitmap(getBitmapFromAssets(arrival.getString("icon")));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public Bitmap getBitmapFromAssets(String fileName) throws IOException {
        AssetManager assetManager = context.getAssets();

        InputStream istr = assetManager.open(fileName+".png");
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        istr.close();

        return bitmap;
    }

    public void removeItem(int position) throws IOException {
        mRecentlyDeletedItem = tripList.get(position);
        mRecentlyDeletedItemPosition = position;
        tripList.remove(position);
        notifyItemRemoved(position);
        updateTrips();
        showUndoSnackbar();
    }

    public void restoreItem() throws IOException {
        tripList.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        updateTrips();
    }

    public Context getContext() {return context;}

    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(myTripsView, "Trip removed",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("undo", v -> {
            try {
                restoreItem();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        snackbar.show();
    }

    private void updateTrips() throws IOException {
        FileOutputStream fos = context.openFileOutput("trips.json",MODE_PRIVATE);
        for (JSONObject o : tripList) {
            fos.write(o.toString().getBytes());
        }
        fos.close();
    }
}
