package com.xanxus.tripky.model;

import java.io.Serializable;

public class Prediction implements Serializable {

    private String mcc, address;
    private double lon, lat;

    public Prediction(String address, double lon, double lat) {
        this.address = address;
        this.lon = lon;
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }
}
