package com.example.b5043566.csc3122;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class StatsActivity extends MainActivity {

    private Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_stats, contentFrameLayout);

        // Adds the menu button and applies a fix.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_stat);
        if (menu.getParent() != null)
            ((ViewGroup) menu.getParent()).removeView(menu); // <- fix for adding menu button.
        constraintLayout.addView(menu);

        test = (Button) findViewById(R.id.testButton);
        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//              mUserReference.child("lastLogin").setValue(System.currentTimeMillis());

            }
        });
    }
}
