package com.example.obstacles2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

    public class SoundService extends Service {
        MediaPlayer player;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        public void onCreate() {
            player = MediaPlayer.create(this, R.raw.little_airplane_song); //select music file
            player.setLooping(true); //set looping

        }

        public int onStartCommand(Intent intent, int flags, int startId) {
            player.start();
            return Service.START_NOT_STICKY;
        }

        public void onStop(){
            player.stop();
            player.release();
        }

        public void onPause(){
            player.stop();
            player.release();
        }

        public void onDestroy() {
            player.stop();
            player.release();
            stopSelf();
            super.onDestroy();
        }

    }

