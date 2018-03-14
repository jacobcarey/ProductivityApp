package com.example.b5043566.csc3122;

import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BuildingActivity extends MainActivity {

    int i = 0; // // TODO: Remove
    private Handler handler = new Handler();
    private Runnable runnable;
    private TextView coins;
    private ImageView bolt;
    private ProgressBar progressBar;
    private Button powerUp;
    boolean studying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check login.
        checkLogin();
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_building, contentFrameLayout);

        // Adds the menu button and applies a fix.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_building);
        if (menu.getParent() != null)
            ((ViewGroup) menu.getParent()).removeView(menu);
        constraintLayout.addView(menu);

        // Page elements.
        powerUp = (Button) findViewById(R.id.powerUp);
        coins = (TextView) findViewById(R.id.coins);
        bolt = (ImageView) findViewById(R.id.bolt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bolt.setVisibility(View.GONE);

        powerUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Value is: " + user.toString());
                coins.setText(String.valueOf(user.powerRemaining));
                if (!studying) {
                    studying = true;
                    powerUp.setText("Stop");
                    bolt.setVisibility(View.VISIBLE);

                    final int time = 1000; // todo magic number
                    handler = new Handler();

                    handler.postDelayed(runnable = new Runnable() {
                        public void run() {
//                            coins.setText(String.valueOf(i++));
                            progressBar.setProgress(i);
                            handler.postDelayed(this, time);
                        }
                    }, time);
                } else {
                    studying = false;
                    bolt.setVisibility(View.GONE);
                    powerUp.setText("Study");
                    handler.removeCallbacks(runnable);
                    coins.setText(String.valueOf(0));
                    i = 0;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
//        coins.setText(String.valueOf(0));
//        i = 0; // TODO Remove and add implemetation.
        bolt.setVisibility(View.GONE);
    }

//    protected void onStart();

//    protected void onRestart();

//    protected void onResume();

//    protected void onStop();

//    protected void onDestroy();

}

