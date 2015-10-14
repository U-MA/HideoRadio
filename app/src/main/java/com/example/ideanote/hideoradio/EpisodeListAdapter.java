package com.example.ideanote.hideoradio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EpisodeListAdapter extends ArrayAdapter<Episode> {
    private LayoutInflater inflater;
    private TextView title;
    private TextView description;

    public EpisodeListAdapter(Context context, List<Episode> objects) {
        super(context, 0, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // １行ごとのViewを生成
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_row, null);
        }

        Episode episode = this.getItem(position);
        if (episode != null) {
            title = (TextView) view.findViewById(R.id.episode_title);
            title.setText(episode.getTitle().toString());

            description = (TextView) view.findViewById(R.id.episode_description);
            description.setText(episode.getDescription().toString());
        }
        return view;
    }
}
