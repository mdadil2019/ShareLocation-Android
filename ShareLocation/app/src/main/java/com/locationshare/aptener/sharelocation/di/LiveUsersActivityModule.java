package com.locationshare.aptener.sharelocation.di;

import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.ui.live.LiveActivityPresenter;
import com.locationshare.aptener.sharelocation.ui.live.LiveUsersActivityMVP;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class LiveUsersActivityModule {

    @Provides
    LiveUsersActivityMVP.Presenter provideLiveActivityPresenter(AppPreferenceHelper appPreferenceHelper){
        return new LiveActivityPresenter(appPreferenceHelper);
    }

}
