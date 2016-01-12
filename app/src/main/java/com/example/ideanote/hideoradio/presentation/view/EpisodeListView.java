package com.example.ideanote.hideoradio.presentation.view;

import com.example.ideanote.hideoradio.Episode;

import java.util.Collection;

public interface EpisodeListView {

    void renderEpisodes(Collection<Episode> episodeList);

    void showLoadingView();

    void hideLoadingView();

    void showRetryView();

    void hideRetryView();

    void showMediaBarView();

    void hideMediaBarView();

    void showClearCacheDialog(Episode episode);

    void downloadEpisode(Episode episode);

}
