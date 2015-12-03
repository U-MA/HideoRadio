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
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ideanote.hideoradio.events.BusHolder;
import com.example.ideanote.hideoradio.events.ClearCacheEvent;
import com.example.ideanote.hideoradio.events.DownloadEvent;
import com.example.ideanote.hideoradio.events.PlayCacheEvent;
import com.example.ideanote.hideoradio.dialog.DownloadFailDialog;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.dialog.MediaPlayConfirmationDialog;
import com.example.ideanote.hideoradio.services.EpisodeDownloadService;
import com.example.ideanote.hideoradio.services.PodcastPlayerService;
import com.squareup.otto.Subscribe;

public class EpisodeDetailActivity extends AppCompatActivity {

    private final static String TAG = EpisodeDetailActivity.class.getName();

    private Button playAndPauseButton;
    private Button stopButton;
    private Button downloadButton;
    private Episode episode;
    private PodcastPlayer podcastPlayer;
    private ImageButton imageButton;
    private SeekBar seekBar;

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

        TextView descriptionView = (TextView) findViewById(R.id.detail_description);
        descriptionView.setText(episode.getDescription());

        podcastPlayer = PodcastPlayer.getInstance();

        TextView durationText = (TextView) findViewById(R.id.duration);
        durationText.setText(episode.getDuration());

        initMediaButton();
        initSeekBar();

        podcastPlayer.setCurrentTimeListener(new PodcastPlayer.CurrentTimeListener() {
            @Override
            public void onTick(int currentPosition) {
                currentTimeUpdate(currentPosition);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusHolder.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusHolder.getInstance().unregister(this);
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
        imageButton = (ImageButton) findViewById(R.id.image_button);
        imageButton.setImageResource(PodcastPlayer.getInstance().isPlaying()
            ? R.drawable.ic_action_playback_pause
            : R.drawable.ic_action_playback_play);
        imageButton.setEnabled(false);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
                if (!podcastPlayer.isStopped()) {
                    imageButton.setImageResource(podcastPlayer.isPlaying()
                            ? R.drawable.ic_action_playback_play
                            : R.drawable.ic_action_playback_pause);
                    Intent intent = PodcastPlayerService.createPlayPauseIntent(getApplicationContext(), episode);
                    startService(intent);
                } else {
                    MediaPlayConfirmationDialog dialog = createPlayConfirmationDialog();
                    dialog.show(getSupportFragmentManager(), "dialog");
                    seekBar.setEnabled(true);
                }
            }
        });

        imageButton.setEnabled(true);
    }

    private void initSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.media_seek_bar);
        seekBar.setEnabled(podcastPlayer.isPlaying());
        seekBar.setMax(durationToMillis(episode.getDuration()));
        seekBar.setEnabled(true);

        if (podcastPlayer.isPlaying()) {
            currentTimeUpdate(podcastPlayer.getCurrentPosition());
        } else {
            currentTimeUpdate(0);
        }
    }

    private void currentTimeUpdate(int currentTimeMillis) {
        seekBar.setProgress(currentTimeMillis);
    }

    private MediaPlayConfirmationDialog createPlayConfirmationDialog() {
        MediaPlayConfirmationDialog dialog = new MediaPlayConfirmationDialog();
        Bundle bundle = new Bundle();
        bundle.putString("episodeId", episode.getEpisodeId());
        dialog.setArguments(bundle);
        return dialog;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * HH:MM:SSのフォーマットの文字列をミリ秒に変換する
     *
     * @param duration
     * @return
     */
    public int durationToMillis(String duration) {
        String[] data = duration.split(":");

        int sec = 0;
        for (int i=0; i < data.length - 1; ++i) {
            sec = (sec + Integer.valueOf(data[i])) * 60;
        }
        sec += Integer.valueOf(data[data.length - 1]);

        return sec * 1000;
    }

    @Subscribe
    public void onPlayEpisode(PlayCacheEvent playCacheEvent) {
        if (!PodcastPlayer.getInstance().isPlaying()) {
            imageButton.setImageResource(R.drawable.ic_action_playback_pause);
            Intent intent = PodcastPlayerService.createPlayPauseIntent(getApplicationContext(), episode);
            startService(intent);
        }
    }

    @Subscribe
    public void onClearCache(ClearCacheEvent clearCacheEvent) {
        if (episode != null && episode.isDownload()) {
            episode.clearCache();
            episode.save();
        }
    }

    @Subscribe
    public void onDownload(DownloadEvent downloadEvent) {
        if (isOnline()) {
            startService(EpisodeDownloadService.createIntent(getApplicationContext(), episode));
        } else {
            DownloadFailDialog dialog = new DownloadFailDialog();
            dialog.show(getSupportFragmentManager(), "DownloadFailDialog");
        }
    }
}
