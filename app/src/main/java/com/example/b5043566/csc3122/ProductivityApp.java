package com.example.b5043566.csc3122;

import android.app.Application;

/**
 * Created by b5043566 on 14/03/2018.
 */

public class ProductivityApp extends Application {

    private User user = new User();
    private int powerPerHour;

    public int getPowerPerHour() {
        return powerPerHour;
    }

    public int setPowerPerHour(int powerPerHour) {
        return this.powerPerHour = powerPerHour;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}