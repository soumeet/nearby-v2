package com.globsyn.nearbyv2;

import java.util.Locale;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, SearchView.OnQueryTextListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener{
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private SearchView searchView;
    private TextView tb1_tv;
    private MapsFragment mapsFragment=new MapsFragment();
    private CategoryFragment categoryFragment= new CategoryFragment();
    private FusedLocationProviderApi fusedLocationProviderApi= LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static Location user_location;
    private SharedPreferences search_location;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private static final int INTERVAL = 10000 ;
    private static final int FASTEST_INTERVAL = 5000 ;
//    private static final float DISPLACEMENT = 10.0f;
    private static final String TAG = "LocationUpdate" ;

    /*
    private static final String LOG_TAG = "GPlaces Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String BROWSER_KEY = "AIzaSyDZ8LlDjowb9TsVFESb_z8mcgLRAvR-z3c";
    */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //CHECK FOR NET CONNECTIVITY AND GOOGLE PLAY SERVICES
        if (!isGooglePlayServicesAvailable()){
            finish();
        }
        search_location= this.getSharedPreferences("search_location", Context.MODE_PRIVATE);

        buildGoogleApiClient();
        createLocationRequest();

        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
//        if(user_location!=null)


    }
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        googleApiClient.connect();
    }
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        stopLocationUpdates();
    }
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if(googleApiClient.isConnected())
            startLocationUpdates();
    }
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        googleApiClient.disconnect();
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem= menu.findItem(R.id.main_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.main_settings:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                finish();
                break;
            case R.id.main_pick:
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(getApplicationContext());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.main_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                finish();
                break;
            case R.id.main_exit:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(MainActivity.this, "Query: " + query+" submitted", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        Log.d("MainActivity", "Query: " + newText);

        return false;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return  mapsFragment;
                case 1:
                    return  categoryFragment;
            }
            return null;
        }

        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Maps";
                case 1:
                    return "Category";
            }
            return null;
        }
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Do you really want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(data, MainActivity.this);
            final CharSequence name = place.getName();
            /*final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            */
            Toast.makeText(MainActivity.this, ""+name, Toast.LENGTH_SHORT).show();
            mapsFragment.setupPlacePicker(place.getLatLng(), name.toString());
//            passLocation(mapsFragment, place.getLatLng());
/*
            mName.setText(name);
            mAddress.setText(address);
            mAttributions.setText(Html.fromHtml(attributions));
*/
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void buildGoogleApiClient(){
        googleApiClient= new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).
                build();
        Log.d(TAG, "buildGoogleApiClient");
    }

    public void createLocationRequest(){
        locationRequest= new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d(TAG, "createLocationRequest");
    }

    public boolean isGooglePlayServicesAvailable(){
        int status= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS==status)
            return true;
        else{
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        Log.d(TAG, "stopLocationUpdates");
    }

    private void startLocationUpdates(){
        if (googleApiClient!=null)
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        Log.d(TAG, "startLocationUpdates");
    }

    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        Log.d(TAG, "onConnected");
    }
    public void onConnectionSuspended(int i) {
//        stopLocationUpdates();
    }
    public void onLocationChanged(Location location) {
//        user_location=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        user_location=location;
        Log.d(TAG, "onLocationChanged @" + user_location.getLatitude() + ", " + user_location.getLongitude());
        updateMap(new LatLng(user_location.getLatitude(), user_location.getLongitude()));
//        search_location.edit().putString(search_loc, "@"+location.getLatitude() + ", " + location.getLongitude()).apply();
    }
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed:"+connectionResult.toString());
    }

    public void updateMap(LatLng point){
            mapsFragment.setupPlacePicker(point, "You");
    }

/*
    public void passLocation(android.support.v4.app.Fragment fragment, LatLng location) {
        Bundle args=new Bundle();
        args.putParcelable("location", location);
        Log.d("MainActivity", "Sending: lat: "+location.latitude+" lng: "+location.longitude);
        fragment.setArguments(args);
    }
*/
}
