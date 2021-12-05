package com.example.obstacles2;

import android.util.Log;

import java.util.ArrayList;

public class MyDB {

    private ArrayList<Record> records = new ArrayList<Record>();

    public MyDB() { };

    public ArrayList<Record> getRecords() {
        if(records == null){
            records = new ArrayList<Record>();
        }
        return records;
    }

    public MyDB setRecords(ArrayList<Record> records) {
        this.records = records;
        return this;
    }
}
