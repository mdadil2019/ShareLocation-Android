package com.locationshare.aptener.sharelocation.data.network.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.di.root.MyApp;
import com.locationshare.aptener.sharelocation.utils.Constants;

import javax.inject.Inject;

public class ListenForChangeInState extends Service {

    Runnable runnable;

    boolean isServiceRunning;

    @Inject
    AppPreferenceHelper prefs;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
//        id = intent.getStringExtra("ID");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MyApp)getApplication()).getApplicationComponent().inject(this);
        final String id = prefs.getId();
        final Intent trackingServiceIntent = new Intent(this,TrackingLocationService.class);
        trackingServiceIntent.putExtra("ID",id);
        runnable = new Runnable() {
            @Override
            public void run() {
                FirebaseInstance.getRootReference().child(id).child(Constants.STATUS).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue().equals(Constants.LISTEN_STATUS)){
                            //start location service to update onLocationChanged into Firebase Database

                            //Note: test it when more then 1 user track at same time using the link shared

                            if(!isServiceRunning){
                                startService(trackingServiceIntent);
                                isServiceRunning = true;
                            }
                        }else if(dataSnapshot.getValue().equals(Constants.STOP_LISTEN_STATUS)){
                            if(isServiceRunning){
                                stopService(trackingServiceIntent);
                                isServiceRunning= false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        new Thread(runnable).start();



    }
}
