package com.example.ideanote.hideoradio;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.deploygate.sdk.DeployGate;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //ActiveAndroid.initialize(this);
        //DeployGate.install(this);
    }
}
