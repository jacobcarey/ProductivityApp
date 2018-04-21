package com.example.b5043566.csc3122;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Window;

import static android.R.transition.fade;
import static com.example.b5043566.csc3122.R.drawable.splash;


public class SplashActivity extends AppCompatActivity {

    protected static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition
            // After 5 seconds redirect to another intent
            Fade fade = new Fade();
            fade.setDuration(5000);
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
        setContentView(R.layout.activity_splash);

        final Intent intent = new Intent(this, BuildingActivity.class);
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        Thread splash = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(3*1000);
                    startActivity(intent, options.toBundle());
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
    splash.start();


    }
}
