package com.mgjg.kmztracker.preference;

import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.mgjg.kmztracker.AppPreferences;

/**
 * Class to hold attributes of a preference
 * Created by ja24120 on 1/13/15.
 */
public final class TypedPreference
{
    public enum TYPE
    {
        STRING,
        INTEGER,
        BOOLEAN,
        FLOAT,
        DOUBLE
    }

    private final String name;
    private final TYPE type;
    private final int key;
    private final int summary;
    private final int def;

    @SuppressWarnings("unused")
    public TypedPreference(String name, TYPE tt, int key, int summary, int def)
    {
        this.name = name;
        this.type = tt;
        this.key = key;
        this.summary = summary;
        this.def = def;
    }

    @SuppressWarnings("unused")
    public TypedPreference(String name, TYPE tt, String key, String def, String summary)
    {
        this.name = name;
        this.type = tt;
        this.key = AppPreferences.getInstance().getStringResourceIdentifier(key);
        this.summary = AppPreferences.getInstance().getStringResourceIdentifier(summary);
        this.def = AppPreferences.getInstance().getStringResourceIdentifier(def);
    }

    @SuppressWarnings("unused")
    public final int getKeyId()
    {
        return key;
    }

    @SuppressWarnings("unused")
    public final int getDefaultId()
    {
        return def;
    }

    @SuppressWarnings("unused")
    public final String getName()
    {
        return name;
    }

    @SuppressWarnings("unused")
    public final void setPreference(PreferenceFragment pref)
    {
        AppPreferences settings = AppPreferences.getInstance();
        switch (type)
        {
            case STRING:
                setPreference(pref, settings.getPreferenceString(key, def));
                break;
            case INTEGER:
                setPreference(pref, String.valueOf(settings.getPreferenceInteger(key, def)));
                break;
            case BOOLEAN:
                setPreference(pref, String.valueOf(settings.getPreferenceBoolean(key, def)));
                break;
            case FLOAT:
                setPreference(pref, String.valueOf(settings.getPreferenceFloat(key, def)));
                break;
            case DOUBLE:
                setPreference(pref, String.valueOf(settings.getPreferenceDouble(key, def)));
                break;
            default:
                // should never get here
                throw new IllegalStateException("Missing type branch for " + name);
        }
    }

    @SuppressWarnings("unused")
    public final void setPreference(PreferenceFragment frag, int defValue)
    {
        setPreference(frag, String.valueOf(defValue));
    }

    @SuppressWarnings("unused")
    public final void setPreference(PreferenceFragment frag, boolean defValue)
    {
        setPreference(frag, String.valueOf(defValue));
    }

    @SuppressWarnings("unused")
    public final void setPreference(PreferenceFragment frag, float defValue)
    {
        setPreference(frag, String.valueOf(defValue));
    }

    @SuppressWarnings("unused")
    public final void setPreference(PreferenceFragment frag, double defValue)
    {
        setPreference(frag, String.valueOf(defValue));
    }

    /**
     * Allows setup of a preference entry from other than settings,
     * for example from a supplier descriptor
     *
     * @param frag
     * @param defValue
     */
    @SuppressWarnings("unused")
    public final void setPreference(PreferenceFragment frag, Object defValue)
    {
        Preference pp = frag.findPreference(frag.getString(key));
        if (null != pp)
        {
            defValue = (null == defValue) ? "" : defValue;
            pp.setDefaultValue(defValue);
            pp.setSummary(frag.getString(summary) + ": " + defValue);
        }
    }

    @SuppressWarnings("unused")
    public final String getSettingString()
    {
        final AppPreferences settings = AppPreferences.getInstance();
        final Object result;
        if (key > 0)
        {
            switch (type)
            {
                case STRING:
                    result = settings.getPreferenceString(key, def);
                    break;
                case INTEGER:
                    result = settings.getPreferenceInteger(key, def);
                    break;
                case BOOLEAN:
                    result = settings.getPreferenceBoolean(key, def);
                    break;
                case FLOAT:
                    result = settings.getPreferenceFloat(key, def);
                    break;
                case DOUBLE:
                    result = settings.getPreferenceDouble(key, def);
                    break;
                default:
                    // should never get here
                    throw new IllegalStateException("Missing type branch for " + name);
            }
        }
        else
        {
            result = "";
        }

        return String.valueOf(result);
    }

