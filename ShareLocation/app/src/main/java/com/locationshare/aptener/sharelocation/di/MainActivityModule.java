package com.locationshare.aptener.sharelocation.di;

import android.content.Context;

import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.ui.main.MainActivityMVP;
import com.locationshare.aptener.sharelocation.ui.main.MainActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    @Provides
    MainActivityMVP.Presenter provideMainActivityPresenter(AppPreferenceHelper appPreferenceHelper, Context context){
        return new MainActivityPresenter(context, appPreferenceHelper);
    }

    @Provides
    AppPreferenceHelper provideAppPrefrencesHelper(Context context){
        return new AppPreferenceHelper(context);
    }
}
