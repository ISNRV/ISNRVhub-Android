package com.isnrv;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.isnrv.notification.Notifications;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat
            implements Preference.OnPreferenceChangeListener {
        private static final String KEY = "notifications";
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String key) {
            setPreferencesFromResource(R.xml.preferences, key);
            final Preference preference = findPreference(KEY);
            if(preference != null) {
                preference.setOnPreferenceChangeListener(this);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            if(KEY.equals(preference.getKey())) {
                Notifications.toggle(getContext(), (boolean) value);
            }
            return true;
        }
    }
}