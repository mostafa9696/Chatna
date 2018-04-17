package com.example.mostafahussien.chatna;

import android.app.Application;
import android.content.Context;

/**
 * Created by Mostafa Hussien on 17/04/2018.
 */

public class TimeUtility extends Application {
    private static final int MILLS_PER_SECOND=1000;
    private static final int MINUTE_PER_MILLIS = 60 * MILLS_PER_SECOND;
    private static final int HOUR_PER_MILLIS = 60 * MINUTE_PER_MILLIS;
    private static final int DAY_PER_MILLIS = 24 * HOUR_PER_MILLIS;
    public static String getTime(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_PER_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_PER_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_PER_MILLIS) {
            return diff / MINUTE_PER_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_PER_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_PER_MILLIS) {
            return diff / HOUR_PER_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_PER_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_PER_MILLIS + " days ago";
        }
    }


}
