package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

public class Activity_Menu extends AppCompatActivity {

    private boolean first_time = true;

    private MaterialButton menu_BTN_easy;
    private MaterialButton menu_BTN_hard;
    private MaterialButton menu_BTN_high_scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageView panel_IMG_background = findViewById(R.id.panel_IMG_menu_background);
        Glide
                .with(this)
                .load(R.drawable.skybackground)
                .centerCrop()
                .into(panel_IMG_background);

        findviews();
        initButtons();

        if(first_time){

            MyDB myDB = new MyDB();
//            myDB.getRecords().add(new Record()
//                        .setName("Test2")
//                        .setScore(0)
//                        .setLat(0)
//                        .setLon(0)
//                );

            String json = new Gson().toJson(myDB);
            MSPV3.getMe().putString("MY_DB", json);

            first_time=false;
            }


        }


    private void findviews() {
        menu_BTN_easy = findViewById(R.id.menu_BTN_easy);
        menu_BTN_hard = findViewById(R.id.menu_BTN_hard);
        menu_BTN_high_scores = findViewById(R.id.menu_BTN_high_scores);
    }

    private void initButtons() {
        menu_BTN_easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(false);
            }
        });

        menu_BTN_hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(true);
            }
        });

        menu_BTN_high_scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHighScores();
            }
        });
    }

    private void showHighScores() {
        Intent highScoresIntent = new Intent(this, Activity_High_Scores.class);

        startActivity(highScoresIntent);
    }

    private void startGame(boolean sensorMode) {
        Intent gameIntent = new Intent(this, MainActivity.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.SENSOR_MODE, sensorMode);

        gameIntent.putExtras(bundle);
        startActivity(gameIntent);
    }
}