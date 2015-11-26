package com.example.ideanote.hideoradio;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideanote.hideoradio.activities.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.activities.MainActivity;

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
        /*
        episodeViewHolder.title.setText(episode.getTitle());
        episodeViewHolder.description.setText(episode.getDescription());
        if (episode.isDownload()) {
            episodeViewHolder.imageButton.setImageResource(R.drawable.ic_remove_circle_outline);
        } else {
            episodeViewHolder.imageButton.setImageResource(R.drawable.ic_get_app);
        }
        */
        episodeViewHolder.bind(episode);
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

        private View view;

        EpisodeViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageButton = (ImageButton) itemView.findViewById(R.id.download_toggle_button);
        }

        public void bind(final Episode episode) {
            title.setText(episode.getTitle());
            description.setText(episode.getDescription());
            if (episode.isDownload()) {
                imageButton.setImageResource(R.drawable.ic_remove_circle_outline);
            } else {
                imageButton.setImageResource(R.drawable.ic_get_app);
            }
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Download or not
                    Toast.makeText(view.getContext(), "IMAGE BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), EpisodeDetailActivity.class);
                    intent.putExtra(MainActivity.EXTRA_EPISODE_ID, episode.getEpisodeId());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
