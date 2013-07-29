package com.citizenservices.observeandreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends Activity {
    LocationManager locationManager;
    LocationListener locationListener;

    private Firebase userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLocationServices();
        identifyUser();
        setupFirebase();
        sendPhoneStatus();

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(locationListener);
    }

    public void setupLocationServices() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                locationUpdated(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void setupFirebase() {
        // Create a reference to a Firebase location
        final Firebase ref = new Firebase("https://citizen-security.firebaseIO.com/");
    }

    private void identifyUser() {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String user_uuid = settings.getString("user_uuid", "");

        if ("" == user_uuid) {
            user_uuid = UUID.randomUUID().toString();

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("user_uuid", user_uuid);
            editor.commit();
        }

        userRef = new Firebase("https://citizen-security.firebaseIO.com/users/" + user_uuid);
    }

    private void sendPhoneStatus() {
        //
    }

    private void locationUpdated(Location location) {
        if (null == userRef) {
            return;
        }

        Long time = new Date().getTime();
        String timestamp = String.valueOf(time);

        Firebase userLocationRef = userRef.child("/locations/" + timestamp);
        userLocationRef.setValue(location);
    }
}
