package com.example.ideanote.hideoradio;

public class Episode {
    private CharSequence title;
    private CharSequence description;

    public Episode() {
        title = "";
        description = "";
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public  CharSequence getDescription() {
        return description;
    }

    public void setDescription(CharSequence description) {
        this.description = description;
    }
}
