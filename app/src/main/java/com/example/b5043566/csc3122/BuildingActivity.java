package com.example.b5043566.csc3122;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class BuildingActivity extends AppCompatActivity {

    private int i = 0;
    private Handler handler;
    private Runnable runnable;
    TextView coins;
    ImageView bolt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        final Button powerUp = (Button) findViewById(R.id.powerUp);
        coins = (TextView) findViewById(R.id.coins);
         bolt = (ImageView) findViewById(R.id.bolt);

        bolt.setVisibility(View.GONE);
        powerUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when play is clicked show stop button and hide play button
                bolt.setVisibility(View.VISIBLE);

                final int time = 2000;
                handler = new Handler();

                handler.postDelayed(runnable = new Runnable(){
                    public void run(){
                        coins.setText(String.valueOf(i++));
                        handler.postDelayed(this, time);
                    }
                }, time);

            }
        });
    }


//    protected void onStart();
//
//    protected void onRestart();
//
//    protected void onResume();

    @Override
    protected void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);
        coins.setText(String.valueOf(0));
        i = 0;
        bolt.setVisibility(View.GONE);
    };

//    protected void onStop();
//
//    protected void onDestroy();

}

