package com.mgjg.kmztracker;

import android.app.Application;

public class KmzTrackerApp extends Application
{

    private static KmzTrackerApp instance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        AppPreferences.makeInstance(this);
    }

    KmzTrackerApp getInstance()
    {
        return instance;
    }
}
