package com.locationshare.aptener.sharelocation.di;

import android.content.Context;

import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.ui.map.MapActivityMVP;
import com.locationshare.aptener.sharelocation.ui.map.MapActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MapActivityModule {
    @Provides
    MapActivityMVP.Presenter provideMapActivityPresenter(Context context, AppPreferenceHelper preferenceHelper){
        return new MapActivityPresenter(context, preferenceHelper);
    }
}
