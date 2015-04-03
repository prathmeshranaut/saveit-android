package com.aayush.bae;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements LocationListener {

    private Toolbar toolbar;
    private Button mButton;
    private GoogleMap googleMap;

    protected double mLatitude;
    protected double mLongitude;
    protected int mHelpId;
    protected List<Hospital> mHospital = new ArrayList<Hospital>() {
    };
    protected TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar added
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mButton = (Button) findViewById(R.id.need_assistance);
        mTextView = (TextView) findViewById(R.id.eta);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));

            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("SaveIt");
        }
//
//        final int sdkVersion = Build.VERSION.SDK_INT;
//        if (sdkVersion >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(getResources().getColor(R.color.md_red_800));
//            window.setNavigationBarColor(getResources().getColor(R.color.md_red_600));
//        }

        try {
            // Loading map
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Utils.substituteString(getString(R.string.need_assistance), new HashMap<String, String>());
                String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                Map<String, String> params = new HashMap<String, String>();
                params.put("device_id", androidId);
                params.put("latitude", Double.toString(mLatitude));
                params.put("longitude", Double.toString(mLongitude));
                Log.d("SEarch", params.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("REsponse", response.toString());
                        try {
                            JSONArray search_results = response.getJSONArray("search_results");
                            mHospital.clear();
                            for(int i = 0; i < search_results.length(); i++) {
                                JSONObject jsonObject = search_results.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
                                String username = jsonObject.getString("username");
                                int ambulanceCount = jsonObject.getInt("ambulance_count");
                                Double latitude = jsonObject.getDouble("latitude");
                                Double longitude = jsonObject.getDouble("longitude");
                                String eta = jsonObject.getString("eta");
                                String distanceKm = jsonObject.getString("distance_km");

                                Hospital hospital = new Hospital(id, name, username, latitude, longitude, ambulanceCount, eta, distanceKm);
                                mHospital.add(hospital);
                                if(i == 0){
                                    mTextView.setVisibility(View.VISIBLE);
                                    mTextView.setText(name+ "\n Arriving in about "+eta);
                                }
                            }
                            locateOnMap(mHospital);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error", error.getMessage());
                    }
                });

                AppController.getInstance().addToRequestQueue(jsonObjectRequest, "url_tag");
            }
        });
    }

    private void locateOnMap(List<Hospital> hospital) {
        for(Hospital hosp: hospital) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(hosp.getLatitude(), hosp.getLongitude())));
        }
    }

    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }else {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // Create a criteria object to retrieve provider
                Criteria criteria = new Criteria();

                // Get the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);

                // Get Current Location
                Location myLocation = locationManager.getLastKnownLocation(provider);

                // set map type
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Get latitude of the current location
                double latitude = myLocation.getLatitude();

                // Get longitude of the current location
                double longitude = myLocation.getLongitude();
                mLatitude = latitude;
                mLongitude = longitude;

                // Create a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);

                // Show the current location in Google Map
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Zoom in the Google Map
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        googleMap.clear();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mLatitude = latitude;
        mLongitude = longitude;

        Log.d("Maps", location.toString());

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
