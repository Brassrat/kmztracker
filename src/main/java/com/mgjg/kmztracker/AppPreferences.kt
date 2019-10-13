@file:Suppress("unused")

package com.mgjg.kmztracker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.util.Log

/**
 * Created by ja24120 on 6/12/14.
 */
class AppPreferences protected constructor(app: Application) {

  //private final Application app;
  private//app.getApplicationContext();
  val appContext: Context
  val sharedPreferences: SharedPreferences

  private val appResources: Resources
    get() = appContext.resources

  init {
    //this.app = app;
    appContext = app.applicationContext
    sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(app)
  }

  fun getStringResourceIdentifier(name: String): Int {
    return getResourceIdentifier(name, "string")
  }

  fun getResourceIdentifier(name: String, type: String): Int {
    try {
      return appContext.resources.getIdentifier(name, type, appContext.packageName)
    } catch (e: Exception) {
      e.printStackTrace()
      return -1
    }

  }

  fun getIdentifier(key: Int): String {
    return appContext.resources.getString(key)
  }

  fun getByResource(keyName: String, defValue: String?): String? {
    val ii = getStringResourceIdentifier(keyName)
    if (ii <= 0) {
      Log.e(TAG, "invalid resource name " + keyName)
      return defValue
    }
    val key = appContext.resources.getString(ii)
    return getPreferenceString(key, defValue)
  }

  /**
   * Used to obtain a string resource
   *
   * @param key resource id of string resource
   * @return String resource
   */
  fun getAppResourceString(key: Int): String {
    var result: String
    try {
      result = appResources.getString(key)
    } catch (e: Exception) {
      result = "" // prevent exception and NPE
    }

    return result
  }

  fun getAppResourceInteger(key: Int): Int {
    var result: Int
    try {
      result = appResources.getInteger(key)
    } catch (e: Exception) {
      // prevent exception and NPE
      result = 0
    }

    return result
  }

  fun getAppResourceBoolean(key: Int): Boolean {
    try {
      return appResources.getBoolean(key)
    } catch (e: Exception) {
      return false // prevent exception and NPE
    }

  }

  /**
   * `String` variant of getByResourceId, as determined by type of 2nd parameter
   *
   * @param key      string resource id of key string
   * @param defValue
   * @return
   */
  fun getByResourceId(key: Int, defValue: String?): String? {
    return getByResource(getAppResourceString(key), defValue)
  }

  /**
   * int variant of getByResourceId, as determined by type of 2nd parameter
   *
   * @param key
   * @param defValue
   * @return
   */
  fun getByResourceId(key: Int, defValue: Int): Int {
    return getByResource(getAppResourceString(key), defValue)
  }

  /**
   * boolean variant of getByResourceId, as determined by type of 2nd parameter
   *
   * @param key      string resource id of key string
   * @param defValue
   * @return
   */
  fun getByResourceId(key: Int, defValue: Boolean): Boolean {
    return getByResource(getAppResourceString(key), defValue)
  }

  /**
   * double variant of getByResourceId, as determined by type of 2nd parameter
   *
   * @param key      string resource id of key string
   * @param defValue
   * @return
   */
  fun getByResourceId(key: Int, defValue: Double): Double {
    return getByResource(getAppResourceString(key), defValue)
  }

  /**
   * float variant of getByResourceId, as determined by type of 2nd parameter
   *
   * @param key      string resource id of key string
   * @param defValue
   * @return
   */
  fun getByResourceId(key: Int, defValue: Float): Float {
    return getByResource(getAppResourceString(key), defValue)
  }

  /**
   * int variant of getByResourceId, as determined by type of 2nd parameter
   *
   * @param keyName
   * @param defValue
   * @return
   */
  fun getByResource(keyName: String, defValue: Int): Int {
    //return getIntegerFromSettings(keyName, defValue);
    val res = getByResource(keyName, defValue)
    var result: Int
    try {
      result = Integer.valueOf(res)
    } catch (e: NumberFormatException) {
      Log.e(TAG, "non-integer preference value " + res)
      result = 0
    }

    return result
  }

