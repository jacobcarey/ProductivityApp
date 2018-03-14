package com.example.b5043566.csc3122;

import android.app.Application;

import java.util.Map;

/**
 * Created by b5043566 on 14/03/2018.
 */

public class ProductivityApp extends Application {

    private User user = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public void setUsername(String username) {
        this.user.setUsername(username);
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        this.user.setEmail(email);
    }

    public int getTimeLimit() {
        return user.getTimeLimit();
    }

    public void setTimeLimit(int timeLimit) {
        this.user.setTimeLimit(timeLimit);
    }

    public int getPowerRemaining() {
        return user.getPowerRemaining();
    }

    public void setPowerRemaining(int powerRemaining) {
        this.user.setPowerRemaining(powerRemaining);
    }

    public long getLastLogin() {
        return user.getLastLogin();
    }

    public void setLastLogin(long lastLogin) {
        this.user.setLastLogin(lastLogin);
    }

    public Map<String, Boolean> getWindows() {
        return user.getWindows();
    }

    public void setWindows(Map<String, Boolean> windows) {
        this.user.setWindows(windows);
    }

    public int getTotalWindows() {
        return user.getTotalWindows();
    }
}