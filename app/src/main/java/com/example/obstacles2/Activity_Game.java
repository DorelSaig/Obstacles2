package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_Game extends AppCompatActivity {

    private final int MAX_LIVES = 4;
    private final int NUM_OF_COLUMNS = 5;
    private final int NUM_OF_OBSTACLE_TYPES = 6;
    public static final String SENSOR_MODE = "SENSOR_MODE";

    private boolean sensorMode = false;

    private ImageView[][] path;
    private int[][] vals;

    private ImageButton panel_BTN_Right;
    private ImageButton panel_BTN_Left;
    private ImageButton panel_BTN_volume;

    private TextView panel_TXT_acc;

    private TextView panel_TXT_time;
    private int count = 0;

    private ImageView[] panel_IMG_engines;
    private ImageView[] panel_IMG_airplane;

    private boolean volume = true;

    private int current = 2;
    private int speed = 300;
    private int i = 0;
    private int lives = MAX_LIVES;

    private Timer display_Real_Timer;
    private Timer game_Speed_Timer = new Timer();
    private int obs_Choose;
    private int column_Choose;

    private Sensors_Utils sensors_utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Screen_Utils.hideSystemUI(this);

        //------- Set Background Image Using Glide --------
        ImageView panel_IMG_background = findViewById(R.id.panel_IMG_background);
        Glide
                .with(this)
                .load(R.drawable.skybackground)
                .centerCrop()
                .into(panel_IMG_background);

        //------- Init Panel --------
        sensorMode = getIntent().getExtras().getBoolean(SENSOR_MODE);
        findViews();
        initButtons();

        //------- Init Vals in Matrix To Zero -------
        for (int i = 0; i < vals.length; i++) {
            for (int j = 0; j < vals[i].length; j++) {
                vals[i][j] = 0;
            }
        }

        //------- Check Sensor Mode (?) -------
        if (sensorMode) {
            sensors_utils = new Sensors_Utils(this);
            sensors_utils.setCallBackSensor(callBackSensor);

            panel_BTN_Left.setVisibility(View.INVISIBLE);
            panel_BTN_Right.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //start service and play music
        startService(new Intent(Activity_Game.this, SoundService.class));
        startTimer();
        startUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorMode) {
            sensors_utils.resumed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorMode) {
            sensors_utils.paused();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //------- Stop background  Music--------
        stopService(new Intent(Activity_Game.this, SoundService.class));

        stopUI();
    }



  /*                              Logics Center!!!                                */

    //------- Play Time Counter For Future Score Board --------
    private void startTimer() {
        display_Real_Timer = new Timer();

        // Timer Set and start running
        display_Real_Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        panel_TXT_time.setText("" + count);
                        count++;

                        //------- Every 10 Seconds Game Speed Increases by 50 millis if speed is more than 200 millis, else by 1 milli. --------
                        if (count % 10 == 0) {
                            if (speed > 200) {
                                speed -= 50;
                            } else {
                                speed -= 5;
                            }
                            game_Speed_Timer.cancel();
                            startUI();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    //------- Responsible for the refresh of the UI every 'speed' milliseconds --------
    private void startUI() {
        game_Speed_Timer = new Timer();

        // Timer Set and start running
        game_Speed_Timer.schedule(new TimerTask() {
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

    //------- Player Manual Movement Logic --------
    private void movePlayer(boolean direction) {

        if (direction && current <= 3) {
            panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
            current++;
            panel_IMG_airplane[current].setVisibility(View.VISIBLE);
        } else if (!direction && current >= 1) {
            panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
            current--;
            panel_IMG_airplane[current].setVisibility(View.VISIBLE);
        }
    }

    //------- Player Sensor Movement Logic Using CallBack --------
    private CallBack_Sensor callBackSensor = new CallBack_Sensor() {
        @Override
        public void move_sensor_mode(float x) {
            if (x > 4) {
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current = 0;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            } else if (x < 4 && x > 2) {
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current = 1;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            } else if (x < 2 && x > -2) {
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current = 2;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            } else if (x < -2 && x > -4) {
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current = 3;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            } else {
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current = 4;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            }
        }
    };

    //------- Game UI Logic - First Working on int Matrix --------
    private void logic() {
        obs_Choose = (int) Math.floor(Math.random() * NUM_OF_OBSTACLE_TYPES);
        column_Choose = (int) Math.floor(Math.random() * NUM_OF_COLUMNS);

        for (int i = vals.length - 1; i > 0; i--) {
            for (int j = 0; j < vals[0].length; j++) {
                vals[i][j] = vals[i - 1][j];
            }
        }

        for (int i = 0; i < vals[0].length; i++) {
            vals[0][i] = 0;
        }

        vals[0][column_Choose] = obs_Choose;

        updateUILogic();
    }

    //------- Check Collision Logic And Game State --------
    private void collisionCheck() {
        if (vals[4][current] == 1 || vals[4][current] == 3 || vals[4][current] == 4 ||vals[4][current] == 2) {

            Game_Utils.getInstance().vibrate(300);

            if(vals[4][current] == 4){
                Game_Utils.getInstance().playSound(R.raw.sound_metal_hit);
            }else {
                Game_Utils.getInstance().playSound(R.raw.sound_hit);
            }

            Game_Utils.getInstance().myToastSHORT("Engine " + lives + " Down ", getApplicationContext());

            lives--;

            panel_IMG_engines[lives].setVisibility(View.INVISIBLE);

            if (lives <= 0) {
                gameOver();
            }
        } else if (vals[4][current] == 5) {
            if (lives < 4) {

                Game_Utils.getInstance().playSound(R.raw.sound_fix);

                Game_Utils.getInstance().vibrate(100);

                panel_IMG_engines[lives].setVisibility(View.VISIBLE);

                lives++;

            }
        }
    }

    private boolean bonus = false;
    //------- Game UI Logic - Second Working on the ImageView Matrix --------
    private void updateUILogic() {

        for (int i = 0; i < path.length; i++) {
            for (int j = 0; j < path[i].length; j++) {
                ImageView im = path[i][j];
                if (vals[i][j] == 0) {
                    im.setVisibility(View.INVISIBLE);
                    //To make the random more Various I made the chance to get life will be 1/4.
                } else if (vals[i][j] == 1 || vals[i][j] == 3) {
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(R.drawable.ic_seagull);
                } else if(vals[i][j] == 4){
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(R.drawable.ic_drone);
                } else if(vals[i][j] == 2){
                    im.setVisibility(View.VISIBLE);
                    im.setImageResource(R.drawable.ic_skydiver);
                } else if (vals[i][j] == 5) {
                    if(true) {
                        im.setVisibility(View.VISIBLE);
                        im.setImageResource(R.drawable.ic_service);
                        bonus = false;
                    }else {
                        bonus = true;
                    }

                }
            }
        }
    }

    //------- Stop UI movement and time count --------
    private void stopUI() {
        game_Speed_Timer.cancel();
        display_Real_Timer.cancel();
    }

    //------- Game Over Tasks --------
    private void gameOver() {
        game_Speed_Timer.cancel();
        display_Real_Timer.cancel();

        Intent gameOverIntent = new Intent(Activity_Game.this, Activity_Game_Over.class);
        gameOverIntent.putExtra("Score", count);
        gameOverIntent.putExtra(SENSOR_MODE, sensorMode);
        startActivity(gameOverIntent);
        this.finish();

    }



    /*                             initialize Center                                */

    //------- Initialize Buttons --------
    private void initButtons() {

        /* Left Button Init */
        panel_BTN_Left.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game_Utils.getInstance().vibrate(100);
                //Right = True Left = False
                movePlayer(false);
            }
        }));

        /* Right Button Init */
        panel_BTN_Right.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game_Utils.getInstance().vibrate(100);
                movePlayer(true);
            }
        }));

        /* Music Control Button Init */
        panel_BTN_volume.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (volume) {
                    /* Mute */
                    stopService(new Intent(Activity_Game.this, SoundService.class));
                    panel_BTN_volume.setBackgroundResource(R.drawable.ic_volume_off);
                    volume = false;
                } else {
                    /* UnMute */
                    startService(new Intent(Activity_Game.this, SoundService.class));
                    panel_BTN_volume.setBackgroundResource(R.drawable.ic_volume_on);
                    volume = true;
                }
            }
        }));
    }

    //------- Finding all views By ID --------
    private void findViews() {

        panel_BTN_Right = findViewById(R.id.panel_BTN_right);

        panel_BTN_Left = findViewById(R.id.panel_BTN_left);

        panel_BTN_volume = findViewById(R.id.panel_BTN_volume);

        panel_TXT_time = findViewById(R.id.panel_TXT_time);

        panel_IMG_engines = new ImageView[]{
                findViewById(R.id.engine1),
                findViewById(R.id.engine2),
                findViewById(R.id.engine3),
                findViewById(R.id.engine4)
        };

        panel_IMG_airplane = new ImageView[]{
                findViewById(R.id.panel_IMG_main_left),
                findViewById(R.id.panel_IMG_main_mid_left),
                findViewById(R.id.panel_IMG_main_mid),
                findViewById(R.id.panel_IMG_main_mid_right),
                findViewById(R.id.panel_IMG_main_right),
        };

        path = new ImageView[][]{
                {findViewById(R.id.panel_IMG_00), findViewById(R.id.panel_IMG_01), findViewById(R.id.panel_IMG_02), findViewById(R.id.panel_IMG_03), findViewById(R.id.panel_IMG_04)},
                {findViewById(R.id.panel_IMG_10), findViewById(R.id.panel_IMG_11), findViewById(R.id.panel_IMG_12), findViewById(R.id.panel_IMG_13), findViewById(R.id.panel_IMG_14)},
                {findViewById(R.id.panel_IMG_20), findViewById(R.id.panel_IMG_21), findViewById(R.id.panel_IMG_22), findViewById(R.id.panel_IMG_23), findViewById(R.id.panel_IMG_24)},
                {findViewById(R.id.panel_IMG_30), findViewById(R.id.panel_IMG_31), findViewById(R.id.panel_IMG_32), findViewById(R.id.panel_IMG_33), findViewById(R.id.panel_IMG_34)},
                {findViewById(R.id.panel_IMG_40), findViewById(R.id.panel_IMG_41), findViewById(R.id.panel_IMG_42), findViewById(R.id.panel_IMG_43), findViewById(R.id.panel_IMG_44)}
        };
        vals = new int[path.length][path[0].length];
    }
}