package com.locationshare.aptener.sharelocation.data.network.service;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.locationshare.aptener.sharelocation.data.network.FirebaseCallback;
import com.locationshare.aptener.sharelocation.utils.Constants;

import static com.locationshare.aptener.sharelocation.data.network.service.FirebaseInstance.getRootReference;
import static com.locationshare.aptener.sharelocation.utils.Constants.LISTEN_STATUS;
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

    public static void stopLocationUpdates(String id){
        getRootReference().child(id).child(STATUS).setValue(STOP_LISTEN_STATUS);
    }

    private static void createChild(String id){
        DatabaseReference currentUser = FirebaseInstance.getRootReference().child(id);
        currentUser.child(Constants.DEVICE_ID).setValue(id);
        currentUser.child(Constants.STATUS).setValue(Constants.STOP_LISTEN_STATUS);
        currentUser.child(LOCATION).setValue("No value");
    }
}
