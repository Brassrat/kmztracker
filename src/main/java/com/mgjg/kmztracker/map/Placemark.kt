package com.mgjg.kmztracker.map

import android.location.Location

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

@Suppress("unused")
class Placemark @JvmOverloads constructor(
  // public Placemark(int latitude, int longitude, String title, String snippet)
  // {
  // super(new GeoPoint(latitude, longitude), ((title != null) ? title : ""), ((snippet != null) ? snippet : ""));
  // }

  /**
   * returns a LatLng which is a copy of the @{code Placemark}'s lat/long
   * @return @{code LatLng}
   */// LatLngs are final/immutable
  val latLng: LatLng,
  val altitude: Double = 0.0,
  val title: String? = null,
  val snippet: String? = null
) {

  private var marker: Marker? = null
    private set

  init {
    marker = null
  }

  constructor(point: LatLng, title: String, snippet: String) : this(point, 0.0, title, snippet)

  // public static final int toMicroDegrees(double deg)
  // {
  // return (int) (deg * 1E6);
  // }
  //
  // public static final double fromMicroDegrees(int microDegrees)
  // {
  // return (double) (microDegrees / 1E6);
  // }

  constructor(
    latitude: Double,
    longitude: Double,
    altitude: Double,
    title: String,
    snippet: String
  ) : this(LatLng(latitude, longitude), altitude, title, snippet)


  constructor(latitude: Double, longitude: Double, altitude: Double) : this(
    LatLng(
      latitude,
      longitude
    ), altitude
  )

  constructor(latitude: Double, longitude: Double, title: String, snippet: String) : this(
    LatLng(
      latitude,
      longitude
    ), title, snippet
  )

  constructor(latitude: Double, longitude: Double) : this(LatLng(latitude, longitude))

  override fun toString(): String {
    return "[lat: ${latLng.latitude}, lon: ${latLng.longitude}]"
  }

  fun toLocation(): Location {
    // LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    // Location loc = locationManager.getLastKnownLocation("gps");
    val location = Location(title)
    location.latitude = latLng.latitude
    location.longitude = latLng.longitude
    return location
  }

  fun addMarker(map: GoogleMap, icon: Int): Marker? {
    marker = map.addMarker(
      MarkerOptions()
        .position(latLng)
        .title(title)
        .icon(BitmapDescriptorFactory.fromResource(icon))
    )
    return marker
  }

  fun removeMarker() {
    if (marker != null) {
      marker!!.remove()
      this.marker = null
    }

  }

  override fun hashCode(): Int {
    return latLng.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    return if (other is Placemark) (latLng == other.latLng) else false
  }

  /**
   * compare latitudes, 'less than, i.e. <0) means this point is WEST of other point
   *
   * @param other
   * @return
   */
  fun compareLat(other: Placemark): Int {
    val xx = latLng.latitude - other.latLng.latitude
    if (Math.abs(xx) < .0000001) {
      return 0 // essentially equal
    }
    if (latLng.latitude < 0 && other.latLng.longitude > 0) {
      return -1
    } else if (latLng.latitude > 0 && other.latLng.longitude < 0) {
      return 1
    }
    // WEST of 0, both negative,
    // -70 vs. -50, -70 - (-50) ==> -20 (<0 ==> WEST)
    // -70 vs -100, -70 - (-100) ==> 30 (>0 ==> EAST)
    // EAST of 0, both positive,
    // 70 vs. 50, 70 - 50 ==> 20 (<0 ==> EAST)
    // 70 vs 100, 70 - 100 ==> -30 (>0 ==> WEST)
    return if (xx < 0) -1 else 1
  }

  /**
   * compare longitudes, 'less than (<0)' means point is SOUTH of other point
   *
   * @param other
   * @return
   */
  fun compareLng(other: Placemark): Int {
    val xx = latLng.longitude - other.latLng.longitude
    if (Math.abs(xx) < .0000001) {
      return 0 // essentially equal
    }
    if (latLng.longitude < 0 && latLng.longitude > 0) {
      // other is above and this point is below equator
      return -1 // this point is south of other point
    } else if (latLng.longitude > 0 && latLng.longitude < 0) {
      // this point is above equator, other point is below equator
      return -1 // this point is north of other point
    }
    // SOUTH of 0, both negative,
    // -70 vs. -50, -70 - (-50) ==> -20 (<0 ==> WEST)
    // -70 vs -100, -70 - (-100) ==> 30 (>0 ==> EAST)
    // NORTH of 0, both positive,
    // 70 vs. 50, 70 - 50 ==> 20 (<0 ==> EAST)
    // 70 vs 100, 70 - 100 ==> -30 (>0 ==> WEST)
    return if (xx < 0) -1 else 1
  }

  companion object {
    val NO_PLACEMARK = Placemark(LatLng(0.0, 0.0), 0.0, null, null)
  }
}
