package com.example.ideanote.hideoradio;

import android.app.Activity;
import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String RSS_FEED_URL = "http://www.konami.jp/kojima_pro/radio/hideradio/podcast.xml";
    private ArrayList<Episode> episodes;
    private RecyclerViewAdapter adapter;

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        episodes = new ArrayList<>();
        adapter = new RecyclerViewAdapter(episodes);

        RssParserTask task = new RssParserTask(this, adapter);
        task.execute(RSS_FEED_URL);
    }
}
