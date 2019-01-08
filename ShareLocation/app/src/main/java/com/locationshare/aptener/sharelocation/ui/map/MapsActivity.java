package com.locationshare.aptener.sharelocation.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.locationshare.aptener.sharelocation.R;
import com.locationshare.aptener.sharelocation.adapter.LiveUserAdapter;
import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.data.model.User;
import com.locationshare.aptener.sharelocation.di.root.MyApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,MapActivityMVP.View,LocationListener {

    private GoogleMap mMap;
    String id;
    @Inject
    MapActivityMVP.Presenter presenter;

    @Inject
    AppPreferenceHelper prefs;

    @BindView(R.id.editTextCurrentUserLink)
    EditText currentUserLinkEt;

    @BindView(R.id.buttonShareCurrentLocation)
    Button shareLocationBtn;

    @BindView(R.id.recyclerViewTrackers)
    RecyclerView recyclerView;

    LiveUserAdapter liveUserAdapter;
    ArrayList<User> users;

    Timer timer;


    GoogleMap googleMap;
    Marker currentLocationMarker;
    private View customMarkerView;
    private View customMarkerViewCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        ((MyApp)getApplication()).getApplicationComponent().inject(this);
        users =  new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        id = getIntent().getStringExtra("ID");
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
        customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_marker, null);
        customMarkerViewCurrentUser = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_marker, null);

        this.googleMap = googleMap;

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(20.5937, 78.9629);
        mMap.addMarker(new MarkerOptions().position(location).title("Fetching location..."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

       //ask presenter to listen to change in location and update the view accordingly
        presenter.fetchLocationUpdateOfFirebase(id,prefs.getId());
        showCurrentUserLocation();
    }

    private void showCurrentUserLocation() {
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
    }

    @Override
    public void updateOnMap(LatLng latLng, String lastUpdate) {

        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title(lastUpdate)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.avatarm,lastUpdate)));

        Marker marker = mMap.addMarker(markerOptions);
        setTimerListner(marker, lastUpdate);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)); ::=> Note: zooming after moving it's position will result into zoom to random location
        mMap.animateCamera(cameraUpdate);

//        updateLastFetchedTime(lastUpdate);


    }

    private void setTimerListner(final Marker marker, final String lastUpdate) {
        if(timer==null){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.avatarm,lastUpdate)));
                        }
                    });
                }
            }, 0, 5000);
        }
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId, String lastUpdate) {
        //inflate the marker layout

        ImageView markerImageView = customMarkerView.findViewById(R.id.imageViewProfile);
        TextView lastUpdateTv= customMarkerView.findViewById(R.id.textViewLastUpdateTime);
        lastUpdateTv.setText(getLastFetchedTime(lastUpdate));
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private String getLastFetchedTime(String lastUpdate) {
        final Date fetchedDate = new Date(lastUpdate);
        final Date currentDate = Calendar.getInstance().getTime();
        long mills = currentDate.getTime() - fetchedDate.getTime();
        int hours = (int) (mills/(1000 * 60 * 60));
        int mins = (int) ((mills/(1000*60)) % 60);
        int seconds = (int) ((mills/(1000)) % 60);

        String diff = hours + " hrs" + ":" + mins +" min" + ":" +seconds + "sec" + " Ago";
        return diff;
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setView(this);
        presenter.isTrackedByAnyone();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude= location.getLatitude();
        double longitude=location.getLongitude();

        LatLng loc = new LatLng(latitude, longitude);

        if (currentLocationMarker!=null){
            currentLocationMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions().position(loc)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromBitmap(getCurrentMarkerBitmapFromView(R.drawable.avatarm)));

         currentLocationMarker = googleMap.addMarker(markerOptions);
    }

    private Bitmap getCurrentMarkerBitmapFromView(@DrawableRes int resId) {
        //inflate the marker layout

        ImageView markerImageView = customMarkerViewCurrentUser.findViewById(R.id.imageViewProfile);
        TextView lastUpdateTv= customMarkerViewCurrentUser.findViewById(R.id.textViewLastUpdateTime);
        View markerView= customMarkerViewCurrentUser.findViewById(R.id.markerview);
        lastUpdateTv.setVisibility(View.GONE);
        markerView.setVisibility(View.GONE);

        markerImageView.setImageResource(resId);
        customMarkerViewCurrentUser.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerViewCurrentUser.layout(0, 0, customMarkerViewCurrentUser.getMeasuredWidth(), customMarkerViewCurrentUser.getMeasuredHeight());
        customMarkerViewCurrentUser.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerViewCurrentUser.getMeasuredWidth(), customMarkerViewCurrentUser.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerViewCurrentUser.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerViewCurrentUser.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, provider, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.buttonShareCurrentLocation)void shareLocation(){
        presenter.addUser(prefs.getId());
    }


    @Override
    public void showLink(String link) {
        currentUserLinkEt.setText(link);
    }

    @Override
    public void showShareLocationWidgets() {
        shareLocationBtn.setVisibility(View.VISIBLE);
        currentUserLinkEt.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideShareLocationTrackingWidgets() {
        shareLocationBtn.setVisibility(View.INVISIBLE);
        currentUserLinkEt.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateList(User user) {
        recyclerView.setVisibility(View.VISIBLE);
        users.add(user);
        liveUserAdapter = new LiveUserAdapter(this,users,prefs.getId());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(liveUserAdapter);
    }
}
