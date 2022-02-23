package com.example.gps_app;

import android.util.Log;

import com.google.android.gms.maps.model.Marker;

public class MyLocation {
    private static final String TAG = "test2";
    private final double latitude, longitude;
    private final String locationName;
    private long timeSpend;
    private static float distanceTraveled;
    private long startTime;
    private Marker marker;

    public MyLocation(double latitude, double longitude, String locationName,
                      long startTime, Marker marker) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.startTime = startTime;
        this.marker = marker;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public long getTimeSpend() {
        return timeSpend;
    }

    public static float getDistanceTraveled() {
        return distanceTraveled;
    }

    public static void addDistance(double distance){
        distanceTraveled += distance;
    }

    public static void resetDistance(){
        distanceTraveled = 0;
    }

    public long getStartTime() {
        return startTime;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setTimeSpend(long timeSpend) {
        Log.d(TAG, "setTimeSpend: " + timeSpend);
        this.timeSpend = timeSpend;
    }
}
