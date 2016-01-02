package com.example.ideanote.hideoradio;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.deploygate.sdk.DeployGate;
import com.example.ideanote.hideoradio.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.internal.di.ApplicationModule;
import com.example.ideanote.hideoradio.internal.di.DaggerApplicationComponent;

public class HideoRadioApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDB();
        initializeInjector();
        DeployGate.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

    protected void initializeDB() {
        Configuration.Builder builder = new Configuration.Builder(this);
        builder.addModelClass(Episode.class);

        ActiveAndroid.initialize(builder.create());
    }

    private void initializeInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule())
                .build();
    }

    public void setComponent(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }
}
