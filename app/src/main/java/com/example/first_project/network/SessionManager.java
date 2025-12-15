package com.example.first_project.network;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS = "session_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DISPLAY_NAME = "display_name";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String userId, String username, String displayName) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .putString(KEY_DISPLAY_NAME, displayName)
                .apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getDisplayName() {
        return prefs.getString(KEY_DISPLAY_NAME, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}

