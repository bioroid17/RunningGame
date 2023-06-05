package com.example.runninggame;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import java.util.Random;

public class RunningEffect {
    ImageView eff;
    float objectSpeed;
    Random random = new Random();
    float randomNum;
    boolean isreversal;

    private Handler gamehandler = new Handler(Looper.getMainLooper());
    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            if(eff.getX() + eff.getWidth() > 0) {
                eff.setX(eff.getX() - objectSpeed);
                if(isreversal)
                    eff.setY(eff.getY() + randomNum);
                else
                    eff.setY(eff.getY() - randomNum);

                gamehandler.postDelayed(this, 1);
            }
            else {
                eff.setVisibility(View.INVISIBLE);
            }
        }
    };

    public void spawnEff(float objSpeed, float playerX, float playerY, boolean isrever){
        objectSpeed = objSpeed;
        isreversal = isrever;
        eff.setVisibility(View.VISIBLE);
        eff.setX(playerX);
        eff.setY(playerY);
        randomNum = random.nextFloat() * 7; //일단보류
        gamehandler.post(gameRunnable);

    }
}