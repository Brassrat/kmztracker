package com.mgjg.kmztracker.map;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.mgjg.kmztracker.R;
import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.cuesheet.parser.CueSheetParser;
import com.mgjg.kmztracker.cuesheet.parser.CueSheetParserFactory;

public class MapOverlayer
{

    private static final int MAX_POINTS = 256;
    // private final Stack<GeoPoint> points = new Stack<GeoPoint>();
    private final Deque<GeoPoint> points = new LinkedList<GeoPoint>();

    int color = 999;
    private int initial_zoom = 12;

    private CueSheet cueSheet;
    private final Activity mapActivity;

    public MapOverlayer(String appName, Activity mainContext)
    {
        cueSheet = new CueSheet(appName);
        this.mapActivity = mainContext;

        MapView mapView = (MapView) mapActivity.findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        MapController mapController = mapView.getController();
        mapController.setZoom(initial_zoom);
    }

    public boolean isRouteDisplayed()
    {
        return (null != cueSheet) && !cueSheet.isEmpty();
    }

    public void setCueSheetFromXml(String url)
    {
        cueSheet.clear();
        Log.d(cueSheet.getAppName(), "parsing urlString " + url);
        // TODO - remove overlay???
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

    }

    public GeoPoint mvPoint(Location loc)
    {
        return mvPoint(loc.getLatitude(), loc.getLongitude());
    }

    public GeoPoint mvPoint(double lat, double lon)
    {
        return mvPoint(Placemark.toMicroDegrees(lat), Placemark.toMicroDegrees(lon));
    }

    private GeoPoint mvPoint(int lat, int lon)
    {
        return mvPoint(new GeoPoint(lat, lon));
    }

    private GeoPoint mvPoint(final GeoPoint geo)
    {
        // String Text = "My current location is: " + "Lat = " + lat +
        // " Long = " + lon;
        // Toast.makeText(getApplicationContext(), Text,
        // Toast.LENGTH_SHORT).show();
        float bearing;
        if (points.size() <= 0)
        {
            bearing = 270;
        }
        else
        {
            GeoPoint prev = points.peek();
            float[] distAndBearing = dist(prev, geo);
            bearing = distAndBearing[1];
        }

        points.push(geo);
        if (points.size() > MAX_POINTS)
        {
            points.removeLast();
        }

        final int newPointLon = geo.getLongitudeE6();
        final int newPointLat = geo.getLatitudeE6();

        // TODO only move map if new point is outside 'center' region of map
        // if point is off the map move the ctr to the new point
        // if the point is outside the 'center' but displayable without
        // moving the map then just show the point
        // TODO deal with rotated map
        MapView mapView = (MapView) mapActivity.findViewById(R.id.mapview);
        if (null == mapView)
        {
            return geo;
        }
        GeoPoint ctr = mapView.getMapCenter();
        int[] boundaries = getMapBoundaries(mapView);
        int ul_lat = boundaries[0];
        int ul_lon = boundaries[1];
        int lr_lat = boundaries[2];
        int lr_lon = boundaries[3];

        int ctrLat = ctr.getLatitudeE6();
        int ctrLon = ctr.getLongitudeE6();
        // final int lonHalf = mapView.getLongitudeSpan() / 2;
        // final int latHalf = mapView.getLatitudeSpan() / 2;

        // if new point is not within the current display
        // make the new point the center since we don't know
        // which way we are moving
        if (newPointLon < ul_lon)
        {
            ctrLat = newPointLat;
            ctrLon = newPointLon;
            ctr = null;
        }
        else if (newPointLon > lr_lon)
        {
            ctrLat = newPointLat;
            ctrLon = newPointLon;
            ctr = null;
        }
        else if (newPointLat < lr_lat)
        {
            ctrLat = newPointLat;
            ctrLon = newPointLon;
            ctr = null;
        }
        else if (newPointLat > ul_lat)
        {
            ctrLat = newPointLat;
            ctrLon = newPointLon;
            ctr = null;
        }
        else
        {
            // compute box within current display which
            // if point stays in the box means we don't move the center
            // box is 80% of screen, i.e., 40% on either side of center
            final double box_width_pct = .80 / 2;
            final double box_height_pct = .80 / 2;
            final double box_move_pct = .5 * 1.2;
            final int halfWindowWidth = (int) ((ul_lon - lr_lon) * box_width_pct);
            final int halfWindowHeight = (int) ((ul_lat - lr_lat) * box_height_pct);

            final int leftLon = ctrLon - halfWindowWidth;
            final int rightLon = ctrLon + halfWindowWidth;
            final int bottomLat = ctrLat - halfWindowHeight;
            final int topLat = ctrLat + halfWindowHeight;

            if (newPointLat < bottomLat)
            {
                // if new point is too low on screen move it up a bit, i.e. move
                // center latitude smaller
                ctrLat -= (ul_lat - lr_lat) * box_move_pct; // (lonHalf * 1.2);
                ctr = null;
            }
            else if (newPointLat > topLat)
            {
                ctrLat += (ul_lat - lr_lat) * box_move_pct; // (lonHalf * 1.2);
                ctr = null;
            }
            if (newPointLon < leftLon)
            {
                ctrLon -= (ul_lon - lr_lon) * box_move_pct; // (latHalf * 1.2);
                ctr = null;
            }
            if (newPointLon > rightLon)
            {
                ctrLon += (ul_lon - lr_lon) * box_move_pct; // (latHalf * 1.2);
                ctr = null;
            }
        }

        List<Overlay> mapOverlays = mapView.getOverlays();

        if ((null == ctr) && (null != cueSheet))
        {
            cueSheet.drawPath(getMapBoundaries(mapView), color, mapOverlays);
        }

        updateLocationOverlay(mapOverlays, geo, bearing, "On the road", "Here");
        if (null == ctr)
        {
            MapController mapController = mapView.getController();
            ctr = new GeoPoint(ctrLat, ctrLon);
            mapController.animateTo(ctr);
            // mapController.setCenter(new GeoPoint(ctrLat, ctrLon));
        }

        mapView.invalidate(); // may be overkill ???
        return geo;
    }

