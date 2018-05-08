package com.example.b5043566.csc3122;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class StatsActivity extends MainActivity {

    // Objects needed for UI.
    private TextView daily;
    private TextView weekly;
    private TextView monthly;
    private TextView overall;
    private TextView powerCuts;
    private TextView residents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check user is logged in.
        checkLogin();
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_stats, contentFrameLayout);

        // Adds the menu button and applies a fix.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_stat);
        if (menu.getParent() != null)
            ((ViewGroup) menu.getParent()).removeView(menu); // <- fix for adding menu button.
        constraintLayout.addView(menu);

        // Set UI elements.
        daily = (TextView) findViewById(R.id.daily);
        weekly = (TextView) findViewById(R.id.weekly);
        monthly = (TextView) findViewById(R.id.monthly);
        overall = (TextView) findViewById(R.id.overall);
        powerCuts = (TextView) findViewById(R.id.powerCuts);
        residents = (TextView) findViewById(R.id.residents);

        // Event listener to update stats line upon change of data.
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Update global user stats.
                ((ProductivityApp) StatsActivity.this.getApplication()).setUser(dataSnapshot.getValue(User.class));

                // Update UI text.
                daily.setText(Integer.toString(((ProductivityApp) StatsActivity.this.getApplication()).getUser().getDailyHours()));
                weekly.setText(Integer.toString(((ProductivityApp) StatsActivity.this.getApplication()).getUser().getWeeklyHours()));
                monthly.setText(Integer.toString(((ProductivityApp) StatsActivity.this.getApplication()).getUser().getMonthlyHours()));
                overall.setText(Integer.toString(((ProductivityApp) StatsActivity.this.getApplication()).getUser().getOverallHours()));
                powerCuts.setText(Integer.toString(((ProductivityApp) StatsActivity.this.getApplication()).getUser().getPowerCuts()));
                residents.setText(Integer.toString(((ProductivityApp) StatsActivity.this.getApplication()).getUser().getResidents()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(StatsActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });

    }
}
