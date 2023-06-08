package com.example.runninggame;

import static com.example.runninggame.maptemp.*;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
public class Choice extends AppCompatActivity {
    private ObjectAnimator animatorX;
    private ObjectAnimator animatorY;
    private boolean isAnimationPaused = false;
    private ViewGroup rootView; //눈 이미지 그룹
    private Random random;
    static int maplevel=0; //0스테이지부터 ~
    static int patNum=0;
    private int score = 0;
    private Timer timer;
    private View menuview;
    private TextView scoreTextView;
    private TextView restartTextView;
    private  TextView mainmenuTextView;
    private  TextView resumeTextView;
    private ImageButton pauseButton;
    private ImageButton restartButton;
    private ImageButton mainmenuButton;
    private ImageButton resumeButton;

    ScoreManager scoreManager;
    private boolean isPaused = false;
    private boolean isDead = false;

    private RunningEffect runningEffect;
    private int effSize = 20;
    private List<RunningEffect> effPool = new ArrayList<>();
    private int effPoolSize = 100;
    private int effPoolNum = 0;

    private List<DeadEffect> deadEffPool = new ArrayList<>();
    private int deadEffPoolSize = 50;


    ////////////////



    private ImageView instance;
    private ImageView speedUpText;

    public ImageView player;
    public int playerSize = 70; //플레이어 크기
    public int gashiSize = 100; //가시 크기
    private int platSize = 30; //플랫폼의 세로 크기(두께)
    private int platY = 120; //땅의 1단 기본 높이

    //////////

    boolean jumpPress = false;

    int jumphei = platY * 2; //최대 점프 높이
    float gameSpeed = 1;
    float B = 15f/gameSpeed; //공중정지까지 걸리는 시간. 게임속도와 반비례
    float A = 2 * jumphei / B;; //시작 속력

    float gravityy = A/B; //중력 크기

    private float objectSpeed = 20 * gameSpeed; //가시와 발판 스피드

    int patternSize = 0;
    int previousPattern = 999999;



    //////////


    private ImageView ground; //가운데 땅
    private float groundY; //가운데 땅의 중간 Y좌표값
    private RectF playerRect;
    private RectF playerHeadRect;
    private RectF groundRect;
    private List<RectF> gashiRect = new ArrayList<>();
    private List<List<RectF>> platRect = new ArrayList<>();
    private float jumpHeight = 50f; //점프 첫속도 (점프하는 힘.)
    private float gravity = A/B; //중력크기
    private boolean isJumping = false; //false일때 점프 가능
    private float translateY = 0; //플레이어 Y값 변경
    private boolean isreversal = false; //true면 반전상태
    private float playerY; // 플레이어의 중간 Y좌표값


    ////////////////////////////////// 이 아래는 패턴을 위한 변수들

    private int gashiNum = 0; //풀 안의 몇번째 가시를 꺼내쓸건지
    //private int removeGashiNum = 0;
    private List<ImageView> gashiPool = new ArrayList<>(); //가시 풀
    private int gashiPoolSize = 1000; //풀을 효율적으로 관리하기 위한 변수
    private int gashiPoolStart = 0; //풀에서 소환된 오브젝트들의 처음 (풀 효율을 위한 것)
    private int gashiPoolEnd = 0; //풀에서 소환된 오브젝트들의 끝 (풀 효율을 위한 것)
    private List<Boolean> gR = new ArrayList<>(); //위쪽에 나올거면 False, 아래쪽에 나올거면 True
    private List<Boolean> gRR = new ArrayList<>(); //트루면 반대방향으로.
    private List<Integer> gY = new ArrayList<>(); //가시와 발판의 Y좌표
    private List<Integer> gD = new ArrayList<>(); //이전 가시와의 거리

    private List<Boolean> pMR = new ArrayList<>();
    private List<Integer> pMY = new ArrayList<>();
    private List<Integer> pML = new ArrayList<>();
    private List<Integer> pMD = new ArrayList<>();

    private int platNum[] = new int[10];
    private List<List<ImageView>> platPool = new ArrayList<>();
    private int platPoolSize = 100; //풀을 효율적으로 관리하기 위한 변수
    private int platPoolStart[] = new int[10]; //가시 스타트랑 동일
    private int platPoolEnd[] = new int[10]; //가시 엔드랑 동일
    //private List<List<Boolean>> pR = new ArrayList<>();
    //private List<List<Integer>> pY = new ArrayList<>();
    //private List<List<Integer>> pl = new ArrayList<>(); //플랫폼의 길이
    //private List<List<Integer>> pD = new ArrayList<>();

    int patternNum; //몇번째 패턴을 할건지
    int speedUpNum = 3;
    int speedUpCount = -1;


    //////////////////////////////////////////

    private Handler gamehandler = new Handler(Looper.getMainLooper());
    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            if(!isPaused && !isDead) {
                if(jumpPress){ jump(); }
                Gravity();
                rectSetting();
                GroundCollisionCheck();
            }

