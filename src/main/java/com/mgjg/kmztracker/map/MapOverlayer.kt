package com.mgjg.kmztracker.map

import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.mgjg.kmztracker.AppResources
import com.mgjg.kmztracker.R
import com.mgjg.kmztracker.cuesheet.CueSheet
import java.util.*

class MapOverlayer(appName: String, private val googleMap: GoogleMap) {
  // private final Stack<LatLng> points = new Stack<LatLng>();
  private val points = LinkedList<LatLng>()

  private val initial_zoom = 12

  private val cueSheet: CueSheet
  private var locationMarker: Marker? = null
  private val color: Int
  private val markers = ArrayList<Marker>()

  private val ALLOW_ROTATION = false

  private var prevDrawId: Int = 0
  private var prevBearing: Float = 0.toFloat()
  private var prevDrawable: BitmapDrawable? = null

  val mapCenter: LatLng
    get() = googleMap.cameraPosition.target

  // right >= left ==> (right-left) >= 0
  val longitudeSpan: Double
    get() {
      val bounds = mapBoundaries
      val left = bounds.southwest.longitude
      val right = bounds.northeast.longitude
      return right - left
    }

  /**
   * returns map boundaries in array where array[0] is UL latitude array[1] is UL longitude array[2] is LR latitude
   * array[3] is LR longitude
   *
   * @return
   */
  // googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
  // Double.parseDouble(lat), Double.parseDouble(lng))));
  // compute width and height of current map window in degrees
  // VisibleRegion vr = googleMap.getProjection().getVisibleRegion();
  // not needed anymore ...
  // right >= left ==> (right-left) >= 0
  // top >= bottom ==> (top-bottom) >= 0
  // double[] boundaries = new double[4];
  // top
  // left
  // bottom
  // right
  // return boundaries;
  val mapBoundaries: LatLngBounds
    get() {
      val ctr = googleMap.cameraPosition.target
      val ctrLat = ctr.latitude
      val ctrLon = ctr.longitude
      val bounds = googleMap.projection.visibleRegion.latLngBounds
      val left = bounds.southwest.longitude
      val top = bounds.northeast.latitude
      val right = bounds.northeast.longitude
      val bottom = bounds.southwest.latitude
      val longitudeSpan = right - left
      val latitudeSpan = top - bottom
      val lonHalf = longitudeSpan / 2
      val latHalf = latitudeSpan / 2
      val xtop = ctrLat + latHalf
      val xleft = ctrLon - lonHalf
      val xbottom = ctrLat - latHalf
      val xright = ctrLon + lonHalf
      val xbounds = LatLngBounds(LatLng(xbottom, xleft), LatLng(xtop, xright))
      return bounds
    }

  init {
    cueSheet = CueSheet(appName, googleMap)

    googleMap.uiSettings.isZoomControlsEnabled = true
    // MapController mapController = googleMap.getUiSettings().getController();
    // mapController.setZoom(initial_zoom);
    // googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
    // Double.parseDouble(lat), Double.parseDouble(lng))));

    googleMap.animateCamera(CameraUpdateFactory.zoomTo(initial_zoom.toFloat()))

    color = AppResources.getColor(R.color.DodgerBlue)
  }

  fun updateCueSheet(url: String) {
    RouteService.updateCueSheet(cueSheet, url, color)
  }

  fun moveToStart() {
    mvPoint(cueSheet!!.moveToStart())
  }

  fun moveNext() {
    mvPoint(cueSheet!!.moveNext())
  }

  fun moveToEnd() {
    mvPoint(cueSheet!!.moveToEnd())
  }

  fun mvPoint(placemark: Placemark): LatLng? {
    return if (placemark !== Placemark.NO_PLACEMARK) mvPoint(placemark.latLng) else null
  }

  fun mvPoint(loc: Location): LatLng {
    return mvPoint(loc.latitude, loc.longitude)
  }

  fun mvPoint(lat: Double, lon: Double): LatLng {
    return mvPoint(LatLng(lat, lon))
  }

