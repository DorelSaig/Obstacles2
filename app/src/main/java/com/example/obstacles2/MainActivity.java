package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final int MAX_LIVES = 4;
    private final int NUM_OF_COLUMNS = 3;
    private final int NUM_OF_OBSTACLE_TYPES = 3;


    private ImageView[][] path;
    private int[][] vals;
    private ImageButton panel_BTN_Right;
    private ImageButton panel_BTN_Left;
    private ImageButton panel_BTN_volume;

    private ImageView[] panel_IMG_engines;
    private ImageView[] panel_IMG_airplane;

    private boolean volume = true;


    private int current = 1;
    private int speed = 200;
    private int i = 0;
    private int lives = MAX_LIVES;

    private Timer timer = new Timer();
    int obsChoose;
    int columnChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView panel_IMG_background = findViewById(R.id.panel_IMG_background);
        Glide
                .with(this)
                .load(R.drawable.skybackground)
                .centerCrop()
                .into(panel_IMG_background);

        findViews();
        initButtons();

        for (int i = 0; i < vals.length; i++) {
            for (int j = 0; j < vals[i].length; j++) {
                vals[i][j] = 0;
            }
        }

    }

    @Override
    protected void onStart() {

        //start service and play music
        startService(new Intent(MainActivity.this, SoundService.class));

        super.onStart();

            startUI();
    }

    private void startUI() {
        timer = new Timer();

        //path[0][columnChoose].setVisibility(View.VISIBLE);

        // Timer Set and start running
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        logic();
                        collisionCheck();

                    }
                });
            }
        }, 0, speed);

    }

    @Override
    protected void onStop() {
        stopService(new Intent(MainActivity.this, SoundService.class));

        super.onStop();
        stopUI();
    }


    private void stopUI() {
        timer.cancel();
    }



    private void collisionCheck() {
        if (vals[4][current] == 1) {

            vibrate(300);

            playSound(R.raw.sound_hit);

            final Toast toast = Toast.makeText(getApplicationContext(),"Engine "+lives+ " Down ",Toast.LENGTH_SHORT);
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);

            lives--;

            panel_IMG_engines[lives].setVisibility(View.INVISIBLE);

            if (lives == 0) {
                gameOver();
            }
        }
        else if (vals[4][current] == 2) {
            if (lives < 4) {

                playSound(R.raw.sound_fix);

                vibrate(100);

                panel_IMG_engines[lives].setVisibility(View.VISIBLE);

                lives++;

            }
        }
    }

    private void logic() {
        obsChoose = (int) Math.floor(Math.random()*NUM_OF_OBSTACLE_TYPES);
        columnChoose = (int) Math.floor(Math.random()*NUM_OF_COLUMNS);

        for (int i = vals.length-1; i > 0; i--) {
            for(int j = 0; j < vals[0].length; j++){
                vals[i][j] = vals[i-1][j];
            }

        }

        for (int i = 0; i < vals[0].length; i++) {
            vals[0][i] = 0;
        }

        vals[0][columnChoose] = obsChoose;
        update_UI_logic();

    }

    private void update_UI_logic() {

        for (int i = 0; i < path.length; i++) {
            for (int j = 0; j < path[i].length; j++) {
                ImageView im = path[i][j];
                if (vals[i][j] == 0) {
                    im.setVisibility(View.INVISIBLE);
                } else if (vals[i][j] == 1) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(R.drawable.ic_seagull);
                } else if (vals[i][j] == 2) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(R.drawable.ic_service);
                }
            }
        }
    }

        private void gameOver() {
            timer.cancel();

            Intent i = new Intent(MainActivity.this, GameOverActivity.class);
            startActivity(i);
            this.finish();

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


    private void initButtons() {
        panel_BTN_Left.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(100);
                //Right = True Left = False
                move(false);
            }
        }));

        panel_BTN_Right.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(100);
                move(true);
            }
        }));

        panel_BTN_volume.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (volume){
                    stopService(new Intent(MainActivity.this, SoundService.class));
                    panel_BTN_volume.setBackgroundResource(R.drawable.ic_volume_off);
                    volume = false;
                } else {
                    startService(new Intent(MainActivity.this, SoundService.class));
                    panel_BTN_volume.setBackgroundResource(R.drawable.ic_volume_on);
                    volume = true;
                }
            }
        }));



    }

    private void move(boolean direction) {
        if(direction && current<=1){
            panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
            current++;
            panel_IMG_airplane[current].setVisibility(View.VISIBLE);
        } else if (!direction && current>=1){
            panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
            current--;
            panel_IMG_airplane[current].setVisibility(View.VISIBLE);
        }
    }

    private void findViews() {

        panel_BTN_Right = findViewById(R.id.panel_BTN_right);

        panel_BTN_Left = findViewById(R.id.panel_BTN_left);

        panel_BTN_volume = findViewById(R.id.panel_BTN_volume);

        panel_IMG_engines = new ImageView[] {
                findViewById(R.id.engine1),
                findViewById(R.id.engine2),
                findViewById(R.id.engine3),
                findViewById(R.id.engine4)
        };

        panel_IMG_airplane = new ImageView[]{
                findViewById(R.id.panel_IMG_main_left),
                findViewById(R.id.panel_IMG_main_mid),
                findViewById(R.id.panel_IMG_main_right),
        };

        path = new ImageView[][] {
                {findViewById(R.id.panel_IMG_00), findViewById(R.id.panel_IMG_01), findViewById(R.id.panel_IMG_02)},
                {findViewById(R.id.panel_IMG_10), findViewById(R.id.panel_IMG_11), findViewById(R.id.panel_IMG_12)},
                {findViewById(R.id.panel_IMG_20), findViewById(R.id.panel_IMG_21), findViewById(R.id.panel_IMG_22)},
                {findViewById(R.id.panel_IMG_30), findViewById(R.id.panel_IMG_31), findViewById(R.id.panel_IMG_32)},
                {findViewById(R.id.panel_IMG_40), findViewById(R.id.panel_IMG_41), findViewById(R.id.panel_IMG_42)}
        };
            vals = new int[path.length][path[0].length];
    }
}