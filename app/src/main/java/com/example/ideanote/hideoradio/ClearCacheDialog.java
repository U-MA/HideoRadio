package com.example.ideanote.hideoradio;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class ClearCacheDialog extends DialogFragment {

    private Episode episode;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete downloaded file")
               .setMessage("XXX")
               .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if (episode != null) {
                           episode.clearCache();
                           episode.save();
                       }
                   }

               });
        return builder.create();
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }
}
