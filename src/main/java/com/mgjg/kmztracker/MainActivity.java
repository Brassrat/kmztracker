package com.mgjg.kmztracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.mgjg.kmztracker.map.MapOverlayer;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback
{

    public static final String APP = "com.mgjg.kmztracker";
    // static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    // static final LatLng KIEL = new LatLng(53.551, 9.993);

    private GoogleMap map;
    private LocationTracker locLstnr;

    private static final int[] frontViews = new int[]{R.id.GPS, R.id.GPS_LAT, R.id.GPS_LON,
            R.id.READ_CUESHEET, R.id.CUESHEET};

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Select the proper xml layout file which includes the matching Google
        // API Key
        if (BuildConfig.DEBUG)
        {
            //setContentView(R.layout.debug_main);
            setContentView(R.layout.activity_main);
        }
        else
        {
            setContentView(R.layout.activity_main);
        }

        // Gets the MapView from the XML layout and creates it
        // mapView = (MapView) v.findViewById(R.id.mapview);
        // mapView.onCreate(savedInstanceState);
        //
        // // Gets to GoogleMap from the MapView and does initialization stuff
        // map = mapView.getMap();
        MapFragment frag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        frag.getMapAsync(this);

        for (int ii : frontViews)
        {
            View vv = findViewById(ii);
            if (null != vv)
            {
                vv.bringToFront();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap theMap)
    {
        this.map = theMap;
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                                     {

                                         @Override
                                         public boolean onMarkerClick(Marker marker)
                                         {
                                             return false;
                                         }

                                     }
        );
        // Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
        // .title("Hamburg"));
        // Marker kiel = map.addMarker(new MarkerOptions()
        // .position(KIEL)
        // .title("Kiel")
        // .snippet("Kiel is cool")
        // .icon(BitmapDescriptorFactory
        // .fromResource(R.drawable.ic_launcher)));

        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        // try {
        // MapsInitializer.initialize(this.getActivity());
        // } catch (GooglePlayServicesNotAvailableException e) {
        // e.printStackTrace();
        // }
        /*
        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
        */
        locLstnr = new LocationTracker(this, new MapOverlayer(MainActivity.APP, this, map));
    }

    @SuppressWarnings("unused")
    void onResume(Activity context)
    {
        if (null != locLstnr)
        {
            locLstnr.onResume(context);
        }
    }

    @SuppressWarnings("unused")
    void onPause(Activity context)
    {
        if (null != locLstnr)
        {
            locLstnr.onPause(context);
        }
    }

    public void showToast(String text)
    {
        Toast.makeText(getApplicationContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * button handler?
     *
     * @param vv
     */
    public void setCueSheetFromXml(View vv)
    {
        EditText et = (EditText) findViewById(R.id.CUESHEET);
        CharSequence seq = et.getText();
        String url = seq.toString();
        if (url.isEmpty() || "test".equalsIgnoreCase(url) || "file".equalsIgnoreCase(url))
        {
            //url = "http://10.0.2.2:8888/kml/test.kml";
            url = "http://192.168.1.2:8080/kml/test.kml";
        }
        locLstnr.updateCueSheet(url);
    }

    // @Override
    // protected boolean isRouteDisplayed()
    // {
    // return false;
    // }

}
