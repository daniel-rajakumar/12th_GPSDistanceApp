package com.example.gps_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_FINE_LOCATION = 99;
    public final String TAG = "test";
    public Location prevLocation;

    TextView textView_lat, textView_lon, textView_locationName, textView_distance;
    Geocoder geocoder;

    void run() {
        geocoder = new Geocoder(this, Locale.getDefault());
        textView_lat = findViewById(R.id.textView_lat);
        textView_lon = findViewById(R.id.textView_lon);
        textView_locationName = findViewById(R.id.textView_locationName);
        textView_distance = findViewById(R.id.textView_distance);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textView_lat.setText(MessageFormat.format("works: {0} ", "no"));
            return;
        }

        GPSLocation gpsLocation = new GPSLocation();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 0, 10, gpsLocation);
    }

    public class GPSLocation implements LocationListener {
        double distance = 0;

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

                textView_lat.setText("lat: " + lat1);
                textView_lon.setText("lon: " + lon1);
                textView_locationName.setText("location name: " + locationName);
                textView_distance.setText("distance: " + distance);

                prevLocation = location;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Thank you for the permission", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "This app requires location permission", Toast.LENGTH_SHORT).show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        makeStatusBarTransparent();
        this.run();
    }

    public void makeStatusBarTransparent(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        WindowManager.LayoutParams winParams = getWindow().getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setAttributes(winParams);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
}





