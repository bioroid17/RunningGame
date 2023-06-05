package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private ViewGroup rootView;
    private Random random;
    private ImageView backgroundImage;
    private Button bt_m_play;
    private MediaPlayer mediaPlayer;
    private AnimationSet animationSet;
    private ImageView character;
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
        maptemp.setmap();

        rootView = findViewById(android.R.id.content);
        random = new Random();

        for (int i = 0; i < 30; i++) { // 20개의 눈을 생성
            ImageView snowflake = new ImageView(MainActivity.this);
            snowflake.setImageResource(R.drawable.snowflake_image);
            rootView.addView(snowflake);

            animateSnowflake(snowflake);
        }

        TextView scoreText = findViewById(R.id.Score);
        scoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                startActivity(intent);
            }
        });


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
        bt_m_play = findViewById(R.id.bt_Play);

        // 캐릭터를 처음에 화면에 보이도록 설정합니다.
        character.setVisibility(View.INVISIBLE);

        bt_m_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NEXT = new Intent(MainActivity.this, Choice.class);
                startActivity(NEXT);
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.sillychipsong);
        mediaPlayer.setLooping(true);
    }

    private void animateSnowflake(final ImageView snowflake) {
        // 눈의 크기 조정
        int desiredWidth = 10;
        int desiredHeight = 10;

        ViewGroup.LayoutParams layoutParams = snowflake.getLayoutParams();
        layoutParams.width = desiredWidth;
        layoutParams.height = desiredHeight;
        snowflake.setLayoutParams(layoutParams);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        int startX = screenWidth;
        int startY = random.nextInt(screenHeight);

        int endX = -snowflake.getWidth(); // 왼쪽 끝으로 이동
        int endY = random.nextInt(screenHeight);

        int duration = random.nextInt(4000) + 4000; // 4000ms부터 8000 사이의 랜덤한 시간 (2초부터 5초)

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(snowflake, "translationX", startX, endX);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(snowflake, "translationY", startY, endY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.setDuration(duration); // 애니메이션 지속 시간
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateSnowflake(snowflake); // 새로운 눈 애니메이션 실행
            }
        });

        animatorSet.start();
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
