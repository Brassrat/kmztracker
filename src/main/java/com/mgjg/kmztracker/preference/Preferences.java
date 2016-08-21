package com.mgjg.kmztracker.preference;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mgjg.kmztracker.AppPreferences;

/**
 * Created by ja24120 on 5/22/14.
 */
public final class Preferences
{
    private Preferences()
    {
    }

    private static SharedPreferences sharedPrefs()
    {
        return AppPreferences.getInstance().getSharedPreferences();
    }

    private interface PutPreference<T>
    {
        T put(SharedPreferences.Editor editor, String name, T value);
    }

    private static <T> T putValue(String name, T value, PutPreference<T> pp)
    {
        if (null != sharedPrefs())
        {
            Editor editor = sharedPrefs().edit();
            value = pp.put(editor, name, value);
            editor.commit();
        }
        return value;
    }

    private interface GetPreference<T>
    {
        T get(String nameList, T def);
    }

    private static <T> T getValue(String nameList, T def, GetPreference<T> prefGetter, PutPreference<T> prefSetter)
    {
        boolean saved = (null == sharedPrefs()) ? false : sharedPrefs().contains(nameList);
        return saved ? prefGetter.get(nameList, def) : putValue(nameList, def, prefSetter);
        // !saved ==> vv == def
        // saved ==> vv is saved value
    }

    public static String getString(int key, String defValue)
    {
        return getString(AppPreferences.getInstance().getIdentifier(key), defValue);
    }

    public static String getString(String nameList, String defValue)
    {
        return getValue(nameList, defValue,
                new GetPreference<String>()
                {
                    @Override
                    public String get(String nameList, String defValue)
                    {
                        return (null == sharedPrefs()) ? defValue : sharedPrefs().getString(nameList, defValue);
                    }
                },
                new PutPreference<String>()
                {
                    @Override
                    public String put(SharedPreferences.Editor editor, String name, String value)
                    {
                        editor.putString(name, value);
                        return value;
                    }
                });
    }

    public static String putString(String nameList, String def)
    {
        return putValue(nameList, def,
                new PutPreference<String>()
                {
                    @Override
                    public String put(SharedPreferences.Editor editor, String name, String value)
                    {
                        editor.putString(name, value);
                        return value;
                    }
                });
    }

    public static boolean getBoolean(String nameList, boolean def)
    {
        return getValue(nameList, def,
                new GetPreference<Boolean>()
                {
                    @Override
                    public Boolean get(String nameList, Boolean def)
                    {
                        return (null == sharedPrefs()) ? def : sharedPrefs().getBoolean(nameList, def);
                    }
                },
                new PutPreference<Boolean>()
                {
                    @Override
                    public Boolean put(SharedPreferences.Editor editor, String name, Boolean value)
                    {
                        editor.putBoolean(name, value);
                        return value;
                    }
                });
    }

    public static boolean putBoolean(String nameList, boolean def)
    {
        return putValue(nameList, def,
                new PutPreference<Boolean>()
                {
                    @Override
                    public Boolean put(SharedPreferences.Editor editor, String name, Boolean value)
                    {
                        editor.putBoolean(name, value);
                        return value;
                    }
                });
    }

    public double getDouble(String nameList, double def)
    {
        return getValue(nameList, def,
                new GetPreference<Double>()
                {
                    @Override
                    public Double get(String nameList, Double def)
                    {
                        def = (null == def) ? 0.0 : def;
                        return (null == sharedPrefs()) ?
                                def :
                                Double.valueOf(sharedPrefs().getFloat(nameList, (def.floatValue())));
                    }
                },
                new PutPreference<Double>()
                {
                    @Override
                    public Double put(SharedPreferences.Editor editor, String name, Double value)
                    {
                        editor.putFloat(name, value.floatValue());
                        return value;
                    }
                });
    }

    public static double putDouble(String nameList, double def)
    {
        return putValue(nameList, def,
                new PutPreference<Double>()
                {
                    @Override
                    public Double put(SharedPreferences.Editor editor, String name, Double value)
                    {
                        editor.putFloat(name, value.floatValue());
                        return value;
                    }
                });
    }

    public static int getInteger(String nameList, int def)
    {
        return getValue(nameList, def,
                new GetPreference<Integer>()
                {
                    @Override
                    public Integer get(String nameList, Integer def)
                    {
                        return (null == sharedPrefs()) ? def : sharedPrefs().getInt(nameList, def);
                    }
                },
                new PutPreference<Integer>()
                {
                    @Override
                    public Integer put(SharedPreferences.Editor editor, String name, Integer value)
                    {
                        editor.putInt(name, value);
                        return value;
                    }
                });
    }

    public static int putInteger(String nameList, int def)
    {
        return putValue(nameList, def,
                new PutPreference<Integer>()
                {
                    @Override
                    public Integer put(SharedPreferences.Editor editor, String name, Integer value)
                    {
                        editor.putInt(name, value);
                        return value;
                    }
                });
    }


}
