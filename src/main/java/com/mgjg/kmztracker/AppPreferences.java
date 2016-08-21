package com.mgjg.kmztracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by ja24120 on 6/12/14.
 */
public class AppPreferences
{

    private static final String TAG = "App";

    public static synchronized AppPreferences getInstance()
    {
        return me;
    }

    public static synchronized void makeInstance(Application app)
    {
        me = new AppPreferences(app);
    }

    private static AppPreferences me;

    //private final Application app;
    private final Context appContext;
    private final SharedPreferences sharedPreferences;

    @SuppressWarnings("unused")
    protected AppPreferences(Application app)
    {
        //this.app = app;
        appContext = app.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);
    }

    @SuppressWarnings("unused")
    public final SharedPreferences getSharedPreferences()
    {
        return sharedPreferences;
    }

    private Resources getAppResources()
    {
        return getAppContext().getResources();
    }

    private Context getAppContext()
    {
        return appContext; //app.getApplicationContext();
    }

    public int getStringResourceIdentifier(String name)
    {
        return getResourceIdentifier(name, "string");
    }

    public int getResourceIdentifier(String name, String type)
    {
        try
        {
            return getAppContext().getResources().getIdentifier(name, type, getAppContext().getPackageName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public final String getIdentifier(int key)
    {
        return getAppContext().getResources().getString(key);
    }

    @SuppressWarnings("unused")
    public final String getByResource(String keyName, String defValue)
    {
        int ii = getStringResourceIdentifier(keyName);
        if (ii <= 0)
        {
            Log.e(TAG, "invalid resource name " + keyName);
            return defValue;
        }
        String key = getAppContext().getResources().getString(ii);
        return getPreferenceString(key, defValue);
    }

    /**
     * Used to obtain a string resource
     *
     * @param key resource id of string resource
     * @return String resource
     */
    @SuppressWarnings("unused")
    public final String getAppResourceString(int key)
    {
        String result;
        try
        {
            result = getAppResources().getString(key);
        }
        catch (Exception e)
        {
            result = ""; // prevent exception and NPE
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final int getAppResourceInteger(int key)
    {
        int result;
        try
        {
            result = getAppResources().getInteger(key);
        }
        catch (Exception e)
        {
            // prevent exception and NPE
            result = 0;
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final boolean getAppResourceBoolean(int key)
    {
        try
        {
            return getAppResources().getBoolean(key);
        }
        catch (Exception e)
        {
            return false; // prevent exception and NPE
        }
    }

    /**
     * <code>String</code> variant of getByResourceId, as determined by type of 2nd parameter
     *
     * @param key      string resource id of key string
     * @param defValue
     * @return
     */
    @SuppressWarnings("unused")
    public String getByResourceId(int key, String defValue)
    {
        return getByResource(getAppResourceString(key), defValue);
    }

    /**
     * int variant of getByResourceId, as determined by type of 2nd parameter
     *
     * @param key
     * @param defValue
     * @return
     */
    @SuppressWarnings("unused")
    public int getByResourceId(int key, int defValue)
    {
        return getByResource(getAppResourceString(key), defValue);
    }

    /**
     * boolean variant of getByResourceId, as determined by type of 2nd parameter
     *
     * @param key      string resource id of key string
     * @param defValue
     * @return
     */
    @SuppressWarnings("unused")
    public boolean getByResourceId(int key, boolean defValue)
    {
        return getByResource(getAppResourceString(key), defValue);
    }

    /**
     * double variant of getByResourceId, as determined by type of 2nd parameter
     *
     * @param key      string resource id of key string
     * @param defValue
     * @return
     */
    @SuppressWarnings("unused")
    public double getByResourceId(int key, double defValue)
    {
        return getByResource(getAppResourceString(key), defValue);
    }

    /**
     * float variant of getByResourceId, as determined by type of 2nd parameter
     *
     * @param key      string resource id of key string
     * @param defValue
     * @return
     */
    @SuppressWarnings("unused")
    public float getByResourceId(int key, float defValue)
    {
        return getByResource(getAppResourceString(key), defValue);
    }

    /**
     * int variant of getByResourceId, as determined by type of 2nd parameter
     *
     * @param keyName
     * @param defValue
     * @return
     */
    @SuppressWarnings("unused")
    public final int getByResource(String keyName, int defValue)
    {
        //return getIntegerFromSettings(keyName, defValue);
        String res = getByResource(keyName, "0");
        int result;
        try
        {
            result = Integer.valueOf(res);
        }
        catch (ClassCastException e)
        {
            Log.e(TAG, "non-integer preference value " + res);
            result = 0;
        }
        return result;
    }

    /**
     * boolean variant of getByResource, as determined by type of 2nd parameter
     *
     * @param keyName  String value of key
     * @param defValue
     * @return
     */
    @SuppressWarnings("unused")
    public final boolean getByResource(String keyName, boolean defValue)
    {
        String res = getByResource(keyName, String.valueOf(defValue));
        boolean result;
        try
        {
            result = Boolean.valueOf(res);
        }
        catch (ClassCastException e)
        {
            Log.e(TAG, "non-boolean preference value " + res);
            result = false;
        }
        return result;
    }

    /**
     * double variant of getByResource, as determined by type of 2nd parameter
     *
     * @param keyName  String value of key
     * @param defValue
     * @return
     */
    @SuppressWarnings("unused")
    public double getByResource(String keyName, double defValue)
    {
        //return getDoubleFromSettings(keyName, defValue);
        String res = getByResource(keyName, String.valueOf(defValue));
        double result;
        try
        {
            result = Double.valueOf(res);
        }
        catch (ClassCastException e)
        {
            Log.e(TAG, "non-double preference value " + res);
            result = 0.0;
        }
        return result;
    }

    /**
     * float variant of getByResource, as determined by type of 2nd parameter
     *
     * @param keyName  String value of key
     * @param defValue
     * @return
     */
    public float getByResource(String keyName, float defValue)
    {
        //return (float) getDoubleFromSettings(key, defValue);
        String res = getByResource(keyName, String.valueOf(defValue));
        float result;
        try
        {
            result = Float.valueOf(res);
        }
        catch (ClassCastException e)
        {
            Log.e(TAG, "non-float preference value " + res);
            result = (float) 0.0;
        }
        return result;
    }

    /**
     * Get Float settings value
     *
     * @param key    Resource ID of 'dotted' NAME of setting, e.g., compass.units, always an R.String.ID
     * @param defKey Resource ID to use to obtain the default value, R.string.ID - since resource file only allows for strings, integer, boolean
     * @return
     */
    @SuppressWarnings("unused")
    public final float getFloatByResourceId(int key, int defKey)
    {
        float result = 0.0f;
        String xx = getByResourceId(key, null);
        if (null != xx)
        {
            try
            {
                result = Float.parseFloat(xx);
            }
            catch (Exception e)
            {
                xx = null; // xx is no good, throw it away
            }
        }
        if (xx == null)
        {
            result = getSettingFloat(defKey);
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final double getDoubleByResourceId(int key, int defKey)
    {
        return getDoubleSetting(getAppResourceString(key), defKey);
    }

    /**
     * Get Integer settings value
     *
     * @param key    Resource ID of 'dotted' NAME of setting, e.g., compass.units, always an R.String.ID
     * @param defKey Resource ID to use to obtain the default value, R.integer.ID
     * @return
     */
    @SuppressWarnings("unused")
    public final int getIntegerByResourceId(int key, int defKey)
    {
        return getIntegerSetting(getAppResourceString(key), defKey);
    }

    @SuppressWarnings("unused")
    public final int getIntegerByResourceId(int key, int defKey, int min, int max)
    {
        int value = getByResourceId(key, getAppResourceInteger(defKey));
        if (value < min)
        {
            value = min;
        }
        if (value > max)
        {
            value = max;
        }
        return value;
    }

    /**
     * Get String settings value
     *
     * @param key    Resource ID of NAME of preference, e.g., , always an R.String.ID
     * @param defKey Resource ID to use to obtain the default value, R.string.ID
     * @return
     */
    @SuppressWarnings("unused")
    public final String getStringByResourceId(int key, int defKey)
    {
        return getByResourceId(key, getAppResourceString(defKey));
    }

    /**
     * Get Boolean settings value
     *
     * @param key    Resource ID of 'dotted' NAME of setting, e.g., compass.units, always an R.String.ID
     * @param defKey Resource ID to use to obtain the default value, R.boolean.ID
     * @return
     */
    @SuppressWarnings("unused")
    public final boolean isBooleanByResourceId(int key, int defKey)
    {
        return getByResourceId(key, getAppResourceBoolean(defKey));
    }

    /**
     * gets settings value
     *
     * @param keyName
     * @param defKey
     * @return
     */
    @SuppressWarnings("unused")
    public final String getStringSetting(String keyName, int defKey)
    {
        return getByResource(keyName, getAppResourceString(defKey));
    }

    @SuppressWarnings("unused")
    public final int getIntegerSetting(String keyName, int defKey)
    {
        return getByResource(keyName, getAppResourceInteger(defKey));
    }

    @SuppressWarnings("unused")
    public final float getFloatSetting(String keyName, int defKey)
    {
        String xx = getByResource(keyName, null);
        if (xx != null)
        {
            try
            {
                return Float.parseFloat(xx);
            }
            catch (Exception e)
            {
                // fall thru
            }
        }
        // no settings value, or value is not valid double
        xx = getAppResourceString(defKey);
        if (!xx.isEmpty())
        {
            try
            {
                return Float.parseFloat(xx);
            }
            catch (Exception e)
            {
            }
        }
        return 0;
    }

    @SuppressWarnings("unused")
    public final double getDoubleSetting(String keyName, int defKey)
    {
        String xx = getByResource(keyName, null);
        if (xx != null)
        {
            try
            {
                return Double.parseDouble(xx);
            }
            catch (Exception e)
            {
                // fall thru
            }
        }
        // no settings value, or value is not valid double
        xx = getAppResourceString(defKey);
        if (!xx.isEmpty())
        {
            try
            {
                return Double.parseDouble(xx);
            }
            catch (Exception e)
            {
            }
        }
        return 0;
    }

    @SuppressWarnings("unused")
    public final boolean isSettingBoolean(String keyName, int defKey)
    {
        return getByResource(keyName, getAppResourceBoolean(defKey));
    }

    // thus all can have same name (except for the addition of isSetting)

    @SuppressWarnings("unused")
    public final String getSetting(String keyName, String defValue)
    {
        return getByResource(keyName, defValue);
    }

    @SuppressWarnings("unused")
    protected final boolean isSetting(String keyName, boolean defValue)
    {
        return getByResource(keyName, defValue);
    }

    @SuppressWarnings("unused")
    protected final int getSetting(int key, int defValue)
    {
        return getByResourceId(key, defValue);
    }

    @SuppressWarnings("unused")
    public final String getSetting(int key, String defValue)
    {
        return getByResourceId(key, defValue);
    }

    @SuppressWarnings("unused")
    public final boolean isSetting(int key, boolean defValue)
    {
        return getByResourceId(key, defValue);
    }

    // COLOR:

    /**
     * retrieves the color value for a given color resource id
     *
     * @param colorResourceId
     * @return a color value
     */
    public final int getAppResourceColor(int colorResourceId)
    {
        return getAppResources().getColor(colorResourceId);
    }

    /**
     * retrieves the color value for a given color resource name
     *
     * @param colorKeyName
     * @return a color value
     */
    public final int getAppResourceColor(String colorKeyName)
    {
        if ("transparent".equalsIgnoreCase(colorKeyName))
        {
            return Color.TRANSPARENT;
        }
        return getAppResourceColor(getResourceColorId(colorKeyName));
    }

    /**
     * retrieves a color resource id NOT a color value
     *
     * @param colorKeyName
     * @return a color resource id
     */
    public final int getAppResourceColorId(String colorKeyName)
    {
        // check for obsolete names ...
        if ("labels".equalsIgnoreCase(colorKeyName))
        {
            colorKeyName = "label";
        }
        else if ("counts".equalsIgnoreCase(colorKeyName))
        {
            colorKeyName = "count";
        }
        return getAppResources().getIdentifier(colorKeyName, "color", getAppContext().getPackageName());
    }

    @SuppressWarnings("unused")
    public int getColorByResourceId(int colorId)
    {
        return getAppResourceColor(colorId);
    }

    @SuppressWarnings("unused")
    public int getColorResource(String colorName)
    {

        return getAppResourceColor(colorName);
    }

    @SuppressWarnings("unused")
    public int getResourceColorId(String colorKeyName)
    {
        return getAppResourceColorId(colorKeyName);
    }

    // PREFERENCES:

    @SuppressWarnings("unused")
    public final String getPreferenceString(int key, int defKey)
    {
        return getPreferenceString(getAppResourceString(key), defKey);
    }

    @SuppressWarnings("unused")
    public final String getPreferenceString(String keyName, int defKey)
    {
        String result = null;
        if (sharedPreferences.contains(keyName))
        {
            try
            {
                result = sharedPreferences.getString(keyName, "");
            }
            catch (ClassCastException e)
            {
                // unexpected ... shared preference is not a string ...
                // no idea what to do here since SharedPreferences has no getObject
                // so fall thru and use default
                Log.e(TAG, "sharedPreference " + keyName + " is not a String");
            }
        }
        if (null == result)
        {
            result = getStringSetting(keyName, defKey);
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final String getPreferenceString(String keyName, String defValue)
    {
        String result = null;
        if (sharedPreferences.contains(keyName))
        {
            try
            {
                result = sharedPreferences.getString(keyName, "");
            }
            catch (ClassCastException e)
            {
                // unexpected ... shared preference is not a string ...
                // no idea what to do here since SharedPreferences has no getObject
                // so fall thru and use default
                Log.e(TAG, "sharedPreference " + keyName + " is not a String");
            }
        }
        if (null == result)
        {
            result = getSetting(keyName, defValue);
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final int getPreferenceInteger(int key, int defKey)
    {
        return getPreferenceInteger(getAppResourceString(key), defKey);
    }

    @SuppressWarnings("unused")
    public final int getPreferenceInteger(String keyName, int defKey)
    {
        Integer result = null;
        try
        {
            if (sharedPreferences.contains(keyName))
            {
                try
                {
                    result = sharedPreferences.getInt(keyName, 0);
                }
                catch (ClassCastException e)
                {
                    result = Integer.valueOf(sharedPreferences.getString(keyName, "0"));
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error parsing app setting for " + keyName + ": " + e.getMessage());
        }
        if (null == result)
        {
            result = getIntegerSetting(keyName, defKey); // not in preferences, get setting or default
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final boolean isPreference(int key, int defKey)
    {
        return getPreferenceBoolean(key, defKey);
    }

    @SuppressWarnings("unused")
    public final boolean getPreferenceBoolean(int key, int defKey)
    {
        Boolean result = null;
        String keyName = getAppResourceString(key);
        if (keyName.isEmpty())
        {
            Log.e(TAG, "Specified key is not a valid string resource id: " + key);
        }
        else
        {
            try
            {
                if (sharedPreferences.contains(keyName))
                {
                    try
                    {
                        result = sharedPreferences.getBoolean(keyName, false);
                    }
                    catch (ClassCastException e)
                    {
                        result = Boolean.valueOf(sharedPreferences.getString(keyName, "false"));
                    }
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error parsing app setting for " + keyName + ": " + e.getMessage());
            }
        }
        if (null == result)
        {
            result = isSettingBoolean(keyName, defKey);
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final float getPreferenceFloat(int key, int defKey)
    {
        Float result = null;
        final String keyName = getAppResourceString(key);
        final Float defValue = getFloatSetting(keyName, defKey);
        if (keyName.isEmpty())
        {
            Log.e(TAG, "Specified key is not a valid string resource id: " + key);
        }
        else
        {
            try
            {
                result = Float.valueOf(sharedPreferences.getString(keyName, String.valueOf(defValue)));
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error parsing app setting for " + keyName + ": " + e.getMessage());
            }
        }
        if (null == result)
        {
            result = getFloatSetting(keyName, defKey);
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final double getPreferenceDouble(int key, int defKey)
    {
        Double result = null;
        final String keyName = getAppResourceString(key);
        final Double defValue = getDoubleSetting(keyName, defKey);
        if (keyName.isEmpty())
        {
            Log.e(TAG, "Specified key is not a valid string resource id: " + key);
        }
        else
        {
            try
            {
                return Double.valueOf(sharedPreferences.getString(keyName, String.valueOf(defValue)));
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error parsing app setting for " + keyName + ": " + e.getMessage());
            }
        }
        if (null == result)
        {
            result = getDoubleSetting(keyName, defKey);
        }
        return result;
    }

    @SuppressWarnings("unused")
    /**
     * creates a SharedPreference editor, updates a name/value, and commits the change
     * @param keyName the key for the preference to be updated
     * @param defValue the new value for the preference
     * @return true if change is committed
     */
    public final boolean setPreferenceString(String keyName, String defValue)
    {
        return getSharedPreferences().edit().putString(keyName, defValue).commit();
    }

    @SuppressWarnings("unused")
    public final boolean setPreferenceString(int keyId, String defValue)
    {
        String keyName = getAppResourceString(keyId);
        return getSharedPreferences().edit().putString(keyName, defValue).commit();
    }

    @SuppressWarnings("unused")
    public final void registerPreferencesListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @SuppressWarnings("unused")
    public final void unregisterPreferencesListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @SuppressWarnings("unused")
    /**
     * @param settingNameResourceId resource id of name of setting, e.g., sensors.color
     * @param defaultColorResourceId Default color resource id, e.g., R.color.yellow
     */
    public final int getColorSettingWithDefaultColorResourceId(int settingNameResourceId, int defaultColorResourceId)
    {
        return parseColorWithDefaultColorResource(getSetting(settingNameResourceId, null), defaultColorResourceId);
    }

    /**
     * @param settingNameResourceId resource id of setting name, e.g., sensors.color
     * @param defaultColorName      color name
     * @return int color
     */
    public final int getColorSettingWithDefaultColorName(int settingNameResourceId, String defaultColorName)
    {
        return getColor(getSetting(settingNameResourceId, null), defaultColorName);
    }

    // internal helper methods
    private interface DefaultColorGetter
    {
        int get();
    }

    private int parseColor(String colorValue, DefaultColorGetter colorGetter)
    {
        int color;
        try
        {
            color = getColor(colorValue);
        }
        catch (IllegalArgumentException e)
        {
            Log.e(TAG, "invalid color name: " + colorValue);
            color = colorGetter.get();
        }
        catch (Resources.NotFoundException e)
        {
            Log.e(TAG, "invalid color name: " + colorValue);
            color = colorGetter.get();
        }
        return color;
    }

    /**
     * @param colorValue             name of color, e.g., dodger
     * @param defaultColorResourceId color resource id to use if colorValue is not defined or valid
     * @return
     */
    private final int parseColorWithDefaultColorResource(String colorValue, final int defaultColorResourceId)
    {

        int color;
        if (null == colorValue)
        {
            color = getAppResourceColor(defaultColorResourceId);
        }
        else
        {
            color = parseColor(colorValue,
                    new DefaultColorGetter()
                    {
                        public int get()
                        {
                            return getAppResourceColor(defaultColorResourceId);
                        }

                    });
        }
        return color;
    }

    /**
     * returns color value
     *
     * @param colorValue        a color value, either a resource name or a hex constant
     * @param defaultColorValue a color value, either a resource name or a hex constant to use if value is not a valid color
     * @return int color value
     */
    private final int getColor(String colorValue, final String defaultColorValue)
    {

        int color;
        if ((null == colorValue) || (colorValue.length() <= 0))
        {
            color = getColor(defaultColorValue, 0);
        }
        else
        {
            color = parseColor(colorValue,
                    new DefaultColorGetter()
                    {
                        public int get()
                        {
                            return getColor(defaultColorValue, 0);
                        }

                    });
        }
        return color;
    }

    /**
     * @param colorValue             may be HEX value, system defined color name, or app defined color name
     * @param defaultColorResourceId a app color id
     * @return
     */
    private int getColor(String colorValue, final int defaultColorResourceId)
    {
        int color;
        if (null == colorValue)
        {
            color = getAppResourceColor(defaultColorResourceId);
        }
        else
        {
            color = parseColor(colorValue,
                    new DefaultColorGetter()
                    {
                        public int get()
                        {
                            return getAppResourceColor(defaultColorResourceId);
                        }

                    });
        }
        return color;
    }

    private int getColor(String colorValue)
    {
        int color;
        try
        {
            color = Color.parseColor(colorValue);
        }
        catch (IllegalArgumentException e)
        {
            // not a valid/known color name or constant
            if (colorValue.startsWith("#"))
            {
                // looks like hex, but its not valid ... just re-throw
                throw e;
            }
            // looks like resource name, i.e., one of our colors
            color = getAppResourceColor(colorValue);
        }
        return color;
    }

    private float getSettingFloat(int key)
    {
        String xx = getAppResourceString(key);
        if (!xx.isEmpty())
        {
            try
            {
                return Float.parseFloat(xx);
            }
            catch (Exception e)
            {
                // ignore?
                Log.e(TAG, "invalid float setting value: " + xx);
            }
        }
        return 0;
    }

}