  private fun mvPoint(newPoint: LatLng): LatLng {
    // String Text = "My current location is: " + "Lat = " + lat +
    // " Long = " + lon;
    // Toast.makeText(getApplicationContext(), Text,
    // Toast.LENGTH_SHORT).show();
    val bearing: Float
    if (points.size <= 0) {
      bearing = 270f
    } else {
      val prev = points.peek()
      val distAndBearing = dist(prev, newPoint)
      bearing = distAndBearing[1]
    }

    points.push(newPoint)
    if (points.size > MAX_POINTS) {
      points.removeLast()
    }

    // final int newPointLon = geo.getLongitudeE6();
    // final int newPointLat = geo.getLatitudeE6();

    // TODO only move map if new point is outside 'center' region of map
    // if point is off the map move the ctr to the new point
    // if the point is outside the 'center' but displayable without
    // moving the map then just show the point
    // TODO deal with rotated map
    // MapView mapView = (MapView) mapActivity.findViewById(R.id.mapview);
    // if (null == mapView)
    // {
    // return geo;
    // }
    var ctr: LatLng? = mapCenter
    val boundaries = mapBoundaries
    ctr = boundaries.center
    // double ctrLat = ctr.latitude;
    // double ctrLon = ctr.longitude;
    var newCtr: LatLng? = null

    // int ctrLat = ctr.getLatitudeE6();
    // int ctrLon = ctr.getLongitudeE6();
    // final int lonHalf = mapView.getLongitudeSpan() / 2;
    // final int latHalf = mapView.getLatitudeSpan() / 2;

    // if new point is not within the current display
    // make the new point the center since we don't know
    // which way we are moving
    if (!boundaries.contains(newPoint)) {
      newCtr = newPoint
    } else {
      var ctrLat = ctr!!.latitude
      var ctrLon = ctr.longitude
      val ul_lat = boundaries.northeast.latitude
      val ul_lon = boundaries.southwest.longitude
      val lr_lat = boundaries.southwest.latitude
      val lr_lon = boundaries.northeast.longitude

      // compute box within current display which
      // if point stays in the box means we don't move the center
      // box is 80% of screen, i.e., 40% on either side of center
      val box_width_pct = .80 / 2
      val box_height_pct = .80 / 2
      val box_move_pct = .5 * 1.2
      val halfWindowWidth = (ul_lon - lr_lon) * box_width_pct
      val halfWindowHeight = (ul_lat - lr_lat) * box_height_pct

      val leftLon = ctrLon - halfWindowWidth
      val rightLon = ctrLon + halfWindowWidth
      val bottomLat = ctrLat - halfWindowHeight
      val topLat = ctrLat + halfWindowHeight

      if (newPoint.latitude < bottomLat) {
        // if new point is too low on screen move it up a bit, i.e. move
        // center latitude smaller
        ctrLat -= (ul_lat - lr_lat) * box_move_pct // (lonHalf * 1.2);
        ctr = null
      } else if (newPoint.latitude > topLat) {
        ctrLat += (ul_lat - lr_lat) * box_move_pct // (lonHalf * 1.2);
        ctr = null
      }
      if (newPoint.longitude < leftLon) {
        ctrLon -= (ul_lon - lr_lon) * box_move_pct // (latHalf * 1.2);
        ctr = null
      }
      if (newPoint.longitude > rightLon) {
        ctrLon += (ul_lon - lr_lon) * box_move_pct // (latHalf * 1.2);
        ctr = null
      }
      newCtr = LatLng(ctrLat, ctrLon)
    }

    if (null != newCtr && null != cueSheet) {
      cueSheet.drawPath(color)
    }

    updateLocationMarker(newPoint, bearing, "On the road", "Here")
    if (null != newCtr) {
      // MapController mapController = mapView.getController();
      ctr = newCtr
      // mapController.animateTo(ctr);
      // mapController.setCenter(new LatLng(ctrLat, ctrLon));
      googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCtr, 12.0f))
    }

    // googleMap.invalidate(); // may be overkill ???
    return newPoint
  }

  private fun updateLocationMarker(point: LatLng, bearing: Float, title: String, snippet: String) {
    // MapView mapView = (MapView) mapActivity.findViewById(R.id.mapview);

    // googleMap.clear();
    // remove our LocationOverlay
    // for (Iterator<Overlay> iter = mapOverlays.iterator(); iter.hasNext();)
    // {
    // Overlay o = iter.next();
    // Log.d(cueSheet.getAppName(), "overlay type: " + o.getClass().getName());
    // if (o instanceof LocationOverlay)
    // {
    // iter.remove();
    // }
    // }

    // Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
    // TODO - determine helmet size by number of degrees of width...
    val bigger = longitudeSpan > 1
    val left =
      if (bigger) R.drawable.mountain_bike_helmet_24 else R.drawable.mountain_bike_helmet_16
    // TODO need larger flipped
    val right =
      if (bigger) R.drawable.mountain_bike_helmet_16_flipped else R.drawable.mountain_bike_helmet_16_flipped
    val drawable = rotateDrawable(left, right, bearing)
    // Drawable drawable = getResources().getDrawable(drawId);
    // LocationOverlay locationOverlay = new LocationOverlay(drawable);

    // LocationOverlayItem overlayitem = new LocationOverlayItem(point, title, snippet);
    // locationOverlay.addOverlay(overlayitem);
    // mapOverlays.add(locationOverlay);
    // googleMap.setEnabled(true);
    // see https://developers.google.com/maps/documentation/android/marker
    // ?? can we change existing marker or do we have to remove it and add another
    if (null == locationMarker) {
      val mo = MarkerOptions()
        .position(point)
        .flat(true)
        .title(title)
        .icon(drawableToIcon(drawable))
      if (ALLOW_ROTATION) {
        mo.anchor(0.5f, 0.5f)
        mo.rotation(bearing)
      }
      locationMarker = googleMap.addMarker(mo)
      // markers.add(googleMap.addMarker(mo));
    } else {
      locationMarker!!.setPosition(point)
      if (ALLOW_ROTATION) {
        locationMarker!!.setAnchor(0.5f, 0.5f)
        locationMarker!!.rotation = bearing
      }
    }
  }

  fun drawMarker(point: LatLng): Marker {
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
    val mm = googleMap.addMarker(markerOptions)
    markers.add(mm)
    return mm
  }

  fun drawCircle(point: LatLng): Circle {

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
    return googleMap.addCircle(circleOptions)
  }

  fun drawableToIcon(drawId: Int): BitmapDescriptor {
    return BitmapDescriptorFactory.fromBitmap(
      BitmapFactory.decodeResource(
        AppResources.resources,
        drawId
      )
    )
  }

  fun drawableToIcon(draw: Drawable): BitmapDescriptor {
    return BitmapDescriptorFactory.fromBitmap(drawableToBitmap(draw))
  }

  fun rotateDrawable(leftId: Int, rightId: Int, bearing: Float): BitmapDrawable {
    // TODO - use BitmapDescriptorFactory.fromResource(xId) to get the descriptors once
    // then just rotate the Marker ...
    // i.e., get rid of all this stuff...
    val drawId = if (bearing < 0) leftId else rightId

    val xxx = prevDrawable;

    if ((xxx == null) || (prevDrawId != drawId) || (prevBearing != bearing)) {
      // Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), drawId);
      // Drawable drawable = getResources().getDrawable(drawId);
      val bitMap = BitmapFactory.decodeResource(AppResources.resources, drawId, null)
      // Create blank bitmap of equal size
      val canvasBitmap = bitMap.copy(Bitmap.Config.ARGB_8888, true)
      canvasBitmap.eraseColor(0x00000000)

      // Create canvas
      val canvas = Canvas(canvasBitmap)

      // Create rotation matrix
      val rotateMatrix = Matrix()
      val angle = if (bearing < 0) 90 + bearing else bearing - 90
      rotateMatrix.setRotate(
        angle, (canvas.width / 2).toFloat(),
        (canvas.height / 2).toFloat()
      )

      // Draw bitmap onto canvas using matrix
      canvas.drawBitmap(bitMap, rotateMatrix, null)

      prevDrawId = drawId
      prevBearing = bearing
      // prevDrawable = new BitmapDrawable(canvasBitmap);
      val yyy = BitmapDrawable(AppResources.resources, canvasBitmap)
      prevDrawable = yyy;
      return yyy;
    }
    return xxx;
  }

  fun isVisible(point: LatLng): Boolean {
    val bounds = googleMap.projection.visibleRegion.latLngBounds
    return bounds.contains(point)
  }

  fun isVisible(lat: Double, lng: Double): Boolean {
    return isVisible(LatLng(lat, lng))
  }

  companion object {

    private val MAX_POINTS = 256

    fun drawableToBitmap(drawable: Drawable): Bitmap {
      if (drawable is BitmapDrawable) {
        return drawable.bitmap
      }

      val bitmap =
        Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Config.ARGB_8888)
      val canvas = Canvas(bitmap)
      drawable.setBounds(0, 0, canvas.width, canvas.height)
      drawable.draw(canvas)
      return bitmap
    }

    internal fun dist(prev: LatLng, current: LatLng): FloatArray {
      val results = FloatArray(2)
      Location.distanceBetween(
        prev.latitude,
        prev.longitude,
        current.latitude,
        current.longitude, results
      )
      return results
    }
  }

}
