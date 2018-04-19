package com.example.b5043566.csc3122;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class LeagueActivity extends MainActivity {

    // Objects needed.
    Map<String, Integer> dailyLeague = new HashMap<String, Integer>();
    Map<String, Integer> weeklyLeague = new HashMap<String, Integer>();
    Map<String, Integer> monthlyLeague = new HashMap<String, Integer>();
    Map<String, Integer> overallLeague = new HashMap<String, Integer>();
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

    private List<TextView> hours;
    private List<TextView> pos;
    private List<TextView> users;

    public static final int TOP_FIVE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check user is logged in.
        checkLogin();

        // Needed for menu item to show show.
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_league, contentFrameLayout);

        pos1 = (TextView) findViewById(R.id.pos1);
        pos2 = (TextView) findViewById(R.id.pos2);
        pos3 = (TextView) findViewById(R.id.pos3);
        pos4 = (TextView) findViewById(R.id.pos4);
        pos5 = (TextView) findViewById(R.id.pos5);

        pos = new ArrayList<>();
        pos.add(pos1);
        pos.add(pos2);
        pos.add(pos3);
        pos.add(pos4);
        pos.add(pos5);

        user1 = (TextView) findViewById(R.id.user1);
        user2 = (TextView) findViewById(R.id.user2);
        user3 = (TextView) findViewById(R.id.user3);
        user4 = (TextView) findViewById(R.id.user4);
        user5 = (TextView) findViewById(R.id.user5);

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);

        hour1 = (TextView) findViewById(R.id.hours1);
        hour2 = (TextView) findViewById(R.id.hours2);
        hour3 = (TextView) findViewById(R.id.hours3);
        hour4 = (TextView) findViewById(R.id.hours4);
        hour5 = (TextView) findViewById(R.id.hours5);

        hours = new ArrayList<>();
        hours.add(hour1);
        hours.add(hour2);
        hours.add(hour3);
        hours.add(hour4);
        hours.add(hour5);

        // Tabs for selecting leaderboard.
        daily = (Button) findViewById(R.id.dailyLeague);
        weekly = (Button) findViewById(R.id.weeklyLeague);
        monthly = (Button) findViewById(R.id.monthlyLeague);
        overall = (Button) findViewById(R.id.overallLeague);

        friends = (Button) findViewById(R.id.friendsLeague);
        global = (Button) findViewById(R.id.globalLeague);
        // Todo, remove on update.
        friends.setAlpha(.5f);
        friends.setClickable(false);
        global.setTextColor(Color.parseColor("#F9DFBE"));

        // Adds the menu button and applies a fix to allow menu item to be clickable.
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.active_league);
        if (menu.getParent() != null)
            ((ViewGroup) menu.getParent()).removeView(menu); // <- fix for adding menu button.
        constraintLayout.addView(menu);

        // Event listener
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Add values to the maps.
                    User user = snapshot.getValue(User.class);
                    Calendar cal = Calendar.getInstance();


                    if (user.getStatStampMonth() == cal.get(Calendar.MONTH)) {
                        monthlyLeague.put(user.getUsername(), user.getMonthlyHours());
                    }
                    if (user.getStatStampWeek() == cal.get(Calendar.WEEK_OF_YEAR)) {
                        weeklyLeague.put(user.getUsername(), user.getWeeklyHours());
                    }
                    if (user.getStatStampDay() == cal.get(Calendar.DAY_OF_YEAR)) {
                        dailyLeague.put(user.getUsername(), user.getDailyHours());
                    }
                    overallLeague.put(user.getUsername(), user.getOverallHours());

                }

                // Daily top 5.
                // Not the nicest solution but works to rank the top 5 users efficiently.
                SortedSet<String> usernames = new TreeSet<String>(dailyLeague.keySet());
                for (int i = 0; i < dailyLeague.size(); i++) { // todo magic
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
                // Not the nicest solution but works to rank the top 5 users efficiently.
                usernames = new TreeSet<String>(weeklyLeague.keySet());
                for (int i = 0; i < weeklyLeague.size(); i++) { // todo magic
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
                // Not the nicest solution but works to rank the top 5 users efficiently.
                usernames = new TreeSet<String>(monthlyLeague.keySet());
                for (int i = 0; i < monthlyLeague.size(); i++) { // todo magic
                    String currentLargest = usernames.first();
                    for (String username : usernames) {
                        if (monthlyLeague.get(username) > monthlyLeague.get(currentLargest)) {
                            currentLargest = username;
                        }
                    }
                    topFiveMonthly.add(currentLargest);
                    usernames.remove(currentLargest);
                }
                // Overall top 5.
                // Not the nicest solution but works to rank the top 5 users efficiently.
                usernames = new TreeSet<String>(overallLeague.keySet());
                for (int i = 0; i < overallLeague.size(); i++) { // todo magic
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

        // NOTE:
        // The above and below could have been done better using methods, however, had to spread them out during debugging.
        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set username and hours on UI.
                for (int i = 0; i < topFiveDaily.size(); i++){
                    users.get(i).setText(topFiveDaily.get(i));
                    hours.get(i).setText(Integer.toString(dailyLeague.get(topFiveDaily.get(i))));
                }

                // Set colours.
                daily.setTextColor(Color.parseColor("#F9DFBE"));
                weekly.setTextColor(Color.parseColor("#FFFFFF"));
                monthly.setTextColor(Color.parseColor("#FFFFFF"));
                overall.setTextColor(Color.parseColor("#FFFFFF"));

                showLeague(false, TOP_FIVE);
                showLeague(true, topFiveDaily.size());
            }
        });

        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set username and hours on UI.
                for (int i = 0; i < topFiveWeekly.size(); i++){
                    users.get(i).setText(topFiveWeekly.get(i));
                    hours.get(i).setText(Integer.toString(weeklyLeague.get(topFiveWeekly.get(i))));
                }

                // Set colours.
                daily.setTextColor(Color.parseColor("#FFFFFF"));
                weekly.setTextColor(Color.parseColor("#F9DFBE"));
                monthly.setTextColor(Color.parseColor("#FFFFFF"));
                overall.setTextColor(Color.parseColor("#FFFFFF"));

                showLeague(false, TOP_FIVE);
                showLeague(true, topFiveWeekly.size());
            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Set username and hours on UI.
                for (int i = 0; i < topFiveMonthly.size(); i++){
                    users.get(i).setText(topFiveMonthly.get(i));
                    hours.get(i).setText(Integer.toString(monthlyLeague.get(topFiveMonthly.get(i))));
                }

                // Set colours.
                daily.setTextColor(Color.parseColor("#FFFFFF"));
                weekly.setTextColor(Color.parseColor("#FFFFFF"));
                monthly.setTextColor(Color.parseColor("#F9DFBE"));
                overall.setTextColor(Color.parseColor("#FFFFFF"));

                showLeague(false, TOP_FIVE);
                showLeague(true, topFiveMonthly.size());
            }
        });

        overall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set username and hours on UI.
                int check = 0;
                if(topFiveOverall.size() > TOP_FIVE){
                    check = TOP_FIVE;
                }else{
                    check = topFiveOverall.size();
                }
                for (int i = 0; i < check; i++){
                    users.get(i).setText(topFiveOverall.get(i));
                    hours.get(i).setText(Integer.toString(overallLeague.get(topFiveOverall.get(i))));
                }

                // Set colours.
                daily.setTextColor(Color.parseColor("#FFFFFF"));
                weekly.setTextColor(Color.parseColor("#FFFFFF"));
                monthly.setTextColor(Color.parseColor("#FFFFFF"));
                overall.setTextColor(Color.parseColor("#F9DFBE"));

                showLeague(false, TOP_FIVE);
                showLeague(true, check);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Hide league.
        showLeague(false, TOP_FIVE);

        // todo remove on update.
        friends.setPaintFlags(friends.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }


    /**
     * Used to find and show the league.
     *
     * @param show
     */
    public void showLeague(boolean show, int size) {
        if (show) {
            for(int i = 0; i < size; i++){
                pos.get(i).setVisibility(View.VISIBLE);
                users.get(i).setVisibility(View.VISIBLE);
                hours.get(i).setVisibility(View.VISIBLE);
            }
        } else {
            for(int i = 0; i < TOP_FIVE; i++){
                pos.get(i).setVisibility(View.GONE);
                users.get(i).setVisibility(View.GONE);
                hours.get(i).setVisibility(View.GONE);
            }
        }
    }
}
