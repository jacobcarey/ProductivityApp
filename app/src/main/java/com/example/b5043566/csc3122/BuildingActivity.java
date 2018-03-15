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

    int progress = 0; // // TODO: Remove
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

        coins.setText(String.valueOf(((ProductivityApp) BuildingActivity.this.getApplication()).getPowerRemaining()));



        powerUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Value is: " +  ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().toString());

                if (!studying) {
                    studying = true;
                    powerUp.setText("Stop");
                    bolt.setVisibility(View.VISIBLE);

                    final int time = 25; // todo magic number
                    handler = new Handler();

                    handler.postDelayed(runnable = new Runnable() {
                        public void run() {
                            progressBar.setProgress(progress);
                            handler.postDelayed(this, time);
                            progress++;
                            if(progress == 100) { // todo magic number
                                studying = false;
                                bolt.setVisibility(View.GONE);
                                powerUp.setText("Study");
                                int powerPerHour = ((ProductivityApp) BuildingActivity.this.getApplication()).getPowerPerHour();
                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() + (powerPerHour)); // todo maiic number
                                coins.setText(String.valueOf(((ProductivityApp) BuildingActivity.this.getApplication()).getPowerRemaining() + (powerPerHour)));
                                handler.removeCallbacks(runnable);
                                progress = 0;
                            }
                        }
                    }, time);
                } else {
                    studying = false;
                    bolt.setVisibility(View.GONE);
                    powerUp.setText("Study");
                    progress = 0;
                    handler.removeCallbacks(runnable);

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        studying = false;
        bolt.setVisibility(View.GONE);
        powerUp.setText("Study");
        progress = 0;
        handler.removeCallbacks(runnable);

    }

//    protected void onStart();

//    protected void onRestart();

//    protected void onResume();

//    protected void onStop();

//    protected void onDestroy();

}

