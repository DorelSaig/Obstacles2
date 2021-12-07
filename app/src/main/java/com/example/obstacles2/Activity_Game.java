package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class Activity_Game extends AppCompatActivity {

    private final int MAX_LIVES = 4;
    private final int NUM_OF_COLUMNS = 5;
    private final int NUM_OF_OBSTACLE_TYPES = 3;
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
    private int speed = 500;
    private int i = 0;
    private int lives = MAX_LIVES;

    private Timer timer_display;
    private Timer timer = new Timer();
    int obsChoose;
    int columnChoose;

    private SensorManager sensorManager;
    private Sensor accSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Background Image Using Glide
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

        if(sensorMode) {
            initSensor();
            panel_BTN_Left.setVisibility(View.INVISIBLE);
            panel_BTN_Right.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < vals.length; i++) {
            for (int j = 0; j < vals[i].length; j++) {
                vals[i][j] = 0;
            }
        }

    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private SensorEventListener accSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(event.values[0]>4 ){
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current=0;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            } else if (event.values[0]<4 && event.values[0]>2){
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current=1;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            }else if (event.values[0]<2 && event.values[0]>-2) {
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current = 2;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            } else if (event.values[0]<-2 && event.values[0]>-4){
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current=3;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            }else{
                panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
                current=4;
                panel_IMG_airplane[current].setVisibility(View.VISIBLE);
            }

            DecimalFormat df = new DecimalFormat("##.##");
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            panel_TXT_acc.setText(
                    sensorMode + "\n" + df.format(x) + "\n" + df.format(y) + "\n" + df.format(z)
            );
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorMode) {
            sensorManager.registerListener(accSensorEventListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStart() {

        //start service and play music
        startService(new Intent(Activity_Game.this, SoundService.class));

        super.onStart();

            startTimer();
            startUI();
    }

    //------- Play Time Counter For Future Score Board --------
    private void startTimer() {
        timer_display = new Timer();

        // Timer Set and start running
        timer_display.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        panel_TXT_time.setText(""+count);
                        count++;

                        //------- Every 10 Seconds Game Speed Increases by 30 millis if speed is more than 200 millis, else by 1 milli. --------
                        if(count%10 == 0) {
                            if(speed>200) {
                                speed -= 50;
                            } else {
                                speed -=1;
                            }
                            timer.cancel();
                            startUI();
                        }

                    }
                });
            }
        }, 0, 1000);
    }

    //------- Responsible for the refresh of the UI every 'speed' milliseconds --------
    private void startUI() {
        timer = new Timer();

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
    protected void onPause() {
        super.onPause();
        if(sensorMode) {
            sensorManager.unregisterListener(accSensorEventListener);
        }
    }

    @Override
    protected void onStop() {
        //------- Stop background  Music--------
        stopService(new Intent(Activity_Game.this, SoundService.class));

        super.onStop();


        stopUI();
    }

    //------- Stop UI movement and time count --------
    private void stopUI() {
        timer.cancel();
        timer_display.cancel();
    }

    //------- Check Collision Logic And Game State --------
    private void collisionCheck() {
        if (vals[4][current] == 1) {

            vibrate(300);

            playSound(R.raw.sound_hit);

            final Toast toast = Toast.makeText(getApplicationContext(),"Engine "+lives+ " Down ",Toast.LENGTH_SHORT);
            toast.show();
            //------- Due to const time of toast (Short = 2.5s, Long= 3.5s) to make it shorter I Used Handler That Cancel the Toast after 500 millis. --------
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

    //------- Game UI Logic - First Working on int Matrix --------
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

    //------- Game UI Logic - Second Working on the ImageView Matrix --------
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

    //------- Game Over Tasks --------
        private void gameOver() {
            timer.cancel();
            timer_display.cancel();

            Intent gameOverIntent = new Intent(Activity_Game.this, Activity_Game_Over.class);
            gameOverIntent.putExtra("Score", count);
            startActivity(gameOverIntent);
            this.finish();

        }

    //TODO make util function
    //------- Vibrate Function --------
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

    //TODO make util function
    //------- Sound Player --------
    private void playSound(int audio_mayday) {

        final MediaPlayer mp = MediaPlayer.create(this, audio_mayday);
        mp.start();

    }

    //------- Initialize Buttons --------
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
                    stopService(new Intent(Activity_Game.this, SoundService.class));
                    panel_BTN_volume.setBackgroundResource(R.drawable.ic_volume_off);
                    volume = false;
                } else {
                    startService(new Intent(Activity_Game.this, SoundService.class));
                    panel_BTN_volume.setBackgroundResource(R.drawable.ic_volume_on);
                    volume = true;
                }
            }
        }));



    }

    //------- Player movement logic --------
    private void move(boolean direction) {
        if(direction && current<=3){
            panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
            current++;
            panel_IMG_airplane[current].setVisibility(View.VISIBLE);
        } else if (!direction && current>=1){
            panel_IMG_airplane[current].setVisibility(View.INVISIBLE);
            current--;
            panel_IMG_airplane[current].setVisibility(View.VISIBLE);
        }
    }

    //------- Finding all views By ID --------
    private void findViews() {

        panel_BTN_Right = findViewById(R.id.panel_BTN_right);

        panel_BTN_Left = findViewById(R.id.panel_BTN_left);

        panel_BTN_volume = findViewById(R.id.panel_BTN_volume);

        panel_TXT_time = findViewById(R.id.panel_TXT_time);

        panel_TXT_acc = findViewById(R.id.panel_TXT_acc);

        panel_IMG_engines = new ImageView[] {
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

        path = new ImageView[][] {
                {findViewById(R.id.panel_IMG_00), findViewById(R.id.panel_IMG_01), findViewById(R.id.panel_IMG_02), findViewById(R.id.panel_IMG_03), findViewById(R.id.panel_IMG_04)},
                {findViewById(R.id.panel_IMG_10), findViewById(R.id.panel_IMG_11), findViewById(R.id.panel_IMG_12), findViewById(R.id.panel_IMG_13), findViewById(R.id.panel_IMG_14)},
                {findViewById(R.id.panel_IMG_20), findViewById(R.id.panel_IMG_21), findViewById(R.id.panel_IMG_22), findViewById(R.id.panel_IMG_23), findViewById(R.id.panel_IMG_24)},
                {findViewById(R.id.panel_IMG_30), findViewById(R.id.panel_IMG_31), findViewById(R.id.panel_IMG_32), findViewById(R.id.panel_IMG_33), findViewById(R.id.panel_IMG_34)},
                {findViewById(R.id.panel_IMG_40), findViewById(R.id.panel_IMG_41), findViewById(R.id.panel_IMG_42), findViewById(R.id.panel_IMG_43), findViewById(R.id.panel_IMG_44)}
        };
            vals = new int[path.length][path[0].length];
    }
}