  /**
   * boolean variant of getByResource, as determined by type of 2nd parameter
   *
   * @param keyName  String value of key
   * @param defValue
   * @return
   */
  fun getByResource(keyName: String, defValue: Boolean): Boolean {
    val res = getByResource(keyName, defValue.toString())
    var result: Boolean
    try {
      result = java.lang.Boolean.valueOf(res)
    } catch (e: NumberFormatException) {
      Log.e(TAG, "non-boolean preference value " + res)
      result = false
    }

    return result
  }

  /**
   * double variant of getByResource, as determined by type of 2nd parameter
   *
   * @param keyName  String value of key
   * @param defValue
   * @return
   */
  fun getByResource(keyName: String, defValue: Double): Double {
    //return getDoubleFromSettings(keyName, defValue);
    val res = getByResource(keyName, defValue.toString())
    var result: Double
    try {
      result = java.lang.Double.valueOf(res!!)
    } catch (e: NumberFormatException) {
      Log.e(TAG, "non-double preference value " + res)
      result = 0.0
    }

    return result
  }

  /**
   * float variant of getByResource, as determined by type of 2nd parameter
   *
   * @param keyName  String value of key
   * @param defValue
   * @return
   */
  fun getByResource(keyName: String, defValue: Float): Float {
    //return (float) getDoubleFromSettings(key, defValue);
    val res = getByResource(keyName, defValue.toString())
    var result: Float
    try {
      result = java.lang.Float.valueOf(res!!)
    } catch (e: NumberFormatException) {
      Log.e(TAG, "non-float preference value " + res)
      result = 0.0.toFloat()
    }

    return result
  }

  /**
   * Get Float settings value
   *
   * @param key    Resource ID of 'dotted' NAME of setting, e.g., compass.units, always an R.String.ID
   * @param defKey Resource ID to use to obtain the default value, R.string.ID - since resource file only allows for strings, integer, boolean
   * @return
   */
  fun getFloatByResourceId(key: Int, defKey: Int): Float {
    var result = 0.0f
    var xx = getByResourceId(key, null)
    if (null != xx) {
      try {
        result = java.lang.Float.parseFloat(xx)
      } catch (e: Exception) {
        xx = null // xx is no good, throw it away
      }

    }
    if (xx == null) {
      result = getSettingFloat(defKey)
    }
    return result
  }

  fun getDoubleByResourceId(key: Int, defKey: Int): Double {
    return getDoubleSetting(getAppResourceString(key), defKey)
  }

  /**
   * Get Integer settings value
   *
   * @param key    Resource ID of 'dotted' NAME of setting, e.g., compass.units, always an R.String.ID
   * @param defKey Resource ID to use to obtain the default value, R.integer.ID
   * @return
   */
  fun getIntegerByResourceId(key: Int, defKey: Int): Int {
    return getIntegerSetting(getAppResourceString(key), defKey)
  }

  fun getIntegerByResourceId(key: Int, defKey: Int, min: Int, max: Int): Int {
    var value = getByResourceId(key, getAppResourceInteger(defKey))
    if (value < min) {
      value = min
    }
    if (value > max) {
      value = max
    }
    return value
  }

  /**
   * Get String settings value
   *
   * @param key    Resource ID of NAME of preference, e.g., , always an R.String.ID
   * @param defKey Resource ID to use to obtain the default value, R.string.ID
   * @return
   */
  fun getStringByResourceId(key: Int, defKey: Int): String? {
    return getByResourceId(key, getAppResourceString(defKey))
  }

  /**
   * Get Boolean settings value
   *
   * @param key    Resource ID of 'dotted' NAME of setting, e.g., compass.units, always an R.String.ID
   * @param defKey Resource ID to use to obtain the default value, R.boolean.ID
   * @return
   */
  fun isBooleanByResourceId(key: Int, defKey: Int): Boolean {
    return getByResourceId(key, getAppResourceBoolean(defKey))
  }

  /**
   * gets settings value
   *
   * @param keyName
   * @param defKey
   * @return
   */
  fun getStringSetting(keyName: String, defKey: Int): String? {
    return getByResource(keyName, getAppResourceString(defKey))
  }

  fun getIntegerSetting(keyName: String, defKey: Int): Int {
    return getByResource(keyName, getAppResourceInteger(defKey))
  }

