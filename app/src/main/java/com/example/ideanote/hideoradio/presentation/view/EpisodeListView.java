package com.example.ideanote.hideoradio.presentation.view;

import android.content.Intent;

import com.example.ideanote.hideoradio.Episode;

import java.util.Collection;

public interface EpisodeListView {

    void renderEpisodes(Collection<Episode> episodeList);

    void showLoadingView();

    void hideLoadingView();

    void showRetryView();

    void hideRetryView();

    void showMediaBarView(Episode episode);

    void hideMediaBarView();

    void showClearCacheDialog(Episode episode);

    void downloadEpisode(Episode episode);

    void launchIntent(Intent intent);

}
