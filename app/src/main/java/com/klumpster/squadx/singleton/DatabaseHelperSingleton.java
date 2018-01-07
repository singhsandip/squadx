package com.klumpster.squadx.singleton;

import android.content.Context;

import com.klumpster.squadx.database.DatabaseHelper;

/**
 * Created by BOX on 07-01-2018.
 */

public class DatabaseHelperSingleton {

    private static DatabaseHelper databaseHelper = null;

    private DatabaseHelperSingleton(){
    }

    public static DatabaseHelper getDatabaseHelperInstance(Context context){
        if (databaseHelper == null){
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }
}
