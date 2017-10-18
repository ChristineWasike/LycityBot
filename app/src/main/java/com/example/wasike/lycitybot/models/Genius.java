package com.example.wasike.lycitybot.models;

/**
 * Created by wasike on 18/10/17.
 */

public class Genius {
    private String songTitle;
    private String lyricsUrl;
    private String artistName;
    private String pushId;
    private String imageThumbnail;

    public Genius() {}

    public Genius(String songTitle, String lyricsUrl, String artistName, String imageThumbnail){
        this.songTitle = songTitle;
        this.lyricsUrl = lyricsUrl;
        this.artistName = artistName;
        this.imageThumbnail = imageThumbnail;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getLyricsUrl() {
        return lyricsUrl;
    }

    public void setLyricsUrl(String lyricsUrl) {
        this.lyricsUrl = lyricsUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }
}
