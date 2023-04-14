package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
public class Choice extends AppCompatActivity {
    private int score = 0;
    private Timer timer;
    private TextView scoreTextView;

    private boolean isPaused = false;


    ////////////////

    public ImageView player;
    private ImageView ground; //가운데 땅
    private float groundY; //가운데 땅의 중간 Y좌표값
    private RectF playerRect;
    private RectF groundRect;
    private float jumpHeight = 80f; //점프 첫속도 (점프 높이)
    private float gravity = 6f; //중력크기
    private boolean isJumping = false; //false일때 점프 가능
    private float translateY = 0; //플레이어 Y값 변경
    private boolean isreversal = false; //true면 반전상태
    private float playerY; // 플레이어의 중간 Y좌표값

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            Gravity();
            rectSetting();
            GroundCollisionCheck();

            handler.postDelayed(this, 10);
        }
    };

    ///////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_choice);

        scoreTextView = findViewById(R.id.score_text_view);
        startTimer();



        player = findViewById(R.id.player);
        ground = findViewById(R.id.ground);
        groundY = ground.getY() + ground.getHeight() / 2f; //땅 Y값의 중간값

        groundRect = new RectF(ground.getLeft(), ground.getTop(), ground.getRight(), ground.getBottom());



        handler.post(gameRunnable);
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    score++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scoreTextView.setText("Score: " + score);
                        }
                    });
                }
            }
        }, 100, 100); // 0.1초마다 실행
    }

    public void onPauseButtonClick(View view) {
        isPaused = !isPaused;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }



    ////////////////////
    private void Gravity(){
        if(!isreversal) translateY -= gravity;
        else translateY += gravity; //중력에 따른 중력가속도(방향)

        player.offsetTopAndBottom(-(int)translateY);
    }

    private void rectSetting(){ //각자 렉트를 재설정
        playerRect = new RectF(player.getX(), player.getY(), player.getX() + player.getWidth(),player.getY() + player.getHeight());
        groundRect = new RectF(ground.getX(),ground.getY(),ground.getX() + ground.getWidth(), ground.getY() + ground.getHeight());
    }

    private void GroundCollisionCheck(){ //땅이랑 닿았는지 체크
        if (RectF.intersects(playerRect, groundRect)) { //땅이랑 닿음
            while(RectF.intersects(playerRect, groundRect))
            {    //땅이랑 닿지 않을때까지 땅위로 올린 후
                if(!isreversal) player.offsetTopAndBottom(-1);
                else player.offsetTopAndBottom(1);

                rectSetting(); //렉트 재설정
            }
            isJumping = false; //점프 가능상태
            translateY = 0; //Y 0으로 고정

        }
    }

    private void reversal(){ //반전 눌렀을때 값들 반전됨

        playerY = player.getY() + player.getHeight() / 2f;
        groundY = ground.getY() + ground.getHeight() / 2f;

        if(!isreversal) isreversal = true;
        else isreversal = false;

        if(playerY - groundY > 0) player.setY((playerY - ((playerY - groundY) * 2)) - player.getHeight()/2);
        else player.setY((playerY - ((playerY - groundY) * 2)) - player.getHeight()/2);

        translateY *= -1;

    }

    private void jump(){
        if(translateY == 0 && !isJumping){
            isJumping = true;
            if(!isreversal) translateY = jumpHeight;
            else translateY = -jumpHeight;
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_Z){
            jump();
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_X){
            reversal();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

