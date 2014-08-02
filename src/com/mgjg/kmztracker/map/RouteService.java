package com.mgjg.kmztracker.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.cuesheet.parser.CueSheetParser;
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

    // public String inputStreamToString(InputStream in) throws IOException
    // {
    // StringBuffer out = new StringBuffer();
    // byte[] b = new byte[4096];
    // for (int n; (n = in.read(b)) != -1;)
    // {
    // out.append(new String(b, 0, n));
    // }
    // return out.toString();
    // }

    public CueSheet calculateRoute(Context context, Location start, Location target, int mode)
    {
        return calculateRoute(context, start.getLatitude() + "," + start.getLongitude(), 
            target.getLatitude() + "," + target.getLongitude(), mode);
    }

    public CueSheet calculateRoute(Context context, Double startLat, Double startLng, Double targetLat, Double targetLng, int mode)
    {
        return calculateRoute(context, startLat + "," + startLng, targetLat + "," + targetLng, mode);
    }

    public CueSheet calculateRoute(Context context, String startCoords, String targetCoords, int mode)
    {

        // TODO ... need to change to get driving directions!!!
        String urlPedestrianMode = "http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
                + targetCoords + "&sll=" + startCoords + "&dirflg=w&hl=en&ie=UTF8&z=14&output=kml";

        Log.d(appName, "urlPedestrianMode: " + urlPedestrianMode);

        String urlCarMode = "http://maps.google.com/maps?" + "saddr=" + startCoords + "&daddr="
                + targetCoords + "&sll=" + startCoords + "&hl=en&ie=UTF8&z=14&output=kml";

        Log.d(appName, "urlCarMode: " + urlCarMode);

        CueSheet navSet = null;
        // for mode_any: try pedestrian route calculation first, if it fails, fall back to car route
        if (mode == MODE_ANY || mode == MODE_WALKING)
            navSet = updateCueSheet(new CueSheet(appName, context), urlPedestrianMode);
        if (mode == MODE_ANY && navSet == null || mode == MODE_CAR)
            navSet = updateCueSheet(new CueSheet(appName, context), urlCarMode);
        return navSet;
    }

    /**
     * Retrieve navigation data set from either remote URL or String
     * 
     * @param url
     * @return navigation set
     */
    public static CueSheet updateCueSheet(CueSheet cueSheet, String url)
    {
        // urlString = "http://192.168.1.100:80/test.kml";
        Log.d(cueSheet.getAppName(), "parsing urlString " + url);

        try
        {
            CueSheetParser parser = CueSheetParserFactory.makeParser(cueSheet.getAppName(), url);
            parser.parse(cueSheet);

            /* Set the result to be displayed in our GUI. */
            Log.d(cueSheet.getAppName(), "CueSheet: " + cueSheet.toString());
        }
        catch (Exception e)
        {
            Log.e(cueSheet.getAppName(), "error with kml url " + url, e);
        }

        return cueSheet;
    }

}