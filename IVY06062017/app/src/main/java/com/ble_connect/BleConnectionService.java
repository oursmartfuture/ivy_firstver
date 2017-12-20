package com.ble_connect;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;
import com.ivy.Landing_Activity;
import com.ivy.SimpleHttpConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ivy.Landing_Activity.mBluetoothLeService;

/**
 * Created by ss106 on 9/30/2016.
 */

public class BleConnectionService extends Service implements CallBackListenar {

    private static final int REQUEST_ENABLE_BT = 1221;
    private static final long SCAN_PERIOD = 5 * 60 * 1000;
    private final IBinder mBinder = new LocalBinder();
    SharedPreferences mainPreferences;
    Activity context;
    static BleConnectionService mBleConnectionService;
    private ArrayList<JSONObject> total_devices;
    private static BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private int currentPos = 0;
    private HashMap<String, Integer> hMap = new HashMap<>();
    static String macAddress;
    private BluetoothManager mBluetoothManager;


    public static BleConnectionService getInstance(Activity context, String macAddress) {
        BleConnectionService.macAddress = macAddress;

        if (mBleConnectionService != null) {
//            mBleConnectionService.loadContacts(false);
            return mBleConnectionService;
        }
        return mBleConnectionService = new BleConnectionService(context);
    }

    public BleConnectionService(Activity context) {
        this.context = context;
        mHandler = new Handler();
        mainPreferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        loadContacts(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        JSONObject jobj = new JSONObject(result);
        if (action.equalsIgnoreCase("Login")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                JSONArray jsonArray = jobj.getJSONArray("data");
                if (jsonArray.length() > 0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        total_devices.add(jsonArray.getJSONObject(i));
                    }

                    if (total_devices.size() > 0) {
                        if (!mBluetoothAdapter.isEnabled()) {
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            }
                        } else {
                            scanLeDevice(true);
                        }
                    } else {
                        if (mBluetoothLeService != null) {
                            mBluetoothLeService.disconnect("");
                        }
                    }

                    for (JSONObject deviceAddress : total_devices) {
                        hMap.put(deviceAddress.getString("mac_address"), 0);
                    }

                }
            } else {
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.disconnect("");
                }

            }
        }
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e("BLE CONNECTION SERVICE", "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e("BLE CONNECTION SERVICE", "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public class LocalBinder extends Binder {
        public BleConnectionService getService() {
            return BleConnectionService.this;
        }
    }

    public void loadContacts(boolean dialogeShow) {
        if (SimpleHttpConnection.isNetworkAvailable(context)) {
            Bundle bundle = new Bundle();
            bundle.putString("showHtml", "no");
            bundle.putString("mode", "");
            bundle.putString("user_id", mainPreferences.getString(Constant.id_user, ""));
            bundle.putString("page", "1");
            new AsyncTaskApp(this, context, Urls.VIEW_DEVICE_LIST, "LOGIN", dialogeShow).execute(bundle);
        } else {
            GlobalMethod.showToast(context, Constant.network_error);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanLeDevice(false);
//                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

//            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
//            listScannedDevice.onRefreshComplete();
            try {
                GlobalMethod.write("BLE DEVICES : " + mLeDeviceList);
                if (total_devices.size() > currentPos) {
                    if (mLeDeviceList.containsKey(total_devices.get(currentPos).getString("unique_name").trim())) {
                        Landing_Activity.getInstance().makeConnection(mLeDeviceList.get(total_devices.get(currentPos).getString("unique_name")));
                    }
                    currentPos++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }


    }

    private HashMap<String, String> mLeDeviceList = new HashMap<>();

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    System.out.println("====device" + device + "====rssi" + rssi + "====scanRecord" + scanRecord);
                    printScanRecord(scanRecord);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (!mLeDeviceList.containsKey(device.getName()))
                                mLeDeviceList.put(device.getName(), device.getAddress());
                            if (device.getAddress().equalsIgnoreCase(macAddress)) {
                                scanLeDevice(false);
                            }

//                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });

         /*   ParcelUuid[] myUUid =device.getUuids();
            for(ParcelUuid a :myUUid){
                Log.d("UUID", a.getUuid().toString());
            }*/
                }
            };

    public void printScanRecord(byte[] scanRecord) {

        // Simply print all raw bytes
        try {
            String decodedRecord = new String(scanRecord, "UTF-8");
//            Log.d("DEBUG", "decoded String : " + ByteArrayToString(scanRecord));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Parse data bytes into individual records
        List<DeviceScanActivity.AdRecord> records = DeviceScanActivity.AdRecord.parseScanRecord(scanRecord);


        // Print individual records
        if (records.size() == 0) {
            Log.i("DEBUG", "Scan Record Empty");
        } else {
            Log.i("DEBUG", "Scan Record: " + TextUtils.join(",", records));
        }

    }

    private void makeConnection(final String mDeviceAddress) {
        GlobalMethod.write("MAKE CONNECTION : ADDRESS : " + mDeviceAddress);
        ServiceConnection mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                GlobalMethod.write("CONNECTED : NOT : " + mBluetoothLeService);
                mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                if (!mBluetoothLeService.initialize()) {
                    Log.e("DEVICESCANACTIVITY", "Unable to initialize Bluetooth");
//                    finish();
                }
                // Automatically connects to the device upon successful start-up initialization.
                GlobalMethod.write("CONNECTED : " + mBluetoothLeService);
                GlobalMethod.write("CONNECTED : " + mBluetoothLeService.connect(mDeviceAddress));
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBluetoothLeService = null;
            }
        };


        //        mBluetoothLeService.setCallback(this);
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        mBluetoothLeService.onBind(gattServiceIntent);
        //        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
}
