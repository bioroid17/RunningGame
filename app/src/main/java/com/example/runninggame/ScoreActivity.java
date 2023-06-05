package com.example.runninggame;

import static android.R.layout.simple_list_item_1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends Activity {
    private ScoreManager scoreManager;
    private ListView scoreListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_score);

        scoreManager = new ScoreManager(this);
        scoreListView = findViewById(R.id.score_list);
        TextView closeButton = findViewById(R.id.exit);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 선택적: ScoreActivity를 종료하여 백스택에서 제거합니다.
            }
        });

        List<Integer> topScores = scoreManager.getTopScores();

        for(int a: topScores){
            System.out.println(a);
        }

        List<String> stringScores = new ArrayList<>();
        int i=1;
        for(Integer score : topScores) {
            if(i==1) {
                stringScores.add(i+ "st   " + String.valueOf(score));
            }else if(i==2){
                stringScores.add(i+ "nd   " + String.valueOf(score));
            }else if(i==3){
                stringScores.add(i+ "rd   " + String.valueOf(score));
            }else{
                stringScores.add(i+ "th   " + String.valueOf(score));
            }
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringScores) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.WHITE);  // 텍스트 색상을 흰색으로 설정
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);  // 텍스트 크기를 20sp로 설정
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };

        ListView listView = findViewById(R.id.score_list);
        listView.setAdapter(adapter);
    }
}
