package com.mgjg.kmztracker

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.mgjg.kmztracker.map.MapOverlayer
import com.mgjg.kmztracker.preference.MapPreferencesActivity
import com.mgjg.kmztracker.preference.Preferences

class MainActivity : AppCompatActivity(), com.google.android.gms.maps.OnMapReadyCallback {
  // static final LatLng HAMBURG = new LatLng(53.558, 9.927);
  // static final LatLng KIEL = new LatLng(53.551, 9.993);

  private lateinit var map: GoogleMap
  private var locationTracker: LocationTracker? = null

  init {
    instance = this
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Select the proper xml layout file which includes the matching Google
    // API Key
    if (BuildConfig.DEBUG) {
      //setContentView(R.layout.debug_main);
      setContentView(R.layout.activity_main)
    } else {
      setContentView(R.layout.activity_main)
    }
    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    setupMap()
  }

  private fun setupMap() {
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    if (ADD_MAP) {
      // Gets to fragment for the GoogleMap from the View and does initialization stuff
      // note, this is done asynchronously
      val fragmentManager = this.supportFragmentManager
      // this.childFragmentManager
      val cfm = fragmentManager.fragments[0].childFragmentManager
      //val frag = cfm.findFragmentById(R.id.map) as SupportMapFragment
      val frag = fragmentManager.fragments[0] as SupportMapFragment;
      frag.getMapAsync(this)
    }
    for (ii in frontViews) {
      val vv: View = findViewById(ii)
      vv.bringToFront()
    }

  }

  override fun onMapReady(googleMap: GoogleMap) {
      map = googleMap
      map.setOnMarkerClickListener { false }
      // Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
      // .title("Hamburg"));
      // Marker kiel = map.addMarker(new MarkerOptions()
      // .position(KIEL)
      // .title("Kiel")
      // .snippet("Kiel is cool")
      // .icon(BitmapDescriptorFactory
      // .fromResource(R.drawable.ic_launcher)));

      map.uiSettings.isMyLocationButtonEnabled = false
      if (ActivityCompat.checkSelfPermission(
          this@MainActivity,
          ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
          this@MainActivity,
          ACCESS_COARSE_LOCATION
        ) == PERMISSION_GRANTED
      ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        map.isMyLocationEnabled = true
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
      locationTracker = LocationTracker(MapOverlayer(MainActivity.APP, map))
    }

  internal fun onResume(context: Activity) {
    locationTracker?.onResume(context)
  }

  internal fun onPause(context: Activity) {
    if (null != locationTracker) {
      locationTracker!!.onPause(context)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.activity_main, menu)
    //inflater.inflate(R.menu.activity_main_actions, menu);

    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Take appropriate action for each action item click
    val result: Boolean
    when (item.itemId) {
      R.id.menu_settings -> {
        // settings
        // launch preference activity
        val ii = Intent(this, MapPreferencesActivity::class.java)
        startActivity(ii)
        result = true
      }
      else -> result = super.onOptionsItemSelected(item)
    }
    return result
  }

  /**
   * On selecting action bar icons
   */
  private fun onActivityMainActionsOptionsItemSelected(item: MenuItem): Boolean {
    // Take appropriate action for each action item click
    val result: Boolean
    when (item.itemId) {
      R.id.action_search ->
        // search action
        result = true
      R.id.action_location_found -> {
        // location found
        LocationFound()
        result = true
      }
      R.id.action_refresh ->
        // refresh
        result = true
      R.id.action_help ->
        // help action
        result = true
      R.id.action_check_updates ->
        // check for updates action
        result = true
      else -> result = super.onOptionsItemSelected(item)
    }
    return result
  }

  /**
   * Launching new activity
   */
  private fun LocationFound() {
    // Intent i = new Intent(MainActivity.this, LocationFound.class);
    //startActivity(i);
  }

  /**
   * button handler?
   *
   * @param vv
   */
  fun setCueSheetFromXml(vv: View) {
    if (null != locationTracker) {
      val et = findViewById<EditText>(R.id.CUESHEET)
      var url = et.text.toString()
      if (url.isEmpty()) {
        url = getString(R.string.map_file)
      }
      // get current preference for serverIP
      val key = R.string.map_server_preference
      val prefName = AppPreferences.instance!!.getIdentifier(key)
      var lanSettings = Preferences.getString(prefName, "")
      //SharedPreferences prefs = getSharedPreferences("general_settings", Context.MODE_PRIVATE);
      //String lanSettings = prefs.getString("serverIP", null);
      if (url.indexOf('.') < 0) {
        url += if (lanSettings.endsWith("gpx")) ".gpx" else ".kml"
      }
      if (lanSettings.length <= 0) {
        lanSettings = "file://";
      }
      url = lanSettings + "/" + url
      locationTracker!!.updateCueSheet(url)
    }
  }

  fun moveToStart(vv: View) {
    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
    locationTracker?.moveToStart()
  }

  fun moveNext(vv: View) {
    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
     locationTracker?.moveNext()
  }

  fun moveToEnd(vv: View) {
    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
      locationTracker?.moveToEnd()
  }

  companion object {

    val APP = "com.mgjg.kmztracker"

    private val frontViews =
      intArrayOf(R.id.GPS, R.id.GPS_LAT, R.id.GPS_LON, R.id.READ_CUESHEET, R.id.CUESHEET)

    lateinit var instance: Activity

    private var ADD_MAP = true

    fun showToast(text: String) {
      Toast.makeText(instance.applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    // @Override
    // protected boolean isRouteDisplayed()
    // {
    // return false;
    // }

    fun runOnUi(runThis: Runnable) {
      MainActivity.instance.runOnUiThread(runThis)
    }

    fun hasLocationPermission(): Boolean {
      return ActivityCompat.checkSelfPermission( instance, ACCESS_FINE_LOCATION ) == PERMISSION_GRANTED
          || ActivityCompat.checkSelfPermission( instance, ACCESS_COARSE_LOCATION ) == PERMISSION_GRANTED }
  }

}
