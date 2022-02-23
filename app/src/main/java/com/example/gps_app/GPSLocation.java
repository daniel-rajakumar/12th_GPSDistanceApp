package com.example.gps_app;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

class GPSLocation implements LocationListener {
    private static final String TAG = "test";
    double distance = 0;
    Geocoder geocoder;
    GlobalVariables globalVariables;
    Location prevLocation;
    MainActivity mainActivity;
    Marker currentMarker;
    MyLocation myLocation;


    GPSLocation(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        globalVariables = (GlobalVariables) mainActivity.getApplication();
        geocoder = new Geocoder(mainActivity, Locale.getDefault());
        if (!globalVariables.isLandScape()){
            setUpResetButton();
            setUpSeekBar();
        }
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
        Log.d(TAG, "manager: " + location.getLatitude() + ", " + location.getLongitude());
        try {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (globalVariables.getToggleButton_track().isChecked()){
                updateDistance(location);
                updateGlobalVariables(location);
                updateViews(location);
                addMarkerTo(latLng);
                if (globalVariables.getToggleButton_camera().isChecked()) {
                    moveCameraTo(latLng);
                }
                drawPrimaryLinePath(globalVariables.getLocationsMap().keySet());
            }
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
        String lastTime = getTimeStamp(System.currentTimeMillis());
        Marker marker = currentMarker;
        myLocation = new MyLocation(latitude, longitude, locationName, 0, lastTime, marker);
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
        globalVariables.getTextView_distance().setText(
//                getTimeStamp(System.currentTimeMillis()) +
                myLocation.getLastTime()
                + "");
        globalVariables.setCurrentLocation(location);
    }

    public static String getTimeStamp(long milliSecond){
        return new SimpleDateFormat("h:mm:ss a", Locale.getDefault())
                    .format(new Date(milliSecond));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static MyLocation getMyLocationInfo(MainActivity mainActivity, LatLng latLng){
        GlobalVariables globalVariables = (GlobalVariables) mainActivity.getApplication();
        for (Map.Entry<Location, MyLocation> entry: globalVariables.getLocationsMap().entrySet()){
            if (latLng.latitude  == entry.getKey().getLatitude()
             && latLng.longitude == entry.getKey().getLongitude()){
                return entry.getValue();
            }
        }
        return null;
    }

    public void addMarkerTo(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
//        currentMarker = globalVariables.getGoogleMap().addMarker(markerOptions);
        Marker marker = globalVariables.getGoogleMap().addMarker(markerOptions);
        assert marker != null;
        marker.setTitle("(" + myLocation.getLatitude() + ", " + myLocation.getLongitude() + ")");
        marker.setSnippet("last visit: " + myLocation.getLastTime() + "\n" +
                    "");
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

    private void drawPrimaryLinePath(Set<Location> listLocsToDraw ) {
        if (globalVariables.getGoogleMap() == null) return;
        if (listLocsToDraw.size() < 2) return;

        PolylineOptions options = new PolylineOptions();

        options.color(ContextCompat.getColor(mainActivity, R.color.trackingColor));
        options.width(10);
        options.visible(true);

        for (Location locRecorded: listLocsToDraw) {
            options.add(new LatLng(locRecorded.getLatitude(), locRecorded.getLongitude()));
        }

        globalVariables.getGoogleMap().addPolyline(options);
    }
}
