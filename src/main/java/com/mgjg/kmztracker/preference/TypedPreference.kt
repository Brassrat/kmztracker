package com.mgjg.kmztracker.preference

import android.preference.Preference
import android.preference.PreferenceFragment

import com.mgjg.kmztracker.AppPreferences

/**
 * Class to hold attributes of a preference
 * Created by ja24120 on 1/13/15.
 */
class TypedPreference {

  val name: String
  private val type: TYPE
  val keyId: Int
  private val summary: Int
  val defaultId: Int

  // should never get here
  val settingString: String
    get() {
      val settings = AppPreferences.instance
      val result: Any
      if (keyId > 0) {
        when (type) {
          TypedPreference.TYPE.STRING -> result = settings!!.getPreferenceString(keyId, defaultId)
          TypedPreference.TYPE.INTEGER -> result = settings!!.getPreferenceInteger(keyId, defaultId)
          TypedPreference.TYPE.BOOLEAN -> result = settings!!.getPreferenceBoolean(keyId, defaultId)
          TypedPreference.TYPE.FLOAT -> result = settings!!.getPreferenceFloat(keyId, defaultId)
          TypedPreference.TYPE.DOUBLE -> result = settings!!.getPreferenceDouble(keyId, defaultId)
          else -> throw IllegalStateException("Missing type branch for " + name)
        }
      } else {
        result = ""
      }

      return result.toString()
    }

  // should never get here
  val settingInteger: Int
    get() {
      val settings = AppPreferences.instance
      val result: Int
      if (keyId > 0) {
        when (type) {
          TypedPreference.TYPE.STRING -> result =
              Integer.valueOf(settings!!.getPreferenceString(keyId, defaultId))!!
          TypedPreference.TYPE.INTEGER -> result = settings!!.getPreferenceInteger(keyId, defaultId)
          TypedPreference.TYPE.BOOLEAN -> result =
              if (settings!!.getPreferenceBoolean(keyId, defaultId)) 1 else 0
          TypedPreference.TYPE.FLOAT -> result =
              settings!!.getPreferenceFloat(keyId, defaultId).toInt()
          TypedPreference.TYPE.DOUBLE -> result =
              settings!!.getPreferenceDouble(keyId, defaultId).toInt()
          else -> throw IllegalStateException("Missing type branch for " + name)
        }
      } else {
        result = 0
      }
      return result
    }

  // should never get here
  val settingDouble: Double
    get() {
      val settings = AppPreferences.instance
      val result: Double
      if (keyId > 0) {
        when (type) {
          TypedPreference.TYPE.STRING -> result =
              java.lang.Double.valueOf(settings!!.getPreferenceString(keyId, defaultId))!!
          TypedPreference.TYPE.INTEGER -> result =
              settings!!.getPreferenceInteger(keyId, defaultId).toDouble()
          TypedPreference.TYPE.BOOLEAN -> result =
              if (settings!!.getPreferenceBoolean(keyId, defaultId)) 1.0 else 0.0
          TypedPreference.TYPE.FLOAT -> result =
              settings!!.getPreferenceFloat(keyId, defaultId).toDouble()
          TypedPreference.TYPE.DOUBLE -> result = settings!!.getPreferenceDouble(keyId, defaultId)
          else -> throw IllegalStateException("Missing type branch for " + name)
        }
      } else {
        return 0.0
      }
      return result
    }

  // should never get here
  val settingFloat: Float
    get() {
      val settings = AppPreferences.instance
      val result: Float
      if (keyId > 0) {
        when (type) {
          TypedPreference.TYPE.STRING -> result =
              java.lang.Float.valueOf(settings!!.getPreferenceString(keyId, defaultId))!!
          TypedPreference.TYPE.INTEGER -> result =
              settings!!.getPreferenceInteger(keyId, defaultId).toFloat()
          TypedPreference.TYPE.BOOLEAN -> result =
              if (settings!!.getPreferenceBoolean(keyId, defaultId)) 1.0f else 0.0f
          TypedPreference.TYPE.FLOAT -> result = settings!!.getPreferenceFloat(keyId, defaultId)
          TypedPreference.TYPE.DOUBLE -> result =
              settings!!.getPreferenceDouble(keyId, defaultId).toFloat()
          else -> throw IllegalStateException("Missing type branch for " + name)
        }
      } else {
        result = 0.0f
      }
      return result
    }

