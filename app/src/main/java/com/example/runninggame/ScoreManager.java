package com.example.runninggame;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private static final String PREFS_NAME = "game_scores";
    private static final String KEY_SCORES = "scores";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public ScoreManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveScore(int newScore) {

        String allScoresString = prefs.getString(KEY_SCORES, "");
        List<Integer> allScores = new ArrayList<>();
        if (!allScoresString.isEmpty()) {
            String[] scoresArray = allScoresString.split(",");
            for (String score : scoresArray) {
                allScores.add(Integer.parseInt(score));
            }
        }

        // 스코어 더함
        allScores.add(newScore);

        // 내림차순 정렬
        Collections.sort(allScores, Collections.reverseOrder());

        // 10개까지 저장
        while (allScores.size() > 10) {
            allScores.remove(allScores.size() - 1);
        }

        // 스트링 형태로 변환
        StringBuilder allScoresStringBuilder = new StringBuilder();
        for (int i = 0; i < allScores.size(); i++) {
            allScoresStringBuilder.append(allScores.get(i));
            if (i < allScores.size() - 1) {
                allScoresStringBuilder.append(",");
            }
        }

        // 저장
        editor.putString(KEY_SCORES, allScoresStringBuilder.toString());
        editor.commit();
    }

    public List<Integer> getTopScores() {
        String allScoresString = prefs.getString(KEY_SCORES, "");
        List<Integer> allScores = new ArrayList<>();
        if (!allScoresString.isEmpty()) {
            String[] scoresArray = allScoresString.split(",");
            for (String score : scoresArray) {
                allScores.add(Integer.parseInt(score));
            }
        }
        return allScores;
    }

    public int firstScore() {
        if(getTopScores().size()!=0){
            return getTopScores().get(0);
        }
        return 0;
    }
}
