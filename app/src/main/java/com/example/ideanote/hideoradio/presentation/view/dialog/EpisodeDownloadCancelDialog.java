package com.example.ideanote.hideoradio.presentation.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;

public class EpisodeDownloadCancelDialog extends DialogFragment {

    private Episode episode;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Download cancel")
               .setMessage("Cancel downloading?");
        return builder.create();
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }
}
