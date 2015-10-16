package com.example.ideanote.hideoradio;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends ListActivity {
    private static final String RSS_FEED_URL = "http://www.konami.jp/kojima_pro/radio/hideradio/podcast.xml";
    private ArrayList<Episode> episodes;
    private EpisodeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        episodes = new ArrayList<>();
        adapter = new EpisodeListAdapter(this, episodes);

        RssParserTask task = new RssParserTask(this, adapter);
        task.execute(RSS_FEED_URL);
    }
}
