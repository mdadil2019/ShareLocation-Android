package com.locationshare.aptener.sharelocation.data.network.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.di.root.MyApp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import static com.locationshare.aptener.sharelocation.utils.Constants.ACCURACY;
import static com.locationshare.aptener.sharelocation.utils.Constants.LOCATION;

public class TrackingLocationService extends Service {
    private LocationManager mLocationManager;
    Criteria criteria;
    private static final int LOCATION_INTERVAL = 1000 * 5;
    private static final int LOCATION_DISTANCE = 1;

    @Inject
    AppPreferenceHelper prefs;

    public class MyLocationListener implements LocationListener {
        Location currentLocation;

        public MyLocationListener(String provider){
            currentLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            currentLocation.set(location);
            String id = prefs.getId();
            String lat = String.valueOf(currentLocation.getLatitude());
            String lng = String.valueOf(currentLocation.getLongitude());
            String accuracy = String.valueOf(location.getAccuracy());
            String infoString = lat + ", " + lng + " ? " + getFormattedDate() + " * " + accuracy;

            FirebaseInstance.getRootReference().child(id).child(LOCATION).setValue(infoString);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
    TrackingLocationService.MyLocationListener[] mLocationListeners = new TrackingLocationService.MyLocationListener[] {
            new TrackingLocationService.MyLocationListener(LocationManager.GPS_PROVIDER),
            new TrackingLocationService.MyLocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        ((MyApp)getApplication()).getApplicationComponent().inject(TrackingLocationService.this);
        initializeLocationManager();
//        if(Permissions.getLocationPermissions(this,))
        try {
//
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[0]);
            mLocationManager.requestLocationUpdates(LOCATION_INTERVAL,LOCATION_DISTANCE,criteria,mLocationListeners[0],null);

        }catch (SecurityException se){
            Toast.makeText(this, "Security Exception", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mLocationManager!=null){
            for(int i = 0;i<mLocationListeners.length;i++){
                try{
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                }catch (Exception ex){
                    //there is an error
                }
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setBearingRequired(false);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        }
    }

    public static String getFormattedDate() {
        Date date = new Date();
        return date.toString();
    }



}
