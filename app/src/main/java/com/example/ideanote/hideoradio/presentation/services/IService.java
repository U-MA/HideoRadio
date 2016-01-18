package com.example.ideanote.hideoradio.presentation.services;

import com.example.ideanote.hideoradio.Episode;

import java.io.File;

public interface IService {
    File getExternalFilesDir();
    void startForeground(Episode episode);
    void stopForeground(Episode episode);
    boolean isNetworkConnected();
    void showErrorMessage();
}
