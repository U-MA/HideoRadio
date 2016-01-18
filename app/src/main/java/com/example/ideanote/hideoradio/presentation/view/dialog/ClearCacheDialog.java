package com.example.ideanote.hideoradio.presentation.view.dialog;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.ClearCacheEvent;

public class ClearCacheDialog extends DialogFragment {

    private static final String EPISODE_ID = "com.example.ideanote.hideoradio.episode_id";
    private static final String EPISODE_TITLE = "com.example.ideanote.hideoradio.episode_title";

    public static ClearCacheDialog newInstance(Episode episode) {
        ClearCacheDialog dialog = new ClearCacheDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EPISODE_ID, episode.getEpisodeId());
        bundle.putString(EPISODE_TITLE, episode.getTitle());
        dialog.setArguments(bundle);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String episodeId = getArguments().getString(EPISODE_ID);
        String episodeTitle = getArguments().getString(EPISODE_TITLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Delete downloaded file")
                .setMessage(episodeTitle)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BusHolder.getInstance().post(new ClearCacheEvent(episodeId));
                    }
                })
                .setNegativeButton("CANCEL", null);

        return builder.create();
    }
}
