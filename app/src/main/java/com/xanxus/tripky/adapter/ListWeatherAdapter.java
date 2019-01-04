package com.xanxus.tripky.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xanxus.tripky.R;
import com.xanxus.tripky.helper.AppHelper;
import com.xanxus.tripky.model.Weather;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ListWeatherAdapter extends RecyclerView.Adapter<ListWeatherAdapter.MyViewHolder> {

    private List<Weather> weatherList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tempTextView,descTextView,humiTextView,presTextView,windTextView,precipTextView,sRiseTextView,
                sSetTextView,dateTextView;
        private ImageView iconImageView;

        public MyViewHolder(View view) {
            super(view);
            //retrieving the views of each list's item
            tempTextView = view.findViewById(R.id.widgetTemperature);
            descTextView = view.findViewById(R.id.widgetDescription);
            humiTextView = view.findViewById(R.id.widgetHumidity);
            presTextView = view.findViewById(R.id.widgetPressure);
            windTextView = view.findViewById(R.id.widgetWind);
            dateTextView = view.findViewById(R.id.widgetDate);
            precipTextView = view.findViewById(R.id.widgetPrecip);
            sRiseTextView = view.findViewById(R.id.widgetSunrise);
            sSetTextView = view.findViewById(R.id.widgetSunset);
            iconImageView = view.findViewById(R.id.widgetIcon);
        }
    }

    public ListWeatherAdapter(List<Weather> weatherList, Context context) {
        this.weatherList = weatherList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_weather_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        //setting the views content from the weather list
        holder.tempTextView.setText(weather.getTemperature() + " \u2103");
        holder.descTextView.setText(weather.getDescription().toUpperCase());
        holder.humiTextView.setText(context.getResources().getString(R.string.humidity) + " " + weather.getHumidity() + " %");
        holder.presTextView.setText(context.getResources().getString(R.string.pressure) + " " + weather.getPressure()+ " hPa");
        holder.windTextView.setText(context.getResources().getString(R.string.wind) + " " + weather.getWind()+ "m/s");
        holder.precipTextView.setText(context.getResources().getString(R.string.precip) + " " + weather.getPrecipitations());
        holder.sRiseTextView.setText(context.getResources().getString(R.string.sunrise) + " " + weather.getSunrise());
        holder.sSetTextView.setText(context.getResources().getString(R.string.sunset) + " " + weather.getSunset());
        SimpleDateFormat f = new SimpleDateFormat("EEE d MMM yyyy");
        holder.dateTextView.setText(f.format(weather.getDate()));
        try {
            holder.iconImageView.setImageBitmap(new AppHelper(context).getBitmapFromAssets(weather.getIcon()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }
}
