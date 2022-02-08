package com.example.gps_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "test";
    public Location location;

    TextView textView_lat, textView_lon;
    Geocoder geocoder;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void run() {
        geocoder = new Geocoder(this, Locale.getDefault());
        textView_lat = findViewById(R.id.textView_lat);
        textView_lon = findViewById(R.id.textView_lon);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GPSLocation());
    }

    public class GPSLocation implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            textView_lat.setText(MessageFormat.format("lat: {0}", location.getLatitude()));
            textView_lon.setText(MessageFormat.format("lon: {0}", location.getLongitude()));

            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Log.d(TAG, MessageFormat.format("run: {0} \n\n-> ({1}, {2})", addresses.get(0).getAddressLine(0), location.getLatitude(), location.getLongitude()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
