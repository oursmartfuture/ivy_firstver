package com.globalclasses;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.widget.Toast;

import com.ivy.R;

/**
 * Created by ss106 on 9/29/2016.
 */

public class GpsLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isEnabled) {
                Toast.makeText(context, context.getString(R.string.closesLocation), Toast.LENGTH_LONG).show();
            }
        }
    }
}
