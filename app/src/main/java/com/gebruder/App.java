package com.gebruder;

import android.app.Application;

import com.gebruder.utils.Shared;

import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        Shared.getInstance().init(this);
    }
}
