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
    private List<Boolean> gR = new ArrayList<>(); //위쪽에 나올거면 False, 아래쪽에 나올거면 True
    private List<Integer> gY = new ArrayList<>(); //가시와 발판의 Y좌표
    private List<Float> gT = new ArrayList<>(); //다음 가시or발판이 나오기까지 대기시간(거리)


    private int platNum = 0;
    private List<ImageView> platPool = new ArrayList<>();
    private int platPoolSize = 0; //풀을 효율적으로 관리하기 위한 변수
    private List<Boolean> pR = new ArrayList<>();
    private List<Integer> pY = new ArrayList<>();
    private List<Float> pT = new ArrayList<>();

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
                for (ImageView gashi : gashiPool) {
                    if (gashi.getVisibility() == View.VISIBLE)
                        if (gashi.getX() + gashi.getWidth() - objectSpeed < 0)
                            removeGashi(gashi);
                        else
                            gashi.setX(gashi.getX() - objectSpeed);
                }
                for(ImageView platform : platPool){
                    if(platform.getVisibility() == View.VISIBLE)
                       if (platform.getX() + platform.getWidth() - objectSpeed < 0)
                            removePlatform(platform);
                       else
                            platform.setX(platform.getX() - objectSpeed);
                }
            }

            moveHandler.postDelayed(this, 1);
        }
    };



    private Handler patternHandler = new Handler();
