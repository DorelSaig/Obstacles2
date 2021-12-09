package com.example.obstacles2;

import android.app.Application;

import com.google.gson.Gson;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MSPV3.initHelper(this);
        Game_Utils.initHelper(this);

        String js = MSPV3.getMe().getString("MY_DB", "");
        MyDB md = new Gson().fromJson(js, MyDB.class);

        if(md==null){
            MyDB myDB = new MyDB();
            String json = new Gson().toJson(myDB);
            MSPV3.getMe().putString("MY_DB", json);
        }
    }
}
