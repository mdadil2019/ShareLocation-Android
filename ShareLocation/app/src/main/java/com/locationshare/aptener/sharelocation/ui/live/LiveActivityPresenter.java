package com.locationshare.aptener.sharelocation.ui.live;

import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.data.model.User;
import com.locationshare.aptener.sharelocation.data.network.FirebaseUserAddedCallback;
import com.locationshare.aptener.sharelocation.data.network.service.FirebaseService;
import com.locationshare.aptener.sharelocation.utils.Constants;

public class LiveActivityPresenter implements LiveUsersActivityMVP.Presenter {
    LiveUsersActivityMVP.View view;
    AppPreferenceHelper prefs;

    public LiveActivityPresenter(AppPreferenceHelper appPreferenceHelper){
        prefs = appPreferenceHelper;
    }

    @Override
    public void setView(LiveUsersActivityMVP.View view) {
        this.view = view;
    }

    @Override
    public void getLiveUsers() {
        String myId = prefs.getId();
        FirebaseService.getTrackingUsers(myId, new FirebaseUserAddedCallback() {
            @Override
            public void userAdded(User user) {
                if(user.getStatus().equals(Constants.LIVE_STATUS_ON))
                    view.updateList(user);
            }
        });
    }
}
