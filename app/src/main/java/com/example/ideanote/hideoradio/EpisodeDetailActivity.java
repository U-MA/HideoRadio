package com.example.ideanote.hideoradio;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

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
                if (podcastPlayer.isPlaying()) {
                    podcastPlayer.pause();
                } else if (podcastPlayer.isStopped()) {
                    podcastPlayer.start(getApplicationContext(), episode);
                    PodcastPlayerNotification.notify(getApplicationContext(), episode);
                } else if (podcastPlayer.isPaused()) {
                    podcastPlayer.start();
                } else {
                    // do something
                }
            }
        });

        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setEnabled(false);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                podcastPlayer.stop();
            }
        });

        downloadButton = (Button) findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("EpisodeDetailActivity", "DownloadOnClick");
                startService(DownloadService.createIntent(getApplicationContext(), episode));
            }
        });

        playAndPauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        downloadButton.setEnabled(true);
    }
}
