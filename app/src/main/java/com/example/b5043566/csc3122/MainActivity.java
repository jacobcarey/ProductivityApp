package com.example.b5043566.csc3122;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {

    public static final int TOTAL_WINDOWS_V1 = 15;
    protected static final String TAG = "MainActivity";
    protected final static int FIVE_MINUTES = 500; // Todo change!
    protected static DatabaseReference mDatabase;
    protected DrawerLayout mDrawerLayout;
    protected ImageView menu;
    protected Toolbar toolbar;
    protected NavigationView navigationView;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu = (ImageView) findViewById(R.id.menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.test, R.string.test);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        setSupportActionBar(toolbar);

        if (mAuth.getCurrentUser() != null) {
            navigationView.inflateMenu(R.menu.drawer_view_signed_in);

        } else {
            navigationView.inflateMenu(R.menu.drawer_view);
        }


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                navigationView.getMenu().clear();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    navigationView.inflateMenu(R.menu.drawer_view_signed_in);
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastActive").setValue(System.currentTimeMillis());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    navigationView.inflateMenu(R.menu.drawer_view);
                }
                // ...
            }
        };

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
                        } else if (id == R.id.register) {
                            Intent newAct = new Intent(getApplicationContext(), RegisterActivity.class);
                            startActivity(newAct);
                        } else if (id == R.id.league) {
                            Intent newAct = new Intent(getApplicationContext(), LeagueActivity.class);
                            startActivity(newAct);
                        } else if (id == R.id.stats) {
                            Intent newAct = new Intent(getApplicationContext(), StatsActivity.class);
                            startActivity(newAct);
                        } else if (id == R.id.settings) {
                            Intent newAct = new Intent(getApplicationContext(), SettingActivity.class);
                            startActivity(newAct);
                        } else if (id == R.id.logout) {
                            FirebaseAuth.getInstance().signOut();
                            Intent newAct = new Intent(getApplicationContext(), LoginActivity.class);
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

    public boolean checkLogin() {
        if (mAuth.getCurrentUser() == null) {
            Log.d(TAG, "No current user!");
            Intent newAct = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(newAct);
            finish();
            return false;
        } else {
            Log.d(TAG, "Current user:" + mAuth.getCurrentUser().getEmail());

            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "On start! MainActivity");
        if (mAuth.getCurrentUser() != null) {
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("lastActive").setValue(System.currentTimeMillis());

            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ((ProductivityApp) MainActivity.this.getApplication()).setUser(dataSnapshot.getValue(User.class));

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

            mDatabase.child("hour").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ((ProductivityApp) MainActivity.this.getApplication()).setPowerPerHour(dataSnapshot.getValue(Integer.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(MainActivity.this, "Failed to load hour.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            });
        }

    }


}