            gamehandler.postDelayed(this, 10);
        }
    };

    int speedUpBlinkNum = 0;
    private Handler speedUpHandler = new Handler();
    private Runnable speedUpRunable = new Runnable() {
        @Override
        public void run() {
            if(speedUpText.getVisibility() == View.INVISIBLE){
                speedUpText.setVisibility(View.VISIBLE);
            } else {
                speedUpText.setVisibility(View.INVISIBLE);
            }

            if(!isDead) {
                if (!isPaused) speedUpBlinkNum++;

                if (speedUpBlinkNum < 10) {
                    float delayy = 100 / gameSpeed;
                    speedUpHandler.postDelayed(this, (int) delayy);
                }
            } else {
                speedUpText.setVisibility(View.INVISIBLE);
            }
        }
    };

    private Handler moveHandler = new Handler();
    private Runnable moveObjects = new Runnable() {
        @Override
        public void run() {
            if(!isPaused && !isDead) {
                if(gashiPoolStart < gashiPoolEnd) {
                    for(int i = gashiPoolStart; i < gashiPoolEnd; i++){
                        gashiMove(i);
                    }
                } else if(gashiPoolStart > gashiPoolEnd){
                    for(int i = gashiPoolStart; i < gashiPoolSize; i++) {
                        gashiMove(i);
                    }
                    for(int i = 0; i < gashiPoolEnd; i++){
                        gashiMove(i);
                    }
                }
                for(int j = 0; j < 10; j++) {
                    if (platPoolStart[j] < platPoolEnd[j]) {
                        for (int i = platPoolStart[j]; i < platPoolEnd[j]; i++) {
                            platMove(j, i);
                        }
                    } else if (platPoolStart[j] > platPoolEnd[j]) {
                        for (int i = platPoolStart[j]; i < platPoolSize; i++) {
                            platMove(j, i);
                        }
                        for (int i = 0; i < platPoolEnd[j]; i++) {
                            platMove(j, i);
                        }
                    }
                }
            }

            moveHandler.postDelayed(this, 1);
        }
    };
    private void gashiMove(int i){
        instance = gashiPool.get(i);
        if(instance.getX() + instance.getWidth() - objectSpeed > 0)
            instance.setX(instance.getX() - objectSpeed);
        else
            removeGashi(instance);
    }
    private void platMove(int j, int i){ //j는 어떤 길이 플랫폼의 풀을 사용할지, i는 그 풀의 몇번째를 움직일지
        instance = platPool.get(j).get(i);
        if(instance.getX() + instance.getWidth() - objectSpeed > 0)
            instance.setX(instance.getX() - objectSpeed);
        else
            removePlatform(j, instance);
    }



    private Handler nextPatternHandler = new Handler();
    private Runnable nextPattern = new Runnable() {
        @Override
        public void run() {
            //patternRunI = 0;
            gY.clear();
            gR.clear();
            gD.clear();
            gRR.clear();
            //pY.clear();
            //pR.clear();
            //pD.clear();

            pMD.clear();
            pMY.clear();
            pMR.clear();
            pML.clear();

            speedUpCount++;
            if(speedUpCount >= speedUpNum){
                speedUpCount = 0;
                gameSpeedChange(0.03f);
            }

            pattern();
        }
    };
    private int screenWidth;
    private int screenHeight;



    ///////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_choice);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        rootView = findViewById(android.R.id.content);
        random = new Random();
