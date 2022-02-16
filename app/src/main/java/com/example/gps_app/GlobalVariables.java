package com.example.gps_app;

import android.app.Application;
import android.location.Location;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

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
    private ToggleButton toggleButton_camera, toggleButton_track;
    private Button button_reset;
    private SeekBar seekBar_cameraZoom;
    static float cameraZoomLevel = 15;


    public void setValues(@NonNull MainActivity mainActivity) {
        textView_latLon = mainActivity.findViewById(R.id.textView_latLon);
        textView_locationName = mainActivity.findViewById(R.id.textView_locationName);
        textView_distance = mainActivity.findViewById(R.id.textView_distance);
        mapView = mainActivity.findViewById(R.id.mapView);
        toggleButton_camera = mainActivity.findViewById(R.id.toggleButton_camera);
        toggleButton_track = mainActivity.findViewById(R.id.toggleButton_track);
        button_reset = mainActivity.findViewById(R.id.button_reset);
        seekBar_cameraZoom = mainActivity.findViewById(R.id.seekBar_cameraZoom);
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

    public ToggleButton getToggleButton_camera() {
        return toggleButton_camera;
    }

    public ToggleButton getToggleButton_track() {
        return toggleButton_track;
    }

    public Button getButton_reset() {
        return button_reset;
    }

    public SeekBar getSeekBar_cameraZoom() {
        return seekBar_cameraZoom;
    }
}
