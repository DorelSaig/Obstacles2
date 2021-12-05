package com.example.obstacles2;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MSPV3.initHelper(this);
    }
}
