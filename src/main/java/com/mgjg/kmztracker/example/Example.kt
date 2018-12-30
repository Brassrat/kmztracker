//Main Activity
package com.mgjg.kmztracker.example

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mgjg.kmztracker.R
import android.Manifest.permission.*
import android.content.pm.PackageManager.*

class Example : FragmentActivity() {

  internal var googleMap: GoogleMap? = null
  internal var locationManager: LocationManager
  internal var pendingIntent: PendingIntent
  internal var sharedPreferences: SharedPreferences
  internal var locationCount = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Getting Google Play availability status

    val apiAvail = GoogleApiAvailability.getInstance()
    val status = apiAvail.isGooglePlayServicesAvailable(baseContext)

    // Showing status
    if (status != ConnectionResult.SUCCESS) {
      // Google Play Services are not available
      val requestCode = 10
      val dialog = apiAvail.getErrorDialog(this, status, requestCode)
      dialog.show()
    } else { // Google Play Services are available

      val activity = this
      // Getting reference to the SupportMapFragment of activity_main.xml
      val fm = supportFragmentManager
        .findFragmentById(R.id.map) as SupportMapFragment

      // Getting GoogleMap object from the fragment
      fm.getMapAsync(OnMapReadyCallback { googleMap ->
        // Enabling MyLocation Layer of Google Map
        if (ActivityCompat.checkSelfPermission(
            activity,
            ACCESS_FINE_LOCATION
          ) != PERMISSION_GRANTED
        ) {
          // TODO: Consider calling
          //    ActivityCompat#requestPermissions
          // here to request the missing permissions, and then overriding
          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
          //                                          int[] grantResults)
          // to handle the case where the user grants the permission. See the documentation
          // for ActivityCompat#requestPermissions for more details.
          return@OnMapReadyCallback
        }
        googleMap.isMyLocationEnabled = true

        // Getting LocationManager object from System Service
        // LOCATION_SERVICE
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Opening the sharedPreferences object
        sharedPreferences = getSharedPreferences("location", 0)

        // Getting number of locations already stored
        locationCount = sharedPreferences.getInt("locationCount", 0)

        // Getting stored zoom level if exists else return 0
        val zoom = sharedPreferences.getString("zoom", "0")

        // If locations are already saved
        if (locationCount != 0) {

          var lat = ""
          var lng = ""

          // Iterating through all the locations stored
          for (i in 0 until locationCount) {

            // Getting the latitude of the i-th location
            lat = sharedPreferences.getString("lat" + i, "0")

            // Getting the longitude of the i-th location
            lng = sharedPreferences.getString("lng" + i, "0")

            // Drawing marker on the map
            drawMarker(
              LatLng(
                java.lang.Double.parseDouble(lat),
                java.lang.Double.parseDouble(lng)
              )
            )

            // Drawing circle on the map
            drawCircle(
              LatLng(
                java.lang.Double.parseDouble(lat),
                java.lang.Double.parseDouble(lng)
              )
            )
          }

          // Moving CameraPosition to last clicked position
          googleMap.moveCamera(
            CameraUpdateFactory.newLatLng(
              LatLng(
                java.lang.Double.parseDouble(lat), java.lang.Double.parseDouble(lng)
              )
            )
          )

          // Setting the zoom level in the map on last position is clicked
          googleMap.animateCamera(
            CameraUpdateFactory.zoomTo(
              java.lang.Float
                .parseFloat(zoom)
            )
          )
        }

        googleMap.setOnMapClickListener { point ->
          // Incrementing location count
          locationCount++

          // Drawing marker on the map
          drawMarker(point)

          // Drawing circle on the map
          drawCircle(point)

          // This intent will call the activity ProximityActivity
          val proximityIntent = Intent(
            "in.wptrafficanalyzer.activity.proximity"
          )

          // Passing latitude to the PendingActivity
          proximityIntent.putExtra("lat", point.latitude)

          // Passing longitude to the PendingActivity
          proximityIntent.putExtra("lng", point.longitude)

          // Creating a pending intent which will be invoked by
          // LocationManager when the specified region is
          // entered or exited
          pendingIntent = PendingIntent.getActivity(
            baseContext,
            0, proximityIntent, PendingIntent.FLAG_ONE_SHOT
          )

          // Setting proximity alert
          // The pending intent will be invoked when the device enters
          // or exits the region 20 meters
          // away from the marked point
          // The -1 indicates that, the monitor will not be expired
          try {
            locationManager.addProximityAlert(
              point.latitude,
              point.longitude, 20f, -1, pendingIntent
            )
          } catch (e: SecurityException) {
            // should never happen since needed acces is checked for above
          }

          /**
           * Opening the editor object to write data to
           * sharedPreferences
           */
          /**
           * Opening the editor object to write data to
           * sharedPreferences
           */
          /**
           * Opening the editor object to write data to
           * sharedPreferences
           */

          /**
           * Opening the editor object to write data to
           * sharedPreferences
           */
          val editor = sharedPreferences.edit()

          // Storing the latitude for the i-th location
          editor.putString(
            "lat" + Integer.toString(locationCount - 1),
            java.lang.Double.toString(point.latitude)
          )

          // Storing the longitude for the i-th location
          editor.putString(
            "lng" + Integer.toString(locationCount - 1),
            java.lang.Double.toString(point.longitude)
          )

          // Storing the count of locations or marker count
          editor.putInt("locationCount", locationCount)

          /** Storing the zoom level to the shared preferences  */

          /** Storing the zoom level to the shared preferences  */

          /** Storing the zoom level to the shared preferences  */

          /** Storing the zoom level to the shared preferences  */
          editor.putString(
            "zoom",
            java.lang.Float.toString(googleMap.cameraPosition.zoom)
          )

          /** Saving the values stored in the shared preferences  */

          /** Saving the values stored in the shared preferences  */

          /** Saving the values stored in the shared preferences  */

          /** Saving the values stored in the shared preferences  */
          editor.commit()

          Toast.makeText(
            baseContext,
            "Proximity Alert is added", Toast.LENGTH_SHORT
          )
            .show()
        }

        googleMap.setOnMapLongClickListener {
          val proximityIntent = Intent(
            "in.wptrafficanalyzer.activity.proximity"
          )

          pendingIntent = PendingIntent.getActivity(
            baseContext,
            0, proximityIntent, PendingIntent.FLAG_ONE_SHOT
          )

          // Removing the proximity alert
          try {
            locationManager.removeProximityAlert(pendingIntent)
          } catch (e: SecurityException) {
            // should never happen since access is checked for earlier
          }

          // Removing the marker and circle from the Google Map
          googleMap.clear()

          // Opening the editor object to delete data from
          // sharedPreferences
          val editor = sharedPreferences.edit()

          // Clearing the editor
          editor.clear()

          // Committing the changes
          editor.commit()

          Toast.makeText(
            baseContext,
            "Proximity Alert is removed", Toast.LENGTH_LONG
          )
            .show()
        }
      })
    }
  }

  private fun drawCircle(point: LatLng) {

    // Instantiating CircleOptions to draw a circle around the marker
    val circleOptions = CircleOptions()

    // Specifying the center of the circle
    circleOptions.center(point)

    // Radius of the circle
    circleOptions.radius(20.0)

    // Border color of the circle
    circleOptions.strokeColor(Color.BLACK)

    // Fill color of the circle
    circleOptions.fillColor(0x30ff0000)

    // Border width of the circle
    circleOptions.strokeWidth(2f)

    // Adding the circle to the GoogleMap
    googleMap!!.addCircle(circleOptions)

  }

  private fun drawMarker(point: LatLng) {
    // Creating an instance of MarkerOptions
    val markerOptions = MarkerOptions()

    // Setting latitude and longitude for the marker
    markerOptions.position(point)

    // Adding InfoWindow title
    markerOptions.title("Location Coordinates")

    // Adding InfoWindow contents
    markerOptions.snippet(
      java.lang.Double.toString(point.latitude) + ","
          + java.lang.Double.toString(point.longitude)
    )

    // Adding marker on the Google Map
    googleMap!!.addMarker(markerOptions)
  }
}