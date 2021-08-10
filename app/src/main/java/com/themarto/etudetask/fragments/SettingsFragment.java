package com.themarto.etudetask.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.themarto.etudetask.BuildConfig;
import com.themarto.etudetask.R;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference shareApp;
    private Preference rateApp;
    private Preference version;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        initializePreferences();
        setupPreferences();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // todo: extract string key theme
        if (key.equals("theme")) {
            onThemePreferenceChanged();
        }
    }

    private void setupPreferences () {
        if (rateApp != null) {
            rateApp.setIntent(openAppInPlayStoreIntent());
        }
        if (shareApp != null) {
            shareApp.setIntent(shareAppIntent());
        }
        if (version != null) {
            version.setSummary(BuildConfig.VERSION_NAME);
        }
    }

    private void initializePreferences() {
        // todo: extract string keys
        rateApp = findPreference("rate");
        shareApp = findPreference("share");
        version = findPreference("version");
    }

    private void onThemePreferenceChanged () {
        // todo: extract string key theme
        String theme = getPreferenceManager().getSharedPreferences().getString("theme", "");
        if (theme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private Intent openAppInPlayStoreIntent(){
        Uri uri = Uri.parse("market://details?id=" + requireContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        return goToMarket;
    }

    private Intent shareAppIntent () {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        String shareMessage= "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        return shareIntent;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}