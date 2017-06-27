package com.example.android.tabdemo;

/**
 * Created by deepak on 21/6/17.
 */

public class SongInfo {
    public String songName, artistName, url;

    //empty constructor
    public SongInfo() {
    }

    public String getUrl() {
        return url;
    }

    public SongInfo(String songName, String artistName, String url) {
        this.songName = songName;

        this.artistName = artistName;
        this.url = url;
    }

}
