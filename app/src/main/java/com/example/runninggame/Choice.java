package com.example.runninggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private float jumpHeight = 65f; //점프 첫속도 (점프 높이)
    private float gravity = 6f; //중력크기
    private boolean isJumping = false; //false일때 점프 가능
    private float translateY = 0; //플레이어 Y값 변경
    private boolean isreversal = false; //true면 반전상태
    private float playerY; // 플레이어의 중간 Y좌표값


    private int gashiNum = 0; //풀 안의 몇번째 가시를 꺼내쓸건지
    private int removeGashiNum = 0;
    private float objectSpeed = 30; //가시와 발판 스피드

    private List<ImageView> gashiPool = new ArrayList<>(); //가시 풀
    private List<Boolean> pR = new ArrayList<>(); //위쪽에 나올거면 False, 아래쪽에 나올거면 True
    private List<Integer> pY = new ArrayList<>(); //가시와 발판의 Y좌표
    private List<Float> pT = new ArrayList<>(); //다음 가시or발판이 나오기까지 대기시간

    int patternNum;

    private Handler gamehandler = new Handler(Looper.getMainLooper());
    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            if(!isPaused) {
                Gravity();
                rectSetting();
                GroundCollisionCheck();
            }

            gamehandler.postDelayed(this, 1);
        }
    };

    private Handler moveHandler = new Handler();
    private Runnable moveObjects = new Runnable() {
        @Override
        public void run() {
            if(!isPaused) {
                for (ImageView gashi : gashiPool) {
                    if (gashi.getVisibility() == View.VISIBLE)
                        if (gashi.getX() + gashi.getWidth() - objectSpeed < 0)
                            removeGashi(gashi);
                        else
                            gashi.setX(gashi.getX() - objectSpeed);
                }
            }

            moveHandler.postDelayed(this, 1);
        }
    };


    int patternRunI = 0; //일단 임시로 생성
    private Handler patternHandler = new Handler();

    private Runnable patternRun = new Runnable() {
        @Override
        public void run() {

            if(patternRunI < pR.size()){
                spawnGashi(pY.get(patternRunI), pR.get(patternRunI));
                patternHandler.postDelayed(this, 100);
                //patternHandler.postDelayed(this, (int)(pT.get(patternRunI)*1000));
                patternRunI++;
            } else {
                nextPatternHandler.postDelayed(nextPattern, 2000);
            }
        }
    };
    private Handler nextPatternHandler = new Handler();
    private Runnable nextPattern = new Runnable() {
        @Override
        public void run() {
            patternRunI = 0;
            pY.clear();
            pR.clear();
            pT.clear();
            pattern();
        }
    };
    private int screenWidth;



    ///////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_choice);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        scoreTextView = findViewById(R.id.score_text_view);
        startTimer();



        player = findViewById(R.id.player);
        ground = findViewById(R.id.ground);

        groundRect = new RectF(ground.getLeft(), ground.getTop(), ground.getRight(), ground.getBottom());

        for(int i = 0; i < 100; i++)
            createGashi();

        gamehandler.post(gameRunnable);
        moveHandler.post(moveObjects);
        moveHandler.postDelayed(nextPattern, 2000);
    }

    protected void onResume(){
        super.onResume();
        ground.post(new Runnable() { //ground가 그려진 후 위치 설정.
            @Override
            public void run() {
                groundY = ground.getY() + ground.getHeight() / 2f; //땅 Y값의 중간값
            }
        });
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

        if(!isreversal) {
            isreversal = true;
            player.setY((playerY - ((playerY - groundY) * 2)) - player.getHeight()/2);
            player.setRotationX(180);
        }
        else{
            isreversal = false;
            player.setY((playerY - ((playerY - groundY) * 2)) - player.getHeight()/2);
            player.setRotationX(0);
        }
/*
        if(playerY - groundY > 0) player.setY((playerY - ((playerY - groundY) * 2)) - player.getHeight()/2);
        else player.setY((playerY - ((playerY - groundY) * 2)) - player.getHeight()/2);
*/
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
        if(!isPaused) {
            if (keyCode == KeyEvent.KEYCODE_Z) {
                jump();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_X) {
                reversal();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_W) {
                spawnGashi(0, false); //false = 똑바로 소환
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_E) {
                spawnGashi(0, true); //true = 거꾸로 소환
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_Q) {
                spawnGashi(200, false);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_R) {
                spawnGashi(200, true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void createGashi(){
        ImageView gashi = new ImageView(this);
        gashi.setImageResource(R.drawable.triangle);

        //gashi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        gashi.setLayoutParams(new ViewGroup.LayoutParams(200, 200));

        gashiPool.add(gashi);
        gashi.setVisibility(View.INVISIBLE);
        //addContentView(gashi, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)); // 이 부분이 추가되었습니다.
        ((ViewGroup)findViewById(android.R.id.content)).addView(gashi); // 부모 뷰를 지정해줍니다.

    }
    private ImageView spawnGashi(int y, boolean isreversal){
        ImageView gashi = gashiPool.get(gashiNum);
        gashi.setVisibility(View.VISIBLE);
        gashi.setX(screenWidth + gashiPool.get(gashiNum).getWidth());

        if(isreversal) { //아래쪽에서 가시 나옴
            gashi.setRotationX(180);

            gashi.setY(groundY + y);//가시 이미지 수정후 수정
            //gashi.setY(groundY + ground.getHeight() / 2f + y);
        }
        else {
            gashi.setRotationX(0);
//아래코드 가시 이미지 수정후 수정
            gashi.setY(groundY - gashiPool.get(gashiNum).getHeight() - y);
            //gashi.setY(groundY - gashiPool.get(i).getHeight() - ground.getHeight() / 2f - y);
        }

        gashiNum++;
        if(gashiNum >= gashiPool.size()) gashiNum = 0;

        return gashi;
    }


    private void removeGashi(final ImageView gashi){

        gashi.setVisibility(View.INVISIBLE);
        Log.d("Debug", "tkfkwla!");

    }


    public int patternDrawing(int min, int max) { //랜덤으로 패턴 뽑아오기
        Random random = new Random();
        return random.nextInt(max - min + 1) + min; //(0, 패턴의 수 - 1)로 호출
    }

    private void patternSet(boolean r, int y, float t){
        pR.add(r);
        pY.add(y);
        pT.add(t);

    }


    private void pattern(){
        patternNum = patternDrawing(0,1);

        //patternNum = 0;

        switch (patternNum){
            case 0:
                patternSet(false,0,.1f);
                patternSet(false,0,.1f);
                patternSet(false,0,.1f);
                patternSet(false,0,.1f);
                patternSet(false,0,.1f);
                break;
            case 1:
                patternSet(true,0,.1f);
                patternSet(true,0,.1f);
                patternSet(true,0,.1f);
                patternSet(true,0,.1f);
                patternSet(true,0,.1f);
                break;
            case 2:
                break;
            case 3:
                break;
        }

        patternHandler.post(patternRun);


    }
}

