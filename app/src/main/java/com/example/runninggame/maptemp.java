package com.example.runninggame;

import java.util.ArrayList;

public class maptemp {
    static ArrayList<Integer>[] stagelevel=new ArrayList[5]; //0~4단계 까지

    public static void setmap() {
        System.out.println("setmap 호출");
        for (int i = 0; i < 5; i++) {
            stagelevel[i] = new ArrayList<Integer>();
        }
        stagelevel[0].add(1);
        stagelevel[0].add(2);
        stagelevel[0].add(3);
        stagelevel[0].add(4);
        stagelevel[1].add(3);
        stagelevel[1].add(2);
        stagelevel[1].add(1);
        stagelevel[2].add(2);
        stagelevel[2].add(3);
        stagelevel[3].add(4);
        stagelevel[4].add(3);
        stagelevel[4].add(2);
        System.out.println(stagelevel[0].size());
    }
}
