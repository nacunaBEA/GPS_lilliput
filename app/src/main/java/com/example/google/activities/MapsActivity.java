package com.example.google.activities;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.google.R;
import com.example.google.adapters.GPSAdapter;
import com.example.google.modules.GPSservice;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements  GPSservice.IgpsListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private List<String> gpsParams = new ArrayList<String>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int counter = 0;
    SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new GPSAdapter(gpsParams,R.layout.res_view_gps_params);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        GPSservice gpSservice = new GPSservice(this,this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng latlon = new LatLng(latitude, longitude);
        Polyline polyline = googleMap.addPolyline(new PolylineOptions().add(latlon));
        polyline.setTag("BEA");
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
        mMap.addMarker(new MarkerOptions().position(latlon).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlon));

    }

    String[] latLng = null;
    double  longitude = 0;
    double latitude = 0;
    @Override
    public void onGetGPSparameters(String params) {
        gpsParams.add(0,params);
        mAdapter.notifyItemInserted(0);
        layoutManager.scrollToPosition(0);

        latLng = params.split("\\ |\\,|\\:");
        mapFragment.getMapAsync(this);

        longitude = Double.valueOf(latLng[8]);

        latitude = Double.valueOf(latLng[3]);

    }
}
