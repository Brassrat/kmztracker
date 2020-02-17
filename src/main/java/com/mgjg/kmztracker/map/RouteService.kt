package com.mgjg.kmztracker.map

import android.app.Activity
import android.location.Location
import android.util.Log

import com.google.android.gms.maps.GoogleMap
import com.mgjg.kmztracker.AppResources
import com.mgjg.kmztracker.R
import com.mgjg.kmztracker.cuesheet.CueSheet
import com.mgjg.kmztracker.cuesheet.parser.CueSheetParserFactory

class RouteService(private val appName: String) {

  fun calculateRoute(
    map: GoogleMap,
    overlayer: MapOverlayer,
    activity: Activity,
    start: Location,
    target: Location,
    mode: Int
  ): CueSheet? {
    return calculateRoute(
      map, overlayer, start.latitude.toString() + "," + start.longitude,
      target.latitude.toString() + "," + target.longitude, mode
    )
  }

  fun calculateRoute(
    map: GoogleMap,
    overlayer: MapOverlayer,
    activity: Activity,
    startLat: Double?,
    startLng: Double?,
    targetLat: Double?,
    targetLng: Double?,
    mode: Int
  ): CueSheet? {
    return calculateRoute(
      map,
      overlayer,
      startLat.toString() + "," + startLng,
      targetLat.toString() + "," + targetLng,
      mode
    )
  }

  fun calculateRoute(
    map: GoogleMap,
    overlayer: MapOverlayer,
    startCoords: String,
    targetCoords: String,
    mode: Int
  ): CueSheet? {

    // TODO ... need to change to get driving directions!!!
    val urlPedestrianMode = ("http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
        + targetCoords + "&sll=" + startCoords + "&dirflg=w&hl=en&ie=UTF8&z=14&output=kml")

    Log.d(appName, "urlPedestrianMode: " + urlPedestrianMode)

    val urlCarMode = ("http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
        + targetCoords + "&sll=" + startCoords + "&hl=en&ie=UTF8&z=14&output=kml")

    Log.d(appName, "urlCarMode: " + urlCarMode)

    val color = AppResources.getColor(R.color.red)
    var navSet: CueSheet? = null
    // for mode_any: try pedestrian route calculation first, if it fails, fall back to car route
    if (mode == MODE_ANY || mode == MODE_WALKING) {
      navSet = updateCueSheet(CueSheet(appName, map, overlayer), urlPedestrianMode, color)
    }
    if (mode == MODE_ANY && navSet == null || mode == MODE_CAR) {
      navSet = updateCueSheet(CueSheet(appName, map, overlayer), urlCarMode, color)
    }
    return navSet
  }

  companion object {
    val MODE_ANY = 0
    val MODE_CAR = 1
    val MODE_WALKING = 2

    /**
     * Retrieve navigation data set from either remote URL or String
     *
     * @param url
     * @return navigation set
     */
    fun updateCueSheet(cueSheet: CueSheet, url: String, color: Int): CueSheet {
      // urlString = "http://192.168.1.100:80/test.kml";
      Log.d(cueSheet.appName, "parsing urlString " + url)
      CueSheetParserFactory.parseUrl(cueSheet, url, color)
      return cueSheet
    }
  }

}