package com.example.obstacles2;

public class Record {



    private String name;
    private long time = 0;
    private int score = 0;
    private double lat = 0.0;
    private double lon = 0.0;

    public Record() { }

    public String getName() {
        return name;
    }

    public Record setName(String name) {
        this.name = name;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Record setTime(long time) {
        this.time = time;
        return this;
    }

    public int getScore() {
        return score;
    }

    public Record setScore(int score) {
        this.score = score;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public Record setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public Record setLon(double lon) {
        this.lon = lon;
        return this;
    }
}
