package com.example.b5043566.csc3122;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BuildingActivity extends MainActivity {

    int progress = 0; // // TODO: Remove
    private Handler handler = new Handler();
    private Runnable runnable;
    private TextView powerTotal;
    private ImageView bolt;
    private ImageView moon;
    private ProgressBar progressBar;
    private Button powerUp;
    boolean studying = false;
    private Map<String, ImageView> windows;
    private final int time = 10; // todo magic number
//    private final int time = 30000; // half a minute


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check login.
        if (checkLogin()) {
            Log.d(TAG, "Value is: " + ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().toString());
            FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
            getLayoutInflater().inflate(R.layout.activity_building, contentFrameLayout);

            // Adds the menu button and applies a fix.
            final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_building);
            if (menu.getParent() != null){
                ((ViewGroup) menu.getParent()).removeView(menu);
            }
            constraintLayout.addView(menu);
            if(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getNightMode()){
                menu.setColorFilter(getResources().getColor(R.color.menuNight));
            }else{
                menu.setColorFilter(getResources().getColor(R.color.menuDay));
            }

            // Page elements.
            powerUp = (Button) findViewById(R.id.powerUp);
            bolt = (ImageView) findViewById(R.id.bolt);
            bolt.setVisibility(View.GONE);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            powerTotal = (TextView) findViewById(R.id.coins);
            moon = (ImageView) findViewById(R.id.moon);
            windows = new HashMap<String, ImageView>();

            for (int i = 0; i < TOTAL_WINDOWS_V1; i++) {
                int id = (int) R.id.window1 + i;
                windows.put("w_" + i, (ImageView) findViewById(id));
            }

            Log.d(TAG, "Value is: " + windows.toString());

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


                        handler = new Handler();

                        handler.postDelayed(runnable = new Runnable() {
                            public void run() {
                                // Progress bar, optional.
                                if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getProgress()) {
                                    progressBar.setProgress(progress);
                                }
                                int powerPerHour = ((ProductivityApp)BuildingActivity.this.getApplication()).getPowerPerHour();
                                handler.postDelayed(this, time);
                                progress++;
                                if (progress == 100) { // todo magic number
                                    studying = false;
                                    bolt.setVisibility(View.GONE);
                                    powerUp.setText("Study");
                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastStudyCheck").setValue(System.currentTimeMillis());
                                    // Increase power.
//
                                    Log.d(TAG, "Power per hour: " + powerPerHour);
                                    int powerRemaining = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() + (powerPerHour);
                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(powerRemaining);
                                    powerTotal.setText(Integer.toString(powerRemaining));
                                    // Check windows.
                                    int currentWindows = 0;
                                    for (int i = 0; i < TOTAL_WINDOWS_V1; i++) {
                                        boolean window = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + i);
                                        if (window) {
                                            currentWindows++;
                                        }
                                    }
                                    int windowCheck = 0;
                                    if(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() != 0){
                                        // todo magic numbers
                                        windowCheck = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() / (powerPerHour * 10);
                                    }

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
                                            int n = rand.nextInt(TOTAL_WINDOWS_V1); // Gives n such that 0 <= n < 15
                                            if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + n)) {
                                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("windows").child("w_" + n).setValue(false);
                                                removeWindow = false;
                                                windows.get("w_" + n).setBackgroundColor(Color.parseColor("#0C2F41"));

                                            }

                                        }
                                    }

                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("dailyHours").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getDailyHours() + 1);
                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("weeklyHours").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWeeklyHours() + 1);
                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("monthlyHours").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getMonthlyHours() + 1);
                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("overallHours").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getOverallHours() + 1);

                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("residents").setValue(windowCheck);

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
            checkPowerRemaining(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining(), ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getLastStudyCheck());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "On start! BuildingActivity");
        mAuth.addAuthStateListener(mAuthListener);

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((ProductivityApp) BuildingActivity.this.getApplication()).setUser(dataSnapshot.getValue(User.class));
                for (int i = 0; i < TOTAL_WINDOWS_V1; i++) {
                    // No resident.
                    if (!dataSnapshot.getValue(User.class).getWindows().get("w_" + i)) {
                        windows.get("w_" + i).setBackgroundColor(Color.parseColor("#0C2F41"));
                    } else {
                        windows.get("w_" + i).setBackgroundColor(Color.parseColor("#F9DFBE"));
                    }
                }

                ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_building);

                if(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getNightMode()){
                    constraintLayout.setBackgroundColor(getResources().getColor(R.color.skyNight));
                    moon.setBackgroundColor(getResources().getColor(R.color.skyNight));
                    moon.getDrawable().setColorFilter(new
                            PorterDuffColorFilter(getResources().getColor(R.color.moon), PorterDuff.Mode.MULTIPLY));

                    bolt.setImageResource(R.drawable.bolt);
                    bolt.setBackgroundColor(getResources().getColor(R.color.skyNight));
                }else{
                    constraintLayout.setBackgroundColor(getResources().getColor(R.color.skyDay));
                    moon.setBackgroundColor(getResources().getColor(R.color.skyDay));
                    moon.getDrawable().setColorFilter(new
                            PorterDuffColorFilter(getResources().getColor(R.color.sun), PorterDuff.Mode.MULTIPLY));

                    bolt.setImageResource(R.drawable.bolt_day);
                    bolt.setBackgroundColor(getResources().getColor(R.color.skyDay));

                }

                checkPowerRemaining(dataSnapshot.getValue(User.class).getPowerRemaining(), dataSnapshot.getValue(User.class).getLastStudyCheck());

                Calendar cal = Calendar.getInstance();
                // Update stats.
                if (dataSnapshot.getValue(User.class).getStatStampMonth() != cal.get(Calendar.MONTH)) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("monthlyHours").setValue(0);
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("statStampMonth").setValue(cal.get(Calendar.MONTH));
                }

                if (dataSnapshot.getValue(User.class).getStatStampWeek() != cal.get(Calendar.WEEK_OF_YEAR)) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("weeklyHours").setValue(0);
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("statStampWeek").setValue(cal.get(Calendar.WEEK_OF_YEAR));
                }

                if (dataSnapshot.getValue(User.class).getStatStampDay() != cal.get(Calendar.DAY_OF_YEAR)) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("dailyHours").setValue(0);
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("statStampDay").setValue(cal.get(Calendar.WEEK_OF_YEAR));
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

    }


    public void checkPowerRemaining(int power, long lastStudy) {
        int powerRemaining = power;
        long lastStudyCheck = lastStudy;
        if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getHoliday()) {
        } else {
            long check = System.currentTimeMillis() - lastStudyCheck;
            if (powerRemaining - (check / FIVE_MINUTES) < 0) {
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(0); // todo magic numberss!!!!!!
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerCuts").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerCuts() + 1);
                powerTotal.setText(String.valueOf(0));

            } else {
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(((int) powerRemaining - ((int) (check) / FIVE_MINUTES))); // todo magic numberss!!!!!!
                powerTotal.setText(String.valueOf(((int) powerRemaining - ((int) (check) / FIVE_MINUTES))));

            }

            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastStudyCheck").setValue(System.currentTimeMillis());
        }
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

    @Override
    protected void onResume() {
        super.onResume();

    }

}

