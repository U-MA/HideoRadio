package com.example.ideanote.hideoradio;

import android.app.Application;
import android.content.res.Configuration;

import com.activeandroid.ActiveAndroid;

public class HideoRadioApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
