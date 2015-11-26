package com.example.ideanote.hideoradio.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ideanote.hideoradio.dialog.DownloadFailDialog;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.services.EpisodeDownloadService;
import com.example.ideanote.hideoradio.services.PodcastPlayerService;

public class EpisodeDetailActivity extends AppCompatActivity {

    private Button playAndPauseButton;
    private Button stopButton;
    private Button downloadButton;
    private Episode episode;
    private PodcastPlayer podcastPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        Drawable d = toolbar.getBackground();
        d.setAlpha(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        String episodeId = getIntent().getStringExtra(MainActivity.EXTRA_EPISODE_ID);
        episode = Episode.findById(episodeId);

        TextView titleView = (TextView) findViewById(R.id.detail_title);
        titleView.setText(episode.getTitle());
        TextView descriptionView = (TextView) findViewById(R.id.detail_description);
        descriptionView.setText(episode.getDescription());

        podcastPlayer = PodcastPlayer.getInstance();

        initMediaButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initMediaButton() {
        playAndPauseButton = (Button) findViewById(R.id.play_and_pause_button);
        playAndPauseButton.setEnabled(false);
        playAndPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PodcastPlayerService.createPlayPauseIntent(getApplicationContext(), episode);
                startService(intent);
            }
        });

        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setEnabled(false);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PodcastPlayerService.createStopIntent(getApplicationContext(), episode);
                startService(intent);
            }
        });

        downloadButton = (Button) findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EpisodeDetailActivity", "DownloadOnClick");
                if (isOnline()) {
                    startService(EpisodeDownloadService.createIntent(getApplicationContext(), episode));
                } else {
                    DownloadFailDialog dialog = new DownloadFailDialog();
                    dialog.show(getSupportFragmentManager(), "DownloadFailDialog");
                }
            }
        });

        playAndPauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        downloadButton.setEnabled(true);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
