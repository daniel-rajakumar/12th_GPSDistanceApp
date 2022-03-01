package com.example.gps_app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.media.VolumeShaper;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSION_FINE_LOCATION = 99;
    public final String TAG = "test";
    LocationManager locationManager;
    GlobalVariables globalVariables;
    Bundle savedInstanceState;
    GPSLocation gpsLocation;

    void run(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        assignValues();
        globalVariables.setSavedInstance(savedInstanceState);
        if (!globalVariables.isHasRotated()){
            checkLocationPermission();
        }
        startLocationRequesting();
        createMap();

        if (globalVariables.isHasRotated()){
            locationManager.removeUpdates(gpsLocation);
            Toast.makeText(this, "locationManager updated", Toast.LENGTH_SHORT).show();
        }
    }

    void checkLocationPermission(){
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_FINE_LOCATION);
    }


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{ permission }, requestCode);
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thank you for the permission", Toast.LENGTH_SHORT).show();
                startLocationRequesting();
            } else {
                Toast.makeText(this, "This app requires location permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void startLocationRequesting() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        globalVariables.setLocationManager(locationManager);
        gpsLocation = new GPSLocation(this);
        locationManager.requestLocationUpdates(getBestProvider(locationManager), 0, 5, gpsLocation);
    }

    static String getBestProvider(LocationManager locationManager) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        return locationManager.getBestProvider(criteria, true);
    }

    void assignValues() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        globalVariables = (GlobalVariables) this.getApplication();
        globalVariables.setValues(this);
        if (globalVariables.isHasRotated()){

        }
    }

    void createMap() {
        globalVariables.getMapView().getMapAsync(this);
        globalVariables.getMapView().onCreate(savedInstanceState);
    }


    public void makeStatusBarTransparent() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        WindowManager.LayoutParams winParams = getWindow().getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setAttributes(winParams);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public void setUpCustomMarkerWindow(){
        globalVariables.getGoogleMap().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(MainActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MainActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MainActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

    }

    void moveCameraTo(double lat, double lon){
        LatLng latLng = new LatLng(lat, lon);
        globalVariables.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, GlobalVariables.cameraZoomLevel));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(this, "map is ready", Toast.LENGTH_SHORT).show();
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map_style));
        globalVariables.setGoogleMap(googleMap);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        if (globalVariables.getCurrentLocation() != null)
            moveCameraTo(globalVariables.getCurrentLocation().getLatitude(),
                         globalVariables.getCurrentLocation().getLongitude());

        setUpCustomMarkerWindow();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeStatusBarTransparent();
        this.run(savedInstanceState);
        globalVariables.getMapView().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        globalVariables.getMapView().onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        globalVariables.getMapView().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        globalVariables.getMapView().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        globalVariables.getMapView().onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        globalVariables.getMapView().onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Intent intent = new Intent(this, Map.class);
        globalVariables.getMapView().onSaveInstanceState(outState);
        Log.d(TAG + "8", "onSaveInstanceState: (length: " + globalVariables.getLocationsMap().size() + ") -> "+ globalVariables.getLocationsMap());
        this.getIntent().putExtra("map", (Serializable) globalVariables.getLocationsMap());
    }

}





