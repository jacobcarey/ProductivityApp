package com.example.b5043566.csc3122;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

    public final static int LOWEST_VALUE = 0;
    //    public final static int time = 50; // debug
    public final static int time = 15000; // quarter of a minute

    int progress = 0;
    private static final int PROGRESS_COMPLETE = 100;
    private static final int NEW_RESIDENT_HOURS = 10;
    boolean productive = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    private TextView powerTotal;
    private ImageView bolt;
    private ImageView moon;
    private ProgressBar progressBar;
    private Button powerUp;
    private Map<String, ImageView> windows;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if user is logged in.
        if (checkLogin()) {

            // Needed to extend the main activity, we can then add the menu item to any activity.
            FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
            getLayoutInflater().inflate(R.layout.activity_building, contentFrameLayout);

            // Adds the menu button and applies a fix allowing it to be clicked.
            final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_building);
            if (menu.getParent() != null) {
                ((ViewGroup) menu.getParent()).removeView(menu);
            }
            constraintLayout.addView(menu);

            // Page elements.
            powerUp = (Button) findViewById(R.id.powerUp);
            bolt = (ImageView) findViewById(R.id.bolt);
            // Hide power bolt.
            bolt.setVisibility(View.GONE);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            powerTotal = (TextView) findViewById(R.id.coins);
            moon = (ImageView) findViewById(R.id.moon);
            windows = new HashMap<String, ImageView>();

            // Create a map of all window screen elements. This is needed to turn the windows on or off.
            for (int i = 0; i < TOTAL_WINDOWS_V1; i++) {
                // Page element IDs are just integer numbers so we can iterate through them easily.
                int id = (int) R.id.window1 + i;
                windows.put("w_" + i, (ImageView) findViewById(id));

            }

            // Important event listener for all user items. The activity WILL NOT run without this listener.
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

            // Power up button listener. This will create a handler. (Possibly changed in a future update to something other than handlers).
            powerUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getHoliday()) {
                        // Needs to turn off holiday.
                        Toast.makeText(BuildingActivity.this, "Please turn off holiday.",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        // Check user is being productive.
                        if (!productive) {
                            // Prevent sleep.
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            productive = true;
                            // Set text to stop as power is generating.
                            powerUp.setText("Stop");
                            // Show user the power up bolt.
                            bolt.setVisibility(View.VISIBLE);

                            // Create new handler.
                            handler = new Handler();

                            // Used to delay handler so it can run over a set period of time.
                            handler.postDelayed(runnable = new Runnable() {
                                public void run() {
                                    // Progress bar, optional. (Can be turned off in settings).
                                    if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getProgress()) {
                                        progressBar.setProgress(progress);
                                    }
                                    // Create local version of powerPerHour so ease of use.
                                    int powerPerHour = ((ProductivityApp) BuildingActivity.this.getApplication()).getPowerPerHour();
                                    // Delay check.
                                    handler.postDelayed(this, time);
                                    // Increment progress.
                                    progress++;
                                    // Check if progress is complete.
                                    if (progress == PROGRESS_COMPLETE) {
                                        // Turn off productivity.
                                        productive = false;
                                        // Find generate view.
                                        bolt.setVisibility(View.GONE);
                                        // Set text back to study.
                                        powerUp.setText("Power");
                                        // Update the last time power was generated.
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastStudyCheck").setValue(System.currentTimeMillis());

                                        // Increase power.
                                        // Calculate power remaining.
                                        int powerRemaining = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() + (powerPerHour);
                                        // Update power remaining.
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(powerRemaining);
                                        // Update power remaining UI.
                                        powerTotal.setText(Integer.toString(powerRemaining));
                                        // Check windows.
                                        int currentWindows = 0;
                                        // Calculate how many windows are currently active.
                                        for (int i = 0; i < TOTAL_WINDOWS_V1; i++) {
                                            boolean window = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + i);
                                            if (window) {
                                                currentWindows++;
                                            }
                                        }
                                        // Calculate how many windows SHOULD be currently active.
                                        int windowCheck = 0;
                                        // Check for devision of 0.
                                        if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() != LOWEST_VALUE) {
                                            windowCheck = ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining() / (powerPerHour * NEW_RESIDENT_HOURS);
                                        }

                                        // Check if windows (residents) need adding or deleting.
                                        if (currentWindows == windowCheck) {
                                            // All if fine
                                        } else if (windowCheck > currentWindows) {
                                            // New window needed.
                                            boolean newWindow = true;
                                            // While loop to check window location.
                                            while (newWindow) {
                                                Random rand = new Random();
                                                int n = rand.nextInt(TOTAL_WINDOWS_V1); // Gives n such that 0 <= n < 15.
                                                if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + n)) {
                                                    // Already set, try other.
                                                } else {
                                                    // Add window here. No window needed.
                                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("windows").child("w_" + n).setValue(true);
                                                    newWindow = false;
                                                    windows.get("w_" + n).setBackgroundColor(Color.parseColor("#F9DFBE"));

                                                }
                                            }
                                            // Check if user has too many windows.
                                        } else if (windowCheck < currentWindows) {
                                            boolean removeWindow = true;
                                            while (removeWindow) {
                                                Random rand = new Random();
                                                int n = rand.nextInt(TOTAL_WINDOWS_V1);
                                                if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWindows().get("w_" + n)) {
                                                    // Remove window.
                                                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("windows").child("w_" + n).setValue(false);
                                                    removeWindow = false;
                                                    windows.get("w_" + n).setBackgroundColor(Color.parseColor("#0C2F41"));
                                                }

                                            }
                                        }

                                        // Increment for stats.
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("dailyHours").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getDailyHours() + 1);
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("weeklyHours").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getWeeklyHours() + 1);
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("monthlyHours").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getMonthlyHours() + 1);
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("overallHours").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getOverallHours() + 1);
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("residents").setValue(windowCheck);

                                        // Remove handler.
                                        handler.removeCallbacks(runnable);
                                        // Set progress back to 0.
                                        progress = 0;

                                    }
                                }
                            }, time);
                        } else {
                            // Reset and called if users stops using button or exits the app.
                            productive = false;
                            // Hide power up UI.
                            bolt.setVisibility(View.GONE);
                            // Set button text back to starting point.
                            powerUp.setText("Power");
                            // Set progress to 0.
                            progress = 0;
                            // Remove handler.
                            handler.removeCallbacks(runnable);
                            // Allow screen lock.
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

                        }
                    }
                }
            });
        }
    }

    // Used to see if user exits the app when power up is in progress. (Maybe change the way this is implemented in later update, however, currently works well.
    @Override
    protected void onPause() {
        super.onPause();
        // Reset and called if users stops using button or exits the app.
        productive = false;
        // Hide power up UI.
        bolt.setVisibility(View.GONE);
        // Set button text back to starting point.
        powerUp.setText("Power");
        // Set progress to 0.
        progress = 0;
        // Remove handler.
        handler.removeCallbacks(runnable);

        // Check remaining power and update if needs be. Used to reduce power.
        if (mAuth.getCurrentUser() != null) {
            checkPowerRemaining(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerRemaining(), ((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getLastStudyCheck());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Add auth listener.
        mAuth.addAuthStateListener(mAuthListener);
        if (mAuth.getCurrentUser() != null) {
            // Single event listener. Used to update vales when app has been exited.
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Set global user values.
                    ((ProductivityApp) BuildingActivity.this.getApplication()).setUser(dataSnapshot.getValue(User.class));
                    // Update windows to show correct data from database.
                    for (int i = 0; i < TOTAL_WINDOWS_V1; i++) {
                        // No resident.
                        if (!dataSnapshot.getValue(User.class).getWindows().get("w_" + i)) {
                            windows.get("w_" + i).setBackgroundColor(Color.parseColor("#0C2F41"));
                        } else {
                            windows.get("w_" + i).setBackgroundColor(Color.parseColor("#F9DFBE"));
                        }
                    }


                    // Variable needed to update UI mode.
                    ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_building);

                    // Check if user has night mode turned on. (Will automatically do this is future update).
                    if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getNightMode()) {
                        // Set background colour to night.
                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.skyNight));
                        // Match sky colour.
                        moon.setBackgroundColor(getResources().getColor(R.color.skyNight));
                        // Change colour to look like a moon.
                        moon.getDrawable().setColorFilter(new
                                PorterDuffColorFilter(getResources().getColor(R.color.moon), PorterDuff.Mode.MULTIPLY));

                        // Set to night bolt.
                        bolt.setImageResource(R.drawable.bolt);
                        bolt.setBackgroundColor(getResources().getColor(R.color.skyNight));
                        // Needed to update menu. Otherwise is hard to see.
                        menu.setColorFilter(getResources().getColor(R.color.menuNight));
                    } else {
                        // Set background colour to day.
                        constraintLayout.setBackgroundColor(getResources().getColor(R.color.skyDay));
                        // Match sky colour.
                        moon.setBackgroundColor(getResources().getColor(R.color.skyDay));
                        // Change colour to look like a moon.
                        moon.getDrawable().setColorFilter(new
                                PorterDuffColorFilter(getResources().getColor(R.color.sun), PorterDuff.Mode.MULTIPLY));

                        //Set to day bolt.
                        bolt.setImageResource(R.drawable.bolt_day);
                        bolt.setBackgroundColor(getResources().getColor(R.color.skyDay));
                        // Needed to update menu. Otherwise is hard to see.
                        menu.setColorFilter(getResources().getColor(R.color.menuDay));

                    }

                    // Check power remaining and initiate the required updates.
                    checkPowerRemaining(dataSnapshot.getValue(User.class).getPowerRemaining(), dataSnapshot.getValue(User.class).getLastStudyCheck());

                    // Needed to update values for stats.
                    Calendar cal = Calendar.getInstance();
                    // Update stats.
                    // Month stats.
                    if (dataSnapshot.getValue(User.class).getStatStampMonth() != (int) cal.get(Calendar.MONTH)) {
                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("monthlyHours").setValue(0);
                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("statStampMonth").setValue(cal.get(Calendar.MONTH));
                    }
                    // Week Stats,
                    if (dataSnapshot.getValue(User.class).getStatStampWeek() != (int) cal.get(Calendar.WEEK_OF_YEAR)) {
                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("weeklyHours").setValue(0);
                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("statStampWeek").setValue(cal.get(Calendar.WEEK_OF_YEAR));
                    }
                    // Year stats.
                    if (dataSnapshot.getValue(User.class).getStatStampDay() != (int) cal.get(Calendar.DAY_OF_YEAR)) {
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
    }

    /**
     * Used to check remaining power and so the required updates.
     *
     * @param power
     * @param lastStudy
     */
    public void checkPowerRemaining(int power, long lastStudy) {
        int powerRemaining = power;
        long lastStudyCheck = lastStudy;
        if (((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getHoliday()) {
            powerTotal.setText(String.valueOf(powerRemaining));
        } else {
            long check = System.currentTimeMillis() - lastStudyCheck;
            if (powerRemaining == LOWEST_VALUE) {
                // Prevent multiple PowerCuts being added.
            }
            // Power cut.
            else if (powerRemaining - (check / FIVE_MINUTES) < LOWEST_VALUE) {
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(LOWEST_VALUE);
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerCuts").setValue(((ProductivityApp) BuildingActivity.this.getApplication()).getUser().getPowerCuts() + 1);
                powerTotal.setText(String.valueOf(LOWEST_VALUE));
            } else {
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(((int) powerRemaining - ((int) (check) / FIVE_MINUTES)));
                powerTotal.setText(String.valueOf(((int) powerRemaining - ((int) (check) / FIVE_MINUTES))));

            }

            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastStudyCheck").setValue(System.currentTimeMillis());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        // Remove auth listener.
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

