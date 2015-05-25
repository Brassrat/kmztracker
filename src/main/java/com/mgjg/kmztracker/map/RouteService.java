package com.mgjg.kmztracker.map;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.mgjg.kmztracker.R;
import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.cuesheet.parser.CueSheetParserFactory;

public class RouteService
{
    public static final int MODE_ANY = 0;
    public static final int MODE_CAR = 1;
    public static final int MODE_WALKING = 2;

    private final String appName;

    public RouteService(String appName)
    {
        this.appName = appName;
    }

    @SuppressWarnings("unused")
    public CueSheet calculateRoute(GoogleMap map, Activity activity, Location start, Location target, int mode)
    {
        return calculateRoute(map, activity, start.getLatitude() + "," + start.getLongitude(),
                target.getLatitude() + "," + target.getLongitude(), mode);
    }

    @SuppressWarnings("unused")
    public CueSheet calculateRoute(GoogleMap map, Activity activity, Double startLat, Double startLng, Double targetLat, Double targetLng, int mode)
    {
        return calculateRoute(map, activity, startLat + "," + startLng, targetLat + "," + targetLng, mode);
    }

    @SuppressWarnings("unused")
    public CueSheet calculateRoute(GoogleMap map, Activity activity, String startCoords, String targetCoords, int mode)
    {

        // TODO ... need to change to get driving directions!!!
        String urlPedestrianMode = "http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
                + targetCoords + "&sll=" + startCoords + "&dirflg=w&hl=en&ie=UTF8&z=14&output=kml";

        Log.d(appName, "urlPedestrianMode: " + urlPedestrianMode);

        String urlCarMode = "http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
                + targetCoords + "&sll=" + startCoords + "&hl=en&ie=UTF8&z=14&output=kml";

        Log.d(appName, "urlCarMode: " + urlCarMode);

        int color = activity.getResources().getColor(R.color.red);
        CueSheet navSet = null;
        // for mode_any: try pedestrian route calculation first, if it fails, fall back to car route
        if (mode == MODE_ANY || mode == MODE_WALKING)
        {
            navSet = updateCueSheet(new CueSheet(appName, activity, map), urlPedestrianMode, color);
        }
        if (mode == MODE_ANY && navSet == null || mode == MODE_CAR)
        {
            navSet = updateCueSheet(new CueSheet(appName, activity, map), urlCarMode, color);
        }
        return navSet;
    }

    /**
     * Retrieve navigation data set from either remote URL or String
     *
     * @param url
     * @return navigation set
     */
    public static CueSheet updateCueSheet(CueSheet cueSheet, String url, int color)
    {
        // urlString = "http://192.168.1.100:80/test.kml";
        Log.d(cueSheet.getAppName(), "parsing urlString " + url);
        CueSheetParserFactory.parseUrl(cueSheet, url, color);
        return cueSheet;
    }

}