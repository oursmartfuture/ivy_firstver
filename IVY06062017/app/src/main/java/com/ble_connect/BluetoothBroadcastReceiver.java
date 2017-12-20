package com.ble_connect;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.globalclasses.GlobalMethod;
import com.ivy.Landing_Activity;
import com.ivy.SimpleHttpConnection;

/**
 * Created by ss106 on 9/5/2016.
 */

public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    Context context;
    private Handler mHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                if (Landing_Activity.mBluetoothLeService != null) {
                    GlobalMethod.write("BLUETOOTH OFF RECEIVER NOT NULL");
                    Landing_Activity.mBluetoothLeService.disconnect("");
                } else {
                    GlobalMethod.write("BLUETOOTH OFF RECEIVER NULL");
                }
                GlobalMethod.write("BLUETOOTH OFF RECEIVER");
            } else {
                GlobalMethod.write("BLUETOOTH ON RECEIVER");
//                loadContacts();
            }
        }
    }

    public void loadContacts() {
        if (SimpleHttpConnection.isNetworkAvailable(context)) {
            mHandler = new Handler();
//            new LoadContacts().execute();
        } else {
//            GlobalMethod.showToast(this, Constant.network_error);
        }
    }

//    class LoadContacts extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... params) {
//            SharedPreferences mainPreferences = context.getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
//            ArrayList<NameValuePair> nameValuePair = new ArrayList<>();
//            nameValuePair.add(new BasicNameValuePair("showHtml", "no"));
//            nameValuePair.add(new BasicNameValuePair("mode", ""));
//            nameValuePair.add(new BasicNameValuePair("user_id", mainPreferences.getString(Constant.id_user, "")));
//            nameValuePair.add(new BasicNameValuePair("page", "1"));
//            return SimpleHttpConnection.sendByPOST(Urls.VIEW_DEVICE_LIST, nameValuePair);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            JSONObject jobj = null;
//            try {
//                jobj = new JSONObject(s);
//
//                if (jobj.getString("success").equalsIgnoreCase("1")) {
//                    JSONArray jsonArray = jobj.getJSONArray("data");
//                    if (jsonArray.length() > 0) {
//                        total_devices.clear();
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            total_devices.add(jsonArray.getJSONObject(i));
//                        }
//                        if (total_devices.size() > 0) {
//                            scanLeDevice(true);
//                        }
//                    } else {
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    scanLeDevice(false);
//                }
//            }, SCAN_PERIOD);
//            mScanning = true;
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//        } else {
////            listScannedDevice.onRefreshComplete();
//            try {
//                GlobalMethod.write("BLE DEVICES : " + mLeDeviceList);
//                if (total_devices.size() > currentPos) {
//                    if (mLeDeviceList.containsKey(total_devices.get(currentPos).getString("unique_name").trim())) {
//                        makeConnection(mLeDeviceList.get(total_devices.get(currentPos).getString("unique_name")));
//                    }
//                    currentPos++;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            mScanning = false;
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//    }

}
