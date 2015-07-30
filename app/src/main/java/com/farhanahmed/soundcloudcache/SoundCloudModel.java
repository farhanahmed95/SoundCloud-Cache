package com.farhanahmed.soundcloudcache;

/**
 * Created by farhanahmed on 23/07/15.
 */
public class SoundCloudModel {
    private String title;
    private String streamUrl;
    private String uri;
    private String duration;
    private String artworkUrl;

    public SoundCloudModel(String title, String streamUrl, String uri, String duration, String artworkUrl) {
        this.title = title;
        this.streamUrl = streamUrl;
        this.uri = uri;
        this.duration = duration;
        this.artworkUrl = artworkUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStreamUrl() {
        return streamUrl+"?"+SoundCloudConfig.CLIENT_ID;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }
}
