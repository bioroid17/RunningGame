package com.example.runninggame;

import static com.example.runninggame.maptemp.*;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
public class Choice extends AppCompatActivity {
    static int maplevel=0; //0스테이지부터 ~~
    static int patNum=0;
    private int score = 0;
    private Timer timer;
    private TextView scoreTextView;
    private TextView restartTextView;
    private  TextView mainmenuTextView;
    private ImageButton restartButton;
    private ImageButton mainmenuButton;

    private boolean isPaused = false;
    private boolean isDead = false;


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
    private List<RectF> gashiRect = new ArrayList<>();
    private List<List<RectF>> platRect = new ArrayList<>();
    private float jumpHeight = 50f; //점프 첫속도 (점프하는 힘.)
    private float gravity = 3.5f; //중력크기
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
    private float objectSpeed = 20; //가시와 발판 스피드

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

        // 화면 가로, 세로 크기
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        scoreTextView = findViewById(R.id.score_text_view);
        startTimer();

        restartTextView = findViewById(R.id.restart_text);
        restartTextView.setVisibility(View.INVISIBLE);
        restartButton = findViewById(R.id.restart_button);
        restartButton.setVisibility(View.INVISIBLE);

        mainmenuTextView = findViewById(R.id.mainmenu_text);
        mainmenuTextView.setVisibility(View.INVISIBLE);
        mainmenuButton = findViewById(R.id.mainmenu_button);
        mainmenuButton.setVisibility(View.INVISIBLE);


        for(int i = 0; i < 10; i++){
            platNum[i] = 0;
            platPoolStart[i] = 0;
            platPoolEnd[i] = 0;
            platRect.add(new ArrayList<>());
            platPool.add(new ArrayList<>());
        }


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
                if (!isPaused && !isDead) {
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
    public void onRestartButtonClick(View view) {
        isDead = false;
        view.setVisibility(View.INVISIBLE);
        restartTextView.setVisibility(View.INVISIBLE);
        mainmenuButton.setVisibility(View.INVISIBLE);
        mainmenuTextView.setVisibility(View.INVISIBLE);
        for(ImageView gashi : gashiPool){
            removeGashi(gashi);
        }
//        for(List<ImageView> platform : platPool){
//            removePlatform(platform);
//        }
        score = 0;
    }
    public void onMainMenuButtonClick(View view){
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
            if (RectF.intersects(playerRect, platRect.get(j).get(i))){

                if(RectF.intersects(playerHeadRect, platRect.get(j).get(i))){
                    while (RectF.intersects(playerRect,platRect.get(j).get(i))){
                        if (!isreversal) {
                            player.offsetTopAndBottom(1);
                        } else {
                            player.offsetTopAndBottom(-1);
                        }
                        rectSetting();
                    }
                    isJumping = true;
                } else {
                    while (RectF.intersects(playerRect, platRect.get(j).get(i))) {
                        if (!isreversal) {
                            player.offsetTopAndBottom(-1);
                        } else {
                            player.offsetTopAndBottom(1);
                        }
                        rectSetting();
                    }
                    isJumping = false;
                }
                translateY = 0;
            }
        }
    }

    private void gashiCollisionCheck(int i){
        if(gashiPool.get(i).getVisibility() == View.VISIBLE) {
            if (RectF.intersects(playerRect, gashiRect.get(i))){
                if(RectF.intersects(playerHeadRect, gashiRect.get(i))){
                    isDead = true;
                    restartButton.setVisibility(View.VISIBLE);
                    restartTextView.setVisibility(View.VISIBLE);
                    scoreTextView.setVisibility(View.VISIBLE);
                    mainmenuButton.setVisibility(View.VISIBLE);
                    mainmenuTextView.setVisibility(View.VISIBLE);
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
        }
        return super.onKeyDown(keyCode, event);
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
        gashiPoolStart++;
        if(gashiPoolStart >= gashiPoolSize)
            gashiPoolStart = 0;

        if(gashiPoolStart == gashiPoolEnd) {
            int platDeleteNum = 0;
            for (int i = 0; i < 10; i++) {
                if (platPoolStart[i] == platPoolEnd[i]) { //전부 사라졌다면 다음 패턴 바로 호출
                    platDeleteNum++;
                }
            }
            if(platDeleteNum == 10) {
                System.out.println("nextPattern 실행");
                nextPatternHandler.post(nextPattern);
            }
        }
    }

    private void removePlatform(int i, ImageView platform){
        platform.setVisibility(View.INVISIBLE);
        platPoolStart[i]++;
        if(platPoolStart[i] >= platPoolSize)
            platPoolStart[i] = 0;

        if(gashiPoolStart == gashiPoolEnd) {
            int platDeleteNum = 0;
            for (int j = 0; j < 10; j++) {
                if (platPoolStart[j] == platPoolEnd[j]) { //전부 사라졌다면 다음 패턴 바로 호출
                    platDeleteNum++;
                }
            }
            if(platDeleteNum == 10) nextPatternHandler.post(nextPattern);
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
        //patternNum = patternDrawing(0,1);
        System.out.println(maplevel+"번째 맵의"+patNum+"번째 패턴 실행");
        MapList(maplevel,patNum);
        //patternHandler.post(patternRun);
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
        }
    }
}//좀더 쉽게 코드를 짤 수 있어도 좋을듯 땅, 발판위 즉석코드같은거.

