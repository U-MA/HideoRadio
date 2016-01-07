package com.example.ideanote.hideoradio.data.net;

import android.net.Uri;
import android.util.Xml;

import com.activeandroid.ActiveAndroid;
import com.example.ideanote.hideoradio.Episode;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;

/**
 * a class which request feed.
 */
public class Feeder {
    private static final String RSS_FEED_URL = "http://www.konami.jp/kojima_pro/radio/hideradio/podcast.xml";

    public static Observable<List<Episode>> requestFeed() {
        return Observable.create(new Observable.OnSubscribe<List<Episode>>() {
            @Override
            public void call(Subscriber<? super List<Episode>> subscriber) {
                InputStream is = null;
                try {
                    URL url = new URL(RSS_FEED_URL);
                    is = url.openConnection().getInputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
                List<Episode> episodes = parseXml(is);
                subscriber.onNext(episodes);
                subscriber.onCompleted();
            }
        });
    }

    private static List<Episode> parseXml(InputStream is) {
        ArrayList<Episode> episodes = new ArrayList<>();

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            Episode currentEpisode = null;
            ActiveAndroid.beginTransaction();
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
                            } else if (tag.equals("pubDate")) {
                                DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
                                currentEpisode.setPostedAt(format.parse(parser.nextText()));
                            } else if (tag.equals("enclosure")) {
                                // TODO enclosureの設定
                                String url = parser.getAttributeValue(0);
                                currentEpisode.setEnclosure(Uri.parse(url));
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
                                episodes.add(currentEpisode);
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActiveAndroid.endTransaction();

        return episodes;
    }
}
