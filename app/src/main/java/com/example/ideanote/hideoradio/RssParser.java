package com.example.ideanote.hideoradio;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RssParser {

    public RssParser() {
    }

    public ArrayList<Episode> parse(InputStream is) throws IOException {
        XmlPullParser parser = Xml.newPullParser();
        ArrayList<Episode> list = new ArrayList<>();
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
                            if (tag.equals("title")) {
                                currentEpisode.setTitle(parser.nextText());
                            } else if (tag.equals("description")) {
                                currentEpisode.setDescription(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {
                            list.add(currentEpisode);
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