    @SuppressWarnings("unused")
    public final int getSettingInteger()
    {
        AppPreferences settings = AppPreferences.getInstance();
        final int result;
        if (key > 0)
        {
            switch (type)
            {
                case STRING:
                    result = Integer.valueOf(settings.getPreferenceString(key, def));
                    break;
                case INTEGER:
                    result = settings.getPreferenceInteger(key, def);
                    break;
                case BOOLEAN:
                    result = settings.getPreferenceBoolean(key, def) ? 1 : 0;
                    break;
                case FLOAT:
                    result = (int) settings.getPreferenceFloat(key, def);
                    break;
                case DOUBLE:
                    result = (int) settings.getPreferenceDouble(key, def);
                    break;
                default:
                    // should never get here
                    throw new IllegalStateException("Missing type branch for " + name);
            }
        }
        else
        {
            result = 0;
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final double getSettingDouble()
    {
        final AppPreferences settings = AppPreferences.getInstance();
        final double result;
        if (key > 0)
        {
            switch (type)
            {
                case STRING:
                    result = Double.valueOf(settings.getPreferenceString(key, def));
                    break;
                case INTEGER:
                    result = (double) settings.getPreferenceInteger(key, def);
                    break;
                case BOOLEAN:
                    result = settings.getPreferenceBoolean(key, def) ? 1.0 : 0.0;
                    break;
                case FLOAT:
                    result = settings.getPreferenceFloat(key, def);
                    break;
                case DOUBLE:
                    result = settings.getPreferenceDouble(key, def);
                    break;
                default:
                    // should never get here
                    throw new IllegalStateException("Missing type branch for " + name);
            }
        }
        else
        {
            return 0.0;
        }
        return result;
    }

    @SuppressWarnings("unused")
    public final float getSettingFloat()
    {
        final AppPreferences settings = AppPreferences.getInstance();
        final float result;
        if (key > 0)
        {
            switch (type)
            {
                case STRING:
                    result = Float.valueOf(settings.getPreferenceString(key, def));
                    break;
                case INTEGER:
                    result = (float) settings.getPreferenceInteger(key, def);
                    break;
                case BOOLEAN:
                    result = settings.getPreferenceBoolean(key, def) ? 1.0F : 0.0F;
                    break;
                case FLOAT:
                    result = settings.getPreferenceFloat(key, def);
                    break;
                case DOUBLE:
                    result = (float) settings.getPreferenceDouble(key, def);
                    break;
                default:
                    // should never get here
                    throw new IllegalStateException("Missing type branch for " + name);
            }
        }
        else
        {
            result = 0.0f;
        }
        return result;
    }

    @SuppressWarnings("unused")
    /**
     * caller wants the boolean value of the option, which may or may not have a preference value
     * @return boolean
     */
    public final boolean isSettingBoolean()
    {
        final AppPreferences settings = AppPreferences.getInstance();
        final boolean result;
        if (key > 0)
        {
            switch (type)
            {
                case STRING:
                    result = Boolean.valueOf(settings.getPreferenceString(key, def));
                    break;
                case INTEGER:
                    result = (settings.getPreferenceInteger(key, def) != 0);
                    break;
                case BOOLEAN:
                    result = settings.getPreferenceBoolean(key, def);
                    break;
                case FLOAT:
                    result = (settings.getPreferenceFloat(key, def) != 0);
                    break;
                case DOUBLE:
                    result = (settings.getPreferenceDouble(key, def) != 0);
                    break;
                default:
                    // should never get here
                    throw new IllegalStateException("Missing type branch for " + name);
            }
        }
        else
        {
            result = false;
        }
        return result;
    }

}

