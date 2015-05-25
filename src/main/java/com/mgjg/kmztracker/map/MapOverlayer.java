package com.mgjg.kmztracker.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mgjg.kmztracker.R;
import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.cuesheet.parser.CueSheetParserFactory;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class MapOverlayer
{

    private static final int MAX_POINTS = 256;
    // private final Stack<LatLng> points = new Stack<LatLng>();
    private final Deque<LatLng> points = new LinkedList<LatLng>();

    private int initial_zoom = 12;

    private CueSheet cueSheet;
    private final Activity mapActivity;
    private final GoogleMap googleMap;
    private Marker locationMarker;
    private final int color;
    private final List<Marker> markers = new ArrayList<Marker>();

    public MapOverlayer(String appName, Activity mainContext, GoogleMap map)
    {
        cueSheet = new CueSheet(appName, mainContext, map);
        this.mapActivity = mainContext;
        this.googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // MapController mapController = googleMap.getUiSettings().getController();
        // mapController.setZoom(initial_zoom);
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
        // Double.parseDouble(lat), Double.parseDouble(lng))));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(initial_zoom));

        color = mainContext.getResources().getColor(R.color.DodgerBlue);
    }

    public void updateCueSheet(String url)
    {
        RouteService.updateCueSheet(cueSheet, url, color);
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
        CueSheetParserFactory.parseUrl(cueSheet, url, color);
    }

    public LatLng mvPoint(Location loc)
    {
        return mvPoint(loc.getLatitude(), loc.getLongitude());
    }

    public LatLng mvPoint(double lat, double lon)
    {
        return mvPoint(new LatLng(lat, lon));
    }

    // private LatLng mvPoint(int lat, int lon)
    // {
    // return mvPoint(new LatLng(lat, lon));
    // }

    private LatLng mvPoint(final LatLng newPoint)
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
            LatLng prev = points.peek();
            float[] distAndBearing = dist(prev, newPoint);
            bearing = distAndBearing[1];
        }

        points.push(newPoint);
        if (points.size() > MAX_POINTS)
        {
            points.removeLast();
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
        LatLng ctr = getMapCenter();
        LatLngBounds boundaries = getMapBoundaries();
        ctr = boundaries.getCenter();
        // double ctrLat = ctr.latitude;
        // double ctrLon = ctr.longitude;
        LatLng newCtr = null;

        // int ctrLat = ctr.getLatitudeE6();
        // int ctrLon = ctr.getLongitudeE6();
        // final int lonHalf = mapView.getLongitudeSpan() / 2;
        // final int latHalf = mapView.getLatitudeSpan() / 2;

        // if new point is not within the current display
        // make the new point the center since we don't know
        // which way we are moving
        if (!boundaries.contains(newPoint))
        {
            newCtr = newPoint;
        }
        else
        {
            double ctrLat = ctr.latitude;
            double ctrLon = ctr.longitude;
            double ul_lat = boundaries.northeast.latitude;
            double ul_lon = boundaries.southwest.longitude;
            double lr_lat = boundaries.southwest.latitude;
            double lr_lon = boundaries.northeast.longitude;

            // compute box within current display which
            // if point stays in the box means we don't move the center
            // box is 80% of screen, i.e., 40% on either side of center
            final double box_width_pct = .80 / 2;
            final double box_height_pct = .80 / 2;
            final double box_move_pct = .5 * 1.2;
            final double halfWindowWidth = ((ul_lon - lr_lon) * box_width_pct);
            final double halfWindowHeight = ((ul_lat - lr_lat) * box_height_pct);

            final double leftLon = ctrLon - halfWindowWidth;
            final double rightLon = ctrLon + halfWindowWidth;
            final double bottomLat = ctrLat - halfWindowHeight;
            final double topLat = ctrLat + halfWindowHeight;

            if (newPoint.latitude < bottomLat)
            {
                // if new point is too low on screen move it up a bit, i.e. move
                // center latitude smaller
                ctrLat -= (ul_lat - lr_lat) * box_move_pct; // (lonHalf * 1.2);
                ctr = null;
            }
            else if (newPoint.latitude > topLat)
            {
                ctrLat += (ul_lat - lr_lat) * box_move_pct; // (lonHalf * 1.2);
                ctr = null;
            }
            if (newPoint.longitude < leftLon)
            {
                ctrLon -= (ul_lon - lr_lon) * box_move_pct; // (latHalf * 1.2);
                ctr = null;
            }
            if (newPoint.longitude > rightLon)
            {
                ctrLon += (ul_lon - lr_lon) * box_move_pct; // (latHalf * 1.2);
                ctr = null;
            }
            newCtr = new LatLng(ctrLat, ctrLon);
        }

        if ((null != newCtr) && (null != cueSheet))
        {
            cueSheet.drawPath(color);
        }

        updateLocationMarker(newPoint, bearing, "On the road", "Here");
        if (null != newCtr)
        {
            // MapController mapController = mapView.getController();
            ctr = newCtr;
            // mapController.animateTo(ctr);
            // mapController.setCenter(new LatLng(ctrLat, ctrLon));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCtr, 12.0f));
        }

        // googleMap.invalidate(); // may be overkill ???
        return newPoint;
    }

    private boolean ALLOW_ROTATION = false;

    private void updateLocationMarker(LatLng point, float bearing, String title, String snippet)
    {
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
        boolean bigger = getLongitudeSpan() > 1;
        final int left = bigger ? R.drawable.mountain_bike_helmet_24 : R.drawable.mountain_bike_helmet_16;
        // TODO need larger flipped
        final int right = bigger ? R.drawable.mountain_bike_helmet_16_flipped : R.drawable.mountain_bike_helmet_16_flipped;
        final BitmapDrawable drawable = rotateDrawable(left, right, bearing);
        // Drawable drawable = getResources().getDrawable(drawId);
        // LocationOverlay locationOverlay = new LocationOverlay(drawable);

        // LocationOverlayItem overlayitem = new LocationOverlayItem(point, title, snippet);
        // locationOverlay.addOverlay(overlayitem);
        // mapOverlays.add(locationOverlay);
        // googleMap.setEnabled(true);
        // see https://developers.google.com/maps/documentation/android/marker
        // ?? can we change existing marker or do we have to remove it and add another
        if (null == locationMarker)
        {
            MarkerOptions mo = new MarkerOptions()
                    .position(point)
                    .flat(true)
                    .title(title)
                    .icon(drawableToIcon(drawable));
            if (ALLOW_ROTATION)
            {
                mo.anchor(0.5f, 0.5f);
                mo.rotation(bearing);
            }
            locationMarker = googleMap.addMarker(mo);
            // markers.add(googleMap.addMarker(mo));
        }
        else
        {
            locationMarker.setPosition(point);
            if (ALLOW_ROTATION)
            {
                locationMarker.setAnchor(0.5f, 0.5f);
                locationMarker.setRotation(bearing);
            }
        }
    }

    public Marker drawMarker(LatLng point)
    {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Adding InfoWindow title
        markerOptions.title("Location Coordinates");

        // Adding InfoWindow contents
        markerOptions.snippet(Double.toString(point.latitude) + ","
                + Double.toString(point.longitude));

        // Adding marker on the Google Map
        Marker mm = googleMap.addMarker(markerOptions);
        markers.add(mm);
        return mm;
    }

    public Circle drawCircle(LatLng point)
    {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(20);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        return googleMap.addCircle(circleOptions);
    }

    public BitmapDescriptor drawableToIcon(int drawId)
    {
        return BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mapActivity.getResources(), drawId));
    }

    public BitmapDescriptor drawableToIcon(Drawable draw)
    {
        return BitmapDescriptorFactory.fromBitmap(drawableToBitmap(draw));
    }

    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private int prevDrawId;
    private float prevBearing;
    private BitmapDrawable prevDrawable;

    public  BitmapDrawable rotateDrawable(int leftId, int rightId, float bearing)
    {
        // TODO - use BitmapDescriptorFactory.fromResource(xId) to get the descriptors once
        // then just rotate the Marker ...
        // i.e., get rid of all this stuff...
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
            // prevDrawable = new BitmapDrawable(canvasBitmap);
            prevDrawable = new BitmapDrawable(this.mapActivity.getResources(), canvasBitmap);
        }
        return prevDrawable;
    }

    static float[] dist(LatLng prev, LatLng current)
    {
        float[] results = new float[2];
        Location.distanceBetween(prev.latitude,
                prev.longitude,
                current.latitude,
                current.longitude, results);
        return results;
    }

    public LatLng getMapCenter()
    {
        return googleMap.getCameraPosition().target;
    }

    public double getLongitudeSpan()
    {
        final LatLngBounds bounds = getMapBoundaries();
        double left = bounds.southwest.longitude;
        double right = bounds.northeast.longitude;
        return (right - left); // right >= left ==> (right-left) >= 0
    }

    /**
     * returns map boundaries in array where array[0] is UL latitude array[1] is UL longitude array[2] is LR latitude
     * array[3] is LR longitude
     *
     * @return
     */
    public LatLngBounds getMapBoundaries()
    {
        LatLng ctr = googleMap.getCameraPosition().target;
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
        // Double.parseDouble(lat), Double.parseDouble(lng))));
        double ctrLat = ctr.latitude;
        double ctrLon = ctr.longitude;
        // compute width and height of current map window in degrees
        // VisibleRegion vr = googleMap.getProjection().getVisibleRegion();
        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;

        // not needed anymore ...
        double left = bounds.southwest.longitude;
        double top = bounds.northeast.latitude;
        double right = bounds.northeast.longitude;
        double bottom = bounds.southwest.latitude;
        double longitudeSpan = (right - left); // right >= left ==> (right-left) >= 0
        double latitudeSpan = (top - bottom); // top >= bottom ==> (top-bottom) >= 0
        final double lonHalf = longitudeSpan / 2;
        final double latHalf = latitudeSpan / 2;
        // double[] boundaries = new double[4];
        double xtop = (ctrLat + latHalf); // top
        double xleft = (ctrLon - lonHalf); // left
        double xbottom = (ctrLat - latHalf); // bottom
        double xright = (ctrLon + lonHalf); // right
        LatLngBounds xbounds = new LatLngBounds(new LatLng(xbottom, xleft), new LatLng(xtop, xright));
        // return boundaries;
        return bounds;
    }

    public boolean isVisible(LatLng point)
    {
        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        return bounds.contains(point);
    }

    public boolean isVisible(double lat, double lng)
    {
        return isVisible(new LatLng(lat, lng));
    }

}
