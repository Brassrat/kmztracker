@file:Suppress("UNUSED_PARAMETER")

package com.mgjg.kmztracker.cuesheet

import android.graphics.Color
import android.util.Log

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.mgjg.kmztracker.R
import com.mgjg.kmztracker.map.Placemark

import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min

/**
 * A list of Placemarks, each Placemark indicates a place on the route. The route consists of lines connecting the
 * Placemarks. There are 2 kinds of placements, those with and those without markers. Ones with markers indicate changes
 * in direction or some other notable place on the route (i.e., beginning/ending location, lunch stop, etc.)
 *
 * @author Jay Goldman
 */
@Suppress("unused")
class CueSheet(val appName: String, private val map: GoogleMap?) {
  private val startIconId: Int
  private val endIconId: Int
  private val locationIconId: Int
  private val markerIconId: Int

  private var trackName = ""
  private var lastx: Int = 0
  private val placemarks = ArrayList<Placemark>()

  //private val NorthWestCorner: Location? = null
  //private val SouthEastCorner: Location? = null

  val isEmpty: Boolean
    get() {
      synchronized(placemarks) {
        return placemarks.isEmpty()
      }
    }

  init {
    startIconId = R.drawable.mm_20_orange
    endIconId = R.drawable.finish1
    locationIconId = R.drawable.icon57 // R.drawable.mountain_bike_helmet_16;
    markerIconId = 0 //R.drawable.icon57;
    lastx = -1
  }

  fun moveToStart(): Placemark {
    synchronized(placemarks) {
      lastx = if (placemarks.isEmpty()) -1 else 0
      return if (placemarks.isEmpty()) Placemark.NO_PLACEMARK else placemarks[lastx]
    }
  }

  fun moveNext(): Placemark {
    synchronized(placemarks) {
      if (++lastx >= placemarks.size) {
        lastx = if (placemarks.isEmpty()) -1 else 0
      }
      return if (placemarks.isEmpty()) Placemark.NO_PLACEMARK else placemarks[lastx]
    }
  }

  fun moveToEnd(): Placemark {
    synchronized(placemarks) {
      lastx = if (placemarks.isEmpty()) -1 else placemarks.size - 1
      return if (placemarks.isEmpty()) Placemark.NO_PLACEMARK else placemarks[lastx]
    }
  }

  fun clear() {
    synchronized(placemarks) {
      placemarks.clear()
    }
  }

  override fun toString(): String {
    val buf = StringBuffer()
    buf.append(appName).append(":\n")
    synchronized(placemarks) {
      for (p in placemarks) {
        buf.append("\n").append(p.toString())
      }
    }
    return buf.toString()
  }

  /**
   * would line between pts show on screen
   *
   * @return
   */
  //    public static boolean isOnScreen(LatLngBounds boundaries, Placemark pl1, Placemark pl2)
  //    {
  //
  //        if (isOnScreen(boundaries, pl1))
  //        {
  //            return true;
  //        }
  //        if (isOnScreen(boundaries, pl2))
  //        {
  //            return true;
  //        }
  //        // neither endpoint is on screen
  //        // but line between them may be on screen
  //        // simple case if both lat or both lon are within screen and other dimension crosses screen
  //
  //        final LatLng pt1 = pl1.getLatLng();
  //        final double pt1_lat = pt1.latitude;
  //        final double pt1_lon = pt1.longitude;
  //        final LatLng pt2 = pl2.getLatLng();
  //        final double pt2_lat = pt2.latitude;
  //        final double pt2_lon = pt2.longitude;
  //        final double bottom_lat = boundaries.southwest.latitude;
  //        final double bottom_lon = boundaries.southwest.longitude;
  //        final double top_lat = boundaries.northeast.latitude;
  //        final double top_lon = boundaries.northeast.longitude;
  //        final double left_lon = bottom_lon;
  //        final double right_lon = top_lon;
  //
  //        if (((bottom_lat <= pt1_lat) && (top_lat >= pt1_lat))
  //                && ((bottom_lat <= pt2_lat) && (top_lat >= pt2_lat)))
  //        {
  //            if (straddles(left_lon, pt1_lon, pt2_lon))
  //            {
  //                return true;
  //            }
  //            if (straddles(right_lon, pt1_lon, pt2_lon))
  //            {
  //                return true;
  //            }
  //            return false;
  //        }
  //
  //        if (((left_lon <= pt1_lon) && (right_lon >= pt1_lon))
  //                && ((left_lon <= pt2_lon) && (right_lon >= pt2_lon)))
  //        {
  //            if (straddles(top_lat, pt1_lat, pt2_lat))
  //            {
  //                return true;
  //            }
  //            if (straddles(bottom_lat, pt1_lat, pt2_lat))
  //            {
  //                return true;
  //            }
  //            return false;
  //        }
  //
  //        // TODO - more complicated is where line crosses screen diagonally
  //        // i.e., one pt is above screen and other pt is to right of screen
  //
  //        return true;
  //    }
  private fun mapColor(cc: Int): Int {
    var color = cc
    Log.d(appName, "map color before: $color")

    // color correction for dining, make it darker
    if (color == Color.parseColor("#add331")) {
      color = Color.parseColor("#6C8715")
    }
    Log.d(appName, "map color after: $color")
    return color
  }

