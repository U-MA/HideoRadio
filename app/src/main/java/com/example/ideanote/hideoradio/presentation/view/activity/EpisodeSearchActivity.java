package com.example.ideanote.hideoradio.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.ideanote.hideoradio.R;

public class EpisodeSearchActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, EpisodeSearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_search);
    }

}
