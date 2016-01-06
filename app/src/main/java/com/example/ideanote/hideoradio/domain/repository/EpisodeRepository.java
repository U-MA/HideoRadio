package com.example.ideanote.hideoradio.domain.repository;

import com.example.ideanote.hideoradio.Episode;

import java.util.List;

import rx.Observable;

public interface EpisodeRepository {

    /**
     * Get an {@link rx.Observable} which emit a List of {@link Episode}.
     */
    Observable<List<Episode>> episodes();

    /**
     * Get an {@link rx.Observable} which emit an specified {@link Episode} by episodeId.
     */
    Observable<Episode> episode(final String episodeId);
}
