package com.mgjg.kmztracker.preference

import android.os.Bundle
import android.preference.*
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
      var value = Preferences.getString(prefName, "")
      if (!value.isEmpty() && (pref is ListPreference)) {
          var ii = pref.findIndexOfValue(value)
          if (ii < 0) {
            ii = 0
            pref.setValueIndex(ii)
            value = pref.getValue()
            Preferences.putString(prefName, value)
          }
          val summary = "current: " + value
          pref.summary = summary
          pref.setValueIndex(ii)
      }
      pref.onPreferenceChangeListener =
          Preference.OnPreferenceChangeListener { preference, newValue ->
            val value = newValue.toString()
            Preferences.putString(prefName, value);
            if (pref is ListPreference) {
              val ii = pref.findIndexOfValue(value)
              // do whatever you want with new value
              // true to update the state of the Preference with the new value
              // in case you want to disallow the change return false
              val summary = "current: " + value
              pref.summary = summary
              pref.setValueIndex(ii)
            }
            true
          }
    }
  }

}

