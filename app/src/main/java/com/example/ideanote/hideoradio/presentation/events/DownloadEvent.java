package com.example.ideanote.hideoradio.presentation.events;

public class DownloadEvent {

    private String episodeId;

    public DownloadEvent(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeId() {
        return episodeId;
    }
}
