package com.example.b5043566.csc3122;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class LeagueActivity extends MainActivity {

    private TextView pos1;
    private TextView pos2;
    private TextView pos3;
    private TextView pos4;
    private TextView pos5;

    private TextView user1;
    private TextView user2;
    private TextView user3;
    private TextView user4;
    private TextView user5;

    private TextView hour1;
    private TextView hour2;
    private TextView hour3;
    private TextView hour4;
    private TextView hour5;

    private Button daily;
    private Button weekly;
    private Button monthly;
    private Button overall;

    private List<String> topFiveDaily = new ArrayList<String>();
    private List<String> topFiveWeekly = new ArrayList<String>();
    private List<String> topFiveMonthly = new ArrayList<String>();
    private List<String> topFiveOverall = new ArrayList<String>();

    Map<String, Integer> dailyLeague = new HashMap<String, Integer>();
    Map<String, Integer> weeklyLeague = new HashMap<String, Integer>();
    Map<String, Integer> monthlyLeague = new HashMap<String, Integer>();
    Map<String, Integer> overallLeague = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check login.
        checkLogin();
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_league, contentFrameLayout);

        pos1 = (TextView) findViewById(R.id.pos1);
        pos2 = (TextView) findViewById(R.id.pos2);
        pos3 = (TextView) findViewById(R.id.pos3);
        pos4 = (TextView) findViewById(R.id.pos4);
        pos5 = (TextView) findViewById(R.id.pos5);

        user1 = (TextView) findViewById(R.id.user1);
        user2 = (TextView) findViewById(R.id.user2);
        user3 = (TextView) findViewById(R.id.user3);
        user4 = (TextView) findViewById(R.id.user4);
        user5 = (TextView) findViewById(R.id.user5);

        hour1 = (TextView) findViewById(R.id.hours1);
        hour2 = (TextView) findViewById(R.id.hours2);
        hour3 = (TextView) findViewById(R.id.hours3);
        hour4 = (TextView) findViewById(R.id.hours4);
        hour5 = (TextView) findViewById(R.id.hours5);

        daily = (Button) findViewById(R.id.daily);
        weekly = (Button) findViewById(R.id.weekly);
        monthly = (Button) findViewById(R.id.monthly);
        overall = (Button) findViewById(R.id.overall);

        // Adds the menu button and applies a fix.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_league);
        if (menu.getParent() != null)
            ((ViewGroup) menu.getParent()).removeView(menu); // <- fix for adding menu button.
        constraintLayout.addView(menu);

        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    dailyLeague.put(user.getUsername(), user.getDailyHours());
                    weeklyLeague.put(user.getUsername(), user.getWeeklyHours());
                    monthlyLeague.put(user.getUsername(), user.getMonthlyHours());
                    overallLeague.put(user.getUsername(), user.getOverallHours());
                }

                // Daily top 5.
                SortedSet<String> usernames = new TreeSet<String>(dailyLeague.keySet());
                for(int i = 0; i < 5 ; i++){ // todo magic
                    String currentLargest = usernames.first();
                    for (String username : usernames) {
                        if (dailyLeague.get(username) > dailyLeague.get(currentLargest)) {
                            currentLargest = username;
                        }
                    }
                    topFiveDaily.add(currentLargest);
                    usernames.remove(currentLargest);
                }

                // Weekly top 5.
                usernames = new TreeSet<String>(weeklyLeague.keySet());
                for(int i = 0; i < 5 ; i++){ // todo magic
                    String currentLargest = usernames.first();
                    for (String username : usernames) {
                        if (weeklyLeague.get(username) > weeklyLeague.get(currentLargest)) {
                            currentLargest = username;
                        }
                    }
                    topFiveWeekly.add(currentLargest);
                    usernames.remove(currentLargest);
                }

                // Monthly top 5.
                usernames = new TreeSet<String>(monthlyLeague.keySet());
                for(int i = 0; i < 5 ; i++){ // todo magic
                    String currentLargest = usernames.first();
                    for (String username : usernames) {
                        if (monthlyLeague.get(username) > monthlyLeague.get(currentLargest)) {
                            currentLargest = username;
                        }
                    }
                    topFiveMonthly.add(currentLargest);
                    usernames.remove(currentLargest);
                }

                usernames = new TreeSet<String>(overallLeague.keySet());
                for(int i = 0; i < 5 ; i++){ // todo magic
                    String currentLargest = usernames.first();
                    for (String username : usernames) {
                        if (overallLeague.get(username) > overallLeague.get(currentLargest)) {
                            currentLargest = username;
                        }
                    }
                    topFiveOverall.add(currentLargest);
                    usernames.remove(currentLargest);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(LeagueActivity.this, "Failed to load.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });

        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user1.setText(topFiveDaily.get(1 - 1));
                user2.setText(topFiveDaily.get(2 - 1));
                user3.setText(topFiveDaily.get(3 - 1));
                user4.setText(topFiveDaily.get(4 - 1));
                user5.setText(topFiveDaily.get(5 - 1));

                hour1.setText(dailyLeague.get(topFiveDaily.get(1 - 1)));
                hour2.setText(dailyLeague.get(topFiveDaily.get(2 - 1)));
                hour3.setText(dailyLeague.get(topFiveDaily.get(3 - 1)));
                hour4.setText(dailyLeague.get(topFiveDaily.get(4 - 1)));
                hour5.setText(dailyLeague.get(topFiveDaily.get(5 - 1)));
            }
        });

        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user1.setText(topFiveWeekly.get(1 - 1));
                user2.setText(topFiveWeekly.get(2 - 1));
                user3.setText(topFiveWeekly.get(3 - 1));
                user4.setText(topFiveWeekly.get(4 - 1));
                user5.setText(topFiveWeekly.get(5 - 1));

                hour1.setText(weeklyLeague.get(topFiveWeekly.get(1 - 1)));
                hour2.setText(weeklyLeague.get(topFiveWeekly.get(2 - 1)));
                hour3.setText(weeklyLeague.get(topFiveWeekly.get(3 - 1)));
                hour4.setText(weeklyLeague.get(topFiveWeekly.get(4 - 1)));
                hour5.setText(weeklyLeague.get(topFiveWeekly.get(5 - 1)));
            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user1.setText(topFiveMonthly.get(1 - 1));
                user2.setText(topFiveMonthly.get(2 - 1));
                user3.setText(topFiveOverall.get(3 - 1));
                user4.setText(topFiveOverall.get(4 - 1));
                user5.setText(topFiveOverall.get(5 - 1));

                hour1.setText(monthlyLeague.get(topFiveMonthly.get(1 - 1)));
                hour2.setText(monthlyLeague.get(topFiveMonthly.get(2 - 1)));
                hour3.setText(monthlyLeague.get(topFiveMonthly.get(3 - 1)));
                hour4.setText(monthlyLeague.get(topFiveMonthly.get(4 - 1)));
                hour5.setText(monthlyLeague.get(topFiveMonthly.get(5 - 1)));
            }
        });

        overall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user1.setText(topFiveOverall.get(1 - 1));
                user2.setText(topFiveOverall.get(2 - 1));
                user3.setText(topFiveOverall.get(3 - 1));
                user4.setText(topFiveOverall.get(4 - 1));
                user5.setText(topFiveOverall.get(5 - 1));

                hour1.setText(overallLeague.get(topFiveOverall.get(1 - 1)));
                hour2.setText(overallLeague.get(topFiveOverall.get(2 - 1)));
                hour3.setText(overallLeague.get(topFiveOverall.get(3 - 1)));
                hour4.setText(overallLeague.get(topFiveOverall.get(4 - 1)));
                hour5.setText(overallLeague.get(topFiveOverall.get(5 - 1)));
            }
        });

    }
}
