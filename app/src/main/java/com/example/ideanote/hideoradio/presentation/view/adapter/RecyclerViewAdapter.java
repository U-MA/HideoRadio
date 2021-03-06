package com.example.ideanote.hideoradio.presentation.view.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;

import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.EpisodeViewHolder> {

    private List<Episode> episodes;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setEpisodeList(Collection<Episode> episodeList) {
        this.episodes = (List<Episode>) episodeList;
        notifyDataSetChanged();
    }

    public void add(Episode episode) {
        episodes.add(episode);
    }

    public void clear() {
        episodes.clear();
    }

    public void addAll(List<Episode> episodes) {
        this.episodes.addAll(episodes);
    }

    public Episode getAt(int position) {
        return episodes.get(position);
    }

    public void setOnItemClickListener(RecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        EpisodeViewHolder episodeViewHolder = new EpisodeViewHolder(v, onItemClickListener);
        return episodeViewHolder;
    }

    // 指定されたpositionのviewを表示するためにRecyclerViewが呼ぶメソッド
    @Override
    public void onBindViewHolder(EpisodeViewHolder episodeViewHolder, int i) {
        episodeViewHolder.bind(episodes.get(i));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        TextView description;
        ImageButton imageButton;
        TextView postedAt;

        private View view;
        private OnItemClickListener onItemClickListener;

        EpisodeViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            view = itemView;
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageButton = (ImageButton) itemView.findViewById(R.id.download_toggle_button);
            postedAt = (TextView) itemView.findViewById(R.id.posted_at);
            this.onItemClickListener = onItemClickListener;
        }

        /**
         * 与えられたEpisodeに対応したViewを準備する
         *
         * @param episode
         */
        public void bind(final Episode episode) {
            title.setText(episode.getTitle());
            description.setText(episode.getDescription());
            imageButton.setImageResource(episode.isDownload() ?
                R.drawable.ic_remove_circle_outline :
                R.drawable.ic_get_app);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onDownloadButtonClick(episode);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(episode);
                }
            });
            postedAt.setText(episode.getPostedAtAsString());
        }
    }

    public interface OnItemClickListener {

        // Viewをクリックしたとき用
        void onClick(Episode episode);

        // Viewの中のDownload buttonをクリックしたとき用
        void onDownloadButtonClick(Episode episode);
    }
}
