package com.example.runninggame;

import static com.example.runninggame.maptemp.*;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.util.TypedValue;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
public class Choice extends AppCompatActivity {

    MediaPlayer deadPlayer;
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
//    private TextView countdownTextView;
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
    private int deadEffPoolSize = 60;


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
    float gameSpeed = 1f;
    float B = 15f/gameSpeed; //공중정지까지 걸리는 시간. 게임속도와 반비례
    float A = 2 * jumphei / B;; //시작 속력

    float gravityy = A/B; //중력 크기

    private float objectSpeed = 20 * gameSpeed; //가시와 발판 스피드

    int patternSize = 0; //패턴이 총 몇개 있는지가 자동으로 설정됨
    int patternCount = 0; //현재가 몇번째로 나오는 패턴인지
    private List<Boolean> isPatternOut = new ArrayList<>();
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

    private float quakeCurPower;
    private final float quakePower = 40;
    private float quakeX, quakeY;
    private Handler quakeHandler = new Handler();
    private Runnable quakeRunnable = new Runnable() {
        @Override
        public void run() {
            if(quakePower != quakeCurPower) {
                ground.setX(ground.getX() - quakeX); ground.setY(ground.getY() - quakeY);
                if(gashiPoolStart < gashiPoolEnd) {
                    for(int i = gashiPoolStart; i < gashiPoolEnd; i++){
                        gashiPool.get(i).setX(gashiPool.get(i).getX() -quakeX);
                        gashiPool.get(i).setY(gashiPool.get(i).getY() -quakeY);
                    }
                } else if(gashiPoolStart > gashiPoolEnd){
                    for(int i = gashiPoolStart; i < gashiPoolSize; i++) {
                        gashiPool.get(i).setX(gashiPool.get(i).getX() -quakeX);
                        gashiPool.get(i).setY(gashiPool.get(i).getY() -quakeY);
                    }
                    for(int i = 0; i < gashiPoolEnd; i++){
                        gashiPool.get(i).setX(gashiPool.get(i).getX() -quakeX);
                        gashiPool.get(i).setY(gashiPool.get(i).getY() -quakeY);
                    }
                }
                for(int j = 0; j < 10; j++) {
                    if (platPoolStart[j] < platPoolEnd[j]) {
                        for (int i = platPoolStart[j]; i < platPoolEnd[j]; i++) {
                            platPool.get(j).get(i).setX(platPool.get(j).get(i).getX() -quakeX);
                            platPool.get(j).get(i).setY(platPool.get(j).get(i).getY() -quakeY);
                        }
                    } else if (platPoolStart[j] > platPoolEnd[j]) {
                        for (int i = platPoolStart[j]; i < platPoolSize; i++) {
                            platPool.get(j).get(i).setX(platPool.get(j).get(i).getX() -quakeX);
                            platPool.get(j).get(i).setY(platPool.get(j).get(i).getY() -quakeY);
                        }
                        for (int i = 0; i < platPoolEnd[j]; i++) {
                            platPool.get(j).get(i).setX(platPool.get(j).get(i).getX() -quakeX);
                            platPool.get(j).get(i).setY(platPool.get(j).get(i).getY() -quakeY);
                        }
                    }
                }
            } //위치 전부 움직인거의 마이너스로
            boolean randomX = random.nextBoolean();
            boolean randomY = random.nextBoolean();
            if(randomX) quakeX = quakeCurPower; else quakeX = -quakeCurPower;
            if(randomY) quakeY = quakeCurPower; else quakeY = -quakeCurPower;
            quakeCurPower -= gameSpeed;
            if(quakeCurPower <= 0) {quakeCurPower = 0; quakeX = 0; quakeY = 0;}
            ground.setX(ground.getX()+quakeX); ground.setY(ground.getY()+quakeY);
            if(gashiPoolStart < gashiPoolEnd) {
                for(int i = gashiPoolStart; i < gashiPoolEnd; i++){
                    gashiPool.get(i).setX(gashiPool.get(i).getX() + quakeX);
                    gashiPool.get(i).setY(gashiPool.get(i).getY() + quakeY);
                }
            } else if(gashiPoolStart > gashiPoolEnd){
                for(int i = gashiPoolStart; i < gashiPoolSize; i++) {
                    gashiPool.get(i).setX(gashiPool.get(i).getX() + quakeX);
                    gashiPool.get(i).setY(gashiPool.get(i).getY() + quakeY);
                }
                for(int i = 0; i < gashiPoolEnd; i++){
                    gashiPool.get(i).setX(gashiPool.get(i).getX() + quakeX);
                    gashiPool.get(i).setY(gashiPool.get(i).getY() + quakeY);
                }
            }
            for(int j = 0; j < 10; j++) {
                if (platPoolStart[j] < platPoolEnd[j]) {
                    for (int i = platPoolStart[j]; i < platPoolEnd[j]; i++) {
                        platPool.get(j).get(i).setX(platPool.get(j).get(i).getX() +quakeX);
                        platPool.get(j).get(i).setY(platPool.get(j).get(i).getY() +quakeY);
                    }
                } else if (platPoolStart[j] > platPoolEnd[j]) {
                    for (int i = platPoolStart[j]; i < platPoolSize; i++) {
                        platPool.get(j).get(i).setX(platPool.get(j).get(i).getX() +quakeX);
                        platPool.get(j).get(i).setY(platPool.get(j).get(i).getY() +quakeY);
                    }
                    for (int i = 0; i < platPoolEnd[j]; i++) {
                        platPool.get(j).get(i).setX(platPool.get(j).get(i).getX() +quakeX);
                        platPool.get(j).get(i).setY(platPool.get(j).get(i).getY() +quakeY);
                    }
                }
            }

            if(quakeCurPower != 0)
                gamehandler.postDelayed(this, 10);
            else{
                menuview.setVisibility(View.VISIBLE);
                restartButton.setVisibility(View.VISIBLE);
                restartTextView.setVisibility(View.VISIBLE);
                scoreTextView.setVisibility(View.VISIBLE);
                mainmenuButton.setVisibility(View.VISIBLE);
                mainmenuTextView.setVisibility(View.VISIBLE);
            }
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
                gameSpeedChange(0.08f);
            }

            pattern();
        }
    };
    private int screenWidth;
    private int screenHeight;



    ///////////////

    private MediaPlayer mediaPlayer;
    private void playRandomMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // 이전에 재생 중이던 음악 해제
        }

        // 랜덤 음악 선택
        int musicResource = getRandomMusic();

        mediaPlayer = MediaPlayer.create(this, musicResource);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
    private int preIndex = 999;
    int randomIndex = 0;
    private int getRandomMusic() {
        String[] musicResources = {"run1", "run2", "run3", "main"};

        randomIndex++;
        if(randomIndex > 3) randomIndex = 0;

        return getResources().getIdentifier(musicResources[randomIndex], "raw", getPackageName());
    }
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    private void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_choice);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        MediaPlayerSingleton.getInstance(this).pause();

        playRandomMusic();
        deadPlayer = MediaPlayer.create(this, R.raw.dead);
        deadPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                deadPlayer.release();
            }
        });

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
        for(int i = 0; i <= patternSize; i++){
            isPatternOut.add(new Boolean(false));
        }



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
        ground.getLayoutParams().width = screenWidth*2;
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
        menuview.setBackgroundResource(R.drawable.menu);
        menuview.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight));
        ((ViewGroup)findViewById(android.R.id.content)).addView(menuview);

        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics());
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics());

        restartTextView = new TextView(this);
        restartTextView.setVisibility(View.INVISIBLE);
        restartTextView.setLayoutParams(new ViewGroup.LayoutParams(width*4, height));
        restartTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        restartTextView.setTextColor(Color.WHITE);
        restartTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        restartTextView.setBackgroundColor(Color.TRANSPARENT);
        restartTextView.setX(screenWidth/2 - width*5);
        restartTextView.setY(screenHeight*5/16);
        restartTextView.setText("처음부터");
        restartTextView.setPaintFlags(restartTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        ((ViewGroup)findViewById(android.R.id.content)).addView(restartTextView);

        restartButton = new ImageButton(this);
        restartButton.setVisibility(View.INVISIBLE);
        restartButton.setLayoutParams(new ViewGroup.LayoutParams(width*2, height*2));
        restartButton.setX(screenWidth/2 - width*4);
        restartButton.setY(screenHeight/2);
        restartButton.setImageResource(R.drawable.restart);
        restartButton.setOnClickListener(v -> onRestartButtonClick(v));
        ((ViewGroup)findViewById(android.R.id.content)).addView(restartButton);

        mainmenuTextView = new TextView(this);
        mainmenuTextView.setVisibility(View.INVISIBLE);
        mainmenuTextView.setLayoutParams(new ViewGroup.LayoutParams(width*4, height));
        mainmenuTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        mainmenuTextView.setTextColor(Color.WHITE);
        mainmenuTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        mainmenuTextView.setBackgroundColor(Color.TRANSPARENT);
        mainmenuTextView.setX(screenWidth/2 + width*2);
        mainmenuTextView.setY(screenHeight*5/16);
        mainmenuTextView.setText("메인으로");
        mainmenuTextView.setPaintFlags(mainmenuTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        ((ViewGroup)findViewById(android.R.id.content)).addView(mainmenuTextView);

        mainmenuButton = new ImageButton(this);
        mainmenuButton.setVisibility(View.INVISIBLE);
        mainmenuButton.setLayoutParams(new ViewGroup.LayoutParams(width*2, height*2));
        mainmenuButton.setX(screenWidth/2 + width*3);
        mainmenuButton.setY(screenHeight/2);
        mainmenuButton.setImageResource(R.drawable.home);
        mainmenuButton.setOnClickListener(v -> onMainMenuButtonClick(v));
        ((ViewGroup)findViewById(android.R.id.content)).addView(mainmenuButton);

        resumeTextView = new TextView(this);
        resumeTextView.setVisibility(View.INVISIBLE);
        resumeTextView.setLayoutParams(new ViewGroup.LayoutParams(width*4, height));
        resumeTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        resumeTextView.setTextColor(Color.WHITE);
        resumeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        resumeTextView.setBackgroundColor(Color.TRANSPARENT);
        resumeTextView.setX(screenWidth/2 - width*5);
        resumeTextView.setY(screenHeight*5/16);
        resumeTextView.setText("계속하기");
        resumeTextView.setPaintFlags(resumeTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        ((ViewGroup)findViewById(android.R.id.content)).addView(resumeTextView);

        resumeButton = new ImageButton(this);
        resumeButton.setVisibility(View.INVISIBLE);
        resumeButton.setLayoutParams(new ViewGroup.LayoutParams(width*2, height*2));
        resumeButton.setX(screenWidth/2 - width*4);
        resumeButton.setY(screenHeight/2);
        resumeButton.setImageResource(R.drawable.resume);
        resumeButton.setOnClickListener(v -> onResumeButtonClick(v));
        ((ViewGroup)findViewById(android.R.id.content)).addView(resumeButton);

//        countdownTextView = new TextView(this);
//        countdownTextView.setVisibility(View.INVISIBLE);
//        countdownTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        countdownTextView.setX(screenWidth/2 - countdownTextView.getLayoutParams().width/2);
//        countdownTextView.setY(screenHeight/2 - countdownTextView.getLayoutParams().height/2);
//        countdownTextView.setTextColor(Color.WHITE);
//        countdownTextView.setTextSize(90);
//        countdownTextView.setBackgroundColor(Color.TRANSPARENT);
//        countdownTextView.setText("3");
//        ((ViewGroup)findViewById(android.R.id.content)).addView(countdownTextView);


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
                ground.setX(ground.getX()-100);
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                ground.getLayoutParams().width = screenWidth*2;
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
        }, 100, 100); // 0.1초마다 실행
    }

    public void onPauseButtonClick(View view) {
        pauseButton.setVisibility(View.INVISIBLE);
        isPaused = true;
        menuview.setVisibility(View.VISIBLE);
        mainmenuTextView.setVisibility(View.VISIBLE);
        mainmenuButton.setVisibility(View.VISIBLE);
        resumeTextView.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.VISIBLE);

        MediaPlayerSingleton.getInstance(this).pause();
        pauseMusic();
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
        gameSpeed = 1f;
        gameSpeedChange(0);
        translateY = 0;

        deadPlayer = MediaPlayer.create(this, R.raw.dead);
        deadPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                deadPlayer.release();
            }
        });

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

        playRandomMusic();
    }
    public void onMainMenuButtonClick(View view){
        finish();
        stopMusic();
    }

    public void onResumeButtonClick(View view){
        pauseButton.setVisibility(View.VISIBLE);
        mainmenuTextView.setVisibility(View.INVISIBLE);
        mainmenuButton.setVisibility(View.INVISIBLE);
        resumeTextView.setVisibility(View.INVISIBLE);
        resumeButton.setVisibility(View.INVISIBLE);
        menuview.setVisibility(View.INVISIBLE);
        isPaused = false;
        resumeMusic();
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
                    quakeCurPower = quakePower;
                    quakeHandler.post(quakeRunnable);
                    speedUpText.setVisibility(View.INVISIBLE);
                    /*
                    menuview.setVisibility(View.VISIBLE);
                    restartButton.setVisibility(View.VISIBLE);
                    restartTextView.setVisibility(View.VISIBLE);
                    scoreTextView.setVisibility(View.VISIBLE);
                    mainmenuButton.setVisibility(View.VISIBLE);
                    mainmenuTextView.setVisibility(View.VISIBLE);
*/
                    player.setVisibility(View.INVISIBLE);
                    spawnDeadEff();

                    stopMusic();



                    deadPlayer.start();

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
        }
        if(keyCode == KeyEvent.KEYCODE_R){
            onRestartButtonClick(restartTextView);
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_S){
            quakeCurPower = quakePower;
            quakeHandler.post(quakeRunnable);
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
        runEff.eff.setImageResource(R.drawable.running_eff);
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
        /*do {
            patternNum = patternDrawing(0, patternSize);
        } while(patternNum == previousPattern);*/
        do{
            patternNum = patternDrawing(0, patternSize);
        } while(isPatternOut.get(patternNum) == true); //이전에 나온 패턴이 아닐 경우에만 통과
        patternCount++;
        isPatternOut.set(patternNum, true);
        if(patternCount > patternSize) {patternCount = 0; for(int i = 0; i <= patternSize; i++) {isPatternOut.set(i, false);}}
        //patternNum = 9; //6번 가시 하나 줄이기
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

    private void gss(boolean re, int j){
        for(int i = 0; i < j; i++){
            gs(re);
        }
    }
    private void hDown(boolean re, int d){ //높은 플랫폼 아래에 생성
        gs(re, d, (int)(platY*2-gashiSize), true);
    }
    private void hUp(boolean re, int d){ //높은 플랫폼 위에 생성
        gs(re, d, (int)(platY*2+platSize), false);
    }
    private void hps(boolean re, int d, int l){
        ps(re, d, l, (int)(platY*2));
    }


    public void SelectPattern(int select){ //여기에 패턴 만들고 패턴번호 붙이면 됨 사용은 maptemp에서 stagelevel list에 add(패턴숫자)하면 됨
//        gs() 위/아래 , 거리 , 공중
        System.out.println(select);
        switch (select){

            case 1:
                gs(true);gs(true);gs(true);gs(true); //4개
                gs(true);gs(false,0);gs(true);gs(false,0); //2개
                ps(false, gashiSize*6, gashiSize*4);
                for(int i=0;i<5;i++){
                    gs(true);gs(false,0);
                }
                gs(false);
                gs(false);
                ps(true,gashiSize*10,gashiSize*4);
                gs(false);
                gs(false);
                gs(false);
                gs(true,0,0);
                gs(false);
                gs(true,0,0);
                gs(false);
                gs(true,0,0);
                gs(false);
                gs(true,0,0);
                gs(false);
                gs(false);
                break;
            case 2:
                gs(true);gs(true);gs(true);gs(true); //4개
                gs(true);gs(false,0);gs(true);gs(false,0); //2개
                ps(false, gashiSize*6, gashiSize*4);
                ps(true, 0, gashiSize*4);
                for(int i=0;i<4;i++){
                    gs(false,0,0);
                    gs(true);gs(false,0);
                }
                gs(false);
                gs(false);
                ps(true,gashiSize*9,gashiSize*4);
                gs(false);
                gs(false);
                gs(false);
                gs(true,0,0);
                gs(false);
                gs(true,0,0);
                gs(false);
                gs(true,0,0);
                gs(false);
                gs(true,0,0);
                gs(false);
                gs(false);
                break;

            case 3:
                gs(true);gs(true);gs(true);gs(true); //4개
                gs(true);gs(false,0);gs(true);gs(false,0); //2개
                ps(false, gashiSize*6, gashiSize*4);
                ps(true, 0, gashiSize*4);
                for(int i=0;i<4;i++){
                    gs(false,0,0);
                    gs(true);gs(false,0);
                }
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(true);
                break;
            case 5:
                gs(false);
                gs(false);
                gs(false);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                ps(true,gashiSize*6,gashiSize*4);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                ps(false,gashiSize*4,gashiSize*4);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                break;
            case 6:
                gs(false);
                gs(false);
                gs(false);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                gs(false,0);
                ps(false,gashiSize*10,gashiSize*4);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(true);gs(true);gs(true);gs(true);gs(true);

            case 7:
                gs(false);
                gs(false);
                gs(false);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(true);
                gs(true);
                gs(true);
                gs(true);

                ps(false,gashiSize*10,gashiSize*4);
                gs(false,gashiSize,0);
                gs(false,gashiSize,0);
                gs(false,gashiSize,0);
                gs(false,0);
                gs(false,gashiSize,0);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(false);
                gs(false);
                break;

            case 8:
                gs(false);gs(false);gs(false);
                ps(false,gashiSize*4,gashiSize*4);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                ps(true,gashiSize*6,gashiSize*4);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(true,0);
                gs(false);gs(false);gs(false);
                break;

            case 9:
                gs(false);
                gs(false);
                gs(false);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(true);gs(true);gs(true);gs(true);
                ps(false,gashiSize*10,gashiSize*5);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(false);
                gs(true,0);
                gs(false);
                gs(false,0,0);
                gs(true,0);
                gs(false);
                gs(false,0,0);
                gs(true,0);
                gs(false);
                gs(false);
                gs(false);
                gs(false);
                gs(false);
                break;

            case 0:
                hps(true, 0, gashiSize*10); ps(false, 0, gashiSize*10); hps(true, gashiSize*10, gashiSize*10); ps(false, gashiSize*6, gashiSize*8); hps(true, gashiSize*4, gashiSize*10); ps(false, gashiSize*9, gashiSize*8); hps(true, gashiSize, gashiSize*10); ps(false, gashiSize*7, gashiSize*5); hps(true, gashiSize*3, gashiSize*10); ps(false, gashiSize*6, gashiSize*10); hps(true, gashiSize*4, gashiSize*10); ps(false, gashiSize*6, gashiSize*5); hps(true, gashiSize*4, gashiSize*10); ps(false, gashiSize*6, gashiSize*8); hps(true, gashiSize*4, gashiSize*10); ps(false, gashiSize*8, gashiSize*3); hps(true, gashiSize*2, gashiSize);
                gs(false, 0); gs(true, 0); hDown(true,0);hUp(true,0);   for(int i = 0; i < 6; i++){gs(false); gs(true,0);hDown(true,0); hUp(true,0);} for(int i = 0; i < 3; i++){gs(false); gs(false,0,0);gs(true,0);hDown(true,0); hUp(true,0);}
                for(int i = 0; i < 6; i++){hDown(true,gashiSize); hUp(true,0);} for(int i = 0; i < 4; i++){gs(false,gashiSize,0);hDown(true,0);hUp(true,0);gs(false,gashiSize,1);hDown(true,0);hUp(true,0);}
                for(int i = 0; i < 5; i++){hDown(true, gashiSize);hUp(true, 0);}
                for(int i = 0; i < 5; i++){hDown(true, gashiSize); gs(true,0);hUp(true, 0); gs(false,0);} for(int i = 0; i < 3; i++){gs(true);hUp(true,0);hDown(true,0);gs(false,0,0);gs(false,0,1);} for(int i = 0; i < 16; i++){gs(true); hUp(true,0);hDown(true,0);gs(false,0);}for(int i = 0; i < 3; i++){gs(true);hUp(true,0);hDown(true,0);gs(false,0,0);gs(false,0,1);}
                for(int i = 0; i < 10; i++){hDown(true, gashiSize); gs(true,0);hUp(true, 0); gs(false,0);} for(int i = 0; i < 5; i++){gs(false);hUp(true,0);} gs(false); hUp(true,0); gs(false); hUp(true,0);gs(false,0,0);gs(false);hUp(true,0);gs(false,0,0);
                for(int i = 0; i < 4; i++){hUp(true,gashiSize);gs(false,0);hDown(true,0);} for(int i = 0; i < 3; i++){hUp(true,gashiSize);hDown(true,0);gs(false,0,0);gs(false,0,1);}
                break;
            case 4: //반 점 점 점 반점
                hps(true, 0, gashiSize*10); hps(true,gashiSize*10,gashiSize*10); hps(true,gashiSize*10,gashiSize*10); ps(false, gashiSize*5, gashiSize*5);
                gs(false,0); hDown(true, 0); hUp(true, 0); for(int i = 0; i < 3; i ++){gss(false, 3); gs(true,0); gss(false, 3); hDown(true,0);}
                gs(true, gashiSize*5); gs(false); hDown(true,0);gs(true);gs(false,0,1);gs(false);hDown(true,0);gs(true);gs(false,0,1); gs(false); hDown(true,0);gs(true);gs(false,0,1);
                break;
            case 10: //반 반~ 반~ 반~ 반~ 반~ 반~ 반 반 반
                ps(true,0,gashiSize); ps(false,0,gashiSize);
                for(int i = 0; i < 15; i++){ps(true, gashiSize*4, gashiSize); ps(false, 0, gashiSize);}
                for(int i = 0; i < 3; i++){gs(false,0,0);gs(false,0,1);gs(true,0,0); gs(true,gashiSize*4,0);gs(true,0,1);gs(false,0,0); gs(true,gashiSize*4,0);gs(true,0,1);gs(false,0,0); gs(true,0,0);gs(true,0,1);gs(false,0,0); gs(false,gashiSize*4,0);gs(false,0,1);gs(true,0,0); gs(false,gashiSize*4,0);gs(false,0,1);gs(true,0,0);} gs(true,gashiSize*4,0);gs(true,0,1);gs(false,0,0); gs(false,gashiSize*4,0);gs(false,0,1);gs(true,0,0);gs(true,gashiSize*4,0);gs(true,0,1);gs(false,0,0);
                break;
            case 11: //반점 점 점 반점 반or점반
                ps(true, gashiSize, gashiSize*10); ps(true, gashiSize*10, gashiSize*2); ps(true, gashiSize*2, gashiSize*6); ps(false, gashiSize*10, gashiSize*3); ps(false, gashiSize*9, gashiSize*5); ps(true, gashiSize*7, gashiSize*4);
                gs(false,0); gs(true,0); gs(false);gs(true,0);gs(false);gs(true,0);gs(false, 0); gs(true,0); gs(false); gs(true, 0); gs(false); gs(true, 0); for(int i = 0; i < 3; i++){gs(false); gs(true,0); gs(true,0,0);} for(int i = 0; i < 4; i++){gs(false); gs(true, 0);} for(int i = 0; i < 3; i++){gs(false); gs(true,0); gs(true,0,0);} for(int i = 0; i < 3; i++){gs(false); gs(true, 0);} gs(true);
                gs(false,0); for(int i = 0; i < 7; i++) {gs(false); gs(true,0);}; gs(false,gashiSize*9); gs(false); gs(false,0,0);gs(false);gs(false,0,0); gss(false,2); for(int i = 0; i < 4; i++){gs(false); gs(true,0,0);}
                break;
            case 12: //반점 점 점 반
                ps(false, gashiSize, gashiSize*10); ps(false, gashiSize*10, gashiSize*2); ps(false, gashiSize*2, gashiSize*6); ps(true, gashiSize*10, gashiSize*3);
                gs(true,0); gs(false,0); gs(true);gs(false,0);gs(true);gs(false,0);gs(true, 0); gs(false,0); gs(true); gs(false, 0); gs(true); gs(false, 0); for(int i = 0; i < 3; i++){gs(true); gs(false,0); gs(false,0,0);} for(int i = 0; i < 4; i++){gs(true); gs(false, 0);} for(int i = 0; i < 3; i++){gs(true); gs(false,0); gs(false,0,0);} for(int i = 0; i < 3; i++){gs(true); gs(false, 0);} gs(false);
                gss(false, 5); gs(true,0,0); gs(false);gs(true,0,0); gs(false); gs(true,0,0); gs(false);
                break;
            case 13: //가점 가점 반가점 반점
                hps(false, 0, gashiSize*10); hps(true, 0, gashiSize*10); hps(false, gashiSize*10, gashiSize*10); hps(true, 0, gashiSize*10); hps(false, gashiSize*10, gashiSize*10); hps(true, 0, gashiSize*10); hps(true, gashiSize*10, gashiSize*3); hps(false, 0, gashiSize*3);
                hUp(false, 0); hDown(false, 0); hUp(true, 0); hDown(true, 0);
                gs(true); hDown(true,gashiSize); gs(true); hDown(true,gashiSize); gs(false, 0);  gs(true); hDown(true, gashiSize);  gs(true); hDown(false, 0); hDown(true, gashiSize); gs(true); hDown(true,gashiSize); gs(true); gs(false, 0);
                gs(false, gashiSize*8); hDown(false, gashiSize); gs(true, 0);  gs(false); hDown(false, gashiSize);  gs(false); hDown(true, 0); hDown(false, gashiSize); hDown(true, gashiSize); hDown(true, gashiSize*2); hDown(true, gashiSize*2); gs(true, 0); gs(false, 0);
                break;
            case 14: //반점 반점 반점 점 점 반점
                ps(false, 0,gashiSize*2); ps(true, gashiSize*9, gashiSize*2); ps(false, gashiSize*9, gashiSize*2); ps(false, gashiSize*9, gashiSize*2); ps(false, gashiSize*9, gashiSize*2); ps(true, gashiSize*9, gashiSize*2);
                gs(false, 0, 0); gs(false, 0, 1); gs(true, 0); gs(true); gs(false, 0, 0); gs(false, 0, 1);
                gs(true, gashiSize*8, 0); gs(true, 0, 1); gs(false, 0); gs(false); gs(true, 0, 0); gs(true, 0, 1);
                gs(false, gashiSize*8, 0); gs(false, 0, 1); gs(true, 0); gs(true); gs(false, 0, 0); gs(false, 0, 1);
                gs(false, gashiSize*8, 0); gs(false, 0, 1); gs(true, 0); gs(true); gs(false, 0, 0); gs(false, 0, 1);
                gs(false, gashiSize*8, 0); gs(false, 0, 1); gs(true, 0); gs(true); gs(false, 0, 0); gs(false, 0, 1);
                gs(true, gashiSize*8, 0); gs(true, 0, 1); gs(false, 0); gs(false); gs(true, 0, 0); gs(true, 0, 1);

                break;
            case 15: //가만히 반 점 반 반 반점 반점
                ps(true, 0, gashiSize*2); ps(false, 0, gashiSize*2); ps(true, gashiSize*9, gashiSize*2); ps(false, 0, gashiSize*2); ps(false, gashiSize*9, gashiSize*2); ps(true, gashiSize*9, gashiSize*2); ps(false, 0, gashiSize*2); ps(true, gashiSize*9, gashiSize*2); ps(false, 0, gashiSize*2); ps(true, gashiSize*9, gashiSize*2); ps(false, gashiSize*9, gashiSize*2);
                gs(true, 0, 0); gs(true, 0, 1); gs(false, 0, 0); gs(false, gashiSize, 0); gs(true, 0, 0); gs(true, 0, 1);
                gs(false, gashiSize*8, 0); gs(false, 0, 1); gs(true, 0, 0); gs(true, gashiSize, 0); gs(false, 0, 0); gs(false, 0, 1);
                gs(false, gashiSize*8, 0); gs(false, 0, 1); gs(true, 0); gs(true); gs(false, 0, 0); gs(false, 0, 1);
                gs(true, gashiSize*8, 0); gs(true, 0, 1); gs(false, 0, 0); gs(false, gashiSize, 0); gs(true, 0, 0); gs(true, 0, 1);
                gs(false, gashiSize*8, 0); gs(false, 0, 1); gs(true, 0, 0); gs(true, gashiSize, 0); gs(false, 0, 0); gs(false, 0, 1);
                gs(true, gashiSize*8, 0); gs(true, 0, 1); gs(false, 0); gs(false); gs(true, 0, 0); gs(true, 0, 1);
                gs(false, gashiSize*8, 0); gs(false, 0, 1); gs(true, 0); gs(true); gs(false, 0, 0); gs(false, 0, 1);


                break;
            case 16: //점프.. 반전.. 반전.. 점프
                ps(false, 0, gashiSize*10); ps(true, 0, gashiSize*2); ps(false, gashiSize*10, gashiSize*2);
                gs(false, 0, 1); gs(true, 0, 1); gs(true, 0, 0); gs(true, gashiSize, 1); gs(true, 0, 0);
                gs(false, gashiSize*6, 0); gs(false, gashiSize, 0); gs(false, gashiSize, 0); gs(false, gashiSize, 0); gs(false, gashiSize, 0); gs(false, 0, 1);
                gs(true, gashiSize*5); gss(true,5); gs(false,0); gs(false);gs(true,0);gs(true);gs(false,0);

                break;

            case 17:
                gs(false, 0); gs(true, 0); gs(true, 0, 0);
                gs(false); gs(true, 0); gs(true, 0, 0);
                gs(false); gs(true, 0); gs(true, 0, 0);

                for(int j = 0; j < 3; j++) {
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
                for(int i = 0; i < 6; i++){
                    ps(true, 800, gashiSize*3);
                    ps(false, 0, gashiSize*3);
                }
                //ps(true, 10000, 100);
                break;
            case 18:
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

            case 19:
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

            case 20:
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                ps(false,gashiSize*7,gashiSize*4);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(true);
                gs(false,0);
                gs(true);
                gs(true);
                gs(true);
                ps(false,gashiSize*8,gashiSize*4,gashiSize*2+20);
                gs(false,0,gashiSize*2+50,false);
                gs(true);
                gs(false,0,gashiSize*2+50,false);
                gs(true);
                gs(false,0);
                gs(false,0,gashiSize*2+50,false);
                gs(true);
                gs(false,0);
                gs(false,0,gashiSize*2+50,false);
                gs(true);
                break;

            case 21:
                gs(false);
                gs(false);
                gs(false);
                gs(false);
                gs(true, gashiSize * 3);
                gs(true);
                gs(true);
                gs(true);
                gs(false, gashiSize * 3);
                gs(false);
                gs(false);
                gs(false);
                gs(true, gashiSize * 3);
                gs(true);
                gs(true);
                gs(true);
                break;

            case 22:
                gs(true);
                gs(true);
                gs(true);
                gs(true);
                gs(false, gashiSize * 3);
                gs(false);
                gs(false);
                gs(false);
                gs(true, gashiSize * 3);
                gs(true);
                gs(true);
                gs(true);
                gs(false, gashiSize * 3);
                gs(false);
                gs(false);
                gs(false);
                break;

            case 23:
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);
                gs(true);
                gs(false, 0);

                ps(false, gashiSize, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                ps(false, gashiSize * 3, gashiSize*2);
                ps(true, 0, gashiSize*2);
                break;

            default:
                break;
        }
    }
}//좀더 쉽게 코드를 짤 수 있어도 좋을듯 땅, 발판위 즉석코드같은거.