  private fun removeMarkers() {
    // List<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
    // remove our RouteOverlays
    synchronized(placemarks) {
      for (next in placemarks) {
        next.removeMarker()
        // Overlay o = iter.next();
        // Log.d(appName, "overlay type: " + o.getClass().getName());
        // if (o instanceof RouteOverlay)
        // {
        // // overlaysToAddAgain.add(o);
        // iter.remove();
        // }
      }
      // mapOverlays.addAll(overlaysToAddAgain);
    }
  }

  /**
   * Does the actual drawing of the route, based on the geo points of the cue sheet
   *
   * @param color Color in which to draw the lines
   */
  fun drawPath(color: Int) {
    // color = mapColor(color);
    // //removeOverlays(mapOverlays);
    // // updateOverLays(boundaries, color, mapOverlays);
    //probably not needed any more... google map v2 will draw the route as needed
    //addMarkers(color);
  }

  fun clearMap() {
    map?.clear()
  }

  /**
   * @param color
   */
  fun drawRoute(color: Int) {
    map?.addPolyline(
      PolylineOptions()
        .color(mapColor(color))
        .width(5f)
        .visible(true)
        .addAll(Pts())
    )
    addMarkers(color)
  }

  private fun addMarkers(color: Int) {
    var start: Placemark? = null
    var prev: Placemark? = null

    val options = MarkerOptions()
    for (next in placemarks) {
      var marker: Marker? = null
      if (null == start) {
        start = next
        Log.d(appName, "start: $start")
        if (map != null) { marker = start.addMarker(map, startIconId) }
      } else if (next === start) {
        // crosses start, assume it is loop end? or use iterator explicitly so we can check if last?
        // add if it has a marker and is on screen
        Log.d(appName, "loop end: $next")
      } else {
        // draw line
        Log.d(appName, "line: ${prev ?: ""} TO $next")
        // mapOverlays.add(new RouteOverlay.LineOverlay(prevPoint, nextPoint, color).withText(next.getTitle()));
        // // CONNECT
        // draw placemark
        // TODO - add placemark only if there is a marker ???
        // TURN, LUNCH, etc marker
        // mapOverlays.add(new RouteOverlay.MarkOverlay(nextPoint).withText(next.getTitle()));
        if (next.title != null) {
          if (map != null) marker = next.addMarker(map, locationIconId)
        } else if (markerIconId > 0) {
          if (map != null) marker = next.addMarker(map, markerIconId)
        }
      }
      if (null != marker) {
        marker.setPosition(start.latLng)
      }
      prev = next
    }

    // if path is not a loop
    if (null != prev && prev != start) {
      if (map != null) prev.addMarker(map, endIconId)// END
    }

  }

  fun addPlacemark(placemark: Placemark) {
    synchronized(placemarks) {
      placemarks.add(placemark)
    }
  }

  fun addTrk(name: String) {
    this.trackName = name
  }

  @JvmOverloads
  fun addPt(lat: Double, lon: Double, altitude: Double = 0.0) {
    addPlacemark(Placemark(lat, lon, altitude))
  }

  fun totalUp(): Double {
    var total = 0.0
    synchronized(placemarks) {
      var alt = placemarks[0].altitude
      for (pm in placemarks) {
        if (pm.altitude > alt) {
          total += pm.altitude - alt
          alt = pm.altitude
        }
      }
    }
    return total
  }

  fun totalDown(): Double {
    var total = 0.0
    synchronized(placemarks) {
      var alt = placemarks[0].altitude
      for (pm in placemarks) {
        if (pm.altitude < alt) {
          total += alt - pm.altitude
          alt = pm.altitude
        }
      }
    }
    return total
  }

  inner class Pts : Iterable<LatLng> {

    override fun iterator(): Iterator<LatLng> {
      return iPt()
    }

  }

  inner class iPt : Iterator<LatLng> {
    private lateinit var pts: Array<LatLng>
    private var last = -1

    init {
      synchronized(placemarks) {
        pts = Array<LatLng>(placemarks.size, { ii ->  placemarks[ii].latLng })
      }
    }

    override fun hasNext(): Boolean {
      return last + 1 < pts.size
    }

    override fun next(): LatLng {
      return pts[++last]
    }

    fun remove() {
      //it.remove();
      throw UnsupportedOperationException("remove using iterator not supported")
    }

  }

  companion object {

    // public ArrayList<Placemark> getPlacemarks()
    // {
    // return placemarks;
    // }

    /**
     * is point on screen
     *
     * @return
     */
    fun isOnScreen(boundaries: LatLngBounds, pl: Placemark): Boolean {
      // GeoPoint pt = pl.getPoint();
      // int pt_lat = pt.getLatitudeE6();
      // int pt_lon = pt.getLongitudeE6();
      // // pt is on screen if its latitude and longitude are in the box
      // if ((bottom_lat <= pt_lat) && (top_lat >= pt_lat))
      // {
      // if ((left_lon <= pt_lon) && (right_lon >= pt_lat))
      // {
      // return true;
      // }
      // }
      // return false;
      return boundaries.contains(pl.latLng)
    }

    fun straddles(boundary: Double, pt1: Double, pt2: Double): Boolean {
      return boundary in min(pt1,pt2)..max(pt1, pt2)
    }
  }
}
