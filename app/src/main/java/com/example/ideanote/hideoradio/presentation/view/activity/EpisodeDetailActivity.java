package com.example.ideanote.hideoradio.presentation.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.databinding.ActivityEpisodeDetailBinding;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.EpisodeCompleteEvent;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.DaggerEpisodeComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeModule;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeDetailPresenter;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;
import com.example.ideanote.hideoradio.presentation.view.dialog.MediaPlayConfirmationDialog;
import com.squareup.otto.Subscribe;

import java.util.Formatter;
import java.util.Locale;

import javax.inject.Inject;

public class EpisodeDetailActivity extends AppCompatActivity
        implements MediaPlayConfirmationDialog.OnClickCallback{

    private final static String TAG = EpisodeDetailActivity.class.getName();


    private int durationMax;
    private ActivityEpisodeDetailBinding binding;
    private EpisodeComponent episodeComponent;

    @Inject
    EpisodeDetailPresenter episodeDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_episode_detail);

        setupToolbar();


        initializeComponent();
        episodeComponent.inject(this);

        // TODO: ここの命名を決め直す. 今のメソッド名は何をするか分かりづらい
        episodeDetailPresenter.setView(this);
        episodeDetailPresenter.initialize();
        episodeDetailPresenter.onCreate();

        binding.episodeDetail.imageButton.setOnClickListener(onClickListener);
        binding.episodeDetail.mediaSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        BusHolder.getInstance().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        episodeDetailPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        episodeDetailPresenter.onDestroy();

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

    @Override
    public void onDialogPlayClicked() {
        episodeDetailPresenter.onDialogPlayClicked();
    }

    @Override
    public void onDialogDownloadClicked() {
        episodeDetailPresenter.onDialogDownloadClicked();
    }

    @Override
    public void onDialogDownloadCancelClicked() {
        episodeDetailPresenter.onDialogDownloadCancelClicked();
    }

    @Override
    public void onDialogClearCacheClicked() {
        episodeDetailPresenter.onDialogClearCacheClicked();
    }

    public void startPodcast(Episode episode) {
        Intent intent = PodcastPlayerService.createStartIntent(
                getApplicationContext(), episode.getEpisodeId());
        startService(intent);
    }

    public void startDownload(Episode episode) {
        Intent intent = EpisodeDownloadService.createIntent(getApplicationContext(), episode);
        startService(intent);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        binding.toolbar.getBackground().setAlpha(0);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void initializeComponent() {
        String episodeId = getIntent().getStringExtra(EpisodeListActivity.EXTRA_EPISODE_ID);

        ApplicationComponent applicationComponent =
                ((HideoRadioApplication) getApplicationContext()).getComponent();

        this.episodeComponent = DaggerEpisodeComponent.builder()
                .applicationComponent(applicationComponent)
                .episodeModule(new EpisodeModule(episodeId))
                .build();
    }

    public void setPlayMediaButton() {
        binding.episodeDetail.imageButton.setImageResource(R.drawable.ic_action_playback_play);
    }

    public void setPauseMediaButton() {
        binding.episodeDetail.imageButton.setImageResource(R.drawable.ic_action_playback_pause);
    }

    public void setSeekBarEnabled(boolean enabled) {
        binding.episodeDetail.mediaSeekBar.setEnabled(enabled);
    }

    public void currentTimeUpdate(int currentTimeMillis) {
        binding.episodeDetail.duration.setText(formatMillis(durationMax - currentTimeMillis));
        binding.episodeDetail.mediaSeekBar.setProgress(currentTimeMillis);
    }

    public void renderEpisode(Episode episode) {
        binding.episodeDetail.episodeTitle.setText(episode.getTitle());
        binding.episodeDetail.detailDescription.setText(episode.getDescription());
        binding.episodeDetail.duration.setText(episode.getDuration());

        durationMax = durationToMillis(episode.getDuration());
        binding.episodeDetail.mediaSeekBar.setMax(durationMax);
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
    public void onEpisodeComplete(final EpisodeCompleteEvent event) {
        setPlayMediaButton();
        setSeekBarEnabled(false);
        currentTimeUpdate(0);
    }

    private View.OnClickListener onClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    episodeDetailPresenter.onClick(v);
                }
            };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // do nothing
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // do nothing
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    episodeDetailPresenter.onStopTrackingTouch(seekBar);
                }
            };
}
