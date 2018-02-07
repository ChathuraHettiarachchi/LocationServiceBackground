package com.chootdev.myapplication.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.chootdev.myapplication.events.LocationEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

import static com.google.android.gms.analytics.internal.zzy.e;

/**
 * Created by Choota on 1/25/18.
 */

public class LocationManagerService extends Service implements Runnable {

    private Handler handler;
    private LocationManager mLocationManager = null;
    private Location mLastLocation;
    private boolean isNotified;

    private Location oldLocatoin, newLocation;

    private static boolean isServiceRuning;
    private static final String TAG = "MyLocationService";
    private static final int LOCATION_INTERVAL = 500;
    private static final float LOCATION_DISTANCE = 50f;

    private LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER),
            new LocationListener(LocationManager.GPS_PROVIDER)
    };

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");

        handler = new Handler();
        this.isNotified = false;
        this.run();

        isServiceRuning = true;

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[1]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public void stopRepeatingTask() {
        handler.removeCallbacks(this);
    }

    public String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            isNotified = false;
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        isServiceRuning = false;
        stopRepeatingTask();
        super.onDestroy();

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        try {
            if (getLastLocation() != null && !isNotified) {

                this.isNotified = true;

                System.out.println(getLastLocation());
                String disteneBetween = "0.0";

                if (newLocation != null)
                    this.oldLocatoin = new Location(this.newLocation);

                this.newLocation = new Location(getLastLocation());

                if (oldLocatoin != null) {
                    disteneBetween = String.valueOf(oldLocatoin.distanceTo(newLocation));

                    String lat = String.valueOf(getLastLocation().getLatitude());
                    String lan = String.valueOf(getLastLocation().getLongitude());

                    LocationEvent event = new LocationEvent(getCurrentTimeStamp(), lat, lan, disteneBetween);
                    event.save();

                    EventBus.getDefault().post(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            handler.postDelayed(this, 2 * LOCATION_INTERVAL);
        }
    }
}
