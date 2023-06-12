package com.example.runninggame;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Random;

public class DeadEffect {

    ImageView deadEff;
    float objectSpeed;
    Random random = new Random();
    float randomY;
    float randomX;
    public int size;
    private int curSize;

    private Handler gamehandler = new Handler(Looper.getMainLooper());
    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            if(curSize > 0){
                deadEff.setX(deadEff.getX() + (randomX * objectSpeed/20));
                deadEff.setY(deadEff.getY() + (randomY * objectSpeed/20));

                //deadEff.setLayoutParams(new ViewGroup.LayoutParams(curSize, curSize));
                deadEff.getLayoutParams().width = curSize;
                deadEff.getLayoutParams().height = curSize;
                deadEff.setLayoutParams(deadEff.getLayoutParams());
//                Log.d("jeje", curSize+"");
                curSize -= 3;

                gamehandler.postDelayed(this, 1);
            } else {
                deadEff.setVisibility(View.INVISIBLE);
            }
        }
    };

    public void spawnDeadEff(float objSpeed, float playerX, float playerY){
        objectSpeed = objSpeed;
        deadEff.setVisibility(View.VISIBLE);
        deadEff.setX(playerX);
        deadEff.setY(playerY);
        randomX = (random.nextFloat() * 40);
        randomY = (random.nextFloat() * 40);

        boolean minus = random.nextBoolean();
        if(minus) randomX *= -1;
        boolean minuss = random.nextBoolean();
        if(minuss) randomY *= -1;

        //deadEff.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        deadEff.getLayoutParams().height = size;
        deadEff.getLayoutParams().width = size;
        curSize = size;

        gamehandler.post(gameRunnable);
    }
}
