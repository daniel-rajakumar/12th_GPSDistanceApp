package com.example.gps_app;

public class MyLocation {
    private double latitude, longitude;
    private String locationName;
    private double timeSpend;
    private static double distanceTraveled;

    public MyLocation(double latitude, double longitude, String locationName, double timeSpend) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.timeSpend = timeSpend;
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

    public static double getDistanceTraveled() {
        return distanceTraveled;
    }

    public static void addDistance(double distance){
        distanceTraveled += distance;
    }
}
