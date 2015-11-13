package com.example.ideanote.hideoradio;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.EpisodeViewHolder> {

    private List<Episode> episodes;

    public RecyclerViewAdapter(List<Episode> episodes) {
        this.episodes = episodes;
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

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        EpisodeViewHolder episodeViewHolder = new EpisodeViewHolder(v);
        return episodeViewHolder;
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder episodeViewHolder, int i) {
        Episode episode = episodes.get(i);
        episodeViewHolder.title.setText(episode.getTitle());
        episodeViewHolder.description.setText(episode.getDescription());
        if (episode.isDownload()) {
            // TODO prepare image
            episodeViewHolder.imageButton.setImageResource(android.R.drawable.btn_minus);
        } else {
            // TODO prepare image
            episodeViewHolder.imageButton.setImageResource(android.R.drawable.btn_plus);
        }
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

        EpisodeViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageButton = (ImageButton) itemView.findViewById(R.id.download_toggle_button);
        }
    }
}
