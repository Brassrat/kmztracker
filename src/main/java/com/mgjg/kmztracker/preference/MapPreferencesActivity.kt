package com.mgjg.kmztracker.preference

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment

import com.mgjg.kmztracker.AppPreferences
import com.mgjg.kmztracker.R

class MapPreferencesActivity : PreferenceActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    fragmentManager.beginTransaction().replace(android.R.id.content, MyPreferenceFragment())
      .commit()
  }

  class MyPreferenceFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      addPreferencesFromResource(R.xml.preferences)

      val pref = findPreference(getString(R.string.map_server_ip))

      val key = R.string.map_server_preference
      val prefName = AppPreferences.instance!!.getIdentifier(key)
      val value = Preferences.getString(prefName, "")
      if (!value.isEmpty()) {
        val summary = "current: " + value
        pref.summary = summary
        if (pref is ListPreference) {
          val ii = pref.findIndexOfValue(value)
          pref.setValueIndex(ii)
        }
      }
      pref.onPreferenceChangeListener =
          Preference.OnPreferenceChangeListener { preference, newValue ->
            // do whatever you want with new value
            Preferences.putString(prefName, newValue.toString())
            // true to update the state of the Preference with the new value
            // in case you want to disallow the change return false
            true
          }
    }
  }

}

