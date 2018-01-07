package com.klumpster.squadx.database;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.http.PUT;

/**
 * Created by BOX on 06-01-2018.
 */

public class PrefrenceManager {
    public static final String SHARED_PREF_NAME = "SQUADX_SHARED_PREFERENCE";
    private static final String Default = "DEFAULT";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public static void setString(String key,String value){
        editor.putString(key,value).commit();
    }

    public static String getString(String key){
        return sharedPreferences.getString(key,Default);
    }

}
