package com.example.gps_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

class GPSLocation implements LocationListener {
    private static final String TAG = "test";
    double distance = 0;
    Geocoder geocoder;
    GlobalVariables globalVariables;
    MainActivity mainActivity;
    Marker currentMarker;
    Map.Entry<Location, MyLocation> oldLocation;
    MyLocation myLocation;


    GPSLocation(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        globalVariables = (GlobalVariables) mainActivity.getApplication();
        geocoder = new Geocoder(mainActivity, Locale.getDefault());
        globalVariables.setCurrentLocation(getLastKnownLocation());
        if (!globalVariables.isLandScape()) {
            setUpResetButton();
            setUpSeekBar();
            setUpOnTrackingListener();
            setUpOnFollowListener();
//            try {
//                updateGlobalVariables(Objects.requireNonNull(getLastKnownLocation()));
//            } catch (IOException e) { e.printStackTrace(); }
        }

        if (!globalVariables.isHasRotated()){
            startTimer();
        }
//        LatLng latLng = new LatLng(globalVariables.getCurrentLocation().getLatitude(), globalVariables.getCurrentLocation().getLongitude());
//        globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GlobalVariables.cameraZoomLevel));
//        addCurrentLocation();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
//        updateCameraBearing(location.getBearing());
//         Toast.makeText(mainActivity, getLastKnownLocation() + "", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "manager: " + location.getLatitude() + ", " + location.getLongitude());
        try {
            if (globalVariables.getToggleButton_track().isChecked()
             || globalVariables.getLocationsMap().isEmpty()) {
                updateDistance(location);
                updateGlobalVariables(location);
                updateViews(location);
                if (globalVariables.getPreviousLocation() != null) {
                    updateDurationTo(globalVariables.getPreviousLocation());
                    addMarkerTo(globalVariables.getPreviousLocation());
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
        String locationName = (addresses.isEmpty() ? "Unavailable" : addresses.get(0).getAddressLine(0));
        long startTime = System.currentTimeMillis();
        Marker marker = currentMarker;
        MyLocation myLocation = new MyLocation(latitude, longitude, locationName, startTime, marker);

        removeOldLocation(location);
        globalVariables.getLocationsMap().put(location, myLocation);
        Log.d(TAG + "3", "updateGlobalVariables: " + globalVariables.getLocationsMap().size() + " -> " + globalVariables.getLocationsMap());
//        Log.d(TAG + "3", "updateGlobalVariables: " + oldLocation + " -> ");
    }

    public void addCurrentLocation(){
        try {
            Location location = getLastKnownLocation();
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String locationName = addresses.get(0).getAddressLine(0);
            long startTime = System.currentTimeMillis();
            Marker marker = currentMarker;
            MyLocation myLocation = new MyLocation(latitude, longitude, locationName, startTime, marker);
            globalVariables.getLocationsMap().put(getLastKnownLocation(), myLocation);
            Toast.makeText(mainActivity, "added success", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(mainActivity, "added fail", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void removeOldLocation(Location previousLocation) {
        if ((oldLocation = getLocationEntry(previousLocation)) != null) {
            oldLocation.getValue().getMarker().remove();
            globalVariables.getLocationsMap().remove(oldLocation.getKey());
        }
    }

    public Map.Entry<Location, MyLocation> getLocationEntry(Location location) {
        for (Map.Entry<Location, MyLocation> entry : globalVariables.getLocationsMap().entrySet()) {
            if (entry.getKey().getLatitude() == location.getLatitude()
                    && entry.getKey().getLongitude() == location.getLongitude()) {
                return entry;
            }
        }
        return null;
    }

    public void updateDistance(Location location) {
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

    public static String getTimeStamp(long milliSecond, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault())
                .format(new Date(milliSecond));
    }

    public void addMarkerTo(@NonNull Location location) {
        Marker marker;
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));

        if ((marker = globalVariables.getGoogleMap().addMarker(markerOptions)) == null) return;
        if ((myLocation = globalVariables.getLocationsMap().get(location)) == null) return;

        marker.setTitle("(" + myLocation.getLatitude() + ", " + myLocation.getLongitude() + ")");
        marker.setSnippet("last visit: " + getTimeStamp(myLocation.getStartTime(), "h:mm:ss a") + "\n" +
                "Time spend: " + getTimeStamp(myLocation.getTimeSpend(), "mm:ss"));
        myLocation.setMarker(marker);
    }

    public void updateDurationTo(@NonNull Location previousLocation) {
        if ((myLocation = globalVariables.getLocationsMap().get(previousLocation)) == null) return;
        long time = System.currentTimeMillis() - myLocation.getStartTime();

        if (oldLocation != null)
            time += oldLocation.getValue().getTimeSpend();

        myLocation.setTimeSpend(time);
    }

    public void moveCameraTo(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GlobalVariables.cameraZoomLevel));
    }

    public void moveCameraTo(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);
        globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GlobalVariables.cameraZoomLevel));
    }

