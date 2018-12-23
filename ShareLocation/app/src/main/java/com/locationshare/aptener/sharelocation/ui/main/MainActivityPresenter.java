package com.locationshare.aptener.sharelocation.ui.main;

import android.content.Context;
import android.content.Intent;

import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.data.network.FirebaseCallback;
import com.locationshare.aptener.sharelocation.data.network.service.FirebaseService;
import com.locationshare.aptener.sharelocation.data.network.service.ListenForChangeInState;
import com.locationshare.aptener.sharelocation.ui.main.MainActivityMVP.View;

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
                prefs.saveId(value);
                String sharableLink = "http://www.share.com/myapp/" + value ;
                view.showLink(sharableLink);

                Intent intent = new Intent(context,ListenForChangeInState.class);
                intent.putExtra("ID",prefs.getId());
                context.startService(intent);
            }
        });
    }
}
