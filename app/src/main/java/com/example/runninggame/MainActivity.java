package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button bt_m_play;
    ImageView im_setting;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        bt_m_play=(Button) findViewById(R.id.bt_Play);
        bt_m_play.setOnClickListener((v) ->{
            Intent GoPlay = new Intent(MainActivity.this,Choice.class);
            startActivity(GoPlay);
        });


        mediaPlayer= MediaPlayer.create(this ,R.raw.sillychipsong);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    protected void onResume(){
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }
}