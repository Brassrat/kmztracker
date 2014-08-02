package com.mgjg.kmztracker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
{

    public static final String APP = "com.mgjg.kmztracker";
    // static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    // static final LatLng KIEL = new LatLng(53.551, 9.993);

    private GoogleMap map;
    private LocationTracker locLstnr;

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
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        locLstnr = new LocationTracker();
        locLstnr.onCreate(this, savedInstanceState, map);

        if (map != null)
        {
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

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
        }

        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        // try {
        // MapsInitializer.initialize(this.getActivity());
        // } catch (GooglePlayServicesNotAvailableException e) {
        // e.printStackTrace();
        // }
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
     * @param vv
     */
    public void setCueSheetFromXml(View vv)
    {
      EditText et = (EditText) findViewById(R.id.CUESHEET);
      CharSequence seq = et.getText();
      String url = seq.toString();
      if (url.isEmpty())
      {
        url = "map.kml";
      }
      locLstnr.updateCueSheet(url);
    }

    // @Override
    // protected boolean isRouteDisplayed()
    // {
    // return false;
    // }

}
