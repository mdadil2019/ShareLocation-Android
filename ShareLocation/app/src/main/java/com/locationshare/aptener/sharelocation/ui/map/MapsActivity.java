package com.locationshare.aptener.sharelocation.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.di.root.MyApp;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,MapActivityMVP.View {

    private GoogleMap mMap;
    String id;
    @Inject
    MapActivityMVP.Presenter presenter;

    @Inject
    AppPreferenceHelper prefs;

    Timer timer;

    View customMarkerView;
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
        LatLng location = new LatLng(20.5937, 78.9629);
        mMap.addMarker(new MarkerOptions().position(location).title("Fetching location..."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        //inflate the marker layout
        customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_marker, null);
        //ask presenter to listen to change in location and update the view accordingly
        presenter.fetchLocationUpdateOfFirebase(id,prefs.getId());


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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
