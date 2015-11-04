package com.example.ideanote.hideoradio;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String RSS_FEED_URL = "http://www.konami.jp/kojima_pro/radio/hideradio/podcast.xml";
    private ArrayList<Episode> episodes;
    private RecyclerViewAdapter adapter;
    private MediaBarView mediaBar;

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";

    public static Intent createIntent(Context context, String episodeId) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        mediaBar = (MediaBarView) findViewById(R.id.media_bar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        episodes = new ArrayList<>();
        adapter = new RecyclerViewAdapter(episodes);

        RssParserTask task = new RssParserTask(this, adapter);
        task.execute(RSS_FEED_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume");
        setMediaBarIfPossible();
    }

    public void setMediaBarIfPossible() {
        mediaBar.setEpisode(PodcastPlayer.getInstance().getEpisode());
    }
}
