package com.example.ideanote.hideoradio.presentation.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.events.ClearCacheEvent;
import com.example.ideanote.hideoradio.presentation.internal.di.EpisodeComponent;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;
import com.example.ideanote.hideoradio.presentation.view.adapter.RecyclerViewAdapter;
import com.example.ideanote.hideoradio.databinding.FragmentEpisodeListBinding;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.EpisodeDownloadCompleteEvent;
import com.example.ideanote.hideoradio.presentation.events.UpdateEpisodeListEvent;
import com.example.ideanote.hideoradio.presentation.internal.di.HasComponent;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeListPresenter;
import com.example.ideanote.hideoradio.presentation.view.EpisodeListView;
import com.example.ideanote.hideoradio.presentation.view.dialog.ClearCacheDialog;
import com.example.ideanote.hideoradio.presentation.view.dialog.EpisodeDownloadCancelDialog;
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

    private RecyclerViewAdapter recyclerViewAdapter;

    private RecyclerViewAdapter.OnItemClickListener onItemClickListener =
            new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onClick(Episode episode) {
                    Intent intent = new Intent(getContext(), EpisodeDetailActivity.class);
                    intent.putExtra(EpisodeListActivity.EXTRA_EPISODE_ID, episode.getEpisodeId());
                    startActivity(intent);
                }

                @Override
                public void onDownloadButtonClick(Episode episode) {
                    episodeListPresenter.onDownloadButtonClicked(episode);
                }
            };


    public EpisodeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

        binding.mediaBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                episodeListPresenter.onMediaBarViewClicked(v);
            }
        });

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
    public void onResume() {
        super.onResume();
        setupMediaBarView();
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

    public void launchIntent(Intent intent) {
        startActivity(intent);
    }

    public void setupMediaBarView() {
        episodeListPresenter.setupMediaBarView();
    }

    public void setupEpisodeListView() {
        RecyclerView episodeListView = binding.episodeListView;
        episodeListView.setHasFixedSize(false);
        episodeListView.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<Episode> episodes = new ArrayList<>();
        recyclerViewAdapter = new RecyclerViewAdapter(episodes);
        recyclerViewAdapter.setOnItemClickListener(onItemClickListener);
        episodeListView.setAdapter(recyclerViewAdapter);
    }

    @SuppressWarnings("unchecked")
    private <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    private void loadEpisodeList() {
        episodeListPresenter.loadEpisodes();
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

    @Override
    public void showClearCacheDialog(Episode episode) {
        ClearCacheDialog dialog = ClearCacheDialog.newInstance(episode);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public void downloadEpisode(Episode episode) {
        Intent intent = EpisodeDownloadService.createIntent(getContext(), episode);
        getActivity().startService(intent);
    }

    @Override
    public void showMediaBarView(Episode episode) {
        binding.mediaBar.show(episode);
    }

    @Override
    public void hideMediaBarView() {
        binding.mediaBar.hide();
    }

    @Subscribe
    public void onClearCache(final ClearCacheEvent event) {
        String episodeId = event.getEpisodeId();
        Episode episode = Episode.findById(episodeId); // TODO: Use Repository

        episode.clearCache();

        recyclerViewAdapter.notifyDataSetChanged();
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
