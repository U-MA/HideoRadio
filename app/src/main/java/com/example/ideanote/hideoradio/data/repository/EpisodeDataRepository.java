package com.example.ideanote.hideoradio.data.repository;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.data.net.Feeder;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class EpisodeDataRepository implements EpisodeRepository {

    private static final String RSS_FEED_URL = "http://www.konami.jp/kojima_pro/radio/hideradio/podcast.xml";

    @Inject
    public EpisodeDataRepository() {
    }

    /**
     * DBにデータがあれば、DBからエピソードリストを取得
     * なければfeedから取得
     */
    @Override
    public Observable<List<Episode>> episodes() {
        List<Episode> episodes = Episode.find();
        if (episodes.isEmpty()) {
            return requestFeed();
        } else {
            ArrayList<List<Episode>> array = new ArrayList<>();
            array.add(episodes);
            return Observable.from(array);
        }
    }

    @Override
    public Observable<Episode> episode(String episodeId) {
        return null;
    }

    private Observable<List<Episode>> requestFeed() {
        return Feeder.requestFeed();
    }
}
