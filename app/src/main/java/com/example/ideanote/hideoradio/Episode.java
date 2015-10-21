package com.example.ideanote.hideoradio;

import android.net.Uri;
import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

@Table(name = "episodes")
public class Episode extends Model {

    @Column(name = "eid")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "link")
    private Uri link;

    @Column(name = "posted_at")
    private Date postedAt;

    @Column(name = "enclosure")
    private Uri enclosure;

    @Column(name = "duration")
    private String duration;

    @Column(name = "media_local_path")
    private String mediaLocalPath;

    public Episode() {
        super();
    }

    public Episode(String id, String title, String description, Uri link,
                   Date postedAt, Uri enclosure, String duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.postedAt = postedAt;
        this.enclosure = enclosure;
        this.duration = duration;
    }

    public String getEpisodeId() {
        return id;
    }

    public void setEpisodeId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public  String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getLink() {
        return link;
    }

    public void setLink(Uri link) {
        this.link = link;
    }

    public String getPostedAtAsString() {
        return dateToString(postedAt);
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public Uri getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(Uri enclosure) {
        this.enclosure = enclosure;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMediaLocalPath() {
        return mediaLocalPath;
    }

    public void setMediaLocalPath(String mediaLocalPath) {
        this.mediaLocalPath = mediaLocalPath;
    }

    public Boolean isDownloaded() {
        if(TextUtils.isEmpty(mediaLocalPath)) {
            return false;
        }
        // TODO
        return true;
    }

    public static Episode findById(String eid) {
        return new Select().from(Episode.class).where("eid=?", eid).executeSingle();
    }

    public static List<Episode> find() {
        List<Episode> episodeList = new Select().from(Episode.class).orderBy("ID ASC").execute();
        return episodeList;
    }

    // from https://github.com/rejasupotaro/Rebuild/blob/master/Rebuild/src/main/java/rejasupotaro/rebuild/utils/DateUtils.java
    public static String dateToString(Date date) {
        int month = date.getMonth() + 1;
        int day = date.getDate();
        int year = 1900 + date.getYear();
        String dateString = monthToName(month) + " " + (day < 10 ? "0" + day : day) + " " + year;
        return dateString;
    }

    public static String monthToName(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }
}