  fun getFloatSetting(keyName: String, defKey: Int): Float {
    var xx = getByResource(keyName, null)
    if (xx != null) {
      try {
        return java.lang.Float.parseFloat(xx)
      } catch (e: Exception) {
        // fall thru
      }

    }
    // no settings value, or value is not valid double
    xx = getAppResourceString(defKey)
    if (xx.isNotEmpty()) {
      try {
        return java.lang.Float.parseFloat(xx)
      } catch (e: Exception) {
      }

    }
    return 0f
  }

  fun getDoubleSetting(keyName: String, defKey: Int): Double {
    var xx = getByResource(keyName, null)
    if (xx != null) {
      try {
        return java.lang.Double.parseDouble(xx)
      } catch (e: Exception) {
        // fall thru
      }

    }
    // no settings value, or value is not valid double
    xx = getAppResourceString(defKey)
    if (xx.isNotEmpty()) {
      try {
        return java.lang.Double.parseDouble(xx)
      } catch (e: Exception) {
      }

    }
    return 0.0
  }

  fun isSettingBoolean(keyName: String, defKey: Int): Boolean {
    return getByResource(keyName, getAppResourceBoolean(defKey))
  }

  // thus all can have same name (except for the addition of isSetting)

  fun getSetting(keyName: String, defValue: String?): String? {
    return getByResource(keyName, defValue)
  }

  protected fun isSetting(keyName: String, defValue: Boolean): Boolean {
    return getByResource(keyName, defValue)
  }

  protected fun getSetting(key: Int, defValue: Int): Int {
    return getByResourceId(key, defValue)
  }

  fun getSetting(key: Int, defValue: String?): String? {
    return getByResourceId(key, defValue)
  }

  fun isSetting(key: Int, defValue: Boolean): Boolean {
    return getByResourceId(key, defValue)
  }

  // COLOR:

  /**
   * retrieves the color value for a given color resource id
   *
   * @param colorResourceId
   * @return a color value
   */
  fun getAppResourceColor(colorResourceId: Int): Int {
    return appResources.getColor(colorResourceId)
  }

  /**
   * retrieves the color value for a given color resource name
   *
   * @param colorKeyName
   * @return a color value
   */
  fun getAppResourceColor(colorKeyName: String): Int {
    return if ("transparent".equals(colorKeyName, ignoreCase = true)) {
      Color.TRANSPARENT
    } else getAppResourceColor(getResourceColorId(colorKeyName))
  }

  /**
   * retrieves a color resource id NOT a color value
   *
   * @param colorKeyName
   * @return a color resource id
   */
  fun getAppResourceColorId(_colorKeyName: String): Int {
    var colorKeyName = _colorKeyName
    // check for obsolete names ...
    if ("labels".equals(colorKeyName, ignoreCase = true)) {
      colorKeyName = "label"
    } else if ("counts".equals(colorKeyName, ignoreCase = true)) {
      colorKeyName = "count"
    }
    return appResources.getIdentifier(colorKeyName, "color", appContext.packageName)
  }

  fun getColorByResourceId(colorId: Int): Int {
    return getAppResourceColor(colorId)
  }

  fun getColorResource(colorName: String): Int {

    return getAppResourceColor(colorName)
  }

  fun getResourceColorId(colorKeyName: String): Int {
    return getAppResourceColorId(colorKeyName)
  }

  // PREFERENCES:

  fun getPreferenceString(key: Int, defKey: Int): String {
    return getPreferenceString(getAppResourceString(key), defKey)
  }

  fun getPreferenceString(keyName: String, defKey: Int): String {
    return if (sharedPreferences.contains(keyName)) {
      try {
        val zz = sharedPreferences.getString(keyName, "")
        if (null == zz) "" else zz
      } catch (e: ClassCastException) {
        // unexpected ... shared preference is not a string ...
        // no idea what to do here since SharedPreferences has no getObject
        // so fall thru and use default
        Log.e(TAG, "sharedPreference $keyName is not a String")
        ""
      }
    } else {
      val zz = getStringSetting(keyName, defKey)
      if (null == zz) "" else zz
    }
  }

