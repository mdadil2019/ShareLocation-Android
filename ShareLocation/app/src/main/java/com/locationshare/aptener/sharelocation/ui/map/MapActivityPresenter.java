package com.locationshare.aptener.sharelocation.ui.map;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.data.model.User;
import com.locationshare.aptener.sharelocation.data.network.FirebaseCallback;
import com.locationshare.aptener.sharelocation.data.network.FirebaseUserAddedCallback;
import com.locationshare.aptener.sharelocation.data.network.service.FirebaseService;
import com.locationshare.aptener.sharelocation.data.network.service.ListenForChangeInState;
import com.locationshare.aptener.sharelocation.utils.Constants;

import static com.locationshare.aptener.sharelocation.utils.Constants.LISTEN_STATUS;
import static com.locationshare.aptener.sharelocation.utils.Constants.LIVE_STATUS_ON;
import static com.locationshare.aptener.sharelocation.utils.Constants.STOP_LISTEN_STATUS;

public class MapActivityPresenter implements MapActivityMVP.Presenter {


    MapActivityMVP.View view;
    LatLng latLng;
    Context mContext;
    AppPreferenceHelper mPrefs;


    public MapActivityPresenter(Context context, AppPreferenceHelper preferenceHelper){
        mContext = context;
        mPrefs = preferenceHelper;
    }


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
                String recentUpdateTime = value.substring(value.indexOf('?')+1,value.indexOf('*'));
                String accuracy = value.substring(value.indexOf("*")+1,value.length());
                latLng = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
                view.updateOnMap(latLng,recentUpdateTime,accuracy);

            }
        });
    }

    @Override
    public void addUser(String deviceId) {
        FirebaseService.addUserToFirebase(deviceId, new FirebaseCallback() {
            @Override
            public void onDataReturn(String value) {
                String sharableLink = "http://www.share.com/myapp/" + value ;
                view.showLink(sharableLink);
                Intent intent = new Intent(mContext,ListenForChangeInState.class);
                intent.putExtra("ID",mPrefs.getId());
                view.modifyShareLocationTrackingWidgets();
                mContext.startService(intent);
            }
        });
    }

    @Override
    public void isTrackedByAnyone() {
        //show progress bar in map view center
        final String deviceId = mPrefs.getId();
        FirebaseService.getStatusOfUser(deviceId, new FirebaseCallback() {
            @Override
            public void onDataReturn(@Nullable String value) {
                if(value==null){
                    view.showShareLocationWidgets();
                }else if(value.equals(LISTEN_STATUS)){

                    /*
                    1. change the text of button from StartShare to StopShare && set the generated link on the edittext
                    2. find other users id which are listening to the current user
                    3. show them in the recycler view with default thumnail and id of the user
                    4. on click of the thumbnail, show it's current location in map view by moving the camera
                    */
                    view.modifyShareLocationTrackingWidgets();
                    FirebaseService.getTrackingUsers(deviceId, new FirebaseUserAddedCallback() {
                        @Override
                        public void userAdded(User user) {
                            if(user.getStatus().equals(LIVE_STATUS_ON)){
                                view.updateList(user);
                            }
                        }
                    });
                }else if(value.equals(STOP_LISTEN_STATUS)){
                    view.showShareLocationWidgets();
                }
            }
        });
    }
}
