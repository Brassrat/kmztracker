package com.mgjg.kmztracker.cuesheet.parser

import android.util.Log
import com.mgjg.kmztracker.MainActivity
import com.mgjg.kmztracker.cuesheet.CueSheet
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object CueSheetParserFactory {
  private val TAG = "kmztracker.parser"

  abstract class CueSheetFactory internal constructor(private val urlString: String) :
    CueSheetParser {

    protected val connection: URL
      @Throws(IOException::class)
      get() {
        try {
          val url = URL(urlString)

          val conn = url.openConnection()
          conn.connectTimeout = 10 * 1000
          conn.readTimeout = 15 * 1000 // timeout for reading the google maps data: 15 secs
          conn.connect()
          return url
        } catch (e: MalformedURLException) {
          Log.e(TAG, "can not open url " + urlString + " because " + e.message, e)
          throw e
        } catch (e: IOException) {
          Log.e(TAG, "can not open url " + urlString + " because " + e.message, e)
          throw e
        }

      }

    @Throws(IOException::class)
    protected fun openConnection(): InputStream {
      return connection.openStream()
    }
  }

  class CueSheetUpdater(
    private val cueSheet: CueSheet,
    private val urlString: String,
    private val color: Int
  ) : Runnable {

    override fun run() {
      // new FileInputStream(filePath)
      // return parse(cueSheet, url.openStream());
      try {
        val parser: CueSheetParser = when {
          urlString.endsWith(".kml") -> CueSheetKmlParser(urlString)
          urlString.endsWith(".gpx") -> CueSheetGpxParser(urlString)
          urlString.endsWith(".xml") -> CueSheetXmlParser(urlString)
          else -> throw UnknownFormatConversionException(urlString)
        }

        cueSheet.clear()
        parser.parse(cueSheet)

        /* Set the result to be displayed in our GUI. */
        Log.d(cueSheet.appName, "CueSheet: " + cueSheet.toString())

        MainActivity.runOnUi (Runnable{
          cueSheet.clearMap()
          cueSheet.drawRoute(color)
        })
      } catch (e: Exception) {
        Log.e(TAG, "unable to process url(" + urlString + ") because " + e.message)
        e.printStackTrace()
      }

    }
  }

  fun parseUrl(cueSheet: CueSheet, urlString: String, color: Int) {
    //   /*
    //   * Creates a new Intent to start the CueSheetService
    //   * IntentService. Passes a URI in the
    //   * Intent's "data" field.
    //   */
    //    Intent ii = new Intent(aa, CueSheetService.class);
    //    ii.putExtra("url", urlString);
    // NO WAY TO PASS object to service ...
    //        ii.putExtra("cuesheet", cueSheet);
    //    ii.putExtra("color", color);
    //    //ii.setData(Uri.parse(dataUrl));
    //    // Starts the IntentService
    //    aa.startService(ii);

    // use background thread which updates activity-aware object
    val th = Thread(CueSheetUpdater(cueSheet, urlString, color), "CueSheetUpdater")
    th.isDaemon = true
    th.start()
  }
}
