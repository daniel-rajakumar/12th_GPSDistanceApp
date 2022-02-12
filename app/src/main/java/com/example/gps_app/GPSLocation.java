package com.example.gps_app;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

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
        Log.d(TAG, "onLocationChanged: " + " updated");
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.d(TAG, MessageFormat.format("run: {0} \n\n-> ({1}, {2})", addresses.get(0).getAddressLine(0), location.getLatitude(), location.getLongitude()));
            double lat1 = location.getLatitude();
            double lon1 = location.getLongitude();
            if (prevLocation == null) prevLocation = location;
            distance += location.distanceTo(prevLocation);
            String locationName = addresses.get(0).getAddressLine(0);
//                String distance = Location.distanceBetween(lat1, lon1);

            globalVariables.getTextView_lat().setText("lat: " + lat1);
            globalVariables.getTextView_lon().setText("lon: " + lon1);
            globalVariables.getTextView_locationName().setText("location name: " + locationName);
            globalVariables.getTextView_distance().setText("distance: " + distance);

            prevLocation = location;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
