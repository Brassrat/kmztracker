package com.mgjg.kmztracker.cuesheet;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mgjg.kmztracker.R;
import com.mgjg.kmztracker.map.Placemark;

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
    private final int start_icon;
    private final int end_icon;
    private final int location_icon;

    public CueSheet(String appName, Context context)
    {
        this.appName = appName;
        start_icon = R.drawable.mountain_bike_helmet_16;
        end_icon = R.drawable.mountain_bike_helmet_16;
        location_icon = R.drawable.mountain_bike_helmet_16;
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
    public static boolean isOnScreen(LatLngBounds boundaries, Placemark pl)
    {
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
        return boundaries.contains(pl.getPoint());
    }

    public static boolean straddles(double boundary, double pt1, double pt2)
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
    public static boolean isOnScreen(LatLngBounds boundaries, Placemark pl1, Placemark pl2)
    {

        if (isOnScreen(boundaries, pl1))
        {
            return true;
        }
        if (isOnScreen(boundaries, pl2))
        {
            return true;
        }
        // neither endpoint is on screen
        // but line between them may be on screen
        // simple case if both lat or both lon are within screen and other dimension crosses screen

        final LatLng pt1 = pl1.getPoint();
        final double pt1_lat = pt1.latitude;
        final double pt1_lon = pt1.longitude;
        final LatLng pt2 = pl2.getPoint();
        final double pt2_lat = pt2.latitude;
        final double pt2_lon = pt2.longitude;
        final double bottom_lat = boundaries.southwest.latitude;
        final double bottom_lon = boundaries.southwest.longitude;
        final double top_lat = boundaries.northeast.latitude;
        final double top_lon = boundaries.northeast.longitude;
        final double left_lon = bottom_lon;
        final double right_lon = top_lon;

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

    private int mapColor(int color)
    {
        Log.d(appName, "map color before: " + color);

        // color correction for dining, make it darker
        if (color == Color.parseColor("#add331"))
            color = Color.parseColor("#6C8715");
        Log.d(appName, "map color after: " + color);
        return color;
    }

    private void removeMarker(GoogleMap map)
    {
        // List<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
        // remove our RouteOverlays
        for (Placemark next : placemarks)
        {
            next.removeMarker(map);
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

    /**
     * Does the actual drawing of the route, based on the geo points of the cue sheet
     * 
     * @param color
     *            Color in which to draw the lines
     * @param mapView
     *            Map view to draw onto
     */
    public void drawPath(GoogleMap map, int color)
    {

        color = mapColor(color);
        // removeOverlays(mapOverlays);
        // updateOverLays(boundaries, color, mapOverlays);
        addMarkers(map, color);
    }

    private void addMarkers(GoogleMap map, int color)
    {

        Placemark start = null;
        Placemark prev = null;

        for (Placemark next : placemarks)
        {
            LatLng nextPoint = next.getPoint();
            if (null == start)
            {
                start = next;
                Log.d(appName, "mark: " + start.toString());
                start.addMarker(map, start_icon);
                // if (isOnScreen(boundaries, start))
                // {
                // // add if it has a marker and is on screen
                // Log.d(appName, "draw: " + start.toString());
                // mapOverlays.add(new RouteOverlay.StartOverlay(nextPoint).withText(next.getTitle())); // START
                // }
            }
            // else if (isOnScreen(boundaries, prev, next))
            // {
            if (next == start)
            {
                // crosses start, assume it is loop end? or use iterator explicitly so we can check if last?
                // add if it has a marker and is on screen
                Log.d(appName, "loop end: " + next.toString());
                // mapOverlays.add(new RouteOverlay.LoopEndOverlay(prevPoint, nextPoint,
                // color).withText(next.getTitle())); // START
            }
            else
            {
                // draw line
                Log.d(appName, "line:" + prev.toString() + " TO " + next.toString());
                // mapOverlays.add(new RouteOverlay.LineOverlay(prevPoint, nextPoint, color).withText(next.getTitle()));
                // // CONNECT
                // draw placemark
                // TODO - add placemark only if there is a marker ???
                // TURN, LUNCH, etc marker
                // mapOverlays.add(new RouteOverlay.MarkOverlay(nextPoint).withText(next.getTitle()));
                if (next.getTitle() != null)
                {
                    next.addMarker(map, start_icon);
                }
                // }
                // }
                prev = next;

            }

            // if path is not a loop
            if (prev != start)
            {
                // if (isOnScreen(boundaries, prev))
                // {
                // Log.d(appName, "end: " + prev.toString());
                // mapOverlays.add(new RouteOverlay.EndOverlay(prev.getPoint()).withText(prev.getTitle())); // END
                prev.addMarker(map, end_icon);// END
            }
        }

    }

    public void addPlacemark(Placemark placemark)
    {
        placemarks.add(placemark);
    }

    public void drawRoute(GoogleMap map, int color)
    {
        map.clear();
        map.addPolyline(new PolylineOptions()
                .color(mapColor(color))
                .addAll(new Pts()));
        addMarkers(map, color);
    }

    public class Pts implements Iterable<LatLng>
    {

        @Override
        public Iterator<LatLng> iterator()
        {
            // TODO Auto-generated method stub
            return new iii();
        }

    }

    public class iii implements Iterator<LatLng>
    {
        Iterator<Placemark> it;

        public iii()
        {
            it = placemarks.iterator();
        }

        @Override
        public boolean hasNext()
        {
            return it.hasNext();
        }

        @Override
        public LatLng next()
        {
            // TODO Auto-generated method stub
            return it.next().getPoint();
        }

        @Override
        public void remove()
        {
            it.remove();
        }

    }
}
