package com.example.ideanote.hideoradio;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        episodeViewHolder.title.setText(episodes.get(i).getTitle());
        episodeViewHolder.description.setText(episodes.get(i).getDescription());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        TextView description;

        EpisodeViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
