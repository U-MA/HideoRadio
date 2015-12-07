package com.example.ideanote.hideoradio.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.ideanote.hideoradio.dialog.ClearCacheDialog;
import com.example.ideanote.hideoradio.dialog.DownloadFailDialog;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.MediaBarView;
import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.RecyclerViewAdapter;
import com.example.ideanote.hideoradio.services.EpisodeDownloadService;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {
    private MediaBarView mediaBar;

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";

    private static final String TAG = MainActivity.class.getName();

    public static Intent createIntent(Context context, String episodeId) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaBar = (MediaBarView) findViewById(R.id.media_bar);
        mediaBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EpisodeDetailActivity.class);
                intent.putExtra(MainActivity.EXTRA_EPISODE_ID, PodcastPlayer.getInstance().getEpisode().getEpisodeId());
                startActivity(intent);
            }
        });
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

    @Override
    public void onClick(Episode episode) {
        // Viewをクリックしたときの処理
        Intent intent = new Intent(this, EpisodeDetailActivity.class);
        intent.putExtra(MainActivity.EXTRA_EPISODE_ID, episode.getEpisodeId());
        startActivity(intent);
    }

    @Override
    public void onDownloadButtonClick(Episode episode) {
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
            dialog.show(getSupportFragmentManager(), "ClearCacheDialog");
        }
    }
}
