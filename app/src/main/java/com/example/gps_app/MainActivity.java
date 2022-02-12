package com.example.gps_app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.os.Build;
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
    LocationManager locationManager;
    GlobalVariables globalVariables;

    void run() {
        assignValues();
        startLocationRequesting();
    }

    void startLocationRequesting(){
        globalVariables.getTextView_distance().setText("Yes working :) ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            globalVariables.getTextView_distance().setText("Not working :( ");
            return;
        }
        locationManager.requestLocationUpdates(getBestProvider(), 0, 0, new GPSLocation(this));
    }

    String getBestProvider(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        return locationManager.getBestProvider(criteria, true);
    }

    void assignValues(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        globalVariables = (GlobalVariables) this.getApplication();
        globalVariables.setValues(this);
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





