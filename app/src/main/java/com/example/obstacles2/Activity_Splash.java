package com.example.obstacles2;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Activity_Splash extends AppCompatActivity {
    private static final String TAG = Activity_Splash.class.getSimpleName();

    final int ANIM_DURATION = 4400;

    private ImageView splash_IMG_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViews();

        ImageView panel_IMG_background = findViewById(R.id.splash_IMG_background);
        Glide
                .with(this)
                .load(R.drawable.skybackground)
                .centerCrop()
                .into(panel_IMG_background);

        splash_IMG_logo.setVisibility(View.INVISIBLE);

        showViewSlideDown(splash_IMG_logo);
    }

    public void showViewSlideDown(final View v) {
        v.setVisibility(View.VISIBLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        v.setY(-height / 2);
        v.setScaleY(0.0f);
        v.setScaleX(0.0f);
        v.animate()
                .scaleY(1.0f)
                .scaleX(1.0f)
                .translationY(0)
                .setDuration(ANIM_DURATION)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animationDone();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    private void animationDone() {
        openHomeActivity();
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, Activity_Menu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
    }
}