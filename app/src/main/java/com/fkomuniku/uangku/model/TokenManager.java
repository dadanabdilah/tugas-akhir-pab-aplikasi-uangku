package com.fkomuniku.uangku.model;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String SHARED_PREFS_NAME = "notepro_prefs";
    private static final String TOKEN_KEY = "token_key";
    private static TokenManager instance;
    private SharedPreferences sharedPreferences;

    private TokenManager(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context != null ? context.getApplicationContext() : null);
        }
        return instance;
    }

    public void setToken(String token) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TOKEN_KEY, token);
            editor.apply();
        }
    }

    public String getToken() {
        return sharedPreferences != null ? sharedPreferences.getString(TOKEN_KEY, null) : null;
    }

    public void clearToken() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(TOKEN_KEY);
            editor.apply();
        }
    }
}
