package com.locationshare.aptener.sharelocation.ui.map;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.locationshare.aptener.sharelocation.data.network.FirebaseCallback;

public interface MapActivityMVP {
    interface View{
        void updateOnMap(LatLng latLng, String lastUpdateTime);
    }

    interface Presenter{
        void setView(View view);

        void fetchLocationUpdateOfFirebase(String id, String myId);


    }
}
