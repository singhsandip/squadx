package com.klumpster.squadx.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by BOX on 07-01-2018.
 */

public class CheckConnectionHelper {

    private static boolean networkStatus = false;

    public static boolean isConnectedToInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected()) {
            networkStatus = true;
        } else if (mobile.isConnected()) {
            networkStatus = true;
        } else {
            networkStatus = false;
        }
        return networkStatus;

    }
}

