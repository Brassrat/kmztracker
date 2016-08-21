package com.mgjg.kmztracker.preference;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.mgjg.kmztracker.AppPreferences;
import com.mgjg.kmztracker.R;

public class MapPreferencesActivity extends PreferenceActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final Preference pref = findPreference(getString(R.string.map_server_ip));

            final int key = R.string.map_server_preference;
            final String prefName = AppPreferences.getInstance().getIdentifier(key);
            String value = Preferences.getString(prefName, "");
            if (!value.isEmpty())
            {
                String summary = "current: " + value;
                pref.setSummary(summary);
                if (pref instanceof ListPreference)
                {
                    int ii = ((ListPreference) pref).findIndexOfValue(value);
                    ((ListPreference) pref).setValueIndex(ii);
                }
            }
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    // do whatever you want with new value
                    Preferences.putString(prefName, String.valueOf(newValue));
                    // true to update the state of the Preference with the new value
                    // in case you want to disallow the change return false
                    return true;
                }
            });
        }
    }

}

