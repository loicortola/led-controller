package com.loicortola.controller;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by loic on 28/03/2016.
 */
public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}