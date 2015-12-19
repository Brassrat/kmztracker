package com.mgjg.kmztracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.mgjg.kmztracker.map.MapOverlayer;
import com.mgjg.kmztracker.preference.MapPreferencesActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupMap();

    }

    private static boolean ADD_MAP = true;

    private void setupMap()
    {

        if (ADD_MAP)
        {
            // Gets the MapView from the XML layout and creates it
            // mapView = (MapView) v.findViewById(R.id.mapview);
            // mapView.onCreate(savedInstanceState);
            //
            // // Gets to GoogleMap from the MapView and does initialization stuff
            // map = mapView.getMap();
            MapFragment frag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            frag.getMapAsync(this);

        }
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
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        else
        {
            map.setMyLocationEnabled(true);
        }

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
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        //inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Take appropriate action for each action item click
        final boolean result;
        switch (item.getItemId())
        {
            case R.id.menu_settings:
                // settings
                // launch preference activity
                Intent ii = new Intent(this, MapPreferencesActivity.class);
                startActivity(ii);
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }

    /**
     * On selecting action bar icons
     */
    private boolean onActivityMainActionsOptionsItemSelected(MenuItem item)
    {
        // Take appropriate action for each action item click
        final boolean result;
        switch (item.getItemId())
        {
            case R.id.action_search:
                // search action
                result = true;
                break;
            case R.id.action_location_found:
                // location found
                LocationFound();
                result = true;
                break;
            case R.id.action_refresh:
                // refresh
                result = true;
                break;
            case R.id.action_help:
                // help action
                result = true;
                break;
            case R.id.action_check_updates:
                // check for updates action
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    /**
     * Launching new activity
     */
    private void LocationFound()
    {
        // Intent i = new Intent(MainActivity.this, LocationFound.class);
        //startActivity(i);
    }

    /**
     * button handler?
     *
     * @param vv
     */
    public void setCueSheetFromXml(View vv)
    {
        if (null != locLstnr)
        {
            EditText et = (EditText) findViewById(R.id.CUESHEET);
            String url = et.getText().toString();
            if (url.isEmpty())
            {
                url = getString(R.string.map_file);
            }
            // get current preference for serverIP
            SharedPreferences prefs = getSharedPreferences("general_settings", Context.MODE_PRIVATE);
            String lanSettings = prefs.getString("serverIP", null);
            url = lanSettings + "/" + url;
            if (url.indexOf('.') < 0)
            {
                url += (lanSettings.endsWith("gpx") ? ".gpx" : ".kml");
            }
            locLstnr.updateCueSheet(url);
        }
    }

    // @Override
    // protected boolean isRouteDisplayed()
    // {
    // return false;
    // }
}
