package com.example.obstacles2;

import java.util.Comparator;

public class SortByScore implements Comparator<Record> {

    @Override
    public int compare(Record rec1, Record rec2) {

        return rec1.getScore()- rec2.getScore();
    }
}
