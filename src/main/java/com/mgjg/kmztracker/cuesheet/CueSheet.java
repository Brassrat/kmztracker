package com.mgjg.kmztracker.cuesheet;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mgjg.kmztracker.R;
import com.mgjg.kmztracker.map.Placemark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A list of Placemarks, each Placemark indicates a place on the route. The route consists of lines connecting the
 * Placemarks. There are 2 kinds of placements, those with and those without markers. Ones with markers indicate changes
 * in direction or some other notable place on the route (i.e., beginning/ending location, lunch stop, etc.)
 *
 * @author Jay Goldman
 */
public class CueSheet
{
    private final String appName;
    private final Activity context;
    private final GoogleMap map;
    private final int start_icon;
    private final int end_icon;
    private final int location_icon;

    private String trackName = "";
    private final List<Placemark> placemarks = new ArrayList<Placemark>();

    private Location NorthWestCorner;
    private Location SouthEastCorner;

    public CueSheet(String appName, Activity context, GoogleMap map)
    {
        this.appName = appName;
        this.context = context;
        this.map = map;
        start_icon = R.drawable.mm_20_orange;
        end_icon = R.drawable.finish1;
        location_icon = R.drawable.mountain_bike_helmet_16;
    }

    public void runOnUi(Runnable runThis)
    {
        context.runOnUiThread(runThis);
    }

    public boolean isEmpty()
    {

        synchronized (placemarks)
        {
            return placemarks.isEmpty();
        }
    }

    public void clear()
    {
        synchronized (placemarks)
        {
            placemarks.clear();
        }
    }

    public String getAppName()
    {
        return appName;
    }

    public Activity getActivty()
    {
        return context;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(appName).append(":\n");
        synchronized (placemarks)
        {
            for (Placemark p : placemarks)
            {
                buf.append("\n").append(p.toString());
            }
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
        return boundaries.contains(pl.getLatLng());
    }

    public static boolean straddles(double boundary, double pt1, double pt2)
    {
        return ((pt1 <= boundary) && (pt2 >= boundary)) ||
                ((pt2 <= boundary) && (pt1 >= boundary));
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
    private int mapColor(int color)
    {
        Log.d(appName, "map color before: " + color);

        // color correction for dining, make it darker
        if (color == Color.parseColor("#add331"))
        {
            color = Color.parseColor("#6C8715");
        }
        Log.d(appName, "map color after: " + color);
        return color;
    }

    @SuppressWarnings("unused")
    private void removeMarkers()
    {
        // List<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
        // remove our RouteOverlays
        synchronized (placemarks)
        {
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
    }

    /**
     * Does the actual drawing of the route, based on the geo points of the cue sheet
     *
     * @param color Color in which to draw the lines
     */
    public void drawPath(int color)
    {
        // color = mapColor(color);
        // //removeOverlays(mapOverlays);
        // // updateOverLays(boundaries, color, mapOverlays);
        //probably not needed any more... google map v2 will draw the route as needed
        //addMarkers(color);
    }

    public void clearMap()
    {
        map.clear();
    }

    /**
     * @param color
     */
    @SuppressWarnings("unused")
    public void drawRoute(int color)
    {
        map.addPolyline(new PolylineOptions()
                .color(mapColor(color))
                .width(5)
                .visible(true)
                .addAll(new Pts()));
        addMarkers(color);
    }

    private void addMarkers(int color)
    {
        Placemark start = null;
        Placemark prev = null;

        for (Placemark next : placemarks)
        {
            //LatLng nextPoint = next.getPoint();
            if (null == start)
            {
                start = next;
                Log.d(appName, "start: " + start.toString());
                start.addMarker(map, start_icon);
            }
            else if (next == start)
            {
                // crosses start, assume it is loop end? or use iterator explicitly so we can check if last?
                // add if it has a marker and is on screen
                Log.d(appName, "loop end: " + next.toString());
            }
            else if (null != prev)
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
                    next.addMarker(map, location_icon);
                }
            }
            prev = next;
        }

        // if path is not a loop
        if ((null != prev) && (!prev.equals(start)))
        {
            prev.addMarker(map, end_icon);// END
        }
    }

    public void addPlacemark(Placemark placemark)
    {
        synchronized (placemarks)
        {
            placemarks.add(placemark);
        }
    }

    public void addTrk(String name)
    {
        this.trackName = name;
    }

    public void addPt(double lat, double lon)
    {
        addPt(lat, lon, 0);
    }

    public void addPt(double lat, double lon, double altitude)
    {
        addPlacemark(new Placemark(lat, lon, altitude));
    }

    @SuppressWarnings("unused")
    public double totalUp()
    {
        double total = 0;
        synchronized (placemarks)
        {
            double alt = placemarks.get(0).getAltitude();
            for (Placemark pm : placemarks)
            {
                if (pm.getAltitude() > alt)
                {
                    total += (pm.getAltitude() - alt);
                    alt = pm.getAltitude();
                }
            }
        }
        return total;
    }

    @SuppressWarnings("unused")
    public double totalDown()
    {
        double total = 0;
        synchronized (placemarks)
        {
            double alt = placemarks.get(0).getAltitude();
            for (Placemark pm : placemarks)
            {
                if (pm.getAltitude() < alt)
                {
                    total += (alt - pm.getAltitude());
                    alt = pm.getAltitude();
                }
            }
        }
        return total;
    }

    public class Pts implements Iterable<LatLng>
    {

        @Override
        public Iterator<LatLng> iterator()
        {
            return new iPt();
        }

    }

    public class iPt implements Iterator<LatLng>
    {
        final LatLng[] pts;
        int last = -1;

        public iPt()
        {
            synchronized (placemarks)
            {
                pts = new LatLng[placemarks.size()];
                int ii = 0;
                for (Placemark pm : placemarks)
                {
                    pts[ii++] = pm.getLatLng();
                }
            }
        }

        @Override
        public boolean hasNext()
        {
            return (last + 1) < pts.length;
        }

        @Override
        public LatLng next()
        {
            return pts[++last];
        }

        @Override
        public void remove()
        {
            //it.remove();
            throw new UnsupportedOperationException("remove using iterator not supported");
        }

    }
}
