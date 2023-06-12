package com.example.runninggame;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerSingleton {

    private static MediaPlayer mediaPlayer;

    private MediaPlayerSingleton() {

    }

    public static MediaPlayer getInstance(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.mainn);
            mediaPlayer.setLooping(true);
        }
        return mediaPlayer;
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}