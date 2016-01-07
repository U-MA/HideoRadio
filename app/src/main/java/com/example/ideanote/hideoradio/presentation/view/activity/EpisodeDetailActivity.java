package com.example.ideanote.hideoradio.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.databinding.ActivityEpisodeDetailBinding;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.ClearCacheEvent;
import com.example.ideanote.hideoradio.presentation.events.DownloadEvent;
import com.example.ideanote.hideoradio.presentation.events.PlayCacheEvent;
import com.example.ideanote.hideoradio.presentation.internal.di.DaggerEpisodeComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeModule;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeDetailPresenter;
import com.example.ideanote.hideoradio.presentation.view.dialog.DownloadFailDialog;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.dialog.MediaPlayConfirmationDialog;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;
import com.squareup.otto.Subscribe;

import java.util.Formatter;
import java.util.Locale;

import javax.inject.Inject;

public class EpisodeDetailActivity extends AppCompatActivity {

    private final static String TAG = EpisodeDetailActivity.class.getName();

    private String episodeId;
    private Episode episode;
    private PodcastPlayer podcastPlayer;
    private ImageButton imageButton;
    private SeekBar seekBar;

    private ActivityEpisodeDetailBinding binding;
    private EpisodeComponent episodeComponent;

    @Inject
    EpisodeDetailPresenter episodeDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_episode_detail);

        binding.toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        binding.toolbar.getBackground().setAlpha(0);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        episodeId = getIntent().getStringExtra(EpisodeListActivity.EXTRA_EPISODE_ID);
        episode = Episode.findById(episodeId);

        // TODO: このクラスがPodcastPlayerを持っている必要はあるのか
        //       PodcastPlayerServiceが一括していても良いのでは？
        podcastPlayer = PodcastPlayer.getInstance();

        initMediaButton();
        initSeekBar();

        podcastPlayer.setCurrentTimeListener(new PodcastPlayer.CurrentTimeListener() {
            @Override
            public void onTick(int currentPosition) {
                if (podcastPlayer.isPlaying() && podcastPlayer.getEpisode().equals(episode)) {
                    currentTimeUpdate(currentPosition);
                }
            }
        });

        initializeComponent();

        episodeDetailPresenter.initialize(episodeId);
        episodeDetailPresenter.setView(this);
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
    protected void onDestroy() {
        super.onDestroy();

        PodcastPlayer player = PodcastPlayer.getInstance();
        if (!player.isPlaying() && player.getService() != null) {
            PodcastPlayer.getInstance().getService().stopSelf();
        }
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

    private void initializeComponent() {
        this.episodeComponent = DaggerEpisodeComponent.builder()
                .applicationComponent(((HideoRadioApplication) getApplication()).getComponent())
                .episodeModule(new EpisodeModule(episodeId))
                .build();

        episodeComponent.inject(this);
    }

    protected void initMediaButton() {
        imageButton = binding.episodeDetail.imageButton;

        if (PodcastPlayer.getInstance().isPlaying() && PodcastPlayer.getInstance().getEpisode().equals(episode)) {
            imageButton.setImageResource(R.drawable.ic_action_playback_pause);
        } else {
            imageButton.setImageResource(R.drawable.ic_action_playback_play);
        }
        imageButton.setEnabled(false);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
                if (!podcastPlayer.isStopped() && podcastPlayer.getEpisode().equals(episode)) {
                    imageButton.setImageResource(podcastPlayer.isPlaying()
                            ? R.drawable.ic_action_playback_play
                            : R.drawable.ic_action_playback_pause);
                    seekBar.setEnabled(!podcastPlayer.isPlaying());

                    Intent intent = null;
                    if (podcastPlayer.isPlaying()) {
                        intent = PodcastPlayerService.createPauseIntent(getApplicationContext());
                    } else {
                        intent = PodcastPlayerService.createRestartIntent(getApplicationContext());
                    }
                    startService(intent);
                } else {
                    MediaPlayConfirmationDialog dialog = createPlayConfirmationDialog();
                    dialog.show(getSupportFragmentManager(), "dialog");
                }
            }
        });

        imageButton.setEnabled(true);
    }

    private void initSeekBar() {
        seekBar = binding.episodeDetail.mediaSeekBar;

        seekBar.setEnabled(podcastPlayer.isPlaying() && podcastPlayer.getEpisode().equals(episode));
        seekBar.setMax(durationToMillis(episode.getDuration()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!PodcastPlayer.getInstance().isPlaying()) {
                    return;
                }
                PodcastPlayer.getInstance().seekTo(seekBar.getProgress());
            }
        });

        if (podcastPlayer.isPlaying() && podcastPlayer.getEpisode().equals(episode)) {
            currentTimeUpdate(podcastPlayer.getCurrentPosition());
        } else {
            currentTimeUpdate(0);
        }
    }

    private void currentTimeUpdate(int currentTimeMillis) {
        final int duration = durationToMillis(episode.getDuration());
        binding.episodeDetail.duration.setText(formatMillis(duration - currentTimeMillis));
        seekBar.setProgress(currentTimeMillis);
    }

    private String formatMillis(int timeMillis) {
        int totalSeconds = timeMillis / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder, Locale.getDefault());

        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
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

    public void renderEpisode(Episode episode) {
        Log.i(TAG, "renderEpisode");
        binding.episodeDetail.episodeTitle.setText(episode.getTitle());
        binding.episodeDetail.detailDescription.setText(episode.getDescription());
        binding.episodeDetail.duration.setText(episode.getDuration());
    }

    @Subscribe
    public void onPlayEpisode(PlayCacheEvent playCacheEvent) {
        Log.i(TAG, "onPlayEpisode");
        if (!PodcastPlayer.getInstance().isPlaying() || !PodcastPlayer.getInstance().getEpisode().equals(episode)) {
            imageButton.setImageResource(R.drawable.ic_action_playback_pause);
            seekBar.setEnabled(true);
            Intent intent = PodcastPlayerService.createStartIntent(getApplicationContext(), episode.getEpisodeId());
            startService(intent);
        } else {
            seekBar.setEnabled(false);
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
