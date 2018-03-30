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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
        checkLogin();

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

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (int i = 0; i < 15; i++) { // todo 15 magic, get map size
                    // No resident.
                    if (!dataSnapshot.getValue(User.class).getWindows().get("w_" + i)) {
                        windows.get("w_" + i).setBackgroundColor(Color.parseColor("#0C2F41"));
                    } else {
                        windows.get("w_" + i).setBackgroundColor(Color.parseColor("#F9DFBE"));
                    }
                }
                coins.setText(String.valueOf(dataSnapshot.getValue(User.class).getPowerRemaining()));

                int powerRemaining = dataSnapshot.getValue(User.class).getPowerRemaining();
                long lastStudyCheck = dataSnapshot.getValue(User.class).getLastStudyCheck();
                if ((System.currentTimeMillis() - lastStudyCheck) > FIVE_MINUTES) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(((int) powerRemaining - ((int) (System.currentTimeMillis() - lastStudyCheck) / FIVE_MINUTES))); // todo magic numberss!!!!!!
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastStudyCheck").setValue(System.currentTimeMillis());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(BuildingActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((ProductivityApp) BuildingActivity.this.getApplication()).setUser(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(BuildingActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });

        powerUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Value is: " + ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().toString());

                if (!studying) {
                    studying = true;
                    powerUp.setText("Stop");
                    bolt.setVisibility(View.VISIBLE);

                    final int time = 10; // todo magic number
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
                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastStudyCheck").setValue(System.currentTimeMillis());
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
                                int windowCheck = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() / (powerPerHour * 10);
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
                                            windows.get("w_" + n).setBackgroundColor(Color.parseColor("#0C2F41"));

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

        if (mAuth.getCurrentUser() != null) {
            int powerRemaining = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining();
            long lastStudyCheck = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getLastStudyCheck();
            if ((System.currentTimeMillis() - lastStudyCheck) > FIVE_MINUTES) {
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(((int) powerRemaining - ((int) (System.currentTimeMillis() - lastStudyCheck) / FIVE_MINUTES))); // todo magic numberss!!!!!!
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastStudyCheck").setValue(System.currentTimeMillis());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "On start! BuildingActivity");
        mAuth.addAuthStateListener(mAuthListener);


    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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

