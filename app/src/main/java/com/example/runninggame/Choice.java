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

    private ImageView instance;

    public ImageView player;
    public int playerSize = 70; //플레이어 크기
    public int gashiSize = 100; //가시 크기
    private int platSize = 30; //플랫폼의 세로 크기(두께)
    private int platY = 120; //땅의 1단 기본 높이
    private ImageView ground; //가운데 땅
    private float groundY; //가운데 땅의 중간 Y좌표값
    private RectF playerRect;
    private RectF playerHeadRect;
    private RectF groundRect;
    private List<RectF> platRect = new ArrayList<>();
    private float jumpHeight = 60f; //점프 첫속도 (점프하는 힘.)
    private float gravity = 6f; //중력크기
    private boolean isJumping = false; //false일때 점프 가능
    private float translateY = 0; //플레이어 Y값 변경
    private boolean isreversal = false; //true면 반전상태
    private float playerY; // 플레이어의 중간 Y좌표값


    ////////////////////////////////// 이 아래는 패턴을 위한 변수들

    private int gashiNum = 0; //풀 안의 몇번째 가시를 꺼내쓸건지
    //private int removeGashiNum = 0;
    private List<ImageView> gashiPool = new ArrayList<>(); //가시 풀
    private int gashiPoolSize = 0; //풀을 효율적으로 관리하기 위한 변수
    private int gashiPoolStart = 0; //풀에서 소환된 오브젝트들의 처음 (풀 효율을 위한 것)
    private int gashiPoolEnd = 0; //풀에서 소환된 오브젝트들의 끝 (풀 효율을 위한 것)
    private List<Boolean> gR = new ArrayList<>(); //위쪽에 나올거면 False, 아래쪽에 나올거면 True
    private List<Boolean> gRR = new ArrayList<>(); //트루면 반대방향으로.
    private List<Integer> gY = new ArrayList<>(); //가시와 발판의 Y좌표
    private List<Integer> gD = new ArrayList<>(); //이전 가시와의 거리


    private int platNum = 0;
    private List<ImageView> platPool = new ArrayList<>();
    private int platPoolSize = 0; //풀을 효율적으로 관리하기 위한 변수
    private int platPoolStart = 0; //가시 스타트랑 동일
    private int platPoolEnd = 0; //가시 엔드랑 동일
    private List<Boolean> pR = new ArrayList<>();
    private List<Integer> pY = new ArrayList<>();
    private List<Integer> pl = new ArrayList<>(); //플랫폼의 길이
    private List<Integer> pD = new ArrayList<>();

    int patternNum; //몇번째 패턴을 할건지
    private float objectSpeed = 30; //가시와 발판 스피드

    //////////////////////////////////////////

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

                if(platPoolStart < platPoolEnd) {
                    for(int i = platPoolStart; i < platPoolEnd; i++){
                        platMove(i);
                    }
                } else if(platPoolStart > platPoolEnd){
                    for(int i = platPoolStart; i < platPoolSize; i++) {
                        platMove(i);
                    }
                    for(int i = 0; i < platPoolEnd; i++){
                        platMove(i);
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
    private void platMove(int i){
        instance = platPool.get(i);
        if(instance.getX() + instance.getWidth() - objectSpeed > 0)
            instance.setX(instance.getX() - objectSpeed);
        else
            removePlatform(instance);
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
            pY.clear();
            pR.clear();
            pD.clear();
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

        for(int i = 0; i < 100; i++) {
            createGashi();
            gashiPoolSize++;
        }

        for(int i = 0; i < 20; i++){
            createPlatform();
            platPoolSize++;
        }

        gamehandler.post(gameRunnable);
        moveHandler.post(moveObjects);
        moveHandler.postDelayed(nextPattern, 1000);
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
        if(!isreversal) //플레이어의 머리 렉트
            playerHeadRect = new RectF(player.getX(), player.getY(), player.getX() + player.getWidth(), player.getY() + 5);
        else
            playerHeadRect = new RectF(player.getX(), player.getY()+player.getHeight()-5, player.getX()+player.getWidth(), player.getY()+player.getHeight());

        if(platPoolStart < platPoolEnd){
            for(int i = platPoolStart; i < platPoolEnd; i++){
                platRectSetting(i);
            }
        } else if(platPoolStart > platPoolEnd){
            for(int i = platPoolStart; i < platPoolSize; i++)
                platRectSetting(i);
            for(int i = 0; i < platPoolEnd; i++)
                platRectSetting(i);
        }
    }
    private void platRectSetting(int i){
        instance = platPool.get(i);
        platRect.get(i).set(new RectF(instance.getX(), instance.getY(), instance.getX() + instance.getWidth(), instance.getY() + instance.getHeight()));
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

        if(platPoolStart < platPoolEnd){
            for(int i = platPoolStart; i < platPoolEnd; i++){
                platCollisionCheck(i);
            }
        } else if(platPoolStart > platPoolEnd){
            for(int i = platPoolStart; i < platPoolSize; i++){
                platCollisionCheck(i);
            }
            for(int i = 0; i < platPoolEnd; i++){
                platCollisionCheck(i);
            }
        }
    }
    private void platCollisionCheck(int i){
        if(platPool.get(i).getVisibility() == View.VISIBLE) {
            if (RectF.intersects(playerRect, platRect.get(i))){

                if(RectF.intersects(playerHeadRect, platRect.get(i))){
                    while (RectF.intersects(playerRect,platRect.get(i))){
                        if (!isreversal) {
                            player.offsetTopAndBottom(1);
                        } else {
                            player.offsetTopAndBottom(-1);
                        }
                        rectSetting();
                    }
                    isJumping = true;
                    translateY = 0;
                } else {
                    while (RectF.intersects(playerRect, platRect.get(i))) {
                        if (!isreversal) {
                            player.offsetTopAndBottom(-1);
                        } else {
                            player.offsetTopAndBottom(1);
                        }

                        rectSetting();


                    }
                    isJumping = false;
                    translateY = 0;
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


    //맨 처음에 예비용 가시 생성해둠 (현재는 100개)
    private void createGashi(){
        ImageView gashi = new ImageView(this);
        gashi.setImageResource(R.drawable.gashi);

        gashi.setLayoutParams(new ViewGroup.LayoutParams(gashiSize, gashiSize));

        gashiPool.add(gashi);
        gashi.setVisibility(View.INVISIBLE);
        ((ViewGroup)findViewById(android.R.id.content)).addView(gashi); // 부모 뷰를 지정

    }

    //맨 처음에 예비용 플랫폼 생성해둠 (현재는 20개)
    private void createPlatform(){
        ImageView plat = new ImageView(this);
        plat.setImageResource(R.drawable.platform);
        plat.setScaleType(ImageView.ScaleType.FIT_XY);

        plat.setLayoutParams(new ViewGroup.LayoutParams(200, platSize));

        platPool.add(plat);
        platRect.add(new RectF());
        plat.setVisibility(View.INVISIBLE);
        ((ViewGroup)findViewById(android.R.id.content)).addView(plat);

    }
    private ImageView spawnGashi(int y, boolean isreversal){
        ImageView gashi = gashiPool.get(gashiNum);
        gashi.setVisibility(View.VISIBLE);
        gashi.setX(screenWidth + gashiPool.get(gashiNum).getWidth());

        if(isreversal) { //아래쪽에서 가시 나옴
            gashi.setRotationX(180);

            gashi.setY(groundY + ground.getHeight() / 2f + y);
        }
        else {
            gashi.setRotationX(0);

            gashi.setY(groundY - gashiPool.get(gashiNum).getHeight() - ground.getHeight() / 2f - y);
        }

        gashiNum++;
        if(gashiNum >= gashiPool.size()) gashiNum = 0;

        return gashi;
    }

    //다른 방식의 장애물 생성 테스트
    private void SpawnObj(){
        float X = screenWidth + 500;
        for(int i = 0; i < gY.size(); i++){
            ImageView gashi = gashiPool.get(gashiNum);
            gashi.setVisibility(View.VISIBLE);
            X += gD.get(i);
            gashi.setX(X);
            if(gR.get(i)){
                if(!gRR.get(i)) gashi.setRotationX(180);
                else gashi.setRotationX(0);

                gashi.setY(groundY + ground.getHeight()/2 + gY.get(i));
            } else{
                if(!gRR.get(i)) gashi.setRotationX(0);
                else gashi.setRotationX(180);

                gashi.setY(groundY - ground.getHeight()/2 - gashiPool.get(gashiNum).getHeight() - gY.get(i));
            }

            gashiPoolEnd++;
            if(gashiPoolEnd >= gashiPoolSize)
                gashiPoolEnd = 0;

            gashiNum++;
            if(gashiNum >= gashiPoolSize) gashiNum = 0;
        }

        X = screenWidth + 500;
        for(int i = 0; i < pY.size(); i++){
            ImageView platform = platPool.get(platNum);
            platform.setVisibility(View.VISIBLE);
            X += pD.get(i);
            platform.setX(X);
            ViewGroup.LayoutParams params = platform.getLayoutParams();
            params.width = pl.get(i);
            platform.setLayoutParams(params);
            //platform.setLayoutParams(new ViewGroup.LayoutParams(30,30));
            if(pR.get(i)){
                platform.setY(groundY + ground.getHeight()/2 + pY.get(i));
            } else{
                platform.setY(groundY - ground.getHeight()/2 - platPool.get(platNum).getHeight() - pY.get(i));
            }

            platPoolEnd++;
            if(platPoolEnd >= platPoolSize)
                platPoolEnd = 0;

            platNum++;
            if(platNum >= platPoolSize)
                platNum = 0;
        }
    }





    private void removeGashi(final ImageView gashi){

        gashi.setVisibility(View.INVISIBLE);
        gashiPoolStart++;
        if(gashiPoolStart >= gashiPoolSize)
            gashiPoolStart = 0;

        if(gashiPoolStart == gashiPoolEnd && platPoolStart == platPoolEnd) //전부 사라졌다면 다음 패턴 바로 호출
            nextPatternHandler.post(nextPattern);
    }
    private void removePlatform(final ImageView platform){
        platform.setVisibility(View.INVISIBLE);
        platPoolStart++;
        if(platPoolStart >= platPoolSize)
            platPoolStart = 0;

        if(gashiPoolStart == gashiPoolEnd && platPoolStart == platPoolEnd) //전부 사라졌다면 다음 패턴 바로 호출
            nextPatternHandler.post(nextPattern);
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
    private void ps(boolean r, int d, int l, int y){
        pR.add(r); pY.add(y); pl.add(l); pD.add(d);
    }

    private void ps(boolean r, int d, int l){
        pR.add(r); pY.add(platY); pl.add(l); pD.add(d);
    }


    private void pattern(){
        //patternNum = patternDrawing(0,1);

        patternNum = 0;
        //gs() 위/아래 , 거리 , 공중
        switch (patternNum){
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
                ps(false, 0, 300);
                break;
            case 1:

                break;
            case 2:

                break;
            case 3:
                break;
        }

        //patternHandler.post(patternRun);
        SpawnObj();


    }
}//좀더 쉽게 코드를 짤 수 있어도 좋을듯 땅, 발판위 즉석코드같은거.


