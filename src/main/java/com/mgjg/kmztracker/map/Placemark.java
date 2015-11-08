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
    private final double altitude;
    private final String title;
    private final String snippet;

    public Placemark(LatLng point, double altitude, String title, String snippet)
    {
        this.point = point;
        this.altitude = altitude;
        this.title = title;
        this.snippet = snippet;
        marker = null;
    }

    public Placemark(LatLng point, double altitude)
    {
        this(point, altitude, null, null);
    }

    public Placemark(LatLng point, String title, String snippet)
    {
        this(point, 0, title, snippet);
    }

    public Placemark(LatLng point)
    {
        this(point, 0, null, null);
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

    public Placemark(double latitude, double longitude, double altitude, String title, String snippet)
    {
        this(new LatLng(latitude, longitude), altitude, title, snippet);
    }

    public Placemark(double latitude, double longitude, double altitude)
    {
        this(new LatLng(latitude, longitude), altitude);
    }

    public Placemark(double latitude, double longitude, String title, String snippet)
    {
        this(new LatLng(latitude, longitude), title, snippet);
    }

    public Placemark(double latitude, double longitude)
    {
        this(new LatLng(latitude, longitude));
    }

    // public Placemark(int latitude, int longitude, String title, String snippet)
    // {
    // super(new GeoPoint(latitude, longitude), ((title != null) ? title : ""), ((snippet != null) ? snippet : ""));
    // }

    @SuppressWarnings("unused")
    /**
     * returns a LatLng which is a copy of the @{code Placemark}'s lat/long
     * @return @{code LatLng}
     */
    public LatLng getLatLng()
    {
        return point; // LatLngs are final/immutable
    }

    @SuppressWarnings("unused")
    public double getAltitude()
    {
        return altitude;
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
        LatLng latlng = getLatLng();
        location.setLatitude(latlng.latitude);
        location.setLongitude(latlng.longitude);
        return location;
    }

    @SuppressWarnings("unused")
    public Marker addMarker(GoogleMap map, int icon)
    {
        marker = map.addMarker(new MarkerOptions()
                .position(getLatLng())
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

    @Override
    public int hashCode()
    {
        return point.hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other instanceof Placemark)
        {
            return point.equals(((Placemark) other).point);
        }
        return false;
    }

    /**
     * compare latitudes, 'less than, i.e. <0) means this point is WEST of other point
     *
     * @param other
     * @return
     */
    public int compareLat(Placemark other)
    {
        double xx = point.latitude - other.point.latitude;
        if (Math.abs(xx) < .0000001)
        {
            return 0; // essentially equal
        }
        if ((point.latitude < 0) && (other.point.longitude > 0))
        {
            return -1;
        }
        else if ((point.latitude > 0) && (other.point.longitude < 0))
        {
            return 1;
        }
        // WEST of 0, both negative,
        // -70 vs. -50, -70 - (-50) ==> -20 (<0 ==> WEST)
        // -70 vs -100, -70 - (-100) ==> 30 (>0 ==> EAST)
        // EAST of 0, both positive,
        // 70 vs. 50, 70 - 50 ==> 20 (<0 ==> EAST)
        // 70 vs 100, 70 - 100 ==> -30 (>0 ==> WEST)
        return (xx < 0) ? -1 : 1;
    }

    /**
     * compare longitudes, 'less than (<0)' means point is SOUTH of other point
     *
     * @param other
     * @return
     */
    public int compareLng(Placemark other)
    {
        double xx = point.longitude - other.point.longitude;
        if (Math.abs(xx) < .0000001)
        {
            return 0; // essentially equal
        }
        if ((point.longitude < 0) && (point.longitude > 0))
        {
            // other is above and this point is below equator
            return -1; // this point is south of other point
        }
        else if ((point.longitude > 0) && (point.longitude < 0))
        {
            // this point is above equator, other point is below equator
            return -1; // this point is north of other point
        }
        // SOUTH of 0, both negative,
        // -70 vs. -50, -70 - (-50) ==> -20 (<0 ==> WEST)
        // -70 vs -100, -70 - (-100) ==> 30 (>0 ==> EAST)
        // NORTH of 0, both positive,
        // 70 vs. 50, 70 - 50 ==> 20 (<0 ==> EAST)
        // 70 vs 100, 70 - 100 ==> -30 (>0 ==> WEST)
        return (xx < 0) ? -1 : 1;
    }
}
