package com.example.ideanote.hideoradio.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.ideanote.hideoradio.events.BusHolder;
import com.example.ideanote.hideoradio.events.EpisodeDownloadCompleteEvent;
import com.example.ideanote.hideoradio.dialog.ClearCacheDialog;
import com.example.ideanote.hideoradio.dialog.DownloadFailDialog;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.MediaBarView;
import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.RecyclerViewAdapter;
import com.example.ideanote.hideoradio.RssParserTask;
import com.example.ideanote.hideoradio.services.EpisodeDownloadService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        mediaBar = (MediaBarView) findViewById(R.id.media_bar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        episodes = new ArrayList<>();
        adapter = new RecyclerViewAdapter(episodes);
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(Episode episode) {
                // Viewをクリックしたときの処理
                Intent intent = new Intent(MainActivity.this, EpisodeDetailActivity.class);
                intent.putExtra(MainActivity.EXTRA_EPISODE_ID, episode.getEpisodeId());
                startActivity(intent);
            }

            @Override
            public void onDownloadButtonClick(Episode episode) {
                // DownloadButtonをクリックしたときの処理

                if (!episode.isDownload()) {
                    ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = manager.getActiveNetworkInfo();
                    if (info != null && info.isConnected()) {
                        startService(EpisodeDownloadService.createIntent(getApplicationContext(), episode));
                    } else {
                        DownloadFailDialog dialog = new DownloadFailDialog();
                        dialog.show(getSupportFragmentManager(), "DownloadFailDialog");
                    }
                } else {
                    ClearCacheDialog dialog = new ClearCacheDialog();
                    dialog.setEpisode(episode);
                    dialog.setRecyclerView(recyclerView);
                    dialog.show(getSupportFragmentManager(), "ClearCacheDialog");
                }
            }
        });

        recyclerView.setAdapter(adapter);
        RssParserTask task = new RssParserTask(this, adapter);
        task.execute(RSS_FEED_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume");
        setMediaBarIfPossible();
        BusHolder.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusHolder.getInstance().unregister(this);
    }

    public void setMediaBarIfPossible() {
        mediaBar.setEpisode(PodcastPlayer.getInstance().getEpisode());
    }

    @Subscribe
    public void onEpisodeDownloadComplete(final EpisodeDownloadCompleteEvent event) {
        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // ImageButtonを変更する
                RssParserTask task = new RssParserTask(MainActivity.this, adapter);
                task.execute(RSS_FEED_URL);
            }
        });
    }
}
