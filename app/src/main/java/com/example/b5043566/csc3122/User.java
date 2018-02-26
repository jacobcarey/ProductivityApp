package com.example.b5043566.csc3122;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by b5043566 on 22/02/2018.
 */

public class User {

    public String username;
    public String email;
    public int timeLimit;
    public int powerRemaining;
    public Map<String, Boolean> windows;
    public final int totalWindows = 15;
    public long lastLogin;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {

        this.username = username;
        this.email = email;
        this.timeLimit = 0;
        this.powerRemaining = 0;
        this.windows = new HashMap<String, Boolean>();
        this.lastLogin = System.currentTimeMillis();

        // TODO Magic Numbers
        for(int i = 0; i < totalWindows; i++){
            windows.put("w_"+Integer.toString(i), false);
        }
    }

    public String toString() {
        return "User" + username + email + timeLimit + powerRemaining + lastLogin;
    }

}
