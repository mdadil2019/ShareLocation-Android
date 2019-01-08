package com.locationshare.aptener.sharelocation.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.data.network.FirebaseCallback;
import com.locationshare.aptener.sharelocation.data.network.service.FirebaseService;
import com.locationshare.aptener.sharelocation.data.network.service.ListenForChangeInState;
import com.locationshare.aptener.sharelocation.ui.main.MainActivityMVP.View;
import com.locationshare.aptener.sharelocation.utils.Constants;

public class MainActivityPresenter implements MainActivityMVP.Presenter {

    View view;

    AppPreferenceHelper prefs;
    Context context;

    public MainActivityPresenter(Context context, AppPreferenceHelper appPreferenceHelper){
        prefs = appPreferenceHelper;
        this.context = context;
    }
    @Override
    public void setView(View v) {
        view = v;
    }

    @Override
    public void addUser(String deviceId) {
        view.showProgressBar();
        FirebaseService.addUserToFirebase(deviceId,new FirebaseCallback(){
            @Override
            public void onDataReturn(String value) {
                view.hideProgressBar();
//                prefs.saveId(value);  Note: we are saving the id when users first opens the app and then we will pass same id everywhere
                String sharableLink = "http://www.share.com/myapp/" + value ;
                view.showLink(sharableLink);

                Intent intent = new Intent(context,ListenForChangeInState.class);
                intent.putExtra("ID",prefs.getId());
                context.startService(intent);
            }
        });
    }

    /*
        Note: Issue in below code because this method should be called once you are sure
              that user is registred in firebase.
     */
    @Override
    public void isTrackedByAnyone() {

        if(prefs.getId()!=null){
            String id = prefs.getId();
            FirebaseService.getStatusOfUser(id,new FirebaseCallback(){
                @Override
                public void onDataReturn(@Nullable String value) {
                    if(value==null){
                        view.deactivateStopButton();
                    }else if(value.equals(Constants.LISTEN_STATUS)){
                        view.activateStopButton();
                    }else if(value.equals(Constants.STOP_LISTEN_STATUS)){
                        view.deactivateStopButton();
                    }
                }
            });
        }
    }


    @Override
    public void stopLocationTracking() {
        String id = prefs.getId();
        FirebaseService.stopLocationUpdates(id);
    }
}
