package com.mgjg.kmztracker;

import java.text.DecimalFormat;

import com.mgjg.kmztracker.map.MapOverlayer;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ja24120 on 4/14/14.
 */
public class LocationTracker implements LocationListener
{
    private LocationManager locationManager;
    String provider;
    private TextView latitudeField;
    private TextView longitudeField;
    private ImageView gpsImage;

    private MapOverlayer tracker;
    private MainActivity myActivity;
    private double initial_latitude = 42.382387;
    private double initial_longitude = -71.235065;
    public LocationTracker()
    {

    }

    public void onCreate(MainActivity context, Bundle savedInstance)
    {
        myActivity = context;
        tracker = new MapOverlayer(MainActivity.APP, context);
        tracker.mvPoint(initial_latitude, initial_longitude);
        
        latitudeField = (TextView) context.findViewById(R.id.GPS_LAT);
        longitudeField = (TextView) context.findViewById(R.id.GPS_LON);
        gpsImage = (ImageView) context.findViewById(R.id.GPS);

        // Get the location manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = (null == locationManager) ? null : locationManager.getBestProvider(criteria, false);
        Location location = (null == provider) ? null : locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null)
        {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
        else
        {
            setGpsImage(context, (provider != null));
            setLatitude("NONE");
            setLongitude("NONE");
        }
    }

    private void setGpsImage(Context context, boolean enabled)
    {
         if (null != gpsImage)
         {
//         gpsImage.setImageDrawable((enabled ? GPSIcon.ON : GPSIcon.OFF).getDrawable(context));
         }
    }

    private void setLatitude(String text)
    {
        if (null != latitudeField)
        {
            latitudeField.setText(text);
        }
    }

    private void setLongitude(String text)
    {
        if (null != longitudeField)
        {
            longitudeField.setText(text);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        DecimalFormat df = new DecimalFormat("#.0000");
        String txt = df.format(location.getLatitude());
        // txt = BigDecimal.valueOf(location.getLatitude()).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
        setLatitude(txt);
        df = new DecimalFormat("#.00000");
        txt = df.format(location.getLongitude());
        // txt = BigDecimal.valueOf(location.getLongitude()).setScale(5, BigDecimal.ROUND_HALF_UP).toString();
        setLongitude(txt);
        tracker.mvPoint(location);
    }

    /* Request updates at startup */
    void onResume(Activity context)
    {
        if ((null != locationManager) && (null != provider))
        {
            setGpsImage(context, true);
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    void onPause(Activity context)
    {
        if (null != locationManager)
        {
            locationManager.removeUpdates(this);
            setGpsImage(context, false);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle aBundle)
    {
        // provider vs. this.provider ???
        // Displayer.showToast("status changed: " + provider);
        // setGpsImage(Displayer.getContext(), (status == LocationProvider.AVAILABLE));
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        // provider vs. this.provider ???
        myActivity.showToast("Enabled provider " + provider);
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        // provider vs. this.provider ???
        myActivity.showToast("Disabled provider " + provider);
    }
}
