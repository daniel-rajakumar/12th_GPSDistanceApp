package com.example.gps_app;

import com.google.android.gms.maps.model.Marker;

public class MyLocation {
    private final double latitude, longitude;
    private final String locationName;
    private long timeSpend;
    private static float distanceTraveled;
    private final long startTime;
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

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setTimeSpend(long timeSpend) {
        this.timeSpend = timeSpend;
    }

}
