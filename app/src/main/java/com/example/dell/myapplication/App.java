package com.example.dell.myapplication;

import android.app.Application;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

/**
 * Created by dell on 21-07-2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}