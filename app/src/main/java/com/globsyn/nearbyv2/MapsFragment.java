package com.globsyn.nearbyv2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap gMap;
    private SupportMapFragment mFragment;
    private LatLng location;

    public MapsFragment() {
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.fragment_maps, container, false);


        mFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_tb1);
        mFragment.getMapAsync(this);

        setupMap();

        return rootview;
    }

    private void setupMap() {
        if(gMap==null){
            gMap= ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_tb1)).getMap();
        }
    }

    public void setupPlacePicker(LatLng point, String name){
        if(point!=null){
            if(gMap==null){
                setupMap();
            }
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(point).title(name));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
            gMap.animateCamera(CameraUpdateFactory.zoomIn());
            gMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        }
    }

    public void onDestroy() {
        if(mFragment.isResumed()){
            getFragmentManager().beginTransaction().remove(mFragment).commit();
        }
        super.onDestroy();
    }


    public void onMapReady(GoogleMap googleMap) {
//        LatLng def_kol = new LatLng(22.5667, 88.3667);


        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(false);

//        googleMap.addMarker(new MarkerOptions().position(def_kol).title("Kolkata"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(def_kol, 15));
    }
}
