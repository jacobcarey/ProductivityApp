package com.example.b5043566.csc3122;

import android.content.Intent;
import android.graphics.Color;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BuildingActivity extends MainActivity {

    int progress = 0; // // TODO: Remove
    private Handler handler = new Handler();
    private Runnable runnable;
    private TextView coins;
    private ImageView bolt;
    private ProgressBar progressBar;
    private Button powerUp;
    boolean studying = false;
    private Map<String, ImageView> windows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check login.

        Log.d(TAG, "Value is: " + ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().toString());
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_building, contentFrameLayout);

        // Adds the menu button and applies a fix.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_building);
        if (menu.getParent() != null)
            ((ViewGroup) menu.getParent()).removeView(menu);
        constraintLayout.addView(menu);

        // Page elements.
        powerUp = (Button) findViewById(R.id.powerUp);
        bolt = (ImageView) findViewById(R.id.bolt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bolt.setVisibility(View.GONE);
        coins = (TextView) findViewById(R.id.coins);
        windows = new HashMap<String, ImageView>();
        for (int i = 0; i < 15; i++) { // todo 15 magic, get map size
            int id = (int) R.id.window1 + i;
            windows.put("w_" + i, (ImageView) findViewById(id));
        }

        Log.d(TAG, "Value is: " + windows.toString());

        if(checkLogin()){

        }

        powerUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Value is: " + ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().toString());

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
                            if (progress == 100) { // todo magic number
                                studying = false;
                                bolt.setVisibility(View.GONE);
                                powerUp.setText("Study");

                                // Increase power.
                                int powerPerHour = ((ProductivityApp) BuildingActivity.this.getApplication()).getPowerPerHour();

                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() + (powerPerHour)); // todo maiic number
                                coins.setText(String.valueOf(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() + (powerPerHour)));

                                // Check windows.
                                int currentWindows = 0;
                                for (int i = 0; i < 15; i++) { // todo 15 magic, get map size
                                    boolean window = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + i);
                                    if (window) {
                                        currentWindows++;
                                    }
                                }
                                // todo magic numbers
                                int windowCheck = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() / (powerPerHour * 8);
                                Log.d(TAG, "Window Check: " + windowCheck);
                                Log.d(TAG, "Current Windows: " + currentWindows);
                                if (currentWindows == windowCheck) {
                                    // All if fine
                                } else if (windowCheck > currentWindows) {
                                    boolean newWindow = true;
                                    while (newWindow) {
                                        Log.d(TAG, "New Window Loop");
                                        Random rand = new Random();
                                        int n = rand.nextInt(15); // Gives n such that 0 <= n < 11
                                        if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + n)) {
                                            // Already set
                                        } else {
                                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("windows").child("w_" + n).setValue(true);
                                            newWindow = false;
                                            windows.get("w_" + n).setBackgroundColor(Color.parseColor("#F9DFBE"));

                                        }

                                    }
                                } else if (windowCheck < currentWindows) {
                                    boolean removeWindow = true;
                                    while (removeWindow) {
                                        Log.d(TAG, "Remove Window Loop");
                                        Random rand = new Random();
                                        int n = rand.nextInt(15); // Gives n such that 0 <= n < 11
                                        if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + n)) {
                                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("windows").child("w_" + n).setValue(false);
                                            removeWindow = false;
                                            windows.get("w_" + n).setBackgroundColor(Color.parseColor("#0B2C3D"));

                                        }

                                    }
                                }




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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "On start!");
        mAuth.addAuthStateListener(mAuthListener);
        for (int i = 0; i < 15; i++) { // todo 15 magic, get map size
            // No resident.
            if (!((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + i)) {
                windows.get("w_" + i).setBackgroundColor(Color.parseColor("#0B2C3D"));
            }else{
                windows.get("w_" + i).setBackgroundColor(Color.parseColor("#F9DFBE"));
            }
        }
        coins.setText(String.valueOf(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining()));
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

//    protected void onStart();

//    protected void onRestart();

    @Override
    protected void onResume() {
        super.onResume();
    }

//    protected void onStop();

//    protected void onDestroy();

}

