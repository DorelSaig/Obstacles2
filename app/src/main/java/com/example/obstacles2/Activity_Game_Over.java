package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.Collections;

public class Activity_Game_Over extends AppCompatActivity {

    private ImageView panel_IMG_gameOver;
    private ImageButton panel_BTN_restart;
    private ImageButton panel_BTN_exit;
    private MaterialButton panel_BTN_saveRecord;
    private EditText panel_ETXT_playerName;
    private TextView panel_TXT_score;
    private String player_Name;

    private int score;

    //Location Service
    private GpsTracker gpsService;

    private MyDB myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);


        panel_IMG_gameOver = findViewById(R.id.panel_IMG_gameOver);
        Glide
                .with(this)
                .load(R.drawable.sky_game_over_background)
                .centerCrop()
                .into(panel_IMG_gameOver);

        initViews();
        initButtons();

        playSound(R.raw.audio_mayday);

        vibrate(2000);

        Toast.makeText(getApplicationContext(), "Game Over", Toast.LENGTH_LONG).show();

        score = getIntent().getExtras().getInt("Score");

        panel_TXT_score.setText("Score: "+ score);

        //Location
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initViews() {

        panel_ETXT_playerName = findViewById(R.id.panel_ETXT_playerName);

        panel_BTN_saveRecord = findViewById(R.id.panel_BTN_saveRecord);

        panel_BTN_restart = findViewById(R.id.panel_BTN_restart);

        panel_BTN_exit = findViewById(R.id.panel_BTN_exit);

        panel_TXT_score = findViewById(R.id.panel_TXT_score);

    }

    private void initButtons() {
        panel_BTN_restart.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart();
            }
        }));

        panel_BTN_exit.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        }));

        panel_BTN_saveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = 0.0;
                double longitude = 0.0;

                player_Name = panel_ETXT_playerName.getText().toString();

                // * Start of Location Service
                gpsService = new GpsTracker(Activity_Game_Over.this);
                if (gpsService.canGetLocation()) {
                    latitude = gpsService.getLatitude();
                    longitude = gpsService.getLongitude();
                    Toast.makeText(getApplicationContext(), "player: " + player_Name + " score: " + score + "long: " + longitude + " lati: " + latitude, Toast.LENGTH_LONG).show();
                } else {
                    gpsService.showSettingsAlert();
                }
                // * End of Location Service

                panel_ETXT_playerName.setVisibility(View.GONE);
                panel_BTN_saveRecord.setVisibility(View.GONE);

                saveRecord(player_Name, score, longitude, latitude);


            }
        });
    }

    private void saveRecord(String player_name, int score, double longitude, double latitude) {

        String js = MSPV3.getMe().getString("MY_DB", "");
        myDB = new Gson().fromJson(js, MyDB.class);

        myDB.getRecords().add(new Record()
                .setName(player_name)
                .setScore(score)
                .setLat(latitude)
                .setLon(longitude)
        );

        Collections.sort(myDB.getRecords(), new SortByScore());

        String json = new Gson().toJson(myDB);
        MSPV3.getMe().putString("MY_DB", json);
    }


    private void restart() {

        vibrate(100);

        this.finish();


        // Cancel the swipe transition
        overridePendingTransition(0, 0);
        String time = System.currentTimeMillis() + "";

    }


    //TODO make global function
    private void vibrate(int millisecond) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(millisecond, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(millisecond);
        }
    }


    //TODO make global function
    private void playSound(int audio_mayday) {

        final MediaPlayer mp = MediaPlayer.create(this, audio_mayday);
        mp.start();

    }
}