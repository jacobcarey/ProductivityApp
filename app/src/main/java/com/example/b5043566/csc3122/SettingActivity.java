package com.example.b5043566.csc3122;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends MainActivity {

    private Button progress;
    private Button notification;
    private Button holiday;
    private Button nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_setting, contentFrameLayout);

        progress = (Button) findViewById(R.id.progress);
        notification = (Button) findViewById(R.id.notification);
        holiday = (Button) findViewById(R.id.holiday);
        nightMode = (Button) findViewById(R.id.nightMode);

        // Adds the menu button and applies a fix.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_settings);
        if (menu.getParent() != null)
            ((ViewGroup) menu.getParent()).removeView(menu); // <- fix for adding menu button.
        constraintLayout.addView(menu);

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ProductivityApp) SettingActivity.this.getApplication()).getUser().getProgress()) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("progress").setValue(false);
                } else {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("progress").setValue(true);
                }
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ProductivityApp) SettingActivity.this.getApplication()).getUser().getNotifications()) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").setValue(false);
                } else {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").setValue(true);
                }
            }
        });

        holiday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ProductivityApp) SettingActivity.this.getApplication()).getUser().getHoliday()) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("holiday").setValue(false);
                } else {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("holiday").setValue(true);
                }
            }
        });

        nightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ProductivityApp) SettingActivity.this.getApplication()).getUser().getNightMode()) {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("nightMode").setValue(false);
                } else {
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("nightMode").setValue(true);
                }
            }
        });


        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((ProductivityApp) SettingActivity.this.getApplication()).setUser(dataSnapshot.getValue(User.class));
                if (dataSnapshot.getValue(User.class).getProgress()) {
                    progress.setBackgroundColor(Color.parseColor("#92D050"));
                    progress.setText("ON");
                } else {
                    progress.setBackgroundColor(Color.parseColor("#df6467"));
                    progress.setText("OFF");
                }

                if (dataSnapshot.getValue(User.class).getNotifications()) {
                    notification.setBackgroundColor(Color.parseColor("#92D050"));
                    notification.setText("ON");
                } else {
                    notification.setBackgroundColor(Color.parseColor("#df6467"));
                    notification.setText("OFF");
                }

                if (dataSnapshot.getValue(User.class).getHoliday()) {
                    holiday.setBackgroundColor(Color.parseColor("#92D050"));
                    holiday.setText("ON");
                } else {
                    holiday.setBackgroundColor(Color.parseColor("#df6467"));
                    holiday.setText("OFF");
                }

                if (dataSnapshot.getValue(User.class).getNightMode()) {
                    nightMode.setBackgroundColor(Color.parseColor("#92D050"));
                    nightMode.setText("ON");
                } else {
                    nightMode.setBackgroundColor(Color.parseColor("#df6467"));
                    nightMode.setText("OFF");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(SettingActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });

    }
}
