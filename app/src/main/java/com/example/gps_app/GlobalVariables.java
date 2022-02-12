package com.example.gps_app;

import android.app.Application;
import android.widget.TextView;

public class GlobalVariables extends Application {

    private TextView textView_lat, textView_lon, textView_locationName, textView_distance;

    public void setValues(MainActivity mainActivity) {
        textView_lat = mainActivity.findViewById(R.id.textView_lat);
        textView_lon = mainActivity.findViewById(R.id.textView_lon);
        textView_locationName = mainActivity.findViewById(R.id.textView_locationName);
        textView_distance = mainActivity.findViewById(R.id.textView_distance);
    }

    public TextView getTextView_lon() {
        return textView_lon;
    }

    public TextView getTextView_locationName() {
        return textView_locationName;
    }

    public TextView getTextView_distance() {
        return textView_distance;
    }

    public TextView getTextView_lat() {
        return textView_lat;
    }
}
