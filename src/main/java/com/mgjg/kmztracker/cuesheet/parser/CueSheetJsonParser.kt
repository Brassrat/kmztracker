package com.mgjg.kmztracker.cuesheet.parser

import android.util.Log

import com.mgjg.kmztracker.cuesheet.CueSheet
import com.mgjg.kmztracker.map.Placemark

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.NoSuchElementException

class CueSheetJsonParser internal constructor(private val inputStream: InputStream) :
  CueSheetParser {

  @Throws(Exception::class)
  override fun parse(cueSheet: CueSheet): CueSheet {
    return JsonParser(cueSheet).parse(inputStream)
  }

  private inner class JSONArrayIterator internal constructor(private val theArray: JSONArray) :
    Iterator<JSONObject> {
    private var nextIndex = 0

    override fun hasNext(): Boolean {
      return nextIndex < theArray.length()
    }

    override fun next(): JSONObject {
      if (nextIndex < theArray.length()) {
        try {
          return theArray.getJSONObject(nextIndex++)
        } catch (e: JSONException) {
          // TODO Auto-generated catch block
          e.printStackTrace()
        }

      }
      throw NoSuchElementException("no element available")
    }

  }

  private inner class JsonParser @Throws(Exception::class)
  constructor(private val cueSheet: CueSheet) {

    @Throws(Exception::class)
    fun parse(inputStream: InputStream): CueSheet {
      val jObject = getJSON(inputStream)
      val placemarks = jObject.getJSONArray("placemarks")

      val it = JSONArrayIterator(placemarks)
      while (it.hasNext()) {
        val obj = it.next()
        val lat = obj.getDouble("lat")
        val lon = obj.getDouble("lon")
        val title = obj.getString("title")
        val description = obj.getString("description")
        cueSheet.addPlacemark(Placemark(lat, lon, title, description))
      }
      return cueSheet
    }

    @Throws(Exception::class)
    fun getJSON(inputStream: InputStream): JSONObject {
      try {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuilder()
        var line: String? =  reader.readLine();
        while (line != null) {
          sb.append(line + "\n")
          line = reader.readLine();
        }
        inputStream.close()
        val json = sb.toString()

        // try parse the string to a JSON object
        try {
          return JSONObject(json)
        } catch (e: JSONException) {
          Log.e(cueSheet.appName, "JSON Error parsing data " + e.toString())
          throw e
        }

      } catch (e: Exception) {
        Log.e(cueSheet.appName, "Error converting result " + e.toString())
        throw e
      }

    }

  }
}
