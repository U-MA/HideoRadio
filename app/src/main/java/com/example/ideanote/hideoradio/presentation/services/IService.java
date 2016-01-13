package com.example.ideanote.hideoradio.presentation.services;

import java.io.File;

public interface IService {
    File getExternalFilesDir();
    void startForeground();
    void stopForeground();
}
