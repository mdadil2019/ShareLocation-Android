package com.locationshare.aptener.sharelocation.ui.live;

import com.locationshare.aptener.sharelocation.data.model.User;

public interface LiveUsersActivityMVP {
    interface View{
        void noOneIsTracking();

        void updateList(User user);
    }

    interface Presenter{
        void setView(View view);

        void getLiveUsers();
    }
}
