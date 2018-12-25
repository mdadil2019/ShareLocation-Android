package com.locationshare.aptener.sharelocation.ui.main;

import android.location.Location;

public interface MainActivityMVP {
    interface View{

        void showLink(String link);

        void showProgressBar();

        void hideProgressBar();

        void activateStopButton();

        void deactivateStopButton();
    }

    interface Presenter{
        void setView(View v);

        void addUser(String deviceId);

        void isTrackedByAnyone();

        void stopLocationTracking();

    }
}
