package com.themarto.etudetask.app;

import android.app.Application;
import android.content.SharedPreferences;

import com.themarto.etudetask.R;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import io.realm.Realm;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        setupDarkMode();
    }

    private void setupDarkMode () {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String darkTheme = getResources().getStringArray(R.array.theme_values)[1];
        String theme = pref.getString("theme", "");
        if (theme.equals(darkTheme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
