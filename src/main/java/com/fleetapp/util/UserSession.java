package com.fleetapp.util;

import com.fleetapp.model.User;

public class UserSession {
    // Static variable to hold the logged-in user globally
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}