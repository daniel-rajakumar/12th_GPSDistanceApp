package com.example.gps_app;

import android.app.Application;
import android.location.Location;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalVariables extends Application {

    private TextView textView_latLon, textView_locationName, textView_distance;
    private MapView mapView;
    private Location currentLocation;
    private GoogleMap googleMap;
    private Map<Location, MyLocation> locationsMap;

    public void setValues(MainActivity mainActivity) {
        textView_latLon = mainActivity.findViewById(R.id.textView_latLon);
        textView_locationName = mainActivity.findViewById(R.id.textView_locationName);
        textView_distance = mainActivity.findViewById(R.id.textView_distance);
        mapView = mainActivity.findViewById(R.id.mapView);
        locationsMap = new LinkedHashMap<>();
    }

    public TextView getTextView_latLon() {
        return textView_latLon;
    }

    public TextView getTextView_locationName() {
        return textView_locationName;
    }

    public TextView getTextView_distance() {
        return textView_distance;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public MapView getMapView() {
        return mapView;
    }

    public Map<Location, MyLocation> getLocationsMap() {
        return locationsMap;
    }

}
