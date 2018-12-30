package com.locationshare.aptener.sharelocation.ui.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.locationshare.aptener.sharelocation.R;
import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.di.root.MyApp;
import com.locationshare.aptener.sharelocation.ui.live.LiveUsersActivity;
import com.locationshare.aptener.sharelocation.ui.map.MapsActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainActivityMVP.View {

    private static final int GLOBAL_REQUEST_CODE = 1;
    @Inject
    MainActivityMVP.Presenter presenter;

    @Inject
    AppPreferenceHelper prefs;

    @BindView(R.id.editTextLink)
    EditText linkEt;

    @BindView(R.id.pgBar)
    ProgressBar progressBar;

    @BindView(R.id.buttonStop)
    Button stopButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((MyApp)getApplication()).getApplicationComponent().inject(this);
        askPermissions();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION ,Manifest.permission.READ_PHONE_STATE}, GLOBAL_REQUEST_CODE);
        }else{
            if(prefs.getId()!=null)
                handleIntent();
            else{
                prefs.saveId(getId());
                handleIntent();
            }
        }

    }

    @OnClick(R.id.buttonGenerate)void generateLink(){
        /*
        1. Add user to firebase (if new) with device id and required childs at normal state
        2. append the deviceId after the app link
        3. show link in edit text
         */
            presenter.addUser(prefs.getId());
    }

    private void handleIntent() {
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        if(appLinkData!=null){
            String deviceId = appLinkData.getLastPathSegment();
            Intent intent = new Intent(this,MapsActivity.class);
            intent.putExtra("ID",deviceId);
            startActivity(intent);
        }
    }

    private String getId(){
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if(android_id!=null&&!android_id.equals("")){
            return android_id;
        }
        return Build.ID;
    }

    @Override
    public void showLink(String link) {
        linkEt.setText(link);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i = 0;i<grantResults.length;i++){
            if(grantResults[i]==-1){
                Toast.makeText(this, "App will not work without these permissions", Toast.LENGTH_SHORT).show();
                finish();
            }else if(grantResults[i]==0 && grantResults.length-1 == i){
                //all permissions are granted
                if(prefs.getId()!=null)
                    handleIntent();
                else{
                    prefs.saveId(getId());
                    handleIntent();
                }
            }
        }
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void activateStopButton() {
        stopButton.setEnabled(true);
    }

    @Override
    public void deactivateStopButton() {
        stopButton.setEnabled(false);
    }

    @OnClick(R.id.buttonStop)void stopTracking(){
        presenter.stopLocationTracking();
        Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.buttonShowLiveUsers)void showLiveUsers(){
        //starts live user activity
        startActivity(new Intent(this,LiveUsersActivity.class));
    }
    @Override
    protected void onResume() {
        super.onResume();
        presenter.setView(this);
//        presenter.isTrackedByAnyone();
    }
}
