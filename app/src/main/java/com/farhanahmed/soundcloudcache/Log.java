package com.farhanahmed.soundcloudcache;

/**
 * Created by farhanahmed on 23/07/15.
 */
public class Log {
    private static boolean isProduction = false;
    public static void setProduction()
    {
        isProduction = true;
    }
    public static void setDevelopment()
    {
        isProduction = false;
    }
    public static void debug(String tag,String arg)
    {
        if(!isProduction)
        {
            android.util.Log.d(tag,arg);
        }
    }
}
