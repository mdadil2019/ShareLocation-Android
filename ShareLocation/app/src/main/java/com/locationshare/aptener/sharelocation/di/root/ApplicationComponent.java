package com.locationshare.aptener.sharelocation.di.root;

import com.locationshare.aptener.sharelocation.data.network.service.ListenForChangeInState;
import com.locationshare.aptener.sharelocation.data.network.service.TrackingLocationService;
import com.locationshare.aptener.sharelocation.di.LiveUsersActivityModule;
import com.locationshare.aptener.sharelocation.di.MainActivityModule;
import com.locationshare.aptener.sharelocation.di.MapActivityModule;
import com.locationshare.aptener.sharelocation.ui.live.LiveActivityPresenter;
import com.locationshare.aptener.sharelocation.ui.live.LiveUsersActivity;
import com.locationshare.aptener.sharelocation.ui.main.MainActivity;
import com.locationshare.aptener.sharelocation.ui.map.MapsActivity;

import dagger.Component;

@Component(modules = {ApplicationModule.class, MainActivityModule.class,MapActivityModule.class, LiveUsersActivityModule.class})
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);

    void inject(ListenForChangeInState listenForChangeInState);

    void inject(TrackingLocationService trackingLocationService);

    void inject(MapsActivity mapsActivity);

    void inject(LiveUsersActivity liveUsersActivity);
}
