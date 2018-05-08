package com.example.b5043566.csc3122;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class HelpActivity extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check user is logged in.
        checkLogin();


        // Needed for menu item to show show.
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_help, contentFrameLayout);

        // Adds the menu button and applies a fix allowing it to be clicked.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_help);
        if (menu.getParent() != null) {
            ((ViewGroup) menu.getParent()).removeView(menu);
        }
        constraintLayout.addView(menu);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