/*
    int patternRunI = 0; //일단 임시로 생성
    private Runnable patternRun = new Runnable() {
        @Override
        public void run() {

            if(patternRunI < gR.size()){
                spawnGashi(gY.get(patternRunI), gR.get(patternRunI));
                if(gT.get(patternRunI) != 0)
                    patternHandler.postDelayed(this, (int)(gT.get(patternRunI)*1000));
                else
                    patternHandler.post(this);
                patternRunI++;
            } else {
                nextPatternHandler.postDelayed(nextPattern, 2000);
            }
        }
    };

 */
    private Handler nextPatternHandler = new Handler();
    private Runnable nextPattern = new Runnable() {
        @Override
        public void run() {
            //patternRunI = 0;
            gY.clear();
            gR.clear();
            gT.clear();
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
        if(!isreversal)
            playerHeadRect = new RectF(player.getX(), player.getY(), player.getX() + player.getWidth(), player.getY() + 5);
        else
            playerHeadRect = new RectF(player.getX(), player.getY()+player.getHeight()-5, player.getX()+player.getWidth(), player.getY()+player.getHeight());
        groundRect = new RectF(ground.getX(),ground.getY(),ground.getX() + ground.getWidth(), ground.getY() + ground.getHeight());
        for(int i = 0; i < platPool.size(); i++){
            if(platPool.get(i).getVisibility() == View.VISIBLE) {
                ImageView plat = platPool.get(i);
                platRect.get(i).set(new RectF(plat.getX(), plat.getY(), plat.getX() + plat.getWidth(), plat.getY() + plat.getHeight()));
            }
        }
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
        for(int i = 0; i < platPool.size(); i++) {
            if(platPool.get(i).getVisibility() == View.VISIBLE) {
                if (RectF.intersects(playerRect, platRect.get(i))){
                    //int ii = 0; // 땅에 닿았을 때 땅위로 이동하기위해 움직인 횟수 체크 (폐기)

                    if(RectF.intersects(playerHeadRect, platRect.get(i))){
                        while (RectF.intersects(playerRect,platRect.get(i))){
                            if (!isreversal) {
                                player.offsetTopAndBottom(1);
                            } else {
                                player.offsetTopAndBottom(-1);
                            }
                            rectSetting();
                            //if(!RectF.intersects(playerRect,platRect.get(i))) continue;
                        }
                    } else {
                        while (RectF.intersects(playerRect, platRect.get(i))) {
                            if (!isreversal) {
                                player.offsetTopAndBottom(-1);
                                //ii++;
                            } else {
                                player.offsetTopAndBottom(1);
                                //ii++;
                            }

                            rectSetting();

                        /*if(ii > 100){
                            while(RectF.intersects(playerRect, platRect.get(i))){
                                if(!isreversal){
                                    player.offsetTopAndBottom(1);
                                } else{
                                    player.offsetTopAndBottom(-1);
                                }
                                rectSetting();
                            }
                            continue;
                        }*/

                        }
                    }
                    isJumping = false;
                    translateY = 0;
                    /*if(ii > 50){  //만약 너무 많이 움직였으면 (아래에서 머리를 박았으면)
                        if(!isreversal){
                            player.offsetTopAndBottom(ii);
                            isJumping = true;
                        } else{
                            player.offsetTopAndBottom(-ii);
                            isJumping = true;
                        }
                    }*/
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


    //맨 처음에 예비용 가시 생성해둠 (현재는 100개)
    private void createGashi(){
        ImageView gashi = new ImageView(this);
        gashi.setImageResource(R.drawable.gashi);

        //gashi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        gashi.setLayoutParams(new ViewGroup.LayoutParams(150, 150));

        gashiPool.add(gashi);
        gashi.setVisibility(View.INVISIBLE);
        //addContentView(gashi, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((ViewGroup)findViewById(android.R.id.content)).addView(gashi); // 부모 뷰를 지정

    }

    //맨 처음에 예비용 플랫폼 생성해둠 (현재는 20개)
    private void createPlatform(){
        ImageView plat = new ImageView(this);
        plat.setImageResource(R.drawable.platform);
        plat.setScaleType(ImageView.ScaleType.FIT_XY);

        plat.setLayoutParams(new ViewGroup.LayoutParams(200, 30));

        platPool.add(plat);
        platRect.add(new RectF());  //이게 문제였다 씨이부랄!!!!!!@~!!
        plat.setVisibility(View.INVISIBLE);
        ((ViewGroup)findViewById(android.R.id.content)).addView(plat);

    }
    private ImageView spawnGashi(int y, boolean isreversal){
        ImageView gashi = gashiPool.get(gashiNum);
        gashi.setVisibility(View.VISIBLE);
        gashi.setX(screenWidth + gashiPool.get(gashiNum).getWidth());

        if(isreversal) { //아래쪽에서 가시 나옴
            gashi.setRotationX(180);

            //gashi.setY(groundY + y);//가시 이미지 수정후 수정
            gashi.setY(groundY + ground.getHeight() / 2f + y);
        }
        else {
            gashi.setRotationX(0);
//아래코드 가시 이미지 수정후 수정
            //gashi.setY(groundY - gashiPool.get(gashiNum).getHeight() - y);
            gashi.setY(groundY - gashiPool.get(gashiNum).getHeight() - ground.getHeight() / 2f - y);
        }

        gashiNum++;
        if(gashiNum >= gashiPool.size()) gashiNum = 0;

        return gashi;
    }

    //다른 방식의 장애물 생성 테스트
    private void SpawnGashi(){
        //float gashiX = screenWidth + gashiPool.get(gashiNum).getWidth();
        float gashiX = screenWidth + 100;
        for(int i = 0; i < gY.size(); i++){
            ImageView gashi = gashiPool.get(gashiNum);
            gashi.setVisibility(View.VISIBLE);
            gashi.setX(gashiX);
            gashiX += gT.get(i);
            if(gR.get(i)){
                gashi.setRotationX(180);

                gashi.setY(groundY + ground.getHeight()/2 + gY.get(i));
            } else{
                gashi.setRotationX(0);

                gashi.setY(groundY - ground.getHeight()/2 - gashiPool.get(gashiNum).getHeight() - gY.get(i));
            }

            gashiNum++;
            if(gashiNum >= gashiPool.size()) gashiNum = 0;
        }

        //float platX = screenWidth + platPool.get(platNum).getWidth();
        float platX = screenWidth + 100;
        for(int i = 0; i < pY.size(); i++){
            ImageView platform = platPool.get(platNum);
            platform.setVisibility(View.VISIBLE);
            platform.setX(platX);
            platX += pT.get(i);
            if(pR.get(i)){
                platform.setY(groundY + ground.getHeight() + pY.get(i));
            } else{
                platform.setY(groundY - ground.getHeight() - platPool.get(platNum).getHeight() - pY.get(i));
            }

            platNum++;
            if(platNum >= platPool.size())
                platNum = 0;
        }

        nextPatternHandler.postDelayed(nextPattern, 3000);
    }





    private void removeGashi(final ImageView gashi){

        gashi.setVisibility(View.INVISIBLE);
    }
    private void removePlatform(final ImageView platform){
        platform.setVisibility(View.INVISIBLE);
    }


    public int patternDrawing(int min, int max) { //랜덤으로 패턴 뽑아오기
        Random random = new Random();
        return random.nextInt(max - min + 1) + min; //(0, 패턴의 수 - 1)로 호출
    }

    private void patternSet(boolean r, int y, float t){
        gR.add(r);
        gY.add(y);
        gT.add(t);
    }
    private void platformSet(boolean r, int y, float t){
        pR.add(r);
        pY.add(y);
        pT.add(t);
    }


    private void pattern(){
        //patternNum = patternDrawing(0,1);

        patternNum = 0;

        switch (patternNum){
            case 0:
                patternSet(true,0,200);
                patternSet(false,0,200);
                patternSet(true,0,100);
                patternSet(false,0,300);
                patternSet(true,0,200);
                patternSet(false,0,200);
                patternSet(true,0,0);
                patternSet(false,0,200);
                patternSet(true,0,0);
                patternSet(false,0,200);

                platformSet(true, 150, 200);
                platformSet(true, 150, 200);
                platformSet(true, 150, 200);
                platformSet(true, 150, 200);
                platformSet(true, 150, 200);
                break;
            case 1:
                patternSet(false,0,0);
                patternSet(true,0,500);
                patternSet(false,0,0);
                patternSet(true,0,500);
                patternSet(false,0,0);
                patternSet(true,0,500);

                platformSet(false, 150, 300);
                platformSet(false, 150, 300);
                platformSet(false, 150, 300);
                break;
            case 2:
                patternSet(false,10,0);
                patternSet(true,10,500);
                patternSet(false,110,0);
                patternSet(true,10,500);
                patternSet(false,110,0);
                patternSet(true,20,500);
                break;
            case 3:
                break;
        }

        //patternHandler.post(patternRun);
        SpawnGashi();


    }
}//이제 해야할게.... 가시 위아래 변형시키는거랑... 좀더 쉽게 코드를 짤 수 있어도 좋을듯 땅, 발판위 즉석코드같은거.
//발판 길이도 수정할 수 있는 코드를 짜면 좋긋다//

// 오브젝트 풀도 좀더 효율적으로. 모든 풀을 매번 VISIBLE인지 검사하지 말고, 소환된 오브젝트만 검사하도록
// 방안 a와 b를 만들어, 소환 시, 변수 b를 증가시키고 리무브 시 변수 a를 증가시킨다.
// a < b일 경우, (a <= i < b) 만 스캔
// b < a일 경우, (a <= i < size || 0 <= i < b) 만 스캔. // (i >= size)일 경우, i = 0 .

