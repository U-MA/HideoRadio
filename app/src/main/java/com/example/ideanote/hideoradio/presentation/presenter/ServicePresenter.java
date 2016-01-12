package com.example.ideanote.hideoradio.presentation.presenter;

import android.content.Intent;
import android.os.IBinder;

public interface ServicePresenter {
    void onCreate();
    void onDestroy();
    IBinder onBind(Intent intent);
    int onStartCommand(Intent intent, int flag, int id);
}
