package com.example.obstacles2;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatActivity;

public class Sensors_Utils {

    private SensorManager sensorManager;
    private Sensor accSensor;
    private CallBack_Sensor callBackSensor;
    private AppCompatActivity activity;

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setCallBackSensor(CallBack_Sensor callBackSensor) {
        this.callBackSensor = callBackSensor;
    }

    public Sensors_Utils(Activity_Game activity_game) {
        setActivity(activity_game);
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    private SensorEventListener accSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            callBackSensor.move_sensor_mode(event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void resumed(){
        sensorManager.registerListener(accSensorEventListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void paused() {
        sensorManager.unregisterListener(accSensorEventListener);
    }
}