//        for (int i = 0; i < 30; i++) { // 30개의 눈을 생성
//            ImageView snowflake = new ImageView(Choice.this);
//            snowflake.setImageResource(R.drawable.snowflake_image2); //눈이미지 변경
//            rootView.addView(snowflake);
////            animateSnowflake(snowflake);
//        }

        //스코어
        scoreManager=new ScoreManager(this);

        // 화면 가로, 세로 크기
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        scoreTextView = findViewById(R.id.score_text_view);
        startTimer();

        pauseButton = findViewById(R.id.pause_button);
        pauseButton.setX(screenWidth - pauseButton.getLayoutParams().width);
        pauseButton.setY(0);

        do{
            gY.clear();
            gR.clear();
            gD.clear();
            gRR.clear();
            pMD.clear();
            pMY.clear();
            pMR.clear();
            pML.clear();
            SelectPattern(patternSize);
            patternSize++;
        } while(!gY.isEmpty() || !pMY.isEmpty());
        patternSize -= 2;



        for(int i = 0; i < 10; i++){
            platNum[i] = 0;
            platPoolStart[i] = 0;
            platPoolEnd[i] = 0;
            platRect.add(new ArrayList<>());
            platPool.add(new ArrayList<>());
        }


        speedUpText = new ImageView(this);
        speedUpText.setImageResource(R.drawable.speed_up);
        ((ViewGroup)findViewById(android.R.id.content)).addView(speedUpText);
        speedUpText.getLayoutParams().width = screenWidth / 3;
        speedUpText.getLayoutParams().height = speedUpText.getLayoutParams().width / 4;
        speedUpText.setScaleType(ImageView.ScaleType.FIT_XY);
        speedUpText.setX(screenWidth/2 - screenWidth/6);
        speedUpText.setY(screenHeight/4 - screenHeight/24);
        speedUpText.setVisibility(View.INVISIBLE);

        //player = findViewById(R.id.player);
        player = new ImageView(this);
        player.setImageResource(R.drawable.player);
        ((ViewGroup)findViewById(android.R.id.content)).addView(player);
        player.getLayoutParams().width = playerSize;
        player.getLayoutParams().height = playerSize;
        player.setScaleType(ImageView.ScaleType.FIT_XY);

        ground = new ImageView(this);
        ground.setImageResource(R.drawable.player);
        ((ViewGroup)findViewById(android.R.id.content)).addView(ground);
        ground.getLayoutParams().height = (int)(playerSize * 0.5f);
        ground.setScaleType(ImageView.ScaleType.FIT_XY);

        groundRect = new RectF(ground.getLeft(), ground.getTop(), ground.getRight(), ground.getBottom());

        for(int i = 0; i < gashiPoolSize; i++) {
            createGashi();
        }

        for(int i = 0; i < platPoolSize; i++){
            createPlatform();
        }

        for(int i = 0; i < effPoolSize; i++){
            createEff();
        }
        for(int i = 0; i < deadEffPoolSize; i++){
            createDeadEff();
        }

        gamehandler.post(gameRunnable);
        moveHandler.post(moveObjects);
        moveHandler.postDelayed(nextPattern, 1000);


        menuview = new View(this);
        menuview.setVisibility(View.INVISIBLE);
        menuview.setBackgroundColor(Color.GRAY);
        menuview.setLayoutParams(new ViewGroup.LayoutParams(screenWidth*11/16, screenHeight*11/16));
        menuview.setX(screenWidth*5/32);
        menuview.setY(screenHeight*5/32);
        ((ViewGroup)findViewById(android.R.id.content)).addView(menuview);

        restartTextView = new TextView(this);
        restartTextView.setVisibility(View.INVISIBLE);
        restartTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        restartTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        restartTextView.setTextColor(Color.WHITE);
        restartTextView.setTextSize(30);
        restartTextView.setBackgroundColor(Color.TRANSPARENT);
        restartTextView.setX(screenWidth*9/32);
        restartTextView.setY(screenHeight/4);
        restartTextView.setText("다시 시작");
        ((ViewGroup)findViewById(android.R.id.content)).addView(restartTextView);

        restartButton = new ImageButton(this);
        restartButton.setVisibility(View.INVISIBLE);
        restartButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        restartButton.setX(screenWidth*5/16);
        restartButton.setY(screenHeight/2);
        restartButton.setImageResource(android.R.drawable.stat_notify_sync);
        restartButton.setOnClickListener(v -> onRestartButtonClick(v));
        ((ViewGroup)findViewById(android.R.id.content)).addView(restartButton);

        mainmenuTextView = new TextView(this);
        mainmenuTextView.setVisibility(View.INVISIBLE);
        mainmenuTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mainmenuTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        mainmenuTextView.setTextColor(Color.WHITE);
        mainmenuTextView.setTextSize(30);
        mainmenuTextView.setBackgroundColor(Color.TRANSPARENT);
        mainmenuTextView.setX(screenWidth*17/32);
        mainmenuTextView.setY(screenHeight/4);
        mainmenuTextView.setText("메인으로");
        ((ViewGroup)findViewById(android.R.id.content)).addView(mainmenuTextView);

        mainmenuButton = new ImageButton(this);
        mainmenuButton.setVisibility(View.INVISIBLE);
        mainmenuButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mainmenuButton.setX(screenWidth*9/16);
        mainmenuButton.setY(screenHeight/2);
        mainmenuButton.setImageResource(android.R.drawable.ic_menu_revert);
        mainmenuButton.setOnClickListener(v -> onMainMenuButtonClick(v));
        ((ViewGroup)findViewById(android.R.id.content)).addView(mainmenuButton);

        resumeTextView = new TextView(this);
        resumeTextView.setVisibility(View.INVISIBLE);
        resumeTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        resumeTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        resumeTextView.setTextColor(Color.WHITE);
        resumeTextView.setTextSize(30);
        resumeTextView.setBackgroundColor(Color.TRANSPARENT);
        resumeTextView.setX(screenWidth*9/32);
        resumeTextView.setY(screenHeight/4);
        resumeTextView.setText("계속하기");
        ((ViewGroup)findViewById(android.R.id.content)).addView(resumeTextView);

        resumeButton = new ImageButton(this);
        resumeButton.setVisibility(View.INVISIBLE);
        resumeButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        resumeButton.setX(screenWidth*5/16);
        resumeButton.setY(screenHeight/2);
        resumeButton.setImageResource(android.R.drawable.ic_media_play);
        resumeButton.setOnClickListener(v -> onResumeButtonClick(v));
        ((ViewGroup)findViewById(android.R.id.content)).addView(resumeButton);

        View view = findViewById(R.id.deltaRelative);
       view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event){
                int screenWidth = v.getWidth();
                int screenHeight = v.getHeight();
                float touchX = event.getX();
                float touchY = event.getY();

                if(!isDead && !isPaused) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (touchY > screenHeight / 5) {
                            if (touchX < screenWidth / 2) { //왼쪽 터치
                                reversal();
                            } else {
                                jumpPress = true;
                            }
                        }
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if (touchY > screenHeight / 5) {
                            if (touchX > screenWidth / 2) {
                                jumpPress = false;
                            }
                        }
                    }
                }
                return true;
            }
        });

    }

    private void animateSnowflake(final ImageView snowflake) {
        // 눈의 크기 조정
        int desiredWidth = 10;
        int desiredHeight = 30;

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

        int duration = random.nextInt(5000) + 4000; // 4000ms부터 8000 사이의 랜덤한 시간 (2초부터 5초)

        animatorX = ObjectAnimator.ofFloat(snowflake, "translationX", startX, endX);
        animatorY = ObjectAnimator.ofFloat(snowflake, "translationY", startY, endY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.setDuration(duration); // 애니메이션 지속 시간
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isAnimationPaused) {
                    animateSnowflake(snowflake); // 다시 애니메이션 시작
                } // 새로운 눈 애니메이션 실행
            }
        });

        animatorSet.start();
    }

    // 애니메이션 일시정지
    private void pauseAnimation() {
        if (animatorX != null && animatorY != null) {
            isAnimationPaused = true;
            animatorX.pause();
            animatorY.pause();
        }
    }

    // 애니메이션 다시 시작
    private void resumeAnimation() {
        if (animatorX != null && animatorY != null) {
            isAnimationPaused = false;
            animatorX.resume();
            animatorY.resume();
        }
    }


    protected void onResume(){
        super.onResume();
        ground.post(new Runnable() { //ground가 그려진 후 위치 설정.
            @Override
            public void run() {
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                ground.setY(screenHeight / 2 - ground.getHeight());
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                ground.getLayoutParams().width = screenWidth;
                groundY = ground.getY() + ground.getHeight() / 2f; //땅 Y값의 중간값
                player.setX(200); //플레이어 시작위치
                player.setY(groundY - ground.getHeight()/2f - player.getHeight());
                groundRect = new RectF(ground.getX(),ground.getY(),ground.getX() + ground.getWidth(), ground.getY() + ground.getHeight());
            }
        });
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused && !isDead) {
                    score++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scoreTextView.setText("Score: " + score + "   Game Speed " + Math.round(gameSpeed*100)/100.0f);
                        }
                    });
                }
            }
        }, 100, (int)(50/(gameSpeed + ((gameSpeed - 1) * 300)))); // 0.1초마다 실행
    }

    public void onPauseButtonClick(View view) {
        pauseButton.setVisibility(View.INVISIBLE);
        isPaused = true;
        menuview.setVisibility(View.VISIBLE);
        mainmenuTextView.setVisibility(View.VISIBLE);
        mainmenuButton.setVisibility(View.VISIBLE);
        resumeTextView.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.VISIBLE);
    }
    public void onRestartButtonClick(View view) {
        pauseButton.setVisibility(View.VISIBLE);
        view.setVisibility(View.INVISIBLE);
        player.setVisibility(View.VISIBLE);
        menuview.setVisibility(View.INVISIBLE);
        restartTextView.setVisibility(View.INVISIBLE);
        mainmenuButton.setVisibility(View.INVISIBLE);
        mainmenuTextView.setVisibility(View.INVISIBLE);
        player.setY(groundY - ground.getHeight()/2f - player.getHeight());
        isreversal = false;
        speedUpCount = -1;
        gameSpeed = 1;
        gameSpeedChange(0);
        translateY = 0;

        for(ImageView gashi : gashiPool){
            removeGashi(gashi);
        }
        for(int i=0; i<10; i++){
            for(ImageView platform : platPool.get(i)){
                removePlatform(i, platform);
            }
        }
        gashiPoolStart = 0; gashiPoolEnd = 0; gashiNum = 0;
        for(int i = 0; i < 10; i++){ platNum[i] = 0; platPoolStart[i] = 0; platPoolEnd[i] = 0; }
        nextPatternHandler.post(nextPattern);
        isDead = false;
        score = 0;
    }
    public void onMainMenuButtonClick(View view){
        finish();
    }

    public void onResumeButtonClick(View view){
        pauseButton.setVisibility(View.VISIBLE);
        isPaused = false;
        menuview.setVisibility(View.INVISIBLE);
        mainmenuTextView.setVisibility(View.INVISIBLE);
        mainmenuButton.setVisibility(View.INVISIBLE);
        resumeTextView.setVisibility(View.INVISIBLE);
        resumeButton.setVisibility(View.INVISIBLE);
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
        if(!isreversal) //플레이어의 머리 렉트
            playerHeadRect = new RectF(player.getX(), player.getY(), player.getX() + player.getWidth(), player.getY() + 15);
        else
            playerHeadRect = new RectF(player.getX(), player.getY()+player.getHeight()-15, player.getX()+player.getWidth(), player.getY()+player.getHeight());

        for(int j = 0; j < 10; j++) {
            if (platPoolStart[j] < platPoolEnd[j]) {
                for (int i = platPoolStart[j]; i < platPoolEnd[j]; i++) {
                    platRectSetting(j, i);
                }
            } else if (platPoolStart[j] > platPoolEnd[j]) {
                for (int i = platPoolStart[j]; i < platPoolSize; i++)
                    platRectSetting(j, i);
                for (int i = 0; i < platPoolEnd[j]; i++)
                    platRectSetting(j, i);
            }
        }

        if(gashiPoolStart < gashiPoolEnd){
            for(int i = gashiPoolStart; i < gashiPoolEnd; i++){
                gashiRectSetting(i);
            }
        } else if(gashiPoolStart > gashiPoolEnd){
            for(int i = gashiPoolStart; i < gashiPoolSize; i++)
                gashiRectSetting(i);
            for(int i = 0; i < gashiPoolEnd; i++)
                gashiRectSetting(i);
        }
    }
    private void platRectSetting(int j, int i){
        instance = platPool.get(j).get(i);
        platRect.get(j).get(i).set(new RectF(instance.getX(), instance.getY(), instance.getX() + instance.getWidth(), instance.getY() + instance.getHeight()));
    }

    private void gashiRectSetting(int i){
        instance = gashiPool.get(i);
        gashiRect.get(i).set(new RectF(instance.getX(), instance.getY(), instance.getX() + instance.getWidth(), instance.getY() + instance.getHeight()));
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
            spawnEff();
        }
        for(int j = 0; j < 10; j++) {
            if (platPoolStart[j] < platPoolEnd[j]) {
                for (int i = platPoolStart[j]; i < platPoolEnd[j]; i++) {
                    platCollisionCheck(j, i);
                }
            } else if (platPoolStart[j] > platPoolEnd[j]) {
                for (int i = platPoolStart[j]; i < platPoolSize; i++) {
                    platCollisionCheck(j, i);
                }
                for (int i = 0; i < platPoolEnd[j]; i++) {
                    platCollisionCheck(j, i);
                }
            }
        }

        if(gashiPoolStart < gashiPoolEnd){
            for(int i = gashiPoolStart; i < gashiPoolEnd; i++){
                gashiCollisionCheck(i);
            }
        } else if(gashiPoolStart > gashiPoolEnd){
            for(int i = gashiPoolStart; i < gashiPoolSize; i++){
                gashiCollisionCheck(i);
            }
            for(int i = 0; i < gashiPoolEnd; i++){
                gashiCollisionCheck(i);
            }
        }
    }
    private void platCollisionCheck(int j, int i){
        if(platPool.get(j).get(i).getVisibility() == View.VISIBLE) {
            if (RectF.intersects(playerRect, platRect.get(j).get(i))) {
                while (RectF.intersects(playerRect, platRect.get(j).get(i))) {
                    if (!isreversal) {
                        if (translateY < 0) {
                            player.offsetTopAndBottom(-1);
                            isJumping = false;
                        } else if (translateY > 0) {
                            player.offsetTopAndBottom(1);
                            isJumping = true;
                        }
                    } else {
                        if (translateY > 0) {
                            player.offsetTopAndBottom(1);
                            isJumping = false;
                        } else if (translateY < 0) {
                            player.offsetTopAndBottom(-1);
                            isJumping = true;
                        }
                    }
                    rectSetting();
                }
                if(!isJumping) { spawnEff(); }
                translateY = 0;
            }
        }
    }

    private void gashiCollisionCheck(int i){
        if(gashiPool.get(i).getVisibility() == View.VISIBLE) {
            if (RectF.intersects(playerRect, gashiRect.get(i))){
                if(RectF.intersects(playerHeadRect, gashiRect.get(i))){

                    scoreManager.saveScore(score);
                    pauseButton.setVisibility(View.INVISIBLE);
                    isDead = true;
                    menuview.setVisibility(View.VISIBLE);
                    speedUpText.setVisibility(View.INVISIBLE);
                    restartButton.setVisibility(View.VISIBLE);
                    restartTextView.setVisibility(View.VISIBLE);
                    scoreTextView.setVisibility(View.VISIBLE);
                    mainmenuButton.setVisibility(View.VISIBLE);
                    mainmenuTextView.setVisibility(View.VISIBLE);

                    player.setVisibility(View.INVISIBLE);
                    spawnDeadEff();
                }
            }
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

        translateY *= -1;

    }

    private void jump(){
        if(!isJumping && (!isreversal && translateY > -gravity*5)||(isreversal&&translateY<gravity*5) && !isJumping){
            isJumping = true;
            if(!isreversal) translateY = A;
            else translateY = -A;
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(!isPaused && !isDead) {
            if (keyCode == KeyEvent.KEYCODE_Z) {
                jump();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_X) {
                reversal();
                return true;
            }
            if(keyCode == KeyEvent.KEYCODE_W){
                gameSpeedChange(-0.03f);
                return true;
            }
            if(keyCode == KeyEvent.KEYCODE_E){
                gameSpeedChange(0.03f);
                return true;
            }
            if(keyCode == KeyEvent.KEYCODE_R){
                spawnEff();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void createDeadEff(){
        DeadEffect deadeff = new DeadEffect();
        deadeff.deadEff = new ImageView(this);
        deadeff.deadEff.setImageResource(R.drawable.player);
        deadeff.deadEff.setScaleType(ImageView.ScaleType.FIT_XY);
        Random random = new Random();
        int size = random.nextInt(20) + 30;
        deadeff.deadEff.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        int angle = random.nextInt(360);
        deadeff.deadEff.setRotation(angle);
        deadeff.deadEff.setVisibility(View.INVISIBLE);
        deadEffPool.add(deadeff);
        deadeff.size = size;
        ((ViewGroup)findViewById(android.R.id.content)).addView(deadeff.deadEff);

    }
    private void spawnDeadEff(){
        for(int i = 0; i < deadEffPoolSize; i++) {

            deadEffPool.get(i).spawnDeadEff(objectSpeed, player.getX() + player.getWidth()/2, player.getY() + player.getHeight()/2);

        }
    }
    private void createEff(){
        RunningEffect runEff = new RunningEffect();
        runEff.eff = new ImageView(this);
        runEff.eff.setImageResource(R.drawable.gashi);
        runEff.eff.setScaleType(ImageView.ScaleType.FIT_XY);
        runEff.eff.setLayoutParams(new ViewGroup.LayoutParams(effSize, effSize));
        Random random = new Random();
        int angle = random.nextInt(360);
        runEff.eff.setRotation(angle);
        effPool.add(runEff);
        runEff.eff.setVisibility(View.INVISIBLE);
        ((ViewGroup)findViewById(android.R.id.content)).addView(runEff.eff);
    }
    private void spawnEff(){
        if(isreversal)
            effPool.get(effPoolNum).spawnEff(objectSpeed, player.getX(), player.getY()-effPool.get(effPoolNum).eff.getHeight(), isreversal);
        else
            effPool.get(effPoolNum).spawnEff(objectSpeed, player.getX(), player.getY()+player.getHeight(), isreversal);

        effPoolNum++;
        if(effPoolNum >= effPoolSize) effPoolNum = 0;
    }

    //맨 처음에 예비용 가시 생성해둠 (현재는 100개)
    private void createGashi(){
        ImageView gashi = new ImageView(this);
        gashi.setImageResource(R.drawable.gashi);
        gashi.setScaleType(ImageView.ScaleType.FIT_XY);

        gashi.setLayoutParams(new ViewGroup.LayoutParams(gashiSize, gashiSize));

        gashiPool.add(gashi);
        gashiRect.add(new RectF());
        gashi.setVisibility(View.INVISIBLE);
        ((ViewGroup)findViewById(android.R.id.content)).addView(gashi); // 부모 뷰를 지정

    }

    //맨 처음에 예비용 플랫폼 생성해둠 (현재는 20개)
    private void createPlatform(){
        for(int i = 0; i < 10; i++) {
            ImageView plat = new ImageView(this);
            plat.setImageResource(R.drawable.platform);
            plat.setScaleType(ImageView.ScaleType.FIT_XY);

            plat.setLayoutParams(new ViewGroup.LayoutParams(gashiSize * (i + 1), platSize));

            platPool.get(i).add(plat);
            platRect.get(i).add(new RectF());
            plat.setVisibility(View.INVISIBLE);
            ((ViewGroup) findViewById(android.R.id.content)).addView(plat);
        }

    }

    //다른 방식의 장애물 생성 테스트
    private void SpawnObj() {
        System.out.println("스폰 실행");
        float X = screenWidth + 500;
        for (int i = 0; i < gY.size(); i++) {
            ImageView gashi = gashiPool.get(gashiNum);
            gashi.setVisibility(View.VISIBLE);
            X += gD.get(i);
            gashi.setX(X);
            if (gR.get(i)) {
                if (!gRR.get(i)) gashi.setRotationX(180);
                else gashi.setRotationX(0);

                gashi.setY(groundY + ground.getHeight() / 2 + gY.get(i));
            } else {
                if (!gRR.get(i)) gashi.setRotationX(0);
                else gashi.setRotationX(180);

                gashi.setY(groundY - ground.getHeight() / 2 - gashiPool.get(gashiNum).getHeight() - gY.get(i));
            }

            gashiPoolEnd++;
            if (gashiPoolEnd >= gashiPoolSize)
                gashiPoolEnd = 0;

            gashiNum++;
            if (gashiNum >= gashiPoolSize) gashiNum = 0;
        }

        X = screenWidth + 500;
        for (int i = 0; i < pMY.size(); i++) {
            ImageView platform = platPool.get(pML.get(i)/gashiSize -1).get(platNum[pML.get(i)/gashiSize -1]);
            platform.setVisibility(View.VISIBLE);
            X += pMD.get(i);
            platform.setX(X);
            ViewGroup.LayoutParams params = platform.getLayoutParams();
            //params.width = pl.get(j).get(i);
            //platform.setLayoutParams(params);
            if (pMR.get(i)) {
                platform.setY(groundY + ground.getHeight() / 2 + pMY.get(i));
            } else {
                platform.setY(groundY - ground.getHeight() / 2 - platPool.get(pML.get(i)/gashiSize -1).get(platNum[pML.get(i)/gashiSize -1]).getHeight() - pMY.get(i));
            }

            platPoolEnd[pML.get(i)/gashiSize -1]++;
            if (platPoolEnd[pML.get(i)/gashiSize -1] >= platPoolSize)
                platPoolEnd[pML.get(i)/gashiSize -1] = 0;

            platNum[pML.get(i)/gashiSize -1]++;
            if (platNum[pML.get(i)/gashiSize -1] >= platPoolSize)
                platNum[pML.get(i)/gashiSize -1] = 0;
        }

    }





    private void removeGashi(ImageView gashi){
        gashi.setVisibility(View.INVISIBLE);

        if(!isDead) {
            gashiPoolStart++;
            if (gashiPoolStart >= gashiPoolSize)
                gashiPoolStart = 0;

            if (gashiPoolStart == gashiPoolEnd) {
                int platDeleteNum = 0;
                for (int i = 0; i < 10; i++) {
                    if (platPoolStart[i] == platPoolEnd[i]) { //전부 사라졌다면 다음 패턴 바로 호출
                        platDeleteNum++;
                    }
                }
                if (platDeleteNum == 10) {
                    System.out.println("nextPattern 실행");
                    nextPatternHandler.post(nextPattern);
                }
            }
        }
    }

    private void removePlatform(int i, ImageView platform){
        platform.setVisibility(View.INVISIBLE);
        if(!isDead) {
            platPoolStart[i]++;
            if (platPoolStart[i] >= platPoolSize)
                platPoolStart[i] = 0;

            if (gashiPoolStart == gashiPoolEnd) {
                int platDeleteNum = 0;
                for (int j = 0; j < 10; j++) {
                    if (platPoolStart[j] == platPoolEnd[j]) { //전부 사라졌다면 다음 패턴 바로 호출
                        platDeleteNum++;
                    }
                }
                if (platDeleteNum == 10) nextPatternHandler.post(nextPattern);
            }
        }
    }

    private void gameSpeedChange(float game_speed){
        gameSpeed += game_speed;
        B = 15f/gameSpeed; //공중정지까지 걸리는 시간. 게임속도와 반비례
        A = 2 * jumphei / B;; //시작 속력

        gravity = A/B; //중력 크기

        objectSpeed = 20 * gameSpeed  ; //가시와 발판 스피드

        if(game_speed != 0) {
            speedUpBlinkNum = 0;
            speedUpHandler.post(speedUpRunable);
        }
    }


    public int patternDrawing(int min, int max) { //랜덤으로 패턴 뽑아오기
        Random random = new Random();
        return random.nextInt(max - min + 1) + min; //(0, 패턴의 수 - 1)로 호출
    }

    //gs = gashiSet. 가시 세팅
    //r = 아래쪽? || y = 바닥으로부터 얼마나 떨어져있는지 || t = 이전 가시와의 거리
    private void gs(boolean r, int d, int y){ //공중가시 소환(1 정방향, 2 역방향)
        gR.add(r); gD.add(d);
        if(y == 0) { gY.add(platY+platSize); gRR.add(false); }
        else if(y == 1) {gY.add(platY-gashiSize); gRR.add(true);}
    }
    private void gs(boolean r){ //땅 위에 바로 소환(이전 가시에 붙어 나오게)
        gR.add(r); gY.add(0); gD.add(gashiSize); gRR.add(false);
    }
    private void gs(boolean r, int d){ //땅 위에 바로 소환(t는 이전 가시와의 거리 설정)
        gR.add(r); gY.add(0); gD.add(d); gRR.add(false);
    }
    private void gs(boolean r, int d, int y, boolean rr){ //공중 뒤집힌 가시 생성
        gR.add(r); gY.add(y); gD.add(d); gRR.add(rr);
    }

    //ps = platformSet. 플랫폼 세팅
//    private void ps(boolean r, int d, int l, int y){
//        pR.add(r); pY.add(y); pl.add(l); pD.add(d);
//    }

    private void ps(boolean r, int d, int l, int y){
        pMR.add(r); pMY.add(y); pML.add(l); pMD.add(d);
    }

    private void ps(boolean r, int d, int l){
        pMR.add(r); pMY.add(platY); pML.add(l); pMD.add(d);
    }

    private void pattern(){
        do {
            patternNum = patternDrawing(0, patternSize);
        } while(patternNum == previousPattern);
        SelectPattern(patternNum);
        previousPattern = patternNum;
        SpawnObj();
    }

    public void MapList(int level,int patternNum) {
        SelectPattern(stagelevel[level].get(patternNum)); //0번쨰 stage의 patterNum째의 패턴을 가져온다
        patNum++;
        if(stagelevel[level].size()==patternNum+1){
            maplevel++;
            if(maplevel==stagelevel.length){ //지금은 0~4단계 까지 maptemp가서 확인
                maplevel=0;
            }
            patNum=0;
            System.out.println("다음 맵");
        }
    }




    public void SelectPattern(int select){ //여기에 패턴 만들고 패턴번호 붙이면 됨 사용은 maptemp에서 stagelevel list에 add(패턴숫자)하면 됨
//        gs() 위/아래 , 거리 , 공중
        switch (select){
            case 0:
                gs(true);
                gs(false);
                gs(true, 200);
                gs(false, 0);
                gs(false, 300, 0);
                gs(true, 200, 1);
                ps(false, 100, gashiSize);
                ps(false, gashiSize*2+400, gashiSize);
                ps(true, 200, gashiSize);
                break;

            case 1:
                gs(false, 0); gs(true, 0); gs(true, 0, 0);
                gs(false); gs(true, 0); gs(true, 0, 0);
                gs(false); gs(true, 0); gs(true, 0, 0);

                for(int j = 0; j < 10; j++) {
                    for (int i = 0; i < 5; i++) {
                        gs(false);
                        gs(true, 0);
                    }
                    for (int i = 0; i < 3; i++) {
                        gs(false);
                        gs(true, 0);
                        gs(false, 0, 0);
                    }
                    for (int i = 0; i < 5; i++) {
                        gs(false);
                        gs(true, 0);
                    }
                    for (int i = 0; i < 3; i++) {
                        gs(false);
                        gs(true, 0);
                        gs(true, 0, 0);
                    }
                }
                ps(false, 0, gashiSize*3);
                ps(true, 0, gashiSize*3);
                for(int i = 0; i < 20; i++){
                    ps(true, 800, gashiSize*3);
                    ps(false, 0, gashiSize*3);
                }
                //ps(true, 10000, 100);
                break;

            case 2:
                ps(true, 0, 1000);
                ps(false, 0, 1000);

                ps(true, 1300, 1000);
                ps(false, 0, 1000);
                break;

            case 3:
                // 위로 18개의 가시가 연속으로 등장
                for(int i=0; i<18; i++)
                    gs(false);
                break;

            case 4:
                // 위로 4개의 가시가 연속으로 등장
                for(int i=0; i<4; i++)
                    gs(false);
                // 아래로 4개의 가시가 연속으로 등장
                for(int i=0; i<4; i++)
                    gs(true);
                // 위로 4개의 가시가 연속으로 등장
                for(int i=0; i<4; i++)
                    gs(false);
                // 아래로 4개의 가시가 연속으로 등장
                for(int i=0; i<4; i++)
                    gs(true);
                break;

            case 5:
                for(int i=0; i<12; i++){
                    gs(false);
                    gs(true, 0);
                    if(i%3 != 2){
                        if((i/3) % 2 == 1){
                            gs(false, 0, gashiSize * 3 / 2, false);
                        }
                        else{
                            gs(true, 0, gashiSize * 3 / 2, false);
                        }
                    }
                }

                ps(false, gashiSize, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                break;

            case 6:
                for(int i=0; i<12; i++){
                    gs(false);
                    gs(true, 0);
                    if(i%3 != 2){
                        if((i/3) % 2 == 0){
                            gs(false, 0, gashiSize * 3 / 2, false);
                        }
                        else{
                            gs(true, 0, gashiSize * 3 / 2, false);
                        }
                    }
                }

                ps(false, gashiSize, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                break;

            case 7:
                for(int i=0; i<12; i++){
                    gs(false);
                    gs(true, 0);
                    if(i%3 != 2){
                        if((i/3) % 3 == 0){
                            gs(false, 0, gashiSize * 3 / 2, false);
                        }
                        else{
                            gs(true, 0, gashiSize * 3 / 2, false);
                        }
                    }
                }

                ps(false, gashiSize, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                break;

            case 8:
                for(int i=0; i<12; i++){
                    gs(false);
                    gs(true, 0);
                    if(i%3 != 2){
                        if((i/3) % 3 == 0){
                            gs(true, 0, gashiSize * 3 / 2, false);
                        }
                        else{
                            gs(false, 0, gashiSize * 3 / 2, false);
                        }
                    }
                }

                ps(false, gashiSize, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                break;

            case 999:
                ps(false, 0, gashiSize*5);
                ps(true, gashiSize*4, gashiSize*5);

                break;

            case 0:
                gs(false, 0); gs(false); gs(false);
                ps(true, 0, gashiSize*3);
                break;
            case 1:
                gs(false, 0); gs(false); gs(false);
                ps(true, 0, gashiSize*3);
                break;

        }
    }
}//좀더 쉽게 코드를 짤 수 있어도 좋을듯 땅, 발판위 즉석코드같은거.

