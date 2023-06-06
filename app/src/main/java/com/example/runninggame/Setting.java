package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class Setting extends AppCompatActivity {

    private LinearLayout volumeControl = null;
    private Button decreaseButton = null;
    private Button increaseButton = null;
    private AudioManager audioManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        volumeControl = findViewById(R.id.volumeControl);
        decreaseButton = findViewById(R.id.decreaseButton);
        increaseButton = findViewById(R.id.increaseButton);


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 현재 볼류 값
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        TextView closeButton = findViewById(R.id.exit);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, MainActivity.class);
                startActivity(intent);
                finish(); // 선택적: ScoreActivity를 종료하여 백스택에서 제거합니다.
            }
        });

        // 각 버튼의 onClick
        for (int i = 0; i < volumeControl.getChildCount(); i++) {
            Button button = (Button) volumeControl.getChildAt(i);
            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //클릭시 볼륨 조절
                    int volume = finalI * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / volumeControl.getChildCount();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                    // 색깔도 바꿈
                    for (int j = 0; j < volumeControl.getChildCount(); j++) {
                        Button otherButton = (Button) volumeControl.getChildAt(j);
                        if (j <= finalI) {
                            otherButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                        } else {
                            otherButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        }
                    }
                }
            });

            // 볼륨 값에 따라 색깔 바꿈
            if (i < currentVolume) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            } else {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }
        }

        // - 버튼
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 볼륨을 1 줄임
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1;
                if (volume < 0) {
                    volume = 0;
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                // 색깔 바꿈
                for (int i = 0; i < volumeControl.getChildCount(); i++) {
                    Button button = (Button) volumeControl.getChildAt(i);
                    if (i < volume) {
                        button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                    } else {
                        button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    }
                }
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // +버튼
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1;
                if (volume > audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                    volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                // 색깔 바꿈
                for (int i = 0; i < volumeControl.getChildCount(); i++) {
                    Button button = (Button) volumeControl.getChildAt(i);
                    if (i < volume) {
                        button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                    } else {
                        button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    }
                }
            }
        });
    }
}
