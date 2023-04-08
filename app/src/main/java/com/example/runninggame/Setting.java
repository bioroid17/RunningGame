package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SeekBar volumeSeekBar = findViewById(R.id.volume_bar);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // SeekBar 값이 변경될 때 호출됩니다.
                // 볼륨을 변경합니다.
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int volume = (int) ((float) progress / 100 * maxVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // SeekBar에서 터치가 시작될 때 호출됩니다.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // SeekBar에서 터치가 종료될 때 호출됩니다.
            }
        });

// 현재 볼륨 값을 가져와서 SeekBar의 초기 값으로 설정합니다.
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int initialProgress = (int) ((float) currentVolume / maxVolume * 100);
        volumeSeekBar.setProgress(initialProgress);

    }
}