package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final int MAX_LIVES = 4;
    private final int NUM_OF_COLUMNS = 3;
    private final int NUM_OF_OBSTACLE_TYPES = 2;


    private ImageView[][] path;
    private ImageButton panel_BTN_Right;
    private ImageButton panel_BTN_Left;
    private ImageView[] panel_IMG_engines;
    private ImageView[] panel_IMG_airplane;

    private int current = 1;
    private int i = 0;
    private int lives = MAX_LIVES;

    private Timer timer = new Timer();
    int randChoose;
    int columnChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initButtons();

    }

    @Override
    protected void onStart() {
        super.onStart();
        startUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopUI();
    }

    private void stopUI() {
        timer.cancel();
    }

    private void startUI() {
        timer = new Timer();

        randChoose = (int) Math.floor(Math.random()*NUM_OF_OBSTACLE_TYPES);
        columnChoose = (int) Math.floor(Math.random()*NUM_OF_COLUMNS);

        setImage(randChoose, columnChoose);

        path[0][columnChoose].setVisibility(View.VISIBLE);

        // Timer Set and start running
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        update_UI_logic(columnChoose);

                    }
                });
            }
        }, 0, 500);

    }

    private void update_UI_logic(int columnC) {

        if(i<4){
            path[i][columnC].setVisibility(View.INVISIBLE);
            path[i+1][columnC].setVisibility(View.VISIBLE);
            i++;
        } else {
            if(current==columnC){
                vibrate(3000);
                lives--;
                panel_IMG_engines[lives].setVisibility(View.INVISIBLE);
                if(lives==0){
                    gameOver();
                }
            }

            path[4][columnC].setVisibility(View.INVISIBLE);
            columnC = (int) Math.floor(Math.random()*NUM_OF_COLUMNS);
            randChoose = (int) Math.floor(Math.random()*NUM_OF_OBSTACLE_TYPES);
            setImage(randChoose, columnC);
            i=0;
            path[0][columnC].setVisibility(View.VISIBLE);
        }

    }

        private void gameOver() {
            timer.cancel();

            playSound(R.raw.audio_mayday);

            //panel_IMG_airplane[current].setImageResource(R.drawable.ic_crush);
            vibrate(2000);
            Toast.makeText(getApplicationContext(),"Game Over",Toast.LENGTH_LONG).show();
            finish();
        }



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


    private void playSound(int audio_mayday) {

        final MediaPlayer mp = MediaPlayer.create(this, audio_mayday);
        mp.start();

    }

    private void setImage(int imageNum, int columnChoose) {
        if(imageNum == 0)
            path[0][columnChoose].setImageResource(R.drawable.ic_seagull);
        else if (imageNum == 1)
            path[0][columnChoose].setImageResource(R.drawable.ic_engine);
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

    }
}