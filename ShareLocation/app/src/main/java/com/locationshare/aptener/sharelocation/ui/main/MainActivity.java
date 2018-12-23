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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.locationshare.aptener.sharelocation.R;
import com.locationshare.aptener.sharelocation.di.root.MyApp;
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

    @BindView(R.id.editTextLink)
    EditText linkEt;

    @BindView(R.id.pgBar)
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((MyApp)getApplication()).getApplicationComponent().inject(this);
        handleIntent();
        askPermissions();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION ,Manifest.permission.READ_PHONE_STATE}, GLOBAL_REQUEST_CODE);
        }

    }

    @OnClick(R.id.buttonGenerate)void generateLink(){
        /*
        1. Add user to firebase (if new) with device id and required childs at normal state
        2. append the deviceId after the app link
        3. show link in edit text
         */
            String id = getId();
            presenter.addUser(id);
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
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
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
    protected void onResume() {
        super.onResume();
        presenter.setView(this);
    }
}
