package com.example.ideanote.hideoradio;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.app.Application;
import com.deploygate.sdk.DeployGate;

public class HideoRadioApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializeDB();
        DeployGate.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    protected void initializeDB() {
        Configuration.Builder builder = new Configuration.Builder(this);
        builder.addModelClass(Episode.class);

        ActiveAndroid.initialize(builder.create());
    }
}
