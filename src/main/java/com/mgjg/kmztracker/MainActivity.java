package com.mgjg.kmztracker;

import android.app.Activity;
import android.content.Intent;
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
import com.mgjg.kmztracker.preference.Preferences;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
{

    public static final String APP = "com.mgjg.kmztracker";
    // static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    // static final LatLng KIEL = new LatLng(53.551, 9.993);

    private GoogleMap map;
    private LocationTracker locLstnr;

    private static final int[] frontViews = new int[]{R.id.GPS, R.id.GPS_LAT, R.id.GPS_LON,
            R.id.READ_CUESHEET, R.id.CUESHEET};


    private static Activity mainActivity;

    public MainActivity()
    {
        mainActivity = this;
    }

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
            // Gets to fragment for the GoogleMap from the View and does initialization stuff
            // note, this is done asynchronously
            MapFragment frag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            frag.getMapAsync(new OnMapReady());
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

    private class OnMapReady implements OnMapReadyCallback
    {
        @Override
        public void onMapReady(GoogleMap googleMap)
        {
            map = googleMap;
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
            if ((ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) ||
                    (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED))
            {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
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
            locLstnr = new LocationTracker(new MapOverlayer(MainActivity.APP, map));
        }
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

    public static void showToast(String text)
    {
        Toast.makeText(getInstance().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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
            final int key = R.string.map_server_preference;
            final String prefName = AppPreferences.getInstance().getIdentifier(key);
            String lanSettings = Preferences.getString(prefName, "");
            //SharedPreferences prefs = getSharedPreferences("general_settings", Context.MODE_PRIVATE);
            //String lanSettings = prefs.getString("serverIP", null);
            if (url.indexOf('.') < 0)
            {
                url += (lanSettings.endsWith("gpx") ? ".gpx" : ".kml");
            }
            url = lanSettings + "/" + url;
            locLstnr.updateCueSheet(url);
        }
    }

    public void moveToStart(View vv)
    {
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        if (null != locLstnr)
        {
            locLstnr.moveToStart();
        }
    }

    public void moveNext(View vv)
    {
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        if (null != locLstnr)
        {
            locLstnr.moveNext();
        }
    }

    public void moveToEnd(View vv)
    {
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        if (null != locLstnr)
        {
            locLstnr.moveToEnd();
        }
    }

    // @Override
    // protected boolean isRouteDisplayed()
    // {
    // return false;
    // }

    public static void runOnUi(Runnable runThis)
    {
        MainActivity.getInstance().runOnUiThread(runThis);
    }

    public static Activity getInstance()
    {
        return mainActivity;
    }

    public static boolean hasLocationPermission()
    {
        return ((ActivityCompat.checkSelfPermission(MainActivity.getInstance(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(MainActivity.getInstance(), ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED));
    }

}
