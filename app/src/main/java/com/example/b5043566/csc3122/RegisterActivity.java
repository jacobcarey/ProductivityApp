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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import static android.R.attr.name;


public class RegisterActivity extends MainActivity {

    private Button submit;
    private EditText emailText;
    private EditText passwordText;

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
        passwordText = (EditText) findViewById(R.id.password);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                createAccount(email, password);
            }
        });
    }


    private void createAccount(String email, String password) {
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
                            writeNewUser(user.getUid(),user.getEmail());
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("powerRemaining").setValue(500);

                            // Windows.
                            Random rand = new Random();
                            int n = rand.nextInt(15); // Gives n such that 0 <= n < 11 // todo map size
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("windows").child("w_" + n).setValue(true);

                            // Last Login.
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastLogin").setValue(System.currentTimeMillis());
                            Intent newAct = new Intent(getApplicationContext(), BuildingActivity.class);
                            startActivity(newAct);
                        }


                    }
                });
    }

    private void writeNewUser(String userId, String email) {
        User user = new User(userId, email);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).setValue(user);
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