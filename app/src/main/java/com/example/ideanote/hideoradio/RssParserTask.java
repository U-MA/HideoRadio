package com.example.ideanote.hideoradio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class RssParserTask extends AsyncTask<String, Integer, RecyclerViewAdapter> {
    private MainActivity activity;
    private RecyclerViewAdapter adapter;
    private ProgressDialog progressDialog;

    public RssParserTask(MainActivity activity, RecyclerViewAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Now Loading...");
        progressDialog.show();
    }

    @Override
    protected RecyclerViewAdapter doInBackground(String... params) {
        RecyclerViewAdapter result = null;
        List<Episode> episodes = Episode.find();
        if (episodes.isEmpty()) {
            try {
                URL url = new URL(params[0]);
                InputStream is = url.openConnection().getInputStream();
                result = parseXml(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("episodes", String.valueOf(episodes.size()));
            adapter.clear();
            adapter.addAll(episodes);
            Log.d("adapter.getItemCount", String.valueOf(adapter.getItemCount()));
            result = adapter;
        }
        return result;
    }

    @Override
    protected void onPostExecute(RecyclerViewAdapter result) {
        progressDialog.dismiss();
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(result);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(activity,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("Click", String.valueOf(position));
                        Intent intent = new Intent(activity, EpisodeDetailActivity.class);
                        activity.startActivity(intent);
                    }
                }));
    }

    public RecyclerViewAdapter parseXml(InputStream is) throws IOException {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            Episode currentEpisode = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {
                            currentEpisode = new Episode();
                        } else if (currentEpisode != null) {
                            if (tag.equals("guid")) {
                                currentEpisode.setEpisodeId(parser.nextText());
                            } else if (tag.equals("title")) {
                                currentEpisode.setTitle(parser.nextText());
                            } else if (tag.equals("description")) {
                                currentEpisode.setDescription(parser.nextText());
                            } else if (tag.equals("link")) {
                                currentEpisode.setLink(Uri.parse(parser.nextText()));
                            } else if (tag.equals("date")) {
                                DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.JAPAN);
                                currentEpisode.setPostedAt(format.parse(parser.nextText()));
                            } else if (tag.equals("enclosure")) {
                                // TODO enclosureの設定
                                // 現在は仮のコード
                                currentEpisode.setEnclosure(Uri.parse("http://www.konami.jp"));
                            } else if (tag.equals("duration")) {
                                currentEpisode.setDuration(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {
                            if (Episode.findById(currentEpisode.getEpisodeId()) == null) {
                                currentEpisode.save();
                                adapter.add(currentEpisode);
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adapter;
    }
}