    public void setUpResetButton() {
        globalVariables.getButton_reset().setOnClickListener(v -> {
            globalVariables.resetValues();
            onLocationChanged(Objects.requireNonNull(getLastKnownLocation()));
        });
    }

    public void setUpSeekBar() {
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
                LatLng latLng = new LatLng(globalVariables.getCurrentLocation().getLatitude(), globalVariables.getCurrentLocation().getLongitude());
                globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GlobalVariables.cameraZoomLevel));
                Log.d(TAG, "onStartTrackingTouch: " + seekBar.getProgress());
            }
        });
        globalVariables.getSeekBar_cameraZoom().setProgress(GlobalVariables.cameraZoomLevel);
    }

    public void setUpOnFollowListener(){
        globalVariables.getToggleButton_camera().setOnClickListener(v -> {
            if (!globalVariables.getToggleButton_track().isChecked()) {
                globalVariables.getToggleButton_camera().setChecked(false);
                Toast.makeText(mainActivity, "Can not follow while not tracking.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpOnTrackingListener(){
        globalVariables.getToggleButton_track().setOnClickListener(v -> {
            globalVariables.getToggleButton_camera().setChecked(false);
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

        for (Location locRecorded : listLocsToDraw) {
            options.add(new LatLng(locRecorded.getLatitude(), locRecorded.getLongitude()));
        }

        globalVariables.getGoogleMap().addPolyline(options);
    }

    //  globalVariables.getTextView_duration().setText(getTimeStamp(System.currentTimeMillis()));
    private void startTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mainActivity.runOnUiThread(() -> {
//                    long time = globalVariables.getStartTime() - System.currentTimeMillis();
//                    long elapsedTime = System.currentTimeMillis() - globalVariables.getStartTime();
                    long elapsedTime = globalVariables.getElapsedTime();
                    long elapsedSeconds = elapsedTime / 1000;
                    long elapsedMinutes = elapsedSeconds / 60;
                    long elapsedHour = elapsedMinutes / 60;
                    globalVariables.getTextView_duration().setText(String.format("Timer: %s:%s:%s", elapsedHour, elapsedMinutes, elapsedSeconds % 60));

//                    globalVariables.getTextView_duration().setText(getTimeStamp(elapsedTime, "hh:mm:ss"));
//                    globalVariables.setElapsedTime(elapsedTime);
                    if (globalVariables.getToggleButton_track().isChecked())
                        globalVariables.addElapsedTime(1000);

                    if (globalVariables.getToggleButton_camera().isChecked()
//                     && globalVariables.getToggleButton_track().isChecked()
                            && globalVariables.getCurrentLocation() != null) {
                        moveCameraTo(globalVariables.getCurrentLocation());

//                        double lat = globalVariables.getGoogleMap().getCameraPosition().target.latitude;
//                        double lon = globalVariables.getGoogleMap().getCameraPosition().target.longitude;
//                        moveCameraTo(lat, lon);
//                        Toast.makeText(mainActivity, "(" + lat + ", " + lon + ")", Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG + "6", "run: " + globalVariables.getLocationsMap().size());

//                    updateCameraBearing(globalVariables.getCurrentLocation().getBearing());
                });
            }
        }, 0, 1000);
    }

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        return globalVariables.getLocationManager().getLastKnownLocation(MainActivity.getBestProvider(globalVariables.getLocationManager()));
    }
}
