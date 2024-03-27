package com.zc.medical_ai;

import android.app.Application;

import speech.Logger;


/**
 * @author Aleksandar Gotev
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}
