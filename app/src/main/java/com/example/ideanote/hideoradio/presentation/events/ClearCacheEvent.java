package com.example.ideanote.hideoradio.presentation.events;

public class ClearCacheEvent {
    private final String episodeId;

    public ClearCacheEvent(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeId() {
        return episodeId;
    }
}
