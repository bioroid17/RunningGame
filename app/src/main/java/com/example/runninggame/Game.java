/*package com.example.runninggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class Game extends View {

    Paint paint = new Paint();  // Paint 정보를 paint에 저장
    private GameThread T;   // T라는 이름의 게임 쓰레드를 Game 클래스 내부에서만 사용한다.
    boolean start = false;
    int count = 0;
    int screenWidth, screenHeight;  // 스크린 너비, 높이를 저장할 정수형 변수

    int obstacleCount = 99;  //
    int[] obsX = new int[100];    // 미사일 x 위치
    int[] obsY = new int[100];    // 미사일 y 위치
    Bitmap[] triangles = new Bitmap[3];

    int obsPattern = 0;

    public Game(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    // 뷰의 크기가 변경될 때 호출
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // 부모 클래스의 멤버 변수 참조
        super.onSizeChanged(w, h, oldw, oldh);
        // 뷰의 너비, 높이 정보 저장
        this.screenWidth = w;
        this.screenHeight = h;

        // 쓰레드 값이 비었다면
        if(T == null){
            T = new GameThread();   // 새로운 게임 스레드 객체 생성
            T.start();              // 게임 스레드 시작
        }
    }

    @Override
    // 뷰가 윈도우에서 분리될 때마다 발생
    protected void onDetachedFromWindow() {
        T.run = false;  // 쓰레드의 run 값으로 false를 줌
        super.onDetachedFromWindow();   // 부모 클래스의 멤버 변수를 참조
        // View 클래스에서 해당 명령어를 처리해보고, 못 찾으면 부모 클래스의 값을 받을 수 있게 한다.
    }

    protected void obstaclePattern1(){
        for(int i = 0; i < 3; i++) {
            triangles[i] = BitmapFactory.decodeResource(getResources(), R.drawable.triangle);
            triangles[i] = Bitmap.createScaledBitmap(triangles[i], screenWidth/16, screenHeight/8, true);
            obsX[i] = screenWidth + screenWidth*i/16;
            obsY[i] = screenHeight/2 - triangles[i].getHeight() *7/8;
        }
    }
    protected void obstaclePattern2(){
        for(int i = 0; i < 3; i++){
            triangles[i] = BitmapFactory.decodeResource(getResources(), R.drawable.triangle);
            triangles[i] = Bitmap.createScaledBitmap(triangles[i], screenWidth/16, screenHeight/8, true);
            obsX[i] = screenWidth + screenWidth*i/8;
            obsY[i] = screenHeight/2 - triangles[i].getHeight() *7/8;
        }
    }
    protected void obstaclePattern3(){
        for(int i = 0; i < 3; i++){
            triangles[i] = BitmapFactory.decodeResource(getResources(), R.drawable.triangle);
            triangles[i] = Bitmap.createScaledBitmap(triangles[i], screenWidth/16, screenHeight/8 * (i%2 + 1), true);
            obsX[i] = screenWidth + screenWidth*i/16;
            obsY[i] = screenHeight/2 - triangles[i].getHeight() *7/8;
        }
    }
    protected void obstaclePattern4(){
        for(int i = 0; i < 3; i++){
            triangles[i] = BitmapFactory.decodeResource(getResources(), R.drawable.triangle);
            triangles[i] = Bitmap.createScaledBitmap(triangles[i], screenWidth/16, screenHeight/8 * (i%2 + 1), true);
            obsX[i] = screenWidth + screenWidth*i/8;
            obsY[i] = screenHeight/4 - triangles[i].getHeight() *7/8;
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);

        canvas.drawLine(0, screenHeight/2, screenWidth, screenHeight/2, paint);

        Bitmap character = BitmapFactory.decodeResource(getResources(), R.drawable.character);
        character = Bitmap.createScaledBitmap(character, screenWidth/8, screenHeight/4, true);
        canvas.drawBitmap(character, screenWidth/16, screenHeight/4, null);

        if (obsPattern != 0){
            for(int i=0; i<3; i++){
                canvas.drawBitmap(triangles[i], obsX[i], obsY[i], null);
                if(screenWidth/16 < obsX[i] && obsX[i] < screenWidth/16 + character.getWidth()){
                    System.out.println("충돌!");
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 만약 화면을 터치했다면
        if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_POINTER_DOWN){
            if((int)event.getX() > screenWidth*3/4){
                obstaclePattern4();
                obsPattern = 4;
                Toast.makeText(getContext().getApplicationContext(),"4번", Toast.LENGTH_SHORT).show();
                start = true;
            }
            else if((int)event.getX() > screenWidth / 2){
                obstaclePattern3();
                obsPattern = 3;
                Toast.makeText(getContext().getApplicationContext(),"3번", Toast.LENGTH_SHORT).show();
                start = true;
            }
            else if((int)event.getX() > screenWidth / 4){
                obstaclePattern2();
                obsPattern = 2;
                Toast.makeText(getContext().getApplicationContext(),"2번", Toast.LENGTH_SHORT).show();
                start = true;
            }
            else{
                obstaclePattern1();
                obsPattern = 1;
                Toast.makeText(getContext().getApplicationContext(),"1번", Toast.LENGTH_SHORT).show();
                start = true;
            }
        }

        if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_POINTER_UP){
            if(obsX[2] < screenWidth/16){
                obsPattern = 0;
                start = false;
            }
        }
        return true;
    }


    class GameThread extends Thread{
        public boolean run = true;

        @Override
        public void run() {
            while(run){
                try{
                    postInvalidate();

                    if(start == true && obsPattern != 0){
                        obsX[0] -= screenWidth/128;
                        obsX[1] -= screenWidth/128;
                        obsX[2] -= screenWidth/128;
                    }
                    sleep(10);
                } catch (Exception e){

                }
            }
        }
    }
}
*/