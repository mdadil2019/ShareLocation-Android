package com.locationshare.aptener.sharelocation.data.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.locationshare.aptener.sharelocation.data.network.FirebaseCallback;
import com.locationshare.aptener.sharelocation.utils.Constants;

import static com.locationshare.aptener.sharelocation.data.network.service.FirebaseInstance.getRootReference;
import static com.locationshare.aptener.sharelocation.utils.Constants.LISTEN_STATUS;
import static com.locationshare.aptener.sharelocation.utils.Constants.LIVE_STATUS_OFF;
import static com.locationshare.aptener.sharelocation.utils.Constants.LIVE_STATUS_ON;
import static com.locationshare.aptener.sharelocation.utils.Constants.LIVE_TRACKING_USERS;
import static com.locationshare.aptener.sharelocation.utils.Constants.LOCATION;
import static com.locationshare.aptener.sharelocation.utils.Constants.STATUS;
import static com.locationshare.aptener.sharelocation.utils.Constants.STOP_LISTEN_STATUS;

public class FirebaseService {
    public static void addUserToFirebase(final String id, final FirebaseCallback myCallBack){
        getRootReference().child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    createChild(id);
                    myCallBack.onDataReturn(id);
                }else{
                    myCallBack.onDataReturn(id);
                }
            }


            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
    }

    public static void getLocationUpdates(String id, final FirebaseCallback myCallback){
        getRootReference().child(id).child(STATUS).setValue(LISTEN_STATUS);
            getRootReference().child(id).child(LOCATION).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue().toString().contains(","))
                        myCallback.onDataReturn(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    public static void stopLocationUpdates(final String id){
        getRootReference().child(id).child(STATUS).setValue(STOP_LISTEN_STATUS);

        //set live status to off for all the childs inside of the LIVE_TRACKING_USER and expect to have only one time response
        // Note: need to be tested
        getRootReference().child(id).child(LIVE_TRACKING_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    getRootReference().child(id).child(LIVE_TRACKING_USERS).child(key).setValue(LIVE_STATUS_OFF);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void createChild(String id){
        DatabaseReference currentUser = FirebaseInstance.getRootReference().child(id);
        currentUser.child(Constants.DEVICE_ID).setValue(id);
        currentUser.child(Constants.STATUS).setValue(Constants.STOP_LISTEN_STATUS);
        currentUser.child(LOCATION).setValue("No value");
    }

    public static void getStatusOfUser(String id, final FirebaseCallback firebaseCallback) {
        getRootReference().child(id).child(Constants.STATUS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String snapShotValue = dataSnapshot.getValue().toString();
                firebaseCallback.onDataReturn(snapShotValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void addLiveTrackingInfo(String id, String myId){
        FirebaseInstance.getRootReference().child(id).child(LIVE_TRACKING_USERS).child(myId).setValue(LIVE_STATUS_ON);
    }
}
