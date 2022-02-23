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
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class GPSLocation implements LocationListener {
    private static final String TAG = "test";
    double distance = 0;
    Geocoder geocoder;
    GlobalVariables globalVariables;
    MainActivity mainActivity;
    Marker currentMarker;
    Map.Entry<Location, MyLocation> oldLocation;


    GPSLocation(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        globalVariables = (GlobalVariables) mainActivity.getApplication();
        geocoder = new Geocoder(mainActivity, Locale.getDefault());
        if (!globalVariables.isLandScape()){
            setUpResetButton();
            setUpSeekBar();
        }
        startTimer();
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
            if (globalVariables.getToggleButton_track().isChecked()){
                updateDistance(location);
                updateGlobalVariables(location);
                updateViews(location);
                if (globalVariables.getPreviousLocation() != null){
                    updateDurationTo(globalVariables.getPreviousLocation());
                    addMarkerTo(globalVariables.getPreviousLocation());
                }
                if (globalVariables.getToggleButton_camera().isChecked()) {
                    moveCameraTo(location);
                }
                drawPrimaryLinePath(globalVariables.getLocationsMap().keySet());
                globalVariables.setPreviousLocation(location);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateGlobalVariables(Location location) throws IOException {
        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String locationName = addresses.get(0).getAddressLine(0);
        long startTime = System.currentTimeMillis();
        Marker marker = currentMarker;
        MyLocation myLocation = new MyLocation(latitude, longitude, locationName, startTime, marker);

        removeOldLocation(location);
        globalVariables.getLocationsMap().put(location, myLocation);

        Log.d(TAG + "3", "updateGlobalVariables: " + globalVariables.getLocationsMap().size() + " -> " + globalVariables.getLocationsMap());
//        Log.d(TAG + "3", "updateGlobalVariables: " + oldLocation + " -> ");
    }


    public void removeOldLocation(Location location){
        for (Map.Entry<Location, MyLocation> entry: globalVariables.getLocationsMap().entrySet()){
            if (entry.getKey().getLatitude() == location.getLatitude()
             && entry.getKey().getLongitude() == location.getLongitude()){
                oldLocation = entry;
            }
        }

        if (oldLocation != null){
            globalVariables.getLocationsMap().get(oldLocation.getKey()).getMarker().remove();
            globalVariables.getLocationsMap().remove(oldLocation.getKey());
        }
    }

    public void updateDistance(Location location){
        if (globalVariables.getPreviousLocation() != null)
            MyLocation.addDistance(location.distanceTo(globalVariables.getPreviousLocation()));
    }

    public void updateViews(Location location) throws IOException {
        MyLocation myLocation = globalVariables.getLocationsMap().get(location);
        globalVariables.getTextView_latLon().setText("(" + myLocation.getLatitude() + ", " + myLocation.getLongitude() + ")");
        globalVariables.getTextView_locationName().setText(myLocation.getLocationName());
        globalVariables.getTextView_distance().setText(MyLocation.getDistanceTraveled() + " meters");
//        globalVariables.getTextView_distance().setText(
////                getTimeStamp(System.currentTimeMillis()) +
//                myLocation.getLastTime()
//                + "");
        globalVariables.setCurrentLocation(location);
    }

    public static String getTimeStamp(long milliSecond, String pattern){
        return new SimpleDateFormat(pattern, Locale.getDefault())
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

    public void addMarkerTo(Location location){
        if (location == null) return;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
//        currentMarker = globalVariables.getGoogleMap().addMarker(markerOptions);
        Marker marker = globalVariables.getGoogleMap().addMarker(markerOptions);
        assert marker != null;
        MyLocation myLocation = globalVariables.getLocationsMap().get(globalVariables.getPreviousLocation());
        marker.setTitle("(" + myLocation.getLatitude() + ", " + myLocation.getLongitude() + ")");
        marker.setSnippet("last visit: " + getTimeStamp(myLocation.getStartTime(), "h:mm:ss a") + "\n" +
                          "Time spend: " + getTimeStamp(myLocation.getTimeSpend(), "mm:ss"));
        globalVariables.getLocationsMap().get(location).setMarker(marker);
    }

    public void updateDurationTo(Location location){
        if (location == null) return;
        long time = System.currentTimeMillis() - globalVariables.getLocationsMap().get(location).getStartTime();
        if (oldLocation != null){
            time += oldLocation.getValue().getTimeSpend();
        }
        globalVariables.getLocationsMap().get(location).setTimeSpend(time);
    }

    public void moveCameraTo(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GlobalVariables.cameraZoomLevel));
    }

    public void setUpResetButton(){
        globalVariables.getButton_reset().setOnClickListener(v -> {
            MyLocation.resetDistance();
            globalVariables.getGoogleMap().clear();
            globalVariables.getTextView_distance().setText(MyLocation.getDistanceTraveled() + " meters");
            globalVariables.getLocationsMap().clear();
            globalVariables.setPreviousLocation(null);
            globalVariables.setStartTime(System.currentTimeMillis());
            globalVariables.setElapsedTime(0);
        });
    }

    public void setUpSeekBar(){
        globalVariables.getSeekBar_cameraZoom().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

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

    private void drawPrimaryLinePath(Set<Location> listLocsToDraw) {
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

//  globalVariables.getTextView_duration().setText(getTimeStamp(System.currentTimeMillis()));
    private void startTimer(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mainActivity.runOnUiThread(() -> {
//                    long time = globalVariables.getStartTime() - System.currentTimeMillis();
                    long elapsedTime = System.currentTimeMillis() - globalVariables.getStartTime();
                    long elapsedSeconds = elapsedTime / 1000;
                    long elapsedMinutes = elapsedSeconds / 60;
                    long elapsedHour = elapsedMinutes / 60;
                    globalVariables.getTextView_duration().setText(String.format("Timer: %s:%s:%s", elapsedHour, elapsedMinutes, elapsedSeconds % 60));
//                    globalVariables.getTextView_duration().setText(getTimeStamp(elapsedTime, "hh:mm:ss"));
                    globalVariables.setElapsedTime(elapsedTime);
                });
            }
        }, 0, 1000);
    }
}
