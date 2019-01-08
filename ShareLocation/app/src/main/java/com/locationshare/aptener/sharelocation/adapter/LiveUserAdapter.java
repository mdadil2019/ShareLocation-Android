package com.locationshare.aptener.sharelocation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.locationshare.aptener.sharelocation.R;
import com.locationshare.aptener.sharelocation.data.model.User;
import com.locationshare.aptener.sharelocation.ui.map.MapsActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.RecyclerView.*;

public class LiveUserAdapter extends RecyclerView.Adapter<LiveUserAdapter.MyLiveUsers> {

    Context mContext;
    ArrayList<User> mUsers;
    String myId;

    public LiveUserAdapter(Context context, ArrayList<User> users, String id){
        mContext = context;
        mUsers = users;
        myId = id;
    }

    @NonNull
    @Override
    public MyLiveUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.live_user_layout,parent,false);
        return new MyLiveUsers(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyLiveUsers holder, final int position) {
        holder.userIdTv.setText(mUsers.get(position).getUserId());
        holder.thumbnailIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = mUsers.get(position).getUserId();
                //show the location of the user in current map view
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class MyLiveUsers extends ViewHolder {
        @BindView(R.id.textViewId)
        TextView userIdTv;

        @BindView(R.id.imageViewThumbnail)
        ImageView thumbnailIv;

        public MyLiveUsers(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
