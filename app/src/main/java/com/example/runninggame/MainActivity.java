package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button bt_m_play;
    private MediaPlayer mediaPlayer;
    private AnimationSet animationSet;
    private ImageView character;
    private View nextButton;
    private int screenWidth;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        // TextView 객체를 참조합니다.
        TextView textView = findViewById(R.id.my_text_view);
        textView.setAlpha(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setAlpha(1);
                Animation animation = new TranslateAnimation(0, 0, -100, 0);
                animation.setDuration(700);
                textView.startAnimation(animation);
            }
        }, 1500);

        ImageView imageView = findViewById(R.id.sound_set);

// 이미지가 처음에 보이지 않도록 visibility 속성을 INVISIBLE로 설정합니다.
        imageView.setVisibility(View.INVISIBLE);

// ImageView가 이동할 위치를 계산합니다.
        float x = (float)(getWindowManager().getDefaultDisplay().getWidth() - imageView.getWidth());
        float y = imageView.getY();

// 1초 후에 애니메이션을 실행합니다.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // ImageView가 이동할 애니메이션을 정의합니다.
                TranslateAnimation animation = new TranslateAnimation(x, imageView.getX(), y, y);
                animation.setDuration(1000);

                // 애니메이션을 ImageView에 적용합니다.
                imageView.setVisibility(View.VISIBLE);
                imageView.startAnimation(animation);
            }
        }, 1000);

        TextView blinkingTextView = findViewById(R.id.blinktext);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.blink);
        blinkingTextView.startAnimation(anim);
        // 앱 실행 시 화면을 가로 방향으로 출력
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
