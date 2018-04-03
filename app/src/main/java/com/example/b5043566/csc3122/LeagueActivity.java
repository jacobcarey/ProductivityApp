package com.example.b5043566.csc3122;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
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

    private Button friends;
    private Button global;

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

        daily = (Button) findViewById(R.id.dailyLeague);
        weekly = (Button) findViewById(R.id.weeklyLeague);
        monthly = (Button) findViewById(R.id.monthlyLeague);
        overall = (Button) findViewById(R.id.overallLeague);

        friends = (Button) findViewById(R.id.friendsLeague);
        global = (Button) findViewById(R.id.globalLeague);

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
                for (int i = 0; i < 5; i++) { // todo magic
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
                for (int i = 0; i < 5; i++) { // todo magic
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
                for (int i = 0; i < 5; i++) { // todo magic
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
                for (int i = 0; i < 5; i++) { // todo magic
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
                showLeague(true);

                user1.setText(topFiveDaily.get(0));
                user2.setText(topFiveDaily.get(1));
                user3.setText(topFiveDaily.get(2));
                user4.setText(topFiveDaily.get(3));
                user5.setText(topFiveDaily.get(4));

                hour1.setText(Integer.toString(dailyLeague.get(topFiveDaily.get(0))));
                hour2.setText(Integer.toString(dailyLeague.get(topFiveDaily.get(1))));
                hour3.setText(Integer.toString(dailyLeague.get(topFiveDaily.get(2))));
                hour4.setText(Integer.toString(dailyLeague.get(topFiveDaily.get(3))));
                hour5.setText(Integer.toString(dailyLeague.get(topFiveDaily.get(4))));

                daily.setTextColor(Color.parseColor("#F9DFBE"));
                weekly.setTextColor(Color.parseColor("#FFFFFF"));
                monthly.setTextColor(Color.parseColor("#FFFFFF"));
                overall.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });

        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeague(true);

                user1.setText(topFiveWeekly.get(0));
                user2.setText(topFiveWeekly.get(1));
                user3.setText(topFiveWeekly.get(2));
                user4.setText(topFiveWeekly.get(3));
                user5.setText(topFiveWeekly.get(4));

                hour1.setText(Integer.toString(weeklyLeague.get(topFiveWeekly.get(0))));
                hour2.setText(Integer.toString(weeklyLeague.get(topFiveWeekly.get(1))));
                hour3.setText(Integer.toString(weeklyLeague.get(topFiveWeekly.get(2))));
                hour4.setText(Integer.toString(weeklyLeague.get(topFiveWeekly.get(3))));
                hour5.setText(Integer.toString(weeklyLeague.get(topFiveWeekly.get(4))));

                daily.setTextColor(Color.parseColor("#FFFFFF"));
                weekly.setTextColor(Color.parseColor("#F9DFBE"));
                monthly.setTextColor(Color.parseColor("#FFFFFF"));
                overall.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeague(true);

                user1.setText(topFiveMonthly.get(0));
                user2.setText(topFiveMonthly.get(1));
                user3.setText(topFiveOverall.get(2));
                user4.setText(topFiveOverall.get(3));
                user5.setText(topFiveOverall.get(4));

                hour1.setText(Integer.toString(monthlyLeague.get(topFiveMonthly.get(0))));
                hour2.setText(Integer.toString(monthlyLeague.get(topFiveMonthly.get(1))));
                hour3.setText(Integer.toString(monthlyLeague.get(topFiveMonthly.get(2))));
                hour4.setText(Integer.toString(monthlyLeague.get(topFiveMonthly.get(3))));
                hour5.setText(Integer.toString(monthlyLeague.get(topFiveMonthly.get(4))));

                daily.setTextColor(Color.parseColor("#FFFFFF"));
                weekly.setTextColor(Color.parseColor("#FFFFFF"));
                monthly.setTextColor(Color.parseColor("#F9DFBE"));
                overall.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });

        overall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeague(true);

                user1.setText(topFiveOverall.get(0));
                user2.setText(topFiveOverall.get(1));
                user3.setText(topFiveOverall.get(2));
                user4.setText(topFiveOverall.get(3));
                user5.setText(topFiveOverall.get(4));

                hour1.setText(Integer.toString(overallLeague.get(topFiveOverall.get(0))));
                hour2.setText(Integer.toString(overallLeague.get(topFiveOverall.get(1))));
                hour3.setText(Integer.toString(overallLeague.get(topFiveOverall.get(2))));
                hour4.setText(Integer.toString(overallLeague.get(topFiveOverall.get(3))));
                hour5.setText(Integer.toString(overallLeague.get(topFiveOverall.get(4))));

                daily.setTextColor(Color.parseColor("#FFFFFF"));
                weekly.setTextColor(Color.parseColor("#FFFFFF"));
                monthly.setTextColor(Color.parseColor("#FFFFFF"));
                overall.setTextColor(Color.parseColor("#F9DFBE"));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        showLeague(false);

        // todo remove on update
        friends.setPaintFlags(friends.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }


    public void showLeague(boolean show) {
        if (show) {
            pos1.setVisibility(View.VISIBLE);
            pos2.setVisibility(View.VISIBLE);
            pos3.setVisibility(View.VISIBLE);
            pos4.setVisibility(View.VISIBLE);
            pos5.setVisibility(View.VISIBLE);

            user1.setVisibility(View.VISIBLE);
            user2.setVisibility(View.VISIBLE);
            user3.setVisibility(View.VISIBLE);
            user4.setVisibility(View.VISIBLE);
            user5.setVisibility(View.VISIBLE);

            hour1.setVisibility(View.VISIBLE);
            hour2.setVisibility(View.VISIBLE);
            hour3.setVisibility(View.VISIBLE);
            hour4.setVisibility(View.VISIBLE);
            hour5.setVisibility(View.VISIBLE);
        } else {
            pos1.setVisibility(View.GONE);
            pos2.setVisibility(View.GONE);
            pos3.setVisibility(View.GONE);
            pos4.setVisibility(View.GONE);
            pos5.setVisibility(View.GONE);

            user1.setVisibility(View.GONE);
            user2.setVisibility(View.GONE);
            user3.setVisibility(View.GONE);
            user4.setVisibility(View.GONE);
            user5.setVisibility(View.GONE);

            hour1.setVisibility(View.GONE);
            hour2.setVisibility(View.GONE);
            hour3.setVisibility(View.GONE);
            hour4.setVisibility(View.GONE);
            hour5.setVisibility(View.GONE);
        }
    }
}
