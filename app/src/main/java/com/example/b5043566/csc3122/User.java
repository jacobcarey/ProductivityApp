package com.example.b5043566.csc3122;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by b5043566 on 22/02/2018.
 */

public class User {

    // Variables needed for user.
    private String username;
    private String email;
    private int timeLimit;
    private int powerRemaining;
    private long lastLogin;
    private long lastStudyCheck;
    private Map<String, Boolean> windows = new HashMap<String, Boolean>();
    private long lastActive;
    private boolean notifications;
    private boolean holiday;
    private boolean progress;
    private int dailyHours;
    private int weeklyHours;
    private int monthlyHours;
    private int overallHours;
    private int powerCuts;
    private int residents;
    private int statStampDay;
    private int statStampWeek;
    private int statStampMonth;
    private boolean nightMode;
    private List<String> friends = new ArrayList<String>();

    // Getters and setters.

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        for (int i = 0; i < MainActivity.TOTAL_WINDOWS_V1; i++) {
            windows.put("w_" + Integer.toString(i), false);
        }
    }

    public String toString() {
        return "User: " + email + timeLimit + powerRemaining + lastLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getPowerRemaining() {
        return powerRemaining;
    }

    public void setPowerRemaining(int powerRemaining) {
        this.powerRemaining = powerRemaining;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Map<String, Boolean> getWindows() {
        return windows;
    }

    public void setWindows(Map<String, Boolean> windows) {
        this.windows = windows;
    }

    public long getLastStudyCheck() {
        return lastStudyCheck;
    }

    public void setLastStudyCheck(long lastStudyCheck) {
        this.lastStudyCheck = lastStudyCheck;
    }

    public boolean getProgress() {
        return progress;
    }

    public void setProgress(boolean progress) {
        this.progress = progress;
    }

    public boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public boolean getHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    public int getDailyHours() {
        return dailyHours;
    }

    public void setDailyHours(int dailyHours) {
        this.dailyHours = dailyHours;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public int getMonthlyHours() {
        return monthlyHours;
    }

    public void setMonthlyHours(int monthlyHours) {
        this.monthlyHours = monthlyHours;
    }

    public int getOverallHours() {
        return overallHours;
    }

    public void setOverallHours(int overallHours) {
        this.overallHours = overallHours;
    }

    public int getPowerCuts() {
        return powerCuts;
    }

    public void setPowerCuts(int powerCuts) {
        this.powerCuts = powerCuts;
    }

    public int getResidents() {
        return residents;
    }

    public void setResidents(int residents) {
        this.residents = residents;
    }

    public int getStatStampDay() {
        return statStampDay;
    }

    public void setStatStampDay(int statStampDay) {
        this.statStampDay = statStampDay;
    }

    public int getStatStampWeek() {
        return statStampWeek;
    }

    public void setStatStampWeek(int statStampWeek) {
        this.statStampWeek = statStampWeek;
    }

    public int getStatStampMonth() {
        return statStampMonth;
    }

    public void setStatStampMonth(int statStampMonth) {
        this.statStampMonth = statStampMonth;
    }

    public boolean getNightMode() {
        return nightMode;
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

}
