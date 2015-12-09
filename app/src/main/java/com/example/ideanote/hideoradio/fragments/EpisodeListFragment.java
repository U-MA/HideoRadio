package com.example.ideanote.hideoradio.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.RecyclerViewAdapter;
import com.example.ideanote.hideoradio.RssParserTask;
import com.example.ideanote.hideoradio.databinding.FragmentEpisodeListBinding;
import com.example.ideanote.hideoradio.events.BusHolder;
import com.example.ideanote.hideoradio.events.EpisodeDownloadCompleteEvent;
import com.example.ideanote.hideoradio.events.UpdateEpisodeListEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


public class EpisodeListFragment extends Fragment {
    private static final String RSS_FEED_URL = "http://www.konami.jp/kojima_pro/radio/hideradio/podcast.xml";
    private static final String TAG = EpisodeListFragment.class.getName();

    private FragmentEpisodeListBinding binding;
    private RecyclerViewAdapter.OnItemClickListener listener;

    public EpisodeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (RecyclerViewAdapter.OnItemClickListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusHolder.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_episode_list, container, false);

        if (new Select().from(Episode.class).execute().size() == 0) {
            binding.episodeListView.setVisibility(View.GONE);
            binding.loadingText.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupEpisodeListView();
        requestFeed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusHolder.getInstance().unregister(this);
    }

    public void setupEpisodeListView() {
        RecyclerView episodeListView = binding.episodeListView;
        episodeListView.setHasFixedSize(false);
        episodeListView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<Episode> episodes = new ArrayList<>();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(episodes);
        adapter.setOnItemClickListener(listener);
        episodeListView.setAdapter(adapter);
    }

    public void requestFeed() {
        Log.i(TAG, "requestFeed");
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) binding.episodeListView.getAdapter();
        RssParserTask task = new RssParserTask(getContext(), adapter);
        task.execute(RSS_FEED_URL);
    }

    @Subscribe
    public void onUpdateEpisodeList(final UpdateEpisodeListEvent event) {
        binding.episodeListView.getAdapter().notifyDataSetChanged();
        if (binding.episodeListView.getVisibility() == View.GONE) {
            binding.episodeListView.setVisibility(View.VISIBLE);
            binding.loadingText.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onEpisodeDownloadComplete(final EpisodeDownloadCompleteEvent event) {
        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO ImageButtonを変更する
                //      notifyDataSetChangedをしてもimageButtonは変わらない
                BusHolder.getInstance().post(new UpdateEpisodeListEvent());
            }
        });
    }
}
