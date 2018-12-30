package com.mgjg.kmztracker.cuesheet.parser

import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.mgjg.kmztracker.MainActivity
import com.mgjg.kmztracker.cuesheet.CueSheet


/**
 * Created by marianne on 1/3/2015.
 */
@Suppress("unused")
class CueSheetService : IntentService("CueSheetService") {

  override fun onHandleIntent(workIntent: Intent?) {
    val appName = "kmztracker"
    val map: GoogleMap? = null // TBD
    val color = workIntent!!.getIntExtra("color", Color.BLACK)
    val cueSheet = CueSheet(appName, map)
    // Gets data from the incoming Intent
    val urlString = workIntent.getStringExtra("url")
    try {
      val parser: CueSheetParser? = when {
        urlString.endsWith(".kml") -> CueSheetKmlParser(urlString)
        urlString.endsWith(".xml") -> CueSheetXmlParser(urlString)
        urlString.endsWith(".gpx") -> CueSheetGpxParser(urlString)
        else -> {
          MainActivity.showToast("Unknown cuesheet file type: $urlString")
          null
        }
      }

      if (null != parser) {
        cueSheet.clear()
        parser.parse(cueSheet)

        /* Set the result to be displayed in our GUI. */
        Log.d(cueSheet.appName, "CueSheet: $cueSheet")

        MainActivity.runOnUi ( Runnable {
          cueSheet.clearMap()
          cueSheet.drawRoute(color)
        })
      }
    } catch (e: Exception) {
      Log.e(TAG, "unable to process url(" + urlString + ") because " + e.message)
      e.printStackTrace()
    }

  }

  private fun setupTurnNotification(point: LatLng) {
    if (MainActivity.hasLocationPermission()) {

      // This intent will call the activity ProximityActivity
      val proximityIntent = Intent("in.wptrafficanalyzer.activity.proximity")

      // Creating a pending intent which will be invoked by LocationManager when the specified region is
      // entered or exited
      val pendingIntent = PendingIntent.getActivity(
        baseContext, 0, proximityIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
      )

      // Setting proximity alert
      // The pending intent will be invoked when the device enters or exits the region 20 meters
      // away from the marked point
      // The -1 indicates that, the monitor will not be expired
      // Getting LocationManager object from System Service
      // LOCATION_SERVICE
      val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      try {
        if (MainActivity.hasLocationPermission()) {
          locationManager.addProximityAlert(point.latitude, point.longitude, 20f, -1, pendingIntent)
        }
      }
        catch (e : SecurityException) {

        }
    }

  }

  companion object {
    private const val TAG = "cueSheetService"
  }
}
