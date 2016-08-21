package com.mgjg.kmztracker.cuesheet.parser;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.mgjg.kmztracker.MainActivity;
import com.mgjg.kmztracker.cuesheet.CueSheet;

/**
 * Created by marianne on 1/3/2015.
 */
public class CueSheetService extends IntentService
{
    private static final String TAG = "cueSheetService";


    public CueSheetService()
    {
        super("CueSheetService");

    }

    @Override
    protected void onHandleIntent(Intent workIntent)
    {
        String appName = "kmztracker";
        GoogleMap map = null; // TBD
        final int color = workIntent.getIntExtra("color", Color.BLACK);
        final CueSheet cueSheet = new CueSheet(appName, map);
        // Gets data from the incoming Intent
        String urlString = workIntent.getStringExtra("url");
        try
        {
            final CueSheetParser parser;
            if (urlString.endsWith(".kml"))
            {
                parser = new CueSheetKmlParser(urlString);
            }
            else if (urlString.endsWith(".xml"))
            {
                parser = new CueSheetXmlParser(urlString);
            }
            else if (urlString.endsWith(".gpx"))
            {
                parser = new CueSheetGpxParser(urlString);
            }
            else
            {
                MainActivity.showToast("Unknown cuesheet file type: " + urlString);
                parser = null;
            }

            if (null != parser)
            {
                cueSheet.clear();
                parser.parse(cueSheet);

      /* Set the result to be displayed in our GUI. */
                Log.d(cueSheet.getAppName(), "CueSheet: " + cueSheet.toString());

                MainActivity.runOnUi(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        cueSheet.clearMap();
                        cueSheet.drawRoute(color);
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "unable to process url(" + urlString + ") because " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTurnNotification(LatLng point)
    {
        if (MainActivity.hasLocationPermission())
        {

            // This intent will call the activity ProximityActivity
            Intent proximityIntent = new Intent("in.wptrafficanalyzer.activity.proximity");

// Creating a pending intent which will be invoked by LocationManager when the specified region is
// entered or exited
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

// Setting proximity alert
// The pending intent will be invoked when the device enters or exits the region 20 meters
// away from the marked point
// The -1 indicates that, the monitor will not be expired
            // Getting LocationManager object from System Service
            // LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //noinspection ResourceType
            locationManager.addProximityAlert(point.latitude, point.longitude, 20, -1, pendingIntent);
        }

    }
}
