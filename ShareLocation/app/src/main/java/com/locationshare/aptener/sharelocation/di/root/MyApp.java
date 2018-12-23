package com.locationshare.aptener.sharelocation.di.root;

import android.app.Application;

import com.locationshare.aptener.sharelocation.di.MainActivityModule;
import com.locationshare.aptener.sharelocation.di.MapActivityModule;

public class MyApp extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .mainActivityModule(new MainActivityModule())
                .mapActivityModule(new MapActivityModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }
}
