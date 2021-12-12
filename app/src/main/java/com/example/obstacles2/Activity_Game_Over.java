package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private ImageButton panel_BTN_exit;
    private MaterialButton panel_BTN_saveRecord, panel_BTN_restart;
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
        Screen_Utils.hideSystemUI(this);

        panel_IMG_gameOver = findViewById(R.id.panel_IMG_gameOver);
        Glide
                .with(this)
                .load(R.drawable.skybackground)
                .centerCrop()
                .into(panel_IMG_gameOver);

        initViews();
        initButtons();

        Game_Utils.getInstance().playSound(R.raw.audio_mayday);

        Game_Utils.getInstance().vibrate(2000);

        Toast.makeText(getApplicationContext(), "Game Over", Toast.LENGTH_LONG).show();

        score = getIntent().getExtras().getInt("Score");

        panel_TXT_score.setText("Score:" + score);

        //----- Get Location use permission and check -----
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*                             initialize Center                                */
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
            public void onClick(View v) { restart(); }
        }));

        //------- Exit Back To Menu --------
        panel_BTN_exit.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
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
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
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


    /*                              Logics Center!!!                                */

    //------- Saves the record to the Database --------
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

    //------- Restarts the last game played --------
    private void restart() {

        Game_Utils.getInstance().vibrate(100);

        Intent gameIntent = new Intent(this, Activity_Game.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean(Activity_Game.SENSOR_MODE, getIntent().getExtras().getBoolean(Activity_Game.SENSOR_MODE));

        gameIntent.putExtras(bundle);
        startActivity(gameIntent);
        this.finish();


    }

}