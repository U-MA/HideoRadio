package com.example.ideanote.hideoradio.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.ideanote.hideoradio.Episode;

/**
 * A dialog that confirm playing or downloading/streaming episode
 */
public class MediaPlayConfirmationDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String episodeId = getArguments().getString("episodeId");
        Episode episode = Episode.findById(episodeId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(episode.getTitle())
               .setMessage(episode.getDescription())
               .setPositiveButton("PLAY STREAMING", null)
               .setNegativeButton("DOWNLOAD", null);

        return builder.create();
    }
}
