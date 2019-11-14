package com.globsyn.nearbyv2;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;


public class LastActivity extends ActionBarActivity implements OnMapReadyCallback{

    private TextView tv_name, tv_vicinity;
    private SupportMapFragment mapFragment;
    private static Location point;
    private int position;
    private static Venue place_selected;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);
        tv_name= (TextView) findViewById(R.id.tv_venue_name);
        tv_vicinity= (TextView) findViewById(R.id.tv_venue_vicinity);

        position=getIntent().getIntExtra("position", 0);
        VenueSingleton data_read= new VenueSingleton();
        if(!data_read.isEmpty()){
            place_selected=data_read.create().getDatasource().get(position);
        }

        tv_name.setText(place_selected.getName());
        tv_vicinity.setText(place_selected.getVicinity());
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.last_map);
        mapFragment.getMapAsync(this);
    }

    public class placeDetailsDownload extends AsyncTask<String, Void, Void>{

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(String... params) {
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        LatLng point= new LatLng(place_selected.getLat(), place_selected.getLng());
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.last_map)).getMap();
        }
            if (point == null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(0, 0)).title("Location Unavailable"));
                Toast.makeText(LastActivity.this, "Location Unavailable",
                        Toast.LENGTH_SHORT).show();
            } else {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.setMyLocationEnabled(true);
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(point).title(
                        place_selected.getName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,
                        16));
            }


    }

    public void onBackPressed() {
        startActivity(new Intent(LastActivity.this, ResultActivity.class));
        finish();
    }
}
