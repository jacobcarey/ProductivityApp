package com.example.b5043566.csc3122;

import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


// Make main activity.
public class BuildingActivity extends AppCompatActivity {

    private int i = 0;
    private Handler handler;
    private Runnable runnable;
    private TextView coins;
    private ImageView bolt;
    private ProgressBar progressBar;
    private Button powerUp;
    private DrawerLayout mDrawerLayout;
    boolean studying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        powerUp = (Button) findViewById(R.id.powerUp);
        coins = (TextView) findViewById(R.id.coins);
        bolt = (ImageView) findViewById(R.id.bolt);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

                        return true;
                    }
                });

        bolt.setVisibility(View.GONE);
        powerUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!studying){
                    studying = true;
                    powerUp.setText("Stop");
                    bolt.setVisibility(View.VISIBLE);

                    final int time = 2000;
                    handler = new Handler();

                    handler.postDelayed(runnable = new Runnable() {
                        public void run() {
                            coins.setText(String.valueOf(i++));
                            progressBar.setProgress(i);
                            handler.postDelayed(this, time);
                        }
                    }, time);
                }else{
                    studying = false;
                    bolt.setVisibility(View.GONE);
                    powerUp.setText("Study");
                    handler.removeCallbacks(runnable);
                    coins.setText(String.valueOf(0));
                    i = 0;
                }




            }
        });
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


//    protected void onStart();
//
//    protected void onRestart();
//
//    protected void onResume();

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        coins.setText(String.valueOf(0));
        i = 0;
        bolt.setVisibility(View.GONE);
    }

    ;

//    protected void onStop();
//
//    protected void onDestroy();

}