    private void updateLocationOverlay(List<Overlay> mapOverlays, GeoPoint geo, float bearing,
            String title, String snippet)
    {
        updateLocationOverlay(mapOverlays, geo.getLatitudeE6(), geo.getLongitudeE6(),
                bearing, title, snippet);
    }

    // private void updateLocationOverlay(double latitude, double longitude,
    // String title, String snippet)
    // {
    // updateLocationOverlay(toMicroDegrees(latitude),
    // toMicroDegrees(longitude), title, snippet);
    // }

    private void updateLocationOverlay(List<Overlay> mapOverlays, int latitude, int longitude,
            float bearing, String title, String snippet)
    {
        MapView mapView = (MapView) mapActivity.findViewById(R.id.mapview);

        // remove our LocationOverlay
        for (Iterator<Overlay> iter = mapOverlays.iterator(); iter.hasNext();)
        {
            Overlay o = iter.next();
            Log.d(cueSheet.getAppName(), "overlay type: " + o.getClass().getName());
            if (o instanceof LocationOverlay)
            {
                iter.remove();
            }
        }

        // Drawable drawable =
        // this.getResources().getDrawable(R.drawable.androidmarker);
        // TODO - determine helmet size by number of degrees of width...
        boolean bigger = Placemark.fromMicroDegrees(mapView.getLongitudeSpan()) > 1;
        int left = bigger ? R.drawable.mountain_bike_helmet_24 : R.drawable.mountain_bike_helmet_16;
        // TODO need larger flipped
        int right = bigger ? R.drawable.mountain_bike_helmet_16_flipped : R.drawable.mountain_bike_helmet_16_flipped;
        Drawable drawable = rotateDrawable(left, right, bearing);
        // Drawable drawable = getResources().getDrawable(drawId);
        LocationOverlay locationOverlay = new LocationOverlay(drawable);

        LocationOverlayItem overlayitem = new LocationOverlayItem(latitude, longitude, title, snippet);
        locationOverlay.addOverlay(overlayitem);
        mapOverlays.add(locationOverlay);

        mapView.setEnabled(true);
    }

    private int prevDrawId;
    private float prevBearing;
    private Drawable prevDrawable;

