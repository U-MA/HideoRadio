package com.example.ideanote.hideoradio.presentation.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.databinding.ActivityEpisodeDetailBinding;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.PlayCacheEvent;
import com.example.ideanote.hideoradio.presentation.internal.di.DaggerEpisodeComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeModule;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeDetailPresenter;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.dialog.MediaPlayConfirmationDialog;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;
import com.squareup.otto.Subscribe;

import java.util.Formatter;
import java.util.Locale;

import javax.inject.Inject;

public class EpisodeDetailActivity extends AppCompatActivity {

    private final static String TAG = EpisodeDetailActivity.class.getName();

    private String episodeId;
    private ImageButton imageButton;
    private SeekBar seekBar;
    private int durationMax;

    private ActivityEpisodeDetailBinding binding;
    private EpisodeComponent episodeComponent;

    private PodcastPlayer podcastPlayer; // TODO: need not

    @Inject
    EpisodeDetailPresenter episodeDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_episode_detail);

        setupToolbar();

        episodeId = getIntent().getStringExtra(EpisodeListActivity.EXTRA_EPISODE_ID);

        initializeComponent();
        episodeComponent.inject(this);

        episodeDetailPresenter.setView(this);
        episodeDetailPresenter.initialize();


        // TODO: このクラスがPodcastPlayerを持っている必要はあるのか
        //       PodcastPlayerServiceが一括していても良いのでは？
        podcastPlayer = PodcastPlayer.getInstance();

        initMediaButton();
        initSeekBar();

        podcastPlayer.setCurrentTimeListener(new PodcastPlayer.CurrentTimeListener() {
            @Override
            public void onTick(int currentPosition) {
                if (podcastPlayer.isPlaying() && podcastPlayer.getEpisode().getEpisodeId().equals(episodeId)) {
                    currentTimeUpdate(currentPosition);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        episodeDetailPresenter.onResume();

        BusHolder.getInstance().register(this); // TODO: deleted this line;
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusHolder.getInstance().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        episodeDetailPresenter.onDestroy();

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

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        binding.toolbar.getBackground().setAlpha(0);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    private void initializeComponent() {
        this.episodeComponent = DaggerEpisodeComponent.builder()
                .applicationComponent(((HideoRadioApplication) getApplication()).getComponent())
                .episodeModule(new EpisodeModule(episodeId))
                .build();
    }

    public void setPlayMediaButton() {
        binding.episodeDetail.imageButton.setImageResource(R.drawable.ic_action_playback_play);
    }

    public void setPauseMediaButton() {
        binding.episodeDetail.imageButton.setImageResource(R.drawable.ic_action_playback_pause);
    }

    protected void initMediaButton() {
        imageButton = binding.episodeDetail.imageButton;

        if (PodcastPlayer.getInstance().isPlaying() && PodcastPlayer.getInstance().getEpisode().getEpisodeId().equals(episodeId)) {
            setPauseMediaButton();
        } else {
            setPlayMediaButton();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: episodeDetailPresenter.onclick(View v);

                PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
                if (!podcastPlayer.isStopped() && podcastPlayer.getEpisode().getEpisodeId().equals(episodeId)) {
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
    }

    private void initSeekBar() {
        seekBar = binding.episodeDetail.mediaSeekBar;

        seekBar.setEnabled(podcastPlayer.isPlaying() && podcastPlayer.getEpisode().getEpisodeId().equals(episodeId));
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

        if (podcastPlayer.isPlaying() && podcastPlayer.getEpisode().getEpisodeId().equals(episodeId)) {
            currentTimeUpdate(podcastPlayer.getCurrentPosition());
        } else {
            currentTimeUpdate(0);
        }
    }

    private void currentTimeUpdate(int currentTimeMillis) {
        binding.episodeDetail.duration.setText(formatMillis(durationMax - currentTimeMillis));
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
        bundle.putString("episodeId", episodeId);
        dialog.setArguments(bundle);
        return dialog;
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
        binding.episodeDetail.episodeTitle.setText(episode.getTitle());
        binding.episodeDetail.detailDescription.setText(episode.getDescription());
        binding.episodeDetail.duration.setText(episode.getDuration());

        durationMax = durationToMillis(episode.getDuration());
        binding.episodeDetail.mediaSeekBar.setMax(durationMax);

    }

    @Subscribe
    public void onPlayEpisode(PlayCacheEvent playCacheEvent) {
        if (!PodcastPlayer.getInstance().isPlaying() || !PodcastPlayer.getInstance().getEpisode().getEpisodeId().equals(episodeId)) {
            imageButton.setImageResource(R.drawable.ic_action_playback_pause);
            seekBar.setEnabled(true);
            Intent intent = PodcastPlayerService.createStartIntent(getApplicationContext(), episodeId);
            startService(intent);
        } else {
            seekBar.setEnabled(false);
        }
    }
}
