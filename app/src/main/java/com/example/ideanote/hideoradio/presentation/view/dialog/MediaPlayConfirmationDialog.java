package com.example.ideanote.hideoradio.presentation.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.ClearCacheEvent;
import com.example.ideanote.hideoradio.presentation.events.DownloadEvent;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.events.PlayCacheEvent;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;

/**
 * A dialog that confirm playing or downloading/streaming episode
 */
public class MediaPlayConfirmationDialog extends DialogFragment {

    private static final String PLAY_STREAMING_TEXT      = "PLAY STREAMING";
    private static final String PLAY_CACHE_TEXT          = "PLAY";
    private static final String DOWNLOAD_TEXT            = "DOWNLOAD";
    private static final String CANCEL_DOWNLOAD_TEXT     = "CANCEL DOWNLOAD";
    private static final String DELETE_EPISODE_FILE_TEXT = "DELETE EPISODE FILE";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String episodeId = getArguments().getString("episodeId");
        final Episode episode = Episode.findById(episodeId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(episode.getTitle())
               .setMessage(episode.getDescription());

        if (episode.isDownload()) {
            builder.setPositiveButton(PLAY_CACHE_TEXT, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BusHolder.getInstance().post(new PlayCacheEvent());
                }
            })
                   .setNegativeButton(DELETE_EPISODE_FILE_TEXT, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           BusHolder.getInstance().post(new ClearCacheEvent());
                       }
                   });
        } else {
            builder.setPositiveButton(PLAY_STREAMING_TEXT, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BusHolder.getInstance().post(new PlayCacheEvent());
                }
            })
                    .setNegativeButton(DOWNLOAD_TEXT, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            });
            // DOWNLOADING中の動作
        }

        return builder.create();
    }
}
