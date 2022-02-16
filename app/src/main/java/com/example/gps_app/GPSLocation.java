package com.example.gps_app;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

class GPSLocation implements LocationListener {
    private static final String TAG = "test";
    double distance = 0;
    Geocoder geocoder;
    GlobalVariables globalVariables;
    Location prevLocation;
    MainActivity mainActivity;


    GPSLocation(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        globalVariables = (GlobalVariables) mainActivity.getApplication();
        geocoder = new Geocoder(mainActivity, Locale.getDefault());
        setUpResetButton();
        setUpSeekBar();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) { }

    @Override
    public void onProviderDisabled(@NonNull String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onLocationChanged(@NonNull Location location) {
//        updateCameraBearing(location.getBearing());
        Log.d(TAG, "onLocationChanged: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
        try {
            updateGlobalVariables(location);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (globalVariables.getToggleButton_track().isChecked()){
                updateDistance(location);
                addMarkerTo(latLng);
            }
            if (globalVariables.getToggleButton_camera().isChecked()) {
                moveCameraTo(latLng);
            }
            updateViews(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
        prevLocation = location;
    }

    public void updateGlobalVariables(Location location) throws IOException {
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String locationName = addresses.get(0).getAddressLine(0);
        MyLocation myLocation = new MyLocation(latitude, longitude, locationName, 0);
        globalVariables.getLocationsMap().put(location, myLocation);
    }

    public void updateDistance(Location location){
        if (prevLocation != null)
            MyLocation.addDistance(location.distanceTo(prevLocation));
    }

    public void updateViews(Location location) throws IOException {
        MyLocation myLocation = globalVariables.getLocationsMap().get(location);
        globalVariables.getTextView_latLon().setText("(" + myLocation.getLatitude() + ", " + myLocation.getLongitude() + ")");
        globalVariables.getTextView_locationName().setText(myLocation.getLocationName());
        globalVariables.getTextView_distance().setText(MyLocation.getDistanceTraveled() + " meters");
//        globalVariables.getTextView_distance().setText(myLocation.getLocationName() + "");
        globalVariables.setCurrentLocation(location);
    }

    public void addMarkerTo(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        globalVariables.getGoogleMap().addMarker(markerOptions);
    }

    public void moveCameraTo(LatLng latLng){
        globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GlobalVariables.cameraZoomLevel));
    }

    public void setUpResetButton(){
        globalVariables.getButton_reset().setOnClickListener(v -> {
            MyLocation.resetDistance();
            globalVariables.getGoogleMap().clear();
            globalVariables.getTextView_distance().setText(MyLocation.getDistanceTraveled() + " meters");
        });
    }

    public void setUpSeekBar(){
        globalVariables.getSeekBar_cameraZoom().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                GlobalVariables.cameraZoomLevel = seekBar.getProgress();
                Toast.makeText(mainActivity, "Zoom level: " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onStartTrackingTouch: " + seekBar.getProgress());
            }
        });
    }

    private void updateCameraBearing(float bearing) {
        CameraPosition camPos = CameraPosition
                .builder(
                        globalVariables.getGoogleMap().getCameraPosition() // current Camera
                )
                .bearing(bearing)
                .build();
        globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }
}
