package com.locationshare.aptener.sharelocation.ui.map;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.locationshare.aptener.sharelocation.R;
import com.locationshare.aptener.sharelocation.di.root.MyApp;

import javax.inject.Inject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,MapActivityMVP.View {

    private GoogleMap mMap;
    String id,myId;
    @Inject
    MapActivityMVP.Presenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ((MyApp)getApplication()).getApplicationComponent().inject(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        id = getIntent().getStringExtra("ID");
        myId = getIntent().getStringExtra("MY_ID");
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //ask presenter to listen to change in location and update the view accordingly
        presenter.fetchLocationUpdateOfFirebase(id,myId);

    }

    @Override
    public void updateOnMap(LatLng latLng, String lastUpdate) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(lastUpdate));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
