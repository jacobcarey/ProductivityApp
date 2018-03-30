package com.example.b5043566.csc3122;

import android.app.Application;

import java.util.Map;

/**
 * Created by b5043566 on 14/03/2018.
 */

public class ProductivityApp extends Application {

    private User user = new User();
    private int powerPerHour;

    public int getPowerPerHour(){
        return powerPerHour;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUsername(String username) {
        this.user.setUsername(username);
    }

    public void setEmail(String email) {
        this.user.setEmail(email);
    }

    public void setTimeLimit(int timeLimit) {
        this.user.setTimeLimit(timeLimit);
    }

    public void setPowerRemaining(int powerRemaining) {
        this.user.setPowerRemaining(powerRemaining);
    }

    public void setLastLogin(long lastLogin) {
        this.user.setLastLogin(lastLogin);
    }

    public void setWindows(Map<String, Boolean> windows) {
        this.user.setWindows(windows);
    }

    public void setPowerPerHour(int powerPerHour){
        this.powerPerHour = powerPerHour;
    }
}