  fun getPreferenceString(keyName: String, defValue: String?): String {
    val zz = if (sharedPreferences.contains(keyName)) {
      try {
        sharedPreferences.getString(keyName, "")
      } catch (e: ClassCastException) {
        // unexpected ... shared preference is not a string ...
        // no idea what to do here since SharedPreferences has no getObject
        // so fall thru and use default
        Log.e(TAG, "sharedPreference $keyName is not a String")
        null
      }
    } else {
      getSetting(keyName, defValue)
    }
    return if (null == zz) "" else zz
  }

  fun getPreferenceInteger(key: Int, defKey: Int): Int {
    return getPreferenceInteger(getAppResourceString(key), defKey)
  }

  fun getPreferenceInteger(keyName: String, defKey: Int): Int {
    var result: Int? = null
    try {
      if (sharedPreferences.contains(keyName)) {
        try {
          result = sharedPreferences.getInt(keyName, 0)
        } catch (e: ClassCastException) {
          result = Integer.valueOf(sharedPreferences.getString(keyName, "0")!!)
        }

      }
    } catch (e: Exception) {
      Log.e(TAG, "Error parsing app setting for " + keyName + ": " + e.message)
    }

    if (null == result) {
      result = getIntegerSetting(keyName, defKey) // not in preferences, get setting or default
    }
    return result
  }

  fun isPreference(key: Int, defKey: Int): Boolean {
    return getPreferenceBoolean(key, defKey)
  }

  fun getPreferenceBoolean(key: Int, defKey: Int): Boolean {
    var result: Boolean? = null
    val keyName = getAppResourceString(key)
    if (keyName.isEmpty()) {
      Log.e(TAG, "Specified key is not a valid string resource id: " + key)
    } else {
      try {
        if (sharedPreferences.contains(keyName)) {
          try {
            result = sharedPreferences.getBoolean(keyName, false)
          } catch (e: ClassCastException) {
            result = java.lang.Boolean.valueOf(sharedPreferences.getString(keyName, "false"))
          }

        }
      } catch (e: Exception) {
        Log.e(TAG, "Error parsing app setting for " + keyName + ": " + e.message)
      }

    }
    if (null == result) {
      result = isSettingBoolean(keyName, defKey)
    }
    return result
  }

  fun getPreferenceFloat(key: Int, defKey: Int): Float {
    var result: Float? = null
    val keyName = getAppResourceString(key)
    val defValue = getFloatSetting(keyName, defKey)
    if (keyName.isEmpty()) {
      Log.e(TAG, "Specified key is not a valid string resource id: " + key)
    } else {
      try {
        result = java.lang.Float.valueOf(sharedPreferences.getString(keyName, defValue.toString())!!)
      } catch (e: Exception) {
        Log.e(TAG, "Error parsing app setting for " + keyName + ": " + e.message)
      }

    }
    if (null == result) {
      result = getFloatSetting(keyName, defKey)
    }
    return result
  }

  fun getPreferenceDouble(key: Int, defKey: Int): Double {
    var result: Double? = null
    val keyName = getAppResourceString(key)
    val defValue = getDoubleSetting(keyName, defKey)
    if (keyName.isEmpty()) {
      Log.e(TAG, "Specified key is not a valid string resource id: " + key)
    } else {
      try {
        return java.lang.Double.valueOf(sharedPreferences.getString(keyName, defValue.toString())!!)
      } catch (e: Exception) {
        Log.e(TAG, "Error parsing app setting for " + keyName + ": " + e.message)
      }

    }
    if (null == result) {
      result = getDoubleSetting(keyName, defKey)
    }
    return result
  }

  /**
   * creates a SharedPreference editor, updates a name/value, and commits the change
   * @param keyName the key for the preference to be updated
   * @param defValue the new value for the preference
   * @return true if change is committed
   */
  fun setPreferenceString(keyName: String, defValue: String): Boolean {
    return sharedPreferences.edit().putString(keyName, defValue).commit()
  }

  fun setPreferenceString(keyId: Int, defValue: String): Boolean {
    val keyName = getAppResourceString(keyId)
    return sharedPreferences.edit().putString(keyName, defValue).commit()
  }

