package com.mgjg.kmztracker.map;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Placemark
{
    private Marker marker;
    private final LatLng point;
    private final String title;
    private final String snippet;

    public Placemark(LatLng point, String title, String snippet)
    {
        this.point = point;
        this.title = title;
        this.snippet = snippet;
        marker = null;
    }

    // public static final int toMicroDegrees(double deg)
    // {
    // return (int) (deg * 1E6);
    // }
    //
    // public static final double fromMicroDegrees(int microDegrees)
    // {
    // return (double) (microDegrees / 1E6);
    // }

    public Placemark(double latitude, double longitude, String title, String snippet)
    {
        this(new LatLng(latitude, longitude), title, snippet);
    }

    // public Placemark(int latitude, int longitude, String title, String snippet)
    // {
    // super(new GeoPoint(latitude, longitude), ((title != null) ? title : ""), ((snippet != null) ? snippet : ""));
    // }

    @SuppressWarnings("unused")
    public LatLng getPoint()
    {
        return point;
    }

    @SuppressWarnings("unused")
    public double getLatitude()
    {
        return getPoint().latitude;
    }

    @SuppressWarnings("unused")
    public double getLongitude()
    {
        return getPoint().longitude;
    }

    @SuppressWarnings("unused")
    public String getTitle()
    {
        return title;
    }

    @SuppressWarnings("unused")
    public String getSnippet()
    {
        return snippet;
    }

    @SuppressWarnings("unused")
    public Marker getMarker()
    {
        return marker;
    }

    @SuppressWarnings("unused")
    public String toString()
    {
        return "[lat: " + point.latitude + ", lon: " + point.longitude + "]";
    }

    @SuppressWarnings("unused")
    public Location toLocation()
    {
        // LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Location loc = locationManager.getLastKnownLocation("gps");
        Location location = new Location(title);
        location.setLatitude(this.getLatitude());
        location.setLongitude(this.getLongitude());
        return location;
    }

    @SuppressWarnings("unused")
    public Marker addMarker(GoogleMap map, int icon)
    {
        marker = map.addMarker(new MarkerOptions()
                .position(getPoint())
                .title(getTitle())
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        return marker;
    }

    @SuppressWarnings("unused")
    public void removeMarker(GoogleMap map)
    {
        if (marker != null)
        {
            marker.remove();
            this.marker = null;
        }

    }
}
