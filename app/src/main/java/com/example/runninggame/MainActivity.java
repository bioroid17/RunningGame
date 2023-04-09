package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Button bt_m_play;
    private MediaPlayer mediaPlayer;
    private AnimationSet animationSet;
    private ImageView character;
    private View nextButton;
    private int screenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        character = findViewById(R.id.character);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setVisibility(View.INVISIBLE);
        bt_m_play = findViewById(R.id.bt_Play);

        // 캐릭터를 처음에 화면에 보이도록 설정합니다.
        character.setVisibility(View.INVISIBLE);

        bt_m_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpAndTranslate();
            }
        });

        screenWidth = getResources().getDisplayMetrics().widthPixels;

        animationSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.jump_translate);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mediaPlayer.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.sillychipsong);
        mediaPlayer.setLooping(true);
    }


    private void jumpAndTranslate() {
        character.startAnimation(animationSet);
    }

    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void onClickNEXT(View view) {
        Intent NEXT = new Intent(MainActivity.this, Choice.class);
        startActivity(NEXT);
    }

    public void onClickSet(View view) {
        Intent NEXT = new Intent(MainActivity.this, Setting.class);
        startActivity(NEXT);
    }
}
