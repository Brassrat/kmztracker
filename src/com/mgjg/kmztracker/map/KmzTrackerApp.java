package com.mgjg.kmztracker.map;

import android.app.Application;

public class KmzTrackerApp extends Application
{

    private static KmzTrackerApp instance;
    
    @Override
    public void onCreate()
    {
        instance = this;
    }
    
    KmzTrackerApp getInstance()
    {
        return instance;
    }
}
