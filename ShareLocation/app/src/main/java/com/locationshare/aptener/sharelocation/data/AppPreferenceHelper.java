package com.locationshare.aptener.sharelocation.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferenceHelper implements PreferenceHelper {

    public static final String SHARED_PREF_KEY = "SharedPrefsKey";
    public static final String ID_KEY = "ID_KEY";


    SharedPreferences prefs;


    public AppPreferenceHelper(Context context){
        prefs = context.getSharedPreferences(SHARED_PREF_KEY,Context.MODE_PRIVATE);
    }
    @Override
    public void saveId(String id) {
        prefs.edit().putString(ID_KEY,id).apply();
    }

    @Override
    public String getId() {
        return prefs.getString(ID_KEY,null);
    }
}
