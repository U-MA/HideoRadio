package com.example.ideanote.hideoradio.internal.di;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.services.PodcastPlayerService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 *
 * アプリケーションというよりServiceかもしれん
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(PodcastPlayer podcastPlayer);
    void inject(PodcastPlayerService podcastPlayerService);

    MediaPlayer mediaPlayer();
}
