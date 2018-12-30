package com.mgjg.kmztracker

import android.app.Activity
import android.content.Context
import android.location.*
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.mgjg.kmztracker.map.MapOverlayer
import java.text.DecimalFormat

/**
 * Created by ja24120 on 4/14/14.
 */
class LocationTracker(private val tracker: MapOverlayer) : LocationListener {
  private val locationManager: LocationManager?
  private var provider: String? = null
  private val latitudeField: TextView?
  private val longitudeField: TextView?
  private val gpsImage: ImageView?
  private val initialLatitude = 42.382387
  private val initialLongitude = -71.235065

  init {
    val context = MainActivity.instance
    tracker.mvPoint(initialLatitude, initialLongitude)

    latitudeField = context.findViewById(R.id.GPS_LAT)
    longitudeField = context.findViewById(R.id.GPS_LON)
    gpsImage = context.findViewById(R.id.GPS)

    // Get the location manager
    locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    // Define the criteria how to select the location provider -> use
    // default
    val criteria = Criteria()

    provider = locationManager?.getBestProvider(criteria, false)

    val location = try {
      if (null == provider || !MainActivity.hasLocationPermission()) null else locationManager!!.getLastKnownLocation(
        provider
      )
    } catch (e: SecurityException) {
      null
    }

    // Initialize the location fields
    if (location != null) {
      println("Provider $provider has been selected.")
      onLocationChanged(location)
    } else {
      setGpsImage(context, provider != null)
      setLatitude("NONE")
      setLongitude("NONE")
    }
  }

  fun updateCueSheet(url: String) {
    tracker.updateCueSheet(url)
  }

  fun moveToStart() {
    tracker.moveToStart()
  }

  fun moveNext() {
    tracker.moveNext()
  }

  fun moveToEnd() {
    tracker.moveToEnd()
  }

  @Suppress("UNUSED_PARAMETER")
  private fun setGpsImage(context: Context, enabled: Boolean) {
    if (null != gpsImage) {
      //         gpsImage.setImageDrawable((enabled ? GPSIcon.ON : GPSIcon.OFF).getDrawable(context));
    }
  }

  private fun setLatitude(text: String) {
    if (null != latitudeField) {
      latitudeField.text = text
    }
  }

  private fun setLongitude(text: String) {
    if (null != longitudeField) {
      longitudeField.text = text
    }
  }

  override fun onLocationChanged(location: Location) {
    var df = DecimalFormat("#.0000")
    var txt = df.format(location.latitude)
    // txt = BigDecimal.valueOf(location.getLatitude()).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
    setLatitude(txt)
    df = DecimalFormat("#.00000")
    txt = df.format(location.longitude)
    // txt = BigDecimal.valueOf(location.getLongitude()).setScale(5, BigDecimal.ROUND_HALF_UP).toString();
    setLongitude(txt)
    tracker.mvPoint(location)
  }

  /* Request updates at startup */
  internal fun onResume(context: Activity) {
    if (null != locationManager && null != provider) {
      setGpsImage(context, true)
      try {
        if (MainActivity.hasLocationPermission()) {
          locationManager.requestLocationUpdates(provider, 400, 1f, this)
        }
      } catch (e: SecurityException) {

      }
    }
  }

  internal fun onPause(context: Activity) {
    if (null != locationManager) {
      if (MainActivity.hasLocationPermission()) {

        locationManager.removeUpdates(this)
      }
      setGpsImage(context, false)
    }
  }

  override fun onStatusChanged(provider: String, status: Int, aBundle: Bundle) {
    // provider vs. this.provider ???
    // Displayer.showToast("status changed: " + provider);
    // setGpsImage(Displayer.getContext(), (status == LocationProvider.AVAILABLE));
  }

  override fun onProviderEnabled(provider: String) {
    // provider vs. this.provider ???
    MainActivity.showToast("Enabled provider $provider")
  }

  override fun onProviderDisabled(provider: String) {
    // provider vs. this.provider ???
    MainActivity.showToast("Disabled provider $provider")
  }
}
