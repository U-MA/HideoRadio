package com.example.ideanote.hideoradio.dialog;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.events.BusHolder;
import com.example.ideanote.hideoradio.events.UpdateEpisodeListEvent;

public class ClearCacheDialog extends DialogFragment {

    private Episode episode;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete downloaded file")
               .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if (episode != null) {
                           episode.clearCache();
                           episode.save();

                           BusHolder.getInstance().post(new UpdateEpisodeListEvent());
                       }
                   }

               });
        if (episode != null) {
            builder.setMessage(episode.getTitle());
        }
        return builder.create();
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }
}
