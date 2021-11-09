package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class GameOverActivity extends AppCompatActivity {

    private ImageView panel_IMG_gameOver;
    private ImageButton panel_BTN_restart;
    private ImageButton panel_BTN_exit;

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

        Toast.makeText(getApplicationContext(),"Game Over",Toast.LENGTH_LONG).show();
    }


    private void initViews() {

        panel_BTN_restart = findViewById(R.id.panel_BTN_restart);

        panel_BTN_exit = findViewById(R.id.panel_BTN_exit);

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
    }


    private void restart() {

        vibrate(100);

        Intent i = new Intent(GameOverActivity.this, MainActivity.class);
        startActivity(i);
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