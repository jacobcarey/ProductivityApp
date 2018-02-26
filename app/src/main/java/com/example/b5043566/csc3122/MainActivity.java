package com.example.b5043566.csc3122;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    protected DrawerLayout mDrawerLayout;
    protected ImageView menu;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    protected static final String TAG = "MainActivity";
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseDatabase database;
    protected static User user;
    protected static DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new User();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.inflateMenu(R.menu.drawer_view);
        menu = (ImageView) findViewById(R.id.menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.test, R.string.test);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        int id = menuItem.getItemId();

                        if (id == R.id.login) {
                            Intent newAct = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(newAct);
                        } else if (id == R.id.home) {
                            Intent newAct = new Intent(getApplicationContext(), BuildingActivity.class);
                            startActivity(newAct);
                        }else if (id == R.id.register) {
                            Intent newAct = new Intent(getApplicationContext(), RegisterActivity.class);
                            startActivity(newAct);
                        }else if (id == R.id.league) {
                            Intent newAct = new Intent(getApplicationContext(), LeagueActivity.class);
                            startActivity(newAct);
                        }else if (id == R.id.stats) {
                            Intent newAct = new Intent(getApplicationContext(), StatsActivity.class);
                            startActivity(newAct);
                        }else if (id == R.id.settings) {
                            Intent newAct = new Intent(getApplicationContext(), SettingActivity.class);
                            startActivity(newAct);
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerLayout.onConfigurationChanged(newConfig);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void setUserListener(){
        mUserReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        user.email =  dataSnapshot.getValue(User.class).email;
                        user.lastLogin =  dataSnapshot.getValue(User.class).lastLogin;
                        user.powerRemaining =  dataSnapshot.getValue(User.class).powerRemaining;
                        user.timeLimit =  dataSnapshot.getValue(User.class).timeLimit;
                        user.username =  dataSnapshot.getValue(User.class).username;
                        user.windows =  dataSnapshot.getValue(User.class).windows;
                        Log.d(TAG, "Value is: " + user.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        Toast.makeText(MainActivity.this, "Failed to load user.",
                                Toast.LENGTH_SHORT).show();
                        // [END_EXCLUDE]
                    }
                });
    }

}
