package com.locationshare.aptener.sharelocation.di;

import com.locationshare.aptener.sharelocation.ui.map.MapActivityMVP;
import com.locationshare.aptener.sharelocation.ui.map.MapActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MapActivityModule {
    @Provides
    MapActivityMVP.Presenter provideMapActivityPresenter(){
        return new MapActivityPresenter();
    }
}
