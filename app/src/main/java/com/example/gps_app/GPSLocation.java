package com.example.gps_app;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
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


    GPSLocation(MainActivity mainActivity){
        globalVariables = (GlobalVariables) mainActivity.getApplication();
        geocoder = new Geocoder(mainActivity, Locale.getDefault());
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) { }

    @Override
    public void onProviderDisabled(@NonNull String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
//            currentLocation = location;
//            Log.d(TAG, "onLocationChanged: " + " updated");
//            Log.d(TAG, MessageFormat.format("run: {0} \n\n-> ({1}, {2})", addresses.get(0).getAddressLine(0), location.getLatitude(), location.getLongitude()));

            updateDistance(location);
            updateViews(location);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            addMarkerTo(latLng);
//            moveCameraTo(latLng);
            prevLocation = location;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateDistance(Location location){
        if (prevLocation == null) prevLocation = location;
        distance += location.distanceTo(prevLocation);
    }

    public void updateViews(Location location) throws IOException {
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String locationName = addresses.get(0).getAddressLine(0);

        globalVariables.getTextView_lat().setText("lat: " + lat);
        globalVariables.getTextView_lon().setText("lon: " + lon);
        globalVariables.getTextView_locationName().setText("location name: " + locationName);
        globalVariables.getTextView_distance().setText("distance: " + distance);
        globalVariables.setCurrentLocation(location);
    }

    public void addMarkerTo(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        globalVariables.getGoogleMap().addMarker(markerOptions);
    }

    public void moveCameraTo(LatLng latLng){
        globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }
}