  fun registerPreferencesListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
  }

  fun unregisterPreferencesListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
  }

  /**
   * @param settingNameResourceId resource id of name of setting, e.g., sensors.color
   * @param defaultColorResourceId Default color resource id, e.g., R.color.yellow
   */
  fun getColorSettingWithDefaultColorResourceId(
    settingNameResourceId: Int,
    defaultColorResourceId: Int
  ): Int {
    return parseColorWithDefaultColorResource(
      getSetting(settingNameResourceId, null),
      defaultColorResourceId
    )
  }

  /**
   * @param settingNameResourceId resource id of setting name, e.g., sensors.color
   * @param defaultColorName      color name
   * @return int color
   */
  fun getColorSettingWithDefaultColorName(
    settingNameResourceId: Int,
    defaultColorName: String
  ): Int {
    return getColor(getSetting(settingNameResourceId, null), defaultColorName)
  }

  // internal helper methods
  private interface DefaultColorGetter {
    fun get(): Int
  }

  private fun parseColor(colorValue: String, colorGetter: DefaultColorGetter): Int {
    var color: Int
    try {
      color = getColor(colorValue)
    } catch (e: IllegalArgumentException) {
      Log.e(TAG, "invalid color name: " + colorValue)
      color = colorGetter.get()
    } catch (e: Resources.NotFoundException) {
      Log.e(TAG, "invalid color name: " + colorValue)
      color = colorGetter.get()
    }

    return color
  }

  /**
   * @param colorValue             name of color, e.g., dodger
   * @param defaultColorResourceId color resource id to use if colorValue is not defined or valid
   * @return
   */
  private fun parseColorWithDefaultColorResource(
    colorValue: String?,
    defaultColorResourceId: Int
  ): Int {

    val color: Int
    if (null == colorValue) {
      color = getAppResourceColor(defaultColorResourceId)
    } else {
      color = parseColor(colorValue,
        object : DefaultColorGetter {
          override fun get(): Int {
            return getAppResourceColor(defaultColorResourceId)
          }

        })
    }
    return color
  }

  /**
   * returns color value
   *
   * @param colorValue        a color value, either a resource name or a hex constant
   * @param defaultColorValue a color value, either a resource name or a hex constant to use if value is not a valid color
   * @return int color value
   */
  private fun getColor(colorValue: String?, defaultColorValue: String): Int {

    val color: Int
    if (null == colorValue || colorValue.length <= 0) {
      color = getColor(defaultColorValue, 0)
    } else {
      color = parseColor(colorValue,
        object : DefaultColorGetter {
          override fun get(): Int {
            return getColor(defaultColorValue, 0)
          }

        })
    }
    return color
  }

  /**
   * @param colorValue             may be HEX value, system defined color name, or app defined color name
   * @param defaultColorResourceId a app color id
   * @return
   */
  private fun getColor(colorValue: String?, defaultColorResourceId: Int): Int {
    val color: Int
    if (null == colorValue) {
      color = getAppResourceColor(defaultColorResourceId)
    } else {
      color = parseColor(colorValue,
        object : DefaultColorGetter {
          override fun get(): Int {
            return getAppResourceColor(defaultColorResourceId)
          }

        })
    }
    return color
  }

  private fun getColor(colorValue: String): Int {
    var color: Int
    try {
      color = Color.parseColor(colorValue)
    } catch (e: IllegalArgumentException) {
      // not a valid/known color name or constant
      if (colorValue.startsWith("#")) {
        // looks like hex, but its not valid ... just re-throw
        throw e
      }
      // looks like resource name, i.e., one of our colors
      color = getAppResourceColor(colorValue)
    }

    return color
  }

  private fun getSettingFloat(key: Int): Float {
    val xx = getAppResourceString(key)
    if (xx.isNotEmpty()) {
      try {
        return java.lang.Float.parseFloat(xx)
      } catch (e: Exception) {
        // ignore?
        Log.e(TAG, "invalid float setting value: " + xx)
      }

    }
    return 0f
  }

  companion object {

    private val TAG = "App"

    @Synchronized
    fun makeInstance(app: Application) {
      instance = AppPreferences(app)
    }

    @get:Synchronized
    var instance: AppPreferences? = null
      private set
  }

}
