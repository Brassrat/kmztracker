package com.mgjg.kmztracker.cuesheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.mgjg.kmztracker.map.Placemark;
import com.mgjg.kmztracker.map.RouteOverlay;

/**
 * A list of Placemarks, each Placemark indicates a place on the route. The route consists of lines connecting the
 * Placemarks. There are 2 kinds of placements, those with and those without markers. Ones with markers indicate changes
 * in direction or some other notable place on the route (i.e., beginning/ending location, lunch stop, etc.)
 * 
 * @author Jay Goldman
 * 
 */
public class CueSheet
{

    private final String appName;
    private ArrayList<Placemark> placemarks = new ArrayList<Placemark>();

    public CueSheet(String appName)
    {
        this.appName = appName;
    }

    public boolean isEmpty()
    {
        return placemarks.isEmpty();
    }

    public void clear()
    {
        placemarks.clear();
    }
    
    public String getAppName()
    {
        return appName;
    }
    
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(appName).append(":\n");
        for (Placemark p : placemarks)
        {
            buf.append("\n").append(p.toString());
        }
        return buf.toString();
    }

    // public ArrayList<Placemark> getPlacemarks()
    // {
    // return placemarks;
    // }

    /**
     * is point on screen
     * 
     * @param top_lat
     * @param left_lon
     * @param bottom_lat
     * @param right_lon
     * @param pt
     * @return
     */
    public static boolean isOnScreen(int top_lat, int left_lon, int bottom_lat, int right_lon, Placemark pl)
    {
        GeoPoint pt = pl.getPoint();
        int pt_lat = pt.getLatitudeE6();
        int pt_lon = pt.getLongitudeE6();
        // pt is on screen if its latitude and longitude are in the box
        if ((bottom_lat <= pt_lat) && (top_lat >= pt_lat))
        {
            if ((left_lon <= pt_lon) && (right_lon >= pt_lat))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean straddles(int boundary, int pt1, int pt2)
    {
        return ((pt1 <= boundary) && (pt2 >= boundary)) ||
                ((pt2 <= boundary) && (pt1 >= boundary));
    }

    /**
     * would line between pts show on screen
     * 
     * @param top_lat
     * @param left_lon
     * @param bottom_lat
     * @param right_lon
     * @param pt1
     * @param pt2
     * @return
     */
    public static boolean isOnScreen(int top_lat, int left_lon, int bottom_lat, int right_lon, Placemark pl1, Placemark pl2)
    {

        if (isOnScreen(top_lat, left_lon, bottom_lat, right_lon, pl1))
        {
            return true;
        }
        if (isOnScreen(top_lat, left_lon, bottom_lat, right_lon, pl2))
        {
            return true;
        }
        // neither endpoint is on screen
        // but line between them may be on screen
        // simple case if both lat or both lon are within screen and other dimension crosses screen

        GeoPoint pt1 = pl1.getPoint();
        GeoPoint pt2 = pl2.getPoint();
        int pt1_lat = pt1.getLatitudeE6();
        int pt1_lon = pt1.getLongitudeE6();
        int pt2_lat = pt2.getLatitudeE6();
        int pt2_lon = pt2.getLongitudeE6();

        if (((bottom_lat <= pt1_lat) && (top_lat >= pt1_lat))
                && ((bottom_lat <= pt2_lat) && (top_lat >= pt2_lat)))
        {
            if (straddles(left_lon, pt1_lon, pt2_lon))
            {
                return true;
            }
            if (straddles(right_lon, pt1_lon, pt2_lon))
            {
                return true;
            }
            return false;
        }

        if (((left_lon <= pt1_lon) && (right_lon >= pt1_lon))
                && ((left_lon <= pt2_lon) && (right_lon >= pt2_lon)))
        {
            if (straddles(top_lat, pt1_lat, pt2_lat))
            {
                return true;
            }
            if (straddles(bottom_lat, pt1_lat, pt2_lat))
            {
                return true;
            }
            return false;
        }

        // TODO - more complicated is where line crosses screen diagonally
        // i.e., one pt is above screen and other pt is to right of screen

        return true;
    }

    /**
     * Does the actual drawing of the route, based on the geo points of the cue sheet
     * 
     * @param color
     *            Color in which to draw the lines
     * @param mapView
     *            Map view to draw onto
     */
    public void drawPath(int[] boundaries, int color, List<Overlay> mapOverlays)
    {

        int top_lat = boundaries[0];
        int left_lon = boundaries[1];
        int bottom_lat = boundaries[2];
        int right_lon = boundaries[3];

        Log.d(appName, "map color before: " + color);

        // color correction for dining, make it darker
        if (color == Color.parseColor("#add331"))
            color = Color.parseColor("#6C8715");
        Log.d(appName, "map color after: " + color);

        // List<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();

        // remove our RouteOverlays
        for (Iterator<Overlay> iter = mapOverlays.iterator(); iter.hasNext();)
        {
            Overlay o = iter.next();
            Log.d(appName, "overlay type: " + o.getClass().getName());
            if (o instanceof RouteOverlay)
            {
                // overlaysToAddAgain.add(o);
                iter.remove();
            }
        }
        // mapOverlays.addAll(overlaysToAddAgain);

        Placemark start = null;
        Placemark prev = null;
        GeoPoint prevPoint = null;
        for (Placemark next : placemarks)
        {
            GeoPoint nextPoint = next.getPoint();
            if (null == start)
            {
                start = next;
                if (isOnScreen(top_lat, left_lon, bottom_lat, right_lon, start))
                {
                    // add if it has a marker and is on screen
                    Log.d(appName, "draw: " + start.toString());
                    mapOverlays.add(new RouteOverlay.StartOverlay(nextPoint).withText(next.getTitle())); // START
                }
            }
            else if (isOnScreen(top_lat, left_lon, bottom_lat, right_lon, prev, next))
            {
                if (next == start)
                {
                    // crosses start, assume it is loop end? or use iterator explicitly so we can check if last?
                    // add if it has a marker and is on screen
                    Log.d(appName, "loop end: " + next.toString());
                    mapOverlays.add(new RouteOverlay.LoopEndOverlay(prevPoint, nextPoint, color).withText(next.getTitle())); // START
                }
                else
                {
                    // draw line
                    Log.d(appName, "line:" + prev.toString() + " TO " + next.toString());
                    mapOverlays.add(new RouteOverlay.LineOverlay(prevPoint, nextPoint, color).withText(next.getTitle())); // CONNECT
                    // draw placemark
                    // TODO - add placemark only if there is a marker ???
                    // TURN, LUNCH, etc marker
                    mapOverlays.add(new RouteOverlay.MarkOverlay(nextPoint).withText(next.getTitle()));
                }
            }
            prev = next;
            prevPoint = nextPoint;
        }

        // if path is not a loop
        if (prev != start)
        {
            if (isOnScreen(top_lat, left_lon, bottom_lat, right_lon, prev))
            {
                Log.d(appName, "end: " + prev.toString());
                mapOverlays.add(new RouteOverlay.EndOverlay(prev.getPoint()).withText(prev.getTitle())); // END
            }
        }

    }

    public void addPlacemark(Placemark placemark)
    {
        placemarks.add(placemark);
    }
}
