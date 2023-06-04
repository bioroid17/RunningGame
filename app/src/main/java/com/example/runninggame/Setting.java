package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
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

        // Initialize AudioManager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Get the current volume level
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // Set onClickListener for each Button in volumeControl
        for (int i = 0; i < volumeControl.getChildCount(); i++) {
            Button button = (Button) volumeControl.getChildAt(i);
            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Change the volume of the system
                    // You need to calculate the correct volume level based on the index of the Button
                    int volume = finalI * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / volumeControl.getChildCount();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                    // Change the background tint of the Buttons
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

            // Set the initial color of the Button based on the current volume level
            if (i < currentVolume) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            } else {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }
        }

        // Set onClickListener for decreaseButton and increaseButton
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decrease the volume
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1;
                if (volume < 0) {
                    volume = 0;
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                // Change the background tint of the Buttons
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
                // Increase the volume
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1;
                if (volume > audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                    volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                // Change the background tint of the Buttons
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
