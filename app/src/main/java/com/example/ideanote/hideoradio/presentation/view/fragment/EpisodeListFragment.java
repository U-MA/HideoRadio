package com.example.ideanote.hideoradio.presentation.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeComponent;
import com.example.ideanote.hideoradio.presentation.view.adapter.RecyclerViewAdapter;
import com.example.ideanote.hideoradio.RssParserTask;
import com.example.ideanote.hideoradio.databinding.FragmentEpisodeListBinding;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.EpisodeDownloadCompleteEvent;
import com.example.ideanote.hideoradio.presentation.events.UpdateEpisodeListEvent;
import com.example.ideanote.hideoradio.presentation.internal.di.HasComponent;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeListPresenter;
import com.example.ideanote.hideoradio.presentation.view.EpisodeListView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;


public class EpisodeListFragment extends Fragment implements EpisodeListView {
    private static final String RSS_FEED_URL = "http://www.konami.jp/kojima_pro/radio/hideradio/podcast.xml";
    private static final String TAG = EpisodeListFragment.class.getName();

    @Inject
    EpisodeListPresenter episodeListPresenter;

    private FragmentEpisodeListBinding binding;
    private RecyclerViewAdapter.OnItemClickListener listener;

    private RecyclerViewAdapter recyclerViewAdapter;

    public EpisodeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecyclerViewAdapter.OnItemClickListener) {
            listener = (RecyclerViewAdapter.OnItemClickListener) context;
        }
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
        setupEpisodeListView();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getComponent(EpisodeComponent.class).inject(this);
        episodeListPresenter.setView(this);

        loadEpisodeList();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        BusHolder.getInstance().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        episodeListPresenter.onDestroy();
    }

    public void setupEpisodeListView() {
        RecyclerView episodeListView = binding.episodeListView;
        episodeListView.setHasFixedSize(false);
        episodeListView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<Episode> episodes = new ArrayList<>();
        recyclerViewAdapter = new RecyclerViewAdapter(episodes);
        recyclerViewAdapter.setOnItemClickListener(listener);
        episodeListView.setAdapter(recyclerViewAdapter);
    }

    @SuppressWarnings("unchecked")
    private <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    private void loadEpisodeList() {
        episodeListPresenter.loadEpisodes();
    }

    public void requestFeed() {
        RecyclerViewAdapter adapter = (RecyclerViewAdapter) binding.episodeListView.getAdapter();
        RssParserTask task = new RssParserTask(getContext(), adapter);
        task.execute(RSS_FEED_URL);
    }

    private void setupUI() {
        if (new Select().from(Episode.class).execute().size() == 0) {
            binding.episodeListView.setVisibility(View.GONE);
            // binding.loadingText.setVisibility(View.VISIBLE);
            // binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void renderEpisodes(Collection<Episode> episodeList) {
        if (episodeList != null) {
            recyclerViewAdapter.setEpisodeList(episodeList);
        }
    }

    @Override
    public void showLoadingView() {
        binding.loadingEpisodesView.loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingView() {
        binding.loadingEpisodesView.loadingView.setVisibility(View.GONE);
    }

    @Override
    public void showRetryView() {

    }

    @Override
    public void hideRetryView() {

    }

    @Subscribe
    public void onUpdateEpisodeList(final UpdateEpisodeListEvent event) {
        binding.episodeListView.getAdapter().notifyDataSetChanged();
        if (binding.episodeListView.getVisibility() == View.GONE) {
            binding.episodeListView.setVisibility(View.VISIBLE);
            // binding.loadingText.setVisibility(View.GONE);
            // binding.progressBar.setVisibility(View.GONE);
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
