package com.richardkoster.fhictagenda.application;

import android.app.Application;
import android.content.SharedPreferences;

import com.richardkoster.fhictagenda.api.objects.User;

public class CalendarApplication extends Application {

    private static final String KEY_TOKEN = "access_token";
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        if (mEditor == null) {
            mEditor = getSharedPreferences().edit();
        }
        return mEditor;
    }

    public void storeToken(String token) {
        getEditor().putString(KEY_TOKEN, token).commit();
    }

    public String getToken() {
        return getSharedPreferences().getString(KEY_TOKEN, null);
    }
}
