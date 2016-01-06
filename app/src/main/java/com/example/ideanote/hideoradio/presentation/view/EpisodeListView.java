package com.example.ideanote.hideoradio.presentation.view;

import com.example.ideanote.hideoradio.Episode;

import java.util.Collection;

public interface EpisodeListView {

    void renderEpisodes(Collection<Episode> episodeList);

    void showLoadingView();

    void hideLoadingView();

    void showRetryView();

    void hideRetryView();
}
