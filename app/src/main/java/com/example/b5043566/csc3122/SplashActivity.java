package com.example.b5043566.csc3122;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.ProgressBar;

import static android.R.transition.fade;
import static com.example.b5043566.csc3122.R.drawable.splash;


public class SplashActivity extends AppCompatActivity {

    protected static final String TAG = "SplashActivity";
    public final static int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        final Runnable runnable;

        final Handler handler = new Handler();
        handler.postDelayed(runnable = new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

                    progressBar.setProgress(progressBar.getProgress() + 1);
                handler.postDelayed(this, SPLASH_DISPLAY_LENGTH / progressBar.getMax());
            }
        }, SPLASH_DISPLAY_LENGTH / progressBar.getMax());

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                for(int i = 0; i < progressBar.getMax(); i++){
                    progressBar.setProgress(i);
                }
                Intent intent = new Intent(SplashActivity.this, BuildingActivity.class);
                SplashActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                SplashActivity.this.finish();
                handler.removeCallbacks(runnable);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