    private Drawable rotateDrawable(int leftId, int rightId, float bearing)
    {
        int drawId = (bearing < 0) ? leftId : rightId;

        if ((null == prevDrawable) || (prevDrawId != drawId) || (prevBearing != bearing))
        {
            // Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), drawId);
            // Drawable drawable = getResources().getDrawable(drawId);
            Bitmap bitMap = BitmapFactory.decodeResource(mapActivity.getResources(), drawId);
            // Create blank bitmap of equal size
            Bitmap canvasBitmap = bitMap.copy(Bitmap.Config.ARGB_8888, true);
            canvasBitmap.eraseColor(0x00000000);

            // Create canvas
            Canvas canvas = new Canvas(canvasBitmap);

            // Create rotation matrix
            Matrix rotateMatrix = new Matrix();
            float angle = (bearing < 0) ? (90 + bearing) : (bearing - 90);
            rotateMatrix.setRotate(angle, canvas.getWidth() / 2,
                    canvas.getHeight() / 2);

            // Draw bitmap onto canvas using matrix
            canvas.drawBitmap(bitMap, rotateMatrix, null);

            prevDrawId = drawId;
            prevBearing = bearing;
            prevDrawable = new BitmapDrawable(canvasBitmap);
        }
        return prevDrawable;
    }

    private static class LocationOverlayItem extends OverlayItem
    {

        // public MyOverlayItem(double latitude, double longitude, String title,
        // String snippet)
        // {
        // this(toMicroDegrees(latitude), toMicroDegrees(longitude), title,
        // snippet);
        // }

        public LocationOverlayItem(int latitude, int longitude, String title, String snippet)
        {
            super(new GeoPoint(latitude, longitude), title, snippet);
        }

    }

    private class LocationOverlay extends ItemizedOverlay<OverlayItem>
    {

        // private final Context mapContext;
        private final List<OverlayItem> overlays = new ArrayList<OverlayItem>();

        public LocationOverlay(Drawable defaultMarker)
        {
            super(boundCenterBottom(defaultMarker));
            // this.mapContext = getApplicationContext();
        }

        // public LocationOverlay(Drawable marker, Context context)
        // {
        // super(boundCenterBottom(marker));
        // mapContext = context;
        // }

        public void addOverlay(OverlayItem overlay)
        {
            overlays.add(overlay);
            populate();
        }

        public void clear()
        {
            overlays.clear();
            populate();
        }

        @Override
        protected OverlayItem createItem(int ii)
        {
            return overlays.get(ii);
        }

        @Override
        public int size()
        {
            return overlays.size();
        }

        @Override
        protected boolean onTap(int index)
        {
            OverlayItem item = overlays.get(index);

            AlertDialog.Builder dialog = new AlertDialog.Builder(mapActivity.getApplicationContext());
            dialog.setTitle(item.getTitle());
            dialog.setMessage(item.getSnippet());
            dialog.show();
            return true;
        }
    }

    static float[] dist(GeoPoint prev, GeoPoint current)
    {
        float[] results = new float[2];
        Location.distanceBetween(Placemark.fromMicroDegrees(prev.getLatitudeE6()),
                Placemark.fromMicroDegrees(prev.getLongitudeE6()),
                Placemark.fromMicroDegrees(current.getLatitudeE6()),
                Placemark.fromMicroDegrees(current.getLongitudeE6()), results);
        return results;
    }

    /**
     * returns map boundaries in array where array[0] is UL latitude array[1] is UL longitude array[2] is LR latitude
     * array[3] is LR longitude
     * 
     * @return
     */
    public static int[] getMapBoundaries(MapView mapView)
    {
        GeoPoint ctr = mapView.getMapCenter();
        int ctrLat = ctr.getLatitudeE6();
        int ctrLon = ctr.getLongitudeE6();
        // compute width and height of current map window in degrees
        final int lonHalf = mapView.getLongitudeSpan() / 2;
        final int latHalf = mapView.getLatitudeSpan() / 2;
        int[] boundaries = new int[4];
        boundaries[0] = (ctrLat + latHalf);
        boundaries[1] = (ctrLon - lonHalf);
        boundaries[2] = (ctrLat - latHalf);
        boundaries[3] = (ctrLon + lonHalf);
        return boundaries;
    }

}
