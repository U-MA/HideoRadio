package com.example.ideanote.hideoradio.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.activeandroid.query.Select;
import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.presentation.events.EpisodeCompleteEvent;
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
        implements HasComponent<EpisodeComponent>, Toolbar.OnMenuItemClickListener {

    private EpisodeComponent episodeComponent;

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";

    private static final String TAG = EpisodeListActivity.class.getName();

    private Toolbar toolbar;

    public static Intent createIntent(Context context, String episodeId) {
        Intent intent = new Intent(context, EpisodeListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        toolbar.setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                startActivity(EpisodeSearchActivity.createIntent(getApplicationContext()));
                return true;
        }
        return false;
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
