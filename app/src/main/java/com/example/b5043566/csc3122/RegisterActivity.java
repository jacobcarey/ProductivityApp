package com.example.b5043566.csc3122;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class RegisterActivity extends MainActivity {

    private Button submit;
    private EditText emailText;
    private EditText passwordText;
    private EditText usernameText;
    int startingPower = 750;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_register, contentFrameLayout);

        // Adds the menu button and applies a fix.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_register);
        if (menu.getParent() != null) {
            ((ViewGroup) menu.getParent()).removeView(menu); // <- fix for adding menu button.
            constraintLayout.addView(menu);
        }

        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password2);
        usernameText = (EditText) findViewById(R.id.username);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                String username = usernameText.getText().toString().trim();
                createAccount(email, password, username);
            }
        });
    }


    private void createAccount(String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                        }

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Success",
                                    Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.child("users").child(user.getUid()).setValue(new User());
                            mDatabase.child("users").child(user.getUid()).child("email").setValue(user.getEmail());
                            mDatabase.child("users").child(user.getUid()).child("powerRemaining").setValue(startingPower);
                            mDatabase.child("users").child(user.getUid()).child("username").setValue(username);

                            // Windows.
                            Random rand = new Random();
                            int n = rand.nextInt(TOTAL_WINDOWS_V1); // Gives n such that 0 <= n < 15
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("windows").child("w_" + n).setValue(true);

                            // Last Login.
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastLogin").setValue(System.currentTimeMillis());
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastStudyCheck").setValue(System.currentTimeMillis());

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("progress").setValue(true);
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("notifications").setValue(true);
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("holiday").setValue(false);

                            // Values initialy set to 0, updated in BuildingActibity.
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("dailyHours").setValue(0);
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("weeklyHours").setValue(0);
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("monthlyHours").setValue(0);
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("overallHours").setValue(0);
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("residents").setValue(0);
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerCuts").setValue(0);

                            Calendar cal = Calendar.getInstance();

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("statStampDay").setValue(cal.get(Calendar.DAY_OF_YEAR));

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("statStampWeek").setValue(cal.get(Calendar.WEEK_OF_YEAR));

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("statStampMonth").setValue(cal.get(Calendar.MONTH));

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("nightMode").setValue(true);

                            List<String> friends = new ArrayList<String>();
                            friends.add(mAuth.getCurrentUser().getUid());

                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").setValue(friends);

                            Intent newAct = new Intent(getApplicationContext(), BuildingActivity.class);
                            startActivity(newAct);
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}