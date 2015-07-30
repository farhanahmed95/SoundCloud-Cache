package com.farhanahmed.soundcloudcache;

/**
 * Created by farhanahmed on 24/07/15.
 */
public class SoundCloudConfig {
    public static final String CLIENT_ID = "client_id=YOUR ID HERE";
    public static final String URL = "https://api.soundcloud.com/tracks.json?"+CLIENT_ID;

    public static class Key{
        public static final String TITLE_KEY = "title";
        public static final String STREAM_URL_KEY = "stream_url";
        public static final String URI_KEY = "uri";
        public static final String DURATION_KEY = "duration";
        public static final String ARTWORK_URL_KEY = "artwork_url";

    }

}
