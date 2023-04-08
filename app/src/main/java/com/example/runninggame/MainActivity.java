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
    private Animation jumpAnimation;
    private Animation translateAnimation;
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



        bt_m_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                character.setVisibility(View.VISIBLE);
                jumpAndTranslate();
            }
        });

        screenWidth = getResources().getDisplayMetrics().widthPixels;

        jumpAnimation = AnimationUtils.loadAnimation(this, R.anim.jump_animation);
        translateAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_animation);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(jumpAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

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
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(jumpAnimation);
        animationSet.addAnimation(translateAnimation);
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
        character.startAnimation(animationSet);
    }

    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
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
