package com.example.ideanote.hideoradio;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends ListActivity {
    private ArrayList<Episode> episodes;
    private EpisodeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        episodes = new ArrayList<>();
        adapter = new EpisodeListAdapter(this, episodes);

        setListAdapter(adapter);

        for (int i=0; i < 10; ++i) {
            adapter.add(new Episode());
        }
    }
}
