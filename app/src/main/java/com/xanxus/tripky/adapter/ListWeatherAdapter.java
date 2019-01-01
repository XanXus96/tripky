package com.xanxus.tripky.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xanxus.tripky.R;
import com.xanxus.tripky.model.Weather;

import java.io.IOException;
import java.io.InputStream;
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
            tempTextView = (TextView) view.findViewById(R.id.widgetTemperature);
            descTextView = (TextView) view.findViewById(R.id.widgetDescription);
            humiTextView = (TextView) view.findViewById(R.id.widgetHumidity);
            presTextView = (TextView) view.findViewById(R.id.widgetPressure);
            windTextView = (TextView) view.findViewById(R.id.widgetWind);
            dateTextView = (TextView) view.findViewById(R.id.widgetDate);
            precipTextView = (TextView) view.findViewById(R.id.widgetPrecip);
            sRiseTextView = (TextView) view.findViewById(R.id.widgetSunrise);
            sSetTextView = (TextView) view.findViewById(R.id.widgetSunset);
            iconImageView = (ImageView) view.findViewById(R.id.widgetIcon);
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
            holder.iconImageView.setImageBitmap(getBitmapFromAssets(weather.getIcon()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public Bitmap getBitmapFromAssets(String fileName) throws IOException {
        AssetManager assetManager = context.getAssets();

        InputStream istr = assetManager.open(fileName+".png");
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        istr.close();

        return bitmap;
    }


}
