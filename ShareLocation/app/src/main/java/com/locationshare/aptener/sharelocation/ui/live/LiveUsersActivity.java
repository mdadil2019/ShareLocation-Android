package com.locationshare.aptener.sharelocation.ui.live;

import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.locationshare.aptener.sharelocation.R;
import com.locationshare.aptener.sharelocation.adapter.LiveUserAdapter;
import com.locationshare.aptener.sharelocation.data.AppPreferenceHelper;
import com.locationshare.aptener.sharelocation.data.model.User;
import com.locationshare.aptener.sharelocation.di.root.MyApp;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveUsersActivity extends AppCompatActivity implements LiveUsersActivityMVP.View {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Inject
    LiveUsersActivityMVP.Presenter presenter;

    @Inject
    AppPreferenceHelper prefs;

    LiveUserAdapter adapter;

    ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_users);
        ButterKnife.bind(this);
        ((MyApp)getApplication()).getApplicationComponent().inject(this);
        users = new ArrayList<>();
        presenter.getLiveUsers();

    }

    @Override
    public void noOneIsTracking() {

    }

    @Override
    public void updateList(User user) {
        users.add(user);
        adapter = new LiveUserAdapter(this,users,prefs.getId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setView(this);
    }
}
