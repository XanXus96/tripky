package com.xanxus.tripky.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather {

    private String city;
    private Date date;
    private String temperature;
    private String description;
    private String wind;
    private String pressure;
    private String humidity;
    private String precipitations;
    private String id;
    private String icon;
    private String sunrise;
    private String sunset;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }



    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getSunrise(){
        return this.sunrise;
    }

    public void setSunrise(Long sunrise) {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        this.sunrise = f.format(new Date(sunrise));
    }

    public String getSunset(){
        return this.sunset;
    }

    public void setSunset(Long sunset) {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        this.sunset = f.format(new Date(sunset));
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getDate(){
        return this.date;
    }

    public void setDate(Long date) { this.date = new Date(date); }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPrecipitations() {
        return precipitations;
    }

    public void setPrecipitations(String precipitations) {
        this.precipitations = precipitations;
    }
}
