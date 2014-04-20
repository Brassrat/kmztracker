package com.mgjg.kmztracker.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Placemark extends OverlayItem
{

  public static final int toMicroDegrees(double deg)
  {
    return (int) (deg * 1E6);
  }

  public static final double fromMicroDegrees(int microDegrees)
  {
    return (double) (microDegrees / 1E6);
  }

  public Placemark(double latitude, double longitude, String title, String snippet)
  {
    this(toMicroDegrees(latitude), toMicroDegrees(longitude), title, snippet);
  }

  public Placemark(int latitude, int longitude, String title, String snippet)
  {
    super(new GeoPoint(latitude, longitude), ((title != null) ? title : ""), ((snippet != null) ? snippet : ""));
  }

  public int getLatitudeE6()
  {
    return getPoint().getLatitudeE6();
  }

  public int getLongitudeE6()
  {
    return getPoint().getLongitudeE6();
  }

  public String toString()
  {
    GeoPoint pt = getPoint();
    return "[lat: " + fromMicroDegrees(pt.getLatitudeE6())
            + ", lon: +" + fromMicroDegrees(pt.getLongitudeE6()) + "]";
  }

}
