package com.locationshare.aptener.sharelocation.ui.map;

import com.google.android.gms.maps.model.LatLng;
import com.locationshare.aptener.sharelocation.data.network.FirebaseCallback;
import com.locationshare.aptener.sharelocation.data.network.service.FirebaseService;

public class MapActivityPresenter implements MapActivityMVP.Presenter {
    MapActivityMVP.View view;
    LatLng latLng;
    @Override
    public void setView(MapActivityMVP.View view) {
        this.view = view;
    }

    @Override
    public void fetchLocationUpdateOfFirebase(final String id, final String myId) {
        //add current user in main user's (id) child
        FirebaseService.addLiveTrackingInfo(id,myId);

        FirebaseService.getLocationUpdates(id, new FirebaseCallback() {
            @Override
            public void onDataReturn(String value) {

                String lat = value.substring(0,value.indexOf(','));
                String lng = value.substring(value.indexOf(',')+2,value.indexOf('?'));
                String recentUpdateTime = value.substring(value.indexOf('?')+1,value.length());
                latLng = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
                view.updateOnMap(latLng,recentUpdateTime);


            }
        });


    }


}
