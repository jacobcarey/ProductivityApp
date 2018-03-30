package com.example.b5043566.csc3122;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by b5043566 on 22/02/2018.
 */

public class User {

    private String username;
    private String email;
    private int timeLimit;
    private int powerRemaining;
    private long lastLogin;
    private Map<String, Boolean> windows;
    private final int totalWindows = 15;
    private long lastActive;



    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.username = "";
        this.email = "";
        this.timeLimit = 0;
        this.powerRemaining = 0;
        this.windows = new HashMap<String, Boolean>();
        this.lastLogin = System.currentTimeMillis();
        this.lastActive = 0;

        // TODO Magic Numbers
        for(int i = 0; i < totalWindows; i++){
            windows.put("w_"+Integer.toString(i), false);
        }
    }

    public User(String username, String email) {

        this.username = username;
        this.email = email;
        this.timeLimit = 0;
        this.powerRemaining = 0;
        this.windows = new HashMap<String, Boolean>();
        this.lastLogin = System.currentTimeMillis();
        this.lastActive = 0;

        // TODO Magic Numbers
        for(int i = 0; i < totalWindows; i++){
            windows.put("w_"+Integer.toString(i), false);
        }
    }

    public String toString() {
        return "User" + username + email + timeLimit + powerRemaining + lastLogin;
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

    public int getTotalWindows() {
        return totalWindows;
    }
}
