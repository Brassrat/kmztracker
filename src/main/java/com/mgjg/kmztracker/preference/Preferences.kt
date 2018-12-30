package com.mgjg.kmztracker.preference

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

import com.mgjg.kmztracker.AppPreferences
import java.lang.Double.valueOf

/**
 * Created by ja24120 on 5/22/14.
 */
class Preferences private constructor() {

  private interface PutPreference<T> {
    fun put(editor: SharedPreferences.Editor, name: String, value: T): T
  }

  private interface GetPreference<T> {
    operator fun get(nameList: String, def: T?): T
  }

  fun getDouble(nameList: String, def: Double): Double {
    return getValue(nameList, def,
      object : GetPreference<Double> {
        override operator fun get(nameList: String, def: Double?): Double {
          val dd = if (null == def) 0.0 else def
          val sp = sharedPrefs()
          return if (null == sp) dd else
            valueOf(sp.getFloat(nameList, dd.toFloat()).toDouble())
        }
      },
      object : PutPreference<Double> {
        override fun put(editor: SharedPreferences.Editor, name: String, value: Double): Double {
          editor.putFloat(name, value.toFloat())
          return value
        }
      })
  }

  companion object {

    private fun sharedPrefs(): SharedPreferences? {
      return AppPreferences.instance!!.sharedPreferences
    }

    private fun <T> putValue(name: String, value: T, pp: PutPreference<T>): T {
      var value = value
      if (null != sharedPrefs()) {
        val editor = sharedPrefs()!!.edit()
        value = pp.put(editor, name, value)
        editor.commit()
      }
      return value
    }

    private fun <T> getValue(
      nameList: String,
      def: T,
      prefGetter: GetPreference<T>,
      prefSetter: PutPreference<T>
    ): T {
      val saved = if (null == sharedPrefs()) false else sharedPrefs()!!.contains(nameList)
      return if (saved) prefGetter[nameList, def] else putValue(nameList, def, prefSetter)
      // !saved ==> vv == def
      // saved ==> vv is saved value
    }

    fun getString(key: Int, defValue: String): String {
      return getString(AppPreferences.instance!!.getIdentifier(key), defValue)
    }

    fun getString(nameList: String, defValue: String): String {
      return getValue(nameList, defValue,
        object : GetPreference<String> {
          override fun get(nameList: String, defValue: String): String? {
            return if (null == sharedPrefs()) defValue else sharedPrefs()!!.getString(
              nameList,
              defValue
            )
          }
        },
        object : PutPreference<String> {
          override fun put(editor: SharedPreferences.Editor, name: String, value: String): String {
            editor.putString(name, value)
            return value
          }
        })
    }

    fun putString(nameList: String, def: String): String {
      return putValue(nameList, def,
        object : PutPreference<String> {
          override fun put(editor: SharedPreferences.Editor, name: String, value: String): String {
            editor.putString(name, value)
            return value
          }
        })
    }

    fun getBoolean(nameList: String, def: Boolean): Boolean {
      return getValue(nameList, def,
        object : GetPreference<Boolean> {
          override operator fun get(nameList: String, def: Boolean?): Boolean? {
            return if (null == sharedPrefs()) def else sharedPrefs()!!.getBoolean(nameList, def!!)
          }
        },
        object : PutPreference<Boolean> {
          override fun put(
            editor: SharedPreferences.Editor,
            name: String,
            value: Boolean?
          ): Boolean? {
            editor.putBoolean(name, value!!)
            return value
          }
        })
    }

    fun putBoolean(nameList: String, def: Boolean): Boolean {
      return putValue(nameList, def,
        object : PutPreference<Boolean> {
          override fun put(
            editor: SharedPreferences.Editor,
            name: String,
            value: Boolean?
          ): Boolean? {
            editor.putBoolean(name, value!!)
            return value
          }
        })
    }

    fun putDouble(nameList: String, def: Double): Double {
      return putValue(nameList, def,
        object : PutPreference<Double> {
          override fun put(
            editor: SharedPreferences.Editor,
            name: String,
            value: Double?
          ): Double? {
            editor.putFloat(name, value!!.toFloat())
            return value
          }
        })
    }

    fun getInteger(nameList: String, def: Int): Int {
      return getValue(nameList, def,
        object : GetPreference<Int> {
          override operator fun get(nameList: String, def: Int?): Int? {
            return if (null == sharedPrefs()) def else sharedPrefs()!!.getInt(nameList, def!!)
          }
        },
        object : PutPreference<Int> {
          override fun put(editor: SharedPreferences.Editor, name: String, value: Int?): Int? {
            editor.putInt(name, value!!)
            return value
          }
        })
    }

    fun putInteger(nameList: String, def: Int): Int {
      return putValue(nameList, def,
        object : PutPreference<Int> {
          override fun put(editor: SharedPreferences.Editor, name: String, value: Int?): Int? {
            editor.putInt(name, value!!)
            return value
          }
        })
    }
  }


}