  /**
   * caller wants the boolean value of the option, which may or may not have a preference value
   * @return boolean
   */// should never get here
  val isSettingBoolean: Boolean
    get() {
      val settings = AppPreferences.instance
      val result: Boolean
      if (keyId > 0) {
        when (type) {
          TypedPreference.TYPE.STRING -> result =
              java.lang.Boolean.valueOf(settings!!.getPreferenceString(keyId, defaultId))!!
          TypedPreference.TYPE.INTEGER -> result = settings!!.getPreferenceInteger(
            keyId,
            defaultId
          ) != 0
          TypedPreference.TYPE.BOOLEAN -> result = settings!!.getPreferenceBoolean(keyId, defaultId)
          TypedPreference.TYPE.FLOAT -> result = settings!!.getPreferenceFloat(keyId, defaultId) !=
              0f
          TypedPreference.TYPE.DOUBLE -> result = settings!!.getPreferenceDouble(
            keyId,
            defaultId
          ) != 0.0
          else -> throw IllegalStateException("Missing type branch for " + name)
        }
      } else {
        result = false
      }
      return result
    }

  enum class TYPE {
    STRING,
    INTEGER,
    BOOLEAN,
    FLOAT,
    DOUBLE
  }

  constructor(name: String, tt: TYPE, key: Int, summary: Int, def: Int) {
    this.name = name
    this.type = tt
    this.keyId = key
    this.summary = summary
    this.defaultId = def
  }

  constructor(name: String, tt: TYPE, key: String, def: String, summary: String) {
    this.name = name
    this.type = tt
    this.keyId = AppPreferences.instance!!.getStringResourceIdentifier(key)
    this.summary = AppPreferences.instance!!.getStringResourceIdentifier(summary)
    this.defaultId = AppPreferences.instance!!.getStringResourceIdentifier(def)
  }

  fun setPreference(pref: PreferenceFragment) {
    val settings = AppPreferences.instance
    when (type) {
      TypedPreference.TYPE.STRING -> setPreference(
        pref,
        settings!!.getPreferenceString(keyId, defaultId)
      )
      TypedPreference.TYPE.INTEGER -> setPreference(
        pref,
        settings!!.getPreferenceInteger(keyId, defaultId).toString()
      )
      TypedPreference.TYPE.BOOLEAN -> setPreference(
        pref,
        settings!!.getPreferenceBoolean(keyId, defaultId).toString()
      )
      TypedPreference.TYPE.FLOAT -> setPreference(
        pref,
        settings!!.getPreferenceFloat(keyId, defaultId).toString()
      )
      TypedPreference.TYPE.DOUBLE -> setPreference(
        pref,
        settings!!.getPreferenceDouble(keyId, defaultId).toString()
      )
      else ->
        // should never get here
        throw IllegalStateException("Missing type branch for " + name)
    }
  }

  fun setPreference(frag: PreferenceFragment, defValue: Int) {
    setPreference(frag, defValue.toString())
  }

  fun setPreference(frag: PreferenceFragment, defValue: Boolean) {
    setPreference(frag, defValue.toString())
  }

  fun setPreference(frag: PreferenceFragment, defValue: Float) {
    setPreference(frag, defValue.toString())
  }

  fun setPreference(frag: PreferenceFragment, defValue: Double) {
    setPreference(frag, defValue.toString())
  }

  /**
   * Allows setup of a preference entry from other than settings,
   * for example from a supplier descriptor
   *
   * @param frag
   * @param defValue
   */
  fun setPreference(frag: PreferenceFragment, defValue: Any?) {
    var defValue = defValue
    val pp = frag.findPreference(frag.getString(keyId))
    if (null != pp) {
      defValue = if (null == defValue) "" else defValue
      pp.setDefaultValue(defValue)
      pp.summary = frag.getString(summary) + ": " + defValue
    }
  }

}

