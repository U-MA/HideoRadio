package com.example.ideanote.hideoradio.presentation.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.ideanote.hideoradio.Episode;

/**
 * A dialog that confirm playing or downloading/streaming episode
 */
public class MediaPlayConfirmationDialog extends DialogFragment {

    private static final String PLAY_STREAMING_TEXT      = "PLAY STREAMING";
    private static final String PLAY_CACHE_TEXT          = "PLAY";
    private static final String DOWNLOAD_TEXT            = "DOWNLOAD";
    private static final String CANCEL_DOWNLOAD_TEXT     = "CANCEL DOWNLOAD";
    private static final String DELETE_EPISODE_FILE_TEXT = "DELETE EPISODE FILE";

    private OnClickCallback onClickCallback;

    public interface OnClickCallback {
        void onDialogPlayClicked();
        void onDialogDownloadClicked();
        void onDialogDownloadCancelClicked();
        void onDialogClearCacheClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnClickCallback) {
            this.onClickCallback = (OnClickCallback) activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String episodeId = getArguments().getString("episodeId");
        final Episode episode = Episode.findById(episodeId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(episode.getTitle())
                .setMessage(episode.getDescription());

        setButton(builder, episode.isDownloaded());

        return builder.create();
    }

    private void setButton(AlertDialog.Builder builder, boolean isDownloaded) {
         if (isDownloaded) {
             builder.setPositiveButton(PLAY_CACHE_TEXT, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     onClickCallback.onDialogPlayClicked();
                 }
             }).setNegativeButton(DELETE_EPISODE_FILE_TEXT, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     onClickCallback.onDialogClearCacheClicked();
                 }
             });
         } else {
             builder.setPositiveButton(PLAY_STREAMING_TEXT, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     onClickCallback.onDialogPlayClicked();
                 }
             }).setNegativeButton(DOWNLOAD_TEXT, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     onClickCallback.onDialogDownloadClicked();
                 }
             });
         }
    }
}
