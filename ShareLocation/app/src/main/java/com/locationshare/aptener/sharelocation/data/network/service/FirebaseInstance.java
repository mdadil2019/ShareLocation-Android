package com.locationshare.aptener.sharelocation.data.network.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInstance {
    public static DatabaseReference getRootReference(){
        return FirebaseDatabase.getInstance().getReference();
    }
}
