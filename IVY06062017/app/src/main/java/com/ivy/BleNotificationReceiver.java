package com.ivy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.ble_connect.NotifyActivity;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;

import static com.ble_connect.BluetoothLeService.SIGNAL_STRENGTH;

/**
 * Created by SS106 on 02-06-2016.
 */
public class BleNotificationReceiver extends BroadcastReceiver {
    static String prevText = "";
    SharedPreferences login_prference;

    @Override
    public void onReceive(Context context, Intent intent) {
        String text = intent.getStringExtra("text").trim();
        String device = intent.getStringExtra("device").trim();
        String address = intent.getStringExtra("mac_address").trim();

        GlobalMethod.write("NUMBER OF TAPS : OUTSIDE : " + text + ", DEVICE : " + device);


//        GlobalMethod.write("NUMBER OF TAPS : OUTSIDE : LIST : " + Landing_Activity.total_devices);
//        int flag = 0;
//        for (int i = 0; i < Landing_Activity.total_devices.size(); i++) {
//            try {
//                if (Landing_Activity.total_devices.get(i).getString("unique_name").trim().equalsIgnoreCase(device)) {
//                    flag = 1;
//                    break;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        GlobalMethod.write("NUMBER OF TAPS : OUTSIDE : FLAG : " + flag);

//        if (flag == 1) {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                prevText = "";
            }
        }, 5000);

        if (!prevText.equalsIgnoreCase(text)) {
            prevText = text;
            GlobalMethod.write("NUMBER OF TAPS : " + text);
            login_prference = context.getSharedPreferences(Constant.pref_main, 0);
            if (!TextUtils.isEmpty(login_prference.getString(Constant.email, ""))) {
                if (!Landing_Activity.total_devices.toString().contains(device)) {
                    Landing_Activity.mBluetoothLeService.disconnect(address);
//                    if (!Landing_Activity.list.contains(address))
//                        Landing_Activity.addToList(address);
                    return;
                }
                if (Landing_Activity.activeDevices.contains(device)) {
                    context.startActivity(new Intent(context, NotifyActivity.class).putExtra("text", text).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                    Intent intent_ = new Intent(SIGNAL_STRENGTH);
//                    intent_.putExtra("distance", "0");
//                    intent_.putExtra("mac_address", address);
//                    intent_.putExtra("name", device);
//                    context.sendBroadcast(intent);
                }
            }
        }
//        } else {
//            Landing_Activity.mBluetoothLeService.disconnect();
//        }
    }
}
