package com.example.gps_app;

import com.google.android.gms.maps.model.Marker;

public class MyLocation {
    private final double latitude, longitude;
    private final String locationName;
    private final double timeSpend;
    private static float distanceTraveled;
    private String lastTime;
    private Marker marker;

    public MyLocation(double latitude, double longitude, String locationName, double timeSpend,
                      String lastTime, Marker marker) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.timeSpend = timeSpend;
        this.lastTime = lastTime;
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

    public double getTimeSpend() {
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

    public String getLastTime() {
        return lastTime;
    }

    public Marker getMarker() {
        return marker;
    }

}
