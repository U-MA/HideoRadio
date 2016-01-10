package com.example.ideanote.hideoradio.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.activeandroid.query.Select;
import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.DaggerEpisodeComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeModule;
import com.example.ideanote.hideoradio.presentation.internal.di.HasComponent;
import com.example.ideanote.hideoradio.presentation.view.fragment.EpisodeListFragment;
import com.example.ideanote.hideoradio.presentation.view.fragment.NetworkErrorFragment;
import com.example.ideanote.hideoradio.presentation.view.dialog.ClearCacheDialog;
import com.example.ideanote.hideoradio.presentation.view.dialog.DownloadFailDialog;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.view.MediaBarView;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.adapter.RecyclerViewAdapter;
import com.example.ideanote.hideoradio.presentation.view.dialog.EpisodeDownloadCancelDialog;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.EpisodeDownloadCancelEvent;
import com.example.ideanote.hideoradio.presentation.events.NetworkErrorEvent;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;
import com.squareup.otto.Subscribe;


public class EpisodeListActivity extends AppCompatActivity
        implements RecyclerViewAdapter.OnItemClickListener, HasComponent<EpisodeComponent> {

    private EpisodeComponent episodeComponent;

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";

    private static final String TAG = EpisodeListActivity.class.getName();

    public static Intent createIntent(Context context, String episodeId) {
        Intent intent = new Intent(context, EpisodeListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new EpisodeListFragment());
        transaction.commit();

        initializeComponent();

        BusHolder.getInstance().register(this);
    }

    private void initializeComponent() {
        ApplicationComponent applicationComponent =
                ((HideoRadioApplication) getApplicationContext()).getComponent();

        this.episodeComponent = DaggerEpisodeComponent.builder()
                .applicationComponent(applicationComponent)
                .episodeModule(new EpisodeModule())
                .build();
    }

    @Override
    public EpisodeComponent getComponent() {
        return episodeComponent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PodcastPlayer player = PodcastPlayer.getInstance();
        if (!player.isPlaying() && player.getService() != null) {
            PodcastPlayer.getInstance().getService().stopSelf();
        }

        BusHolder.getInstance().unregister(this);
    }

    @Override
    public void onClick(Episode episode) {
        // Viewをクリックしたときの処理
        Intent intent = new Intent(this, EpisodeDetailActivity.class);
        intent.putExtra(EpisodeListActivity.EXTRA_EPISODE_ID, episode.getEpisodeId());
        startActivity(intent);
    }

    @Override
    public void onDownloadButtonClick(Episode episode) {
        if (!episode.isDownload()) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (!EpisodeDownloadService.isDownloading(episode.getEpisodeId())) {
                    startService(EpisodeDownloadService.createIntent(getApplicationContext(), episode));
                } else {
                    // create episode download cancel dialog
                    EpisodeDownloadCancelDialog dialog = new EpisodeDownloadCancelDialog();
                    dialog.setEpisode(episode);
                    dialog.show(getSupportFragmentManager(), "DownloadCancelDialog");
                }
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

    @Subscribe
    public void onNetworkErrorWhenInitialize(NetworkErrorEvent event) {
        if (new Select().from(Episode.class).execute().size() == 0) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new NetworkErrorFragment());
            transaction.commit();
        }
    }

    @Subscribe
    public void onEpisodeDownloadCancel(EpisodeDownloadCancelEvent event) {
    }
}
