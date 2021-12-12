package com.example.obstacles2;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

public class Game_Utils {
    private static Game_Utils instance;
    private static Context appContext;

    public static Game_Utils getInstance() {
        return instance;
    }

    private Game_Utils(Context context) {
        appContext = context;
    }

    public static Game_Utils initHelper(Context context) {
        if (instance == null)
            instance = new Game_Utils(context);
        return instance;
    }

    //--------- Vibrate Function ----------
    public void vibrate(int millisecond) {
        Vibrator v = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(millisecond, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(millisecond);
        }
    }

    //------- Sound Player --------
    public void playSound(int audio) {

        final MediaPlayer mp = MediaPlayer.create(appContext, audio);
        mp.start();
        mp.setOnCompletionListener(MediaPlayer::pause);

    }

    public void myToastSHORT(String str, Context context){
        final Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.show();

        //------- Due to const time of toast (Short = 2.5s, Long= 3.5s) to make it shorter I Used Handler That Cancel the Toast after 500 millis. --------
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    public void my_Toast_LONG(String str, Context context){
        final Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.show();
    }
}
