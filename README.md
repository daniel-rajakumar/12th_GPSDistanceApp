# 12th_GPSDistanceApp

An Android app from my 12th-grade mobile development class. It follows your location on a Google Map, drops markers, looks up the address you're at, and keeps track of the distance you've traveled.

Built with Java, the Android SDK, and the Google Maps SDK for Android.

## Running it

The app needs a Google Maps API key, which isn't included. Create one in the [Google Cloud console](https://console.cloud.google.com/google/maps-apis) with the Maps SDK for Android enabled, then put it in `app/src/main/res/values/APIKeys.xml` in place of `YOUR_GOOGLE_MAPS_API_KEY`. Open the project in Android Studio and run it on a device or emulator with location turned on.
