/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ble_connect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.globalclasses.GlobalMethod;
import com.ivy.Landing_Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    public static final String SIGNAL_STRENGTH = "SIGNAL_STRENGTH";
    public static final String BATTERY_LEVEL_INTENT = "BATTERY_LEVEL_INTENT";
    private static Handler handler;
    private Handler mHandler;
    static HashMap<String, BluetoothGatt> hMapGatt = new HashMap<>();
    static HashMap<String, BluetoothGattCallback> hMapGattGattCallback = new HashMap<>();
//    HashMap<String, BluetoothGattServerCallback> hMapGattServerCallback = new HashMap<>();
//    HashMap<String, BluetoothGattServer> hMapGattServer = new HashMap<>();

    //    private DeviceControlActivity receiveNotificationCallback;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    ArrayList<Double> doubleArrayList = new ArrayList<>(20);


    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;

    protected static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int STATE_CONNECTING = 1;
    private static final int ALERT_LEVEL_CHARACTERISTIC_FORMATTYPE = 17;
    private static final int ALERT_LEVEL_CHARACTERISTIC_OFFSET = 0;
    private static final int ALERT_LEVEL_CHARACTERISTIC_VALUE = 2;
    public final static String EXTRA_DATA_RAW = "com.example.bluetooth.le.EXTRA_DATA_RAW";
    private static final int STATE_CONNECTED = 2;
    private static final UUID Battery_Service_UUID =
            UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");//
    //            UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    private static final UUID Battery_Level_UUID =
            UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");//
    //            UUID.fromString("00002A07-0000-1000-8000-00805f9b34fb");
    BluetoothGattCharacteristic batteryLevel;
    private static final UUID ALERT_LEVEL_CHAR = UUID.fromString("00002A06-0000-1000-8000-00805f9b34fb");
    private static final UUID IMMEDIATE_ALERT_SERVICE = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String EXTRA_UUID_CHAR = "com.example.bluetooth.le.EXTRA_UUID_CHAR";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    private boolean received;
    public static String NOTIFY = "com.example.android.bluetoothlegatt";
    private BluetoothGattServer gattServer;
    static Activity context;
    static BluetoothLeService mLeServiceInstance;
    private Timer signalTimer;

    BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    GlobalMethod.write("LANDING ACTIVITY BLUETOOTH OFF");
                    if (gattServer != null) {
                        gattServer.close();
                    }
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    GlobalMethod.write("LANDING ACTIVITY BLUETOOTH ON");
                    initialize();
//                    setupGattServer("");

                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GlobalMethod.write("BluetoothLeService onStartCommand");
//        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startForeground(1, new Notification());
    }

    @Override
    public void onDestroy() {
//        if()
//        GlobalMethod.write("onDestroy : "+);
        try {
            unregisterReceiver(mBluetoothReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        stopForeground(true);
        super.onDestroy();
    }

    public static BluetoothLeService getInstance(Activity activity) {
        context = activity;
        if (mLeServiceInstance != null) {
            return mLeServiceInstance;
        }
        return mLeServiceInstance = new BluetoothLeService(activity);
    }

    public BluetoothLeService(Activity receiveNotificationCallback) {
        this();
        context = receiveNotificationCallback;
        mHandler = new Handler();
//        this.receiveNotificationCallback = receiveNotificationCallback;
    }

    public BluetoothLeService() {
    }

//    public void setCallback(DeviceControlActivity receiveNotificationCallback) {
//        this.receiveNotificationCallback = receiveNotificationCallback;
////        if (received)
////            receiveNotificationCallback.onReceive();
//    }

    static class C00611 extends Handler {
        C00611() {
        }

        public void handleMessage(Message msg) {
            Log.e("handleMessage", "DATA FROM BLE :: " + msg.getData().getString("myKey"));
        }
    }

    private int device_distance;
    private long BATTERY_STATUS_INTERVAL = 5 * 1000;
    private Timer tTask;
    private Runnable mRunnable;
    private BleConnectionService bleConnectionService;
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.

    private BluetoothGattCallback getCallback() {

        BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                System.out.println("VALUE READ : " + descriptor.getValue());
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
                String intentAction;
                if (newState == BluetoothProfile.STATE_CONNECTED) {

                    hMapGatt.put(gatt.getDevice().getAddress(), gatt);
                    gatt.readRemoteRssi();
                    scheduleRemoteRssiStrength();
                    intentAction = ACTION_GATT_CONNECTED;
                    mConnectionState = STATE_CONNECTED;
//                Intent in=new Intent(intentAction);
                    broadcastUpdate(intentAction, gatt.getDevice().getAddress(), gatt.getDevice().getName());
//                getbattery();
                    Log.i(TAG, "Connected to GATT server.");
                    // Attempts to discover services after successful connection.
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Attempting to start service discovery:" +
                                    gatt.discoverServices());
                        }
                    });

                    if (mHandler != null && mRunnable != null) {
                        mHandler.removeCallbacks(mRunnable);
                    }

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    close(gatt);
                    if (hMapGatt.containsKey(gatt.getDevice().getAddress())) {
                        hMapGatt.remove(gatt.getDevice().getAddress());
                    }
                    intentAction = ACTION_GATT_DISCONNECTED;
                    mConnectionState = STATE_DISCONNECTED;
                    Log.i(TAG, "Disconnected from GATT server.");
                    broadcastUpdate(intentAction, gatt.getDevice().getAddress(), gatt.getDevice().getName());
                    if (Landing_Activity.total_devices.toString().contains(gatt.getDevice().getName().trim())) {
                        connect(gatt.getDevice().getAddress().trim());
                        if (mHandler != null) {
                            mHandler.postDelayed(mRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    // Start Service
                                    if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                                        bleConnectionService = BleConnectionService.getInstance(context, gatt.getDevice().getAddress());

                                        ServiceConnection mServiceConnection = new ServiceConnection() {
                                            @Override
                                            public void onServiceConnected(ComponentName componentName, IBinder service) {
                                                GlobalMethod.write("CONNECTED : onServiceConnected bleConnectionService : " + bleConnectionService);
                                                bleConnectionService = ((BleConnectionService.LocalBinder) service).getService();
                                                if (!bleConnectionService.initialize()) {
                                                    Log.e("DEVICESCANACTIVITY", "Unable to initialize Bluetooth");
//                                    finish();
                                                    if (bleConnectionService.initialize()) {
                                                        bleConnectionService.loadContacts(false);
                                                    }
                                                }
                                                // Automatically connects to the device upon successful start-up initialization.
//                                GlobalMethod.write("CONNECTED : " + mBluetoothLeService);
//                                GlobalMethod.write("CONNECTED : " + mBluetoothLeService.connect(mDeviceAddress));

                                            }

                                            @Override
                                            public void onServiceDisconnected(ComponentName componentName) {
                                                bleConnectionService = null;
                                            }
                                        };


                                        //        mBluetoothLeService.setCallback(this);
                                        Intent gattServiceIntent = new Intent(context, BleConnectionService.class);
                                        bleConnectionService.onBind(gattServiceIntent);
                                        //        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                                        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


//                        startService(new Intent(context, BleConnectionService.getInstance(context, gatt.getDevice().getAddress())));
                                    }
                                }
                            }, 30 * 1000);
                        }
                    }
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                GlobalMethod.write("SERVICE WRITE : " + characteristic.getUuid());
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, "", gatt.getDevice().getName());
                    for (BluetoothGattService bgService :
                            gatt.getServices()) {
                        GlobalMethod.write("SERVICE DISCOVERED : " + bgService.getUuid());
                        for (BluetoothGattCharacteristic mGattCharacteristics : bgService.getCharacteristics()) {
                            GlobalMethod.write("SERVICE DISCOVERED : CHAR : " + mGattCharacteristics.getUuid());
//                        if (mGattCharacteristics != null) {
//                            final BluetoothGattCharacteristic characteristic =
//                                    mGattCharacteristics;
//                            final int charaProp = characteristic.getProperties();
//                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                                // If there is an active notification on a characteristic, clear
//                                // it first so it doesn't update the data field on the user interface.
////                                if (mNotifyCharacteristic != null) {
//                                setCharacteristicNotification(
//                                        characteristic, false);
//                                GlobalMethod.write("SERVICE DISCOVERED : CHAR : READ : " + mGattCharacteristics.getUuid());
////                                    mNotifyCharacteristic = null;
////                                }
//                                mBluetoothGatt.readCharacteristic(characteristic);
//                            }
//                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
////                                mNotifyCharacteristic = characteristic;
//                                setCharacteristicNotification(
//                                        characteristic, true);
//                                GlobalMethod.write("SERVICE DISCOVERED : CHAR : NOTIFY : " + mGattCharacteristics.getUuid());
//                            }
////                            return true;
//                        }
                        }

                    }

//                Handler handler=new Handler();
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
                    getbattery(gatt);
                    tTask = null;
//                    }
//                });

                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }


            @Override
            public void onReadRemoteRssi(final BluetoothGatt gatt, int rssi, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
//                GlobalMethod.write("rsssiivalue-----" + String.format("BluetoothGatt ReadRssi[%d]", rssi));
//                device_distance = (int) calculateAccuracy(-67, rssi);
//                    getbattery(gatt);
                    final String address = gatt.getDevice().getAddress().trim();
                    doubleArrayList.add(0, calculateAccuracy(-67, rssi));
                    double sum = 0;
                    int size = (doubleArrayList.size() > 20 ? 20 : doubleArrayList.size());
                    for (int i = 0; i < size; i++) {
                        sum += doubleArrayList.get(i);
                    }
                    device_distance = (int) (sum / size);

                    Intent intent = new Intent(SIGNAL_STRENGTH);
                    intent.putExtra("distance", device_distance + "");
                    intent.putExtra("mac_address", gatt.getDevice().getAddress());
                    intent.putExtra("name", gatt.getDevice().getName());
                    sendBroadcast(intent);
//                GlobalMethod.write("calculateAccuracy+++" + device_distance);
//                context.runOnUiThread(new Runnable() {
//                    public void run() {

                    if (device_distance >= 6) {
//                    if (context != null) {
//                    NotificationManager mnotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    NotificationCompat.Builder notif = new NotificationCompat.Builder(context);
//                    notif.mContentTitle = getString(R.string.connection_fading);
//                    notif.mContentText = gatt.getDevice().getName() + " is fading.";
//                    notif.setSmallIcon(R.drawable.notifications_icon);
//                    mnotificationManager.notify(0, notif.build());
//                    } else {
//                        GlobalMethod.write("CONTEXT NULL");
//                    }
                    } else {
//                    getbattery();
                    }
//                        if (device_distance <= 1) {
//                            strength_img.setImageResource(R.drawable.full_filled_strength);
//                            strength_img.invalidate();
//                        } else if (device_distance > 1 && device_distance <= 3) {
//                            strength_img.setImageResource(R.drawable.three_filled);
//                            strength_img.invalidate();
//                        } else if (device_distance > 3 && device_distance <= 5) {
//                            strength_img.setImageResource(R.drawable.two_filled);
//                            strength_img.invalidate();
//                        } else if (device_distance > 5 && device_distance <= 8) {
//                            strength_img.setImageResource(R.drawable.one_filled);
//                            strength_img.invalidate();
//                        } else if (device_distance > 8) {
//                            strength_img.setImageResource(R.drawable.empty_strength);
//                            strength_img.invalidate();
//                        }
//                    }
//                });

                }
            }


            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                Log.i(TAG, "VALUE ON CHARACTERISTIC CHANGE : " + characteristic.getValue());
                Log.i(TAG, "VALUE ON CHARACTERISTIC CHANGE : " + byteArrayToString(characteristic.getValue()));
                int battLevel_ = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, ALERT_LEVEL_CHARACTERISTIC_OFFSET).intValue();
                GlobalMethod.write("BATTERY LEVEL OUTSIDE: CHARACTERISTIC CHANGED : " + battLevel_);
                if (Battery_Level_UUID.equals(characteristic.getUuid())) {
                    int battLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, ALERT_LEVEL_CHARACTERISTIC_OFFSET).intValue();
                    GlobalMethod.write("BATTERY LEVEL : CHARACTERISTIC CHANGED : " + battLevel);

                    Intent batteryIntent = new Intent(BATTERY_LEVEL_INTENT);
                    batteryIntent.putExtra("battery", battLevel + "");
                    batteryIntent.putExtra("mac_address", gatt.getDevice().getAddress());
                    batteryIntent.putExtra("name", gatt.getDevice().getName());
                    sendBroadcast(batteryIntent);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.i(TAG, "CHARACTERISITC READ : ");
                GlobalMethod.write("CHARACTERISTIC READ : " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
                GlobalMethod.write("CHARACTERISTIC READ : " + byteArrayToString(characteristic.getValue()));
                GlobalMethod.write("CHARACTERISTIC READ : " + characteristic.getValue());
                GlobalMethod.write("CHARACTERISTIC READ : " + characteristic.getStringValue(ALERT_LEVEL_CHARACTERISTIC_OFFSET));

                if (Battery_Level_UUID.equals(characteristic.getUuid())) {
                    int battLevel = characteristic.getIntValue(ALERT_LEVEL_CHARACTERISTIC_FORMATTYPE, ALERT_LEVEL_CHARACTERISTIC_OFFSET).intValue();
                    GlobalMethod.write("BATTERY LEVEL : " + battLevel);
                    GlobalMethod.write("BATTERY LEVEL : " + characteristic.getDescriptors());
                    GlobalMethod.write("BATTERY LEVEL : " + byteArrayToString(characteristic.getValue()));
                    for (BluetoothGattDescriptor desc : characteristic.getDescriptors()) {
                        GlobalMethod.write("BATTERY LEVEL :." +
                                " DESC : " + desc.getValue());
                    }

//                if (tTask == null) {
//                    tTask = new Timer("battery", false);
//                    tTask.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            getbattery();
//                        }
//                    }, BATTERY_STATUS_INTERVAL, BATTERY_STATUS_INTERVAL);
//                }
//                getbattery();

                    Intent batteryIntent = new Intent(BATTERY_LEVEL_INTENT);
                    batteryIntent.putExtra("battery", battLevel + "");
                    batteryIntent.putExtra("mac_address", gatt.getDevice().getAddress());
                    batteryIntent.putExtra("name", gatt.getDevice().getName());
                    sendBroadcast(batteryIntent);
                }
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                }
            }


        };

        return mGattCallback;
    }

    private void scheduleRemoteRssiStrength() {
        try {
            if (signalTimer != null) {
                signalTimer.cancel();
            }

            signalTimer = new Timer();
            signalTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for (String key : hMapGatt.keySet()) {
                        BluetoothGatt gatt = hMapGatt.get(key);
                        if (gatt != null) {
                            try {
                                gatt.readRemoteRssi();
                            } catch (Exception e) {
                                if (e instanceof DeadObjectException) {
                                    hMapGatt.remove(key);
                                }
                            }
                        }
                    }
//                    if (hMapGatt.get(address) != null)
//                        hMapGatt.get(address).readRemoteRssi();
//                                gatt.readRemoteRssi();
                }
            }, 1000, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void broadcastUpdate(final String action, String address, String name) {
        final Intent intent = new Intent(action);
        if (!TextUtils.isEmpty(address)) {
            intent.putExtra("address", address);
        }
        if (!TextUtils.isEmpty(name)) {
            intent.putExtra("name", name);
        }
        sendBroadcast(intent);
    }

    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        GlobalMethod.write("++=+in_broadcast update+=+==");
        Intent intent = new Intent(action);
        byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            StringBuilder stringBuilder = new StringBuilder(data.length);
            int length = data.length;
            for (int i = 0; i < length; i += STATE_CONNECTING) {
                Object[] objArr = new Object[STATE_CONNECTING];
                objArr[0] = Byte.valueOf(data[i]);
                stringBuilder.append(String.format("%02X ", objArr));
            }
            intent.putExtra(EXTRA_DATA, new StringBuilder(String.valueOf(new String(data))).append("\n").append(stringBuilder.toString()).toString());
        }
        sendBroadcast(intent);
    }
 /*   private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
    *//*    if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            if (DeviceControlActivity.mHeartBeat != null) {
                if (DeviceControlActivity.isNotify) {
                    DeviceControlActivity.mHeartBeat_text.setText("Value");
                    DeviceControlActivity.mHeartBeat.setText(heartRate + "");
                }
            }
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
            intent.putExtra("isNotify", true);*//*
//            Toast.makeText(BluetoothLeService.this, "Received heart rate" + heartRate, Toast.LENGTH_SHORT).show();
//        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                Log.v(TAG, "characteristic.getStringValue(0) = " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
            }
//        }
        sendBroadcast(intent);
    }*/

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    public static UUID getBattery_Level_UUID() {
        return Battery_Level_UUID;
    }

    public static UUID getBattery_Service_UUID() {
        return Battery_Service_UUID;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        disconnect("");
//        close(gatt);
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {

        GlobalMethod.write("====CONNECT" + TAG + " " + address);
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }


        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && hMapGatt.get(address) != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            boolean bool;
            if (hMapGatt.get(address).connect()) {
                mConnectionState = STATE_CONNECTING;
                setupGattServer(address);
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if (hMapGattGattCallback.containsKey(address) && hMapGattGattCallback.get(address) != null) {
        } else {
            hMapGattGattCallback.put(address, getCallback());
        }
        Handler handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt = device.connectGatt(BluetoothLeService.this, false, hMapGattGattCallback.get(address));
            }
        });
//        hMapGatt.put(address, mBluetoothGatt);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
//        if (gattServer == null)
        handler.post(new Runnable() {
            @Override
            public void run() {
                setupGattServer(address);
            }
        });
        return true;
    }

    public void setupGattServer(String address) {
//        if (hMapGattServerCallback.containsKey(address) && hMapGattServerCallback.get(address) != null) {
//        } else {
//            hMapGattServerCallback.put(address, new C00609());
//        }
//        if (hMapGattServer.containsKey(address) && hMapGattServer.get(address) != null) {
//        } else {
//        hMapGattServerCallback.get(address)
        gattServer = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).openGattServer(getApplicationContext(), new C00609());
        BluetoothGattService service = new BluetoothGattService(IMMEDIATE_ALERT_SERVICE, ALERT_LEVEL_CHARACTERISTIC_OFFSET);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(ALERT_LEVEL_CHAR, 4, 16);
//        characteristic.setValue(new byte[]{0x02});
//        ALERT_LEVEL_CHARACTERISTIC_VALUE  , ALERT_LEVEL_CHARACTERISTIC_FORMATTYPE, ALERT_LEVEL_CHARACTERISTIC_OFFSET);
        service.addCharacteristic(characteristic);
        if (gattServer != null) {
            gattServer.addService(service);
//        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(ALERT_LEVEL_CHAR, 4, 16);
//        gattServer.sendResponse()
//        if (gattServer != null)
            Log.i("setupGattServer", "Gatt server setup complete : " + gattServer.toString());
        }
//            gattServer
//            hMapGattServer.put(address, gattServer);
//        }

    }

    class C00609 extends BluetoothGattServerCallback {
        //        C00609() {
//            if (Looper.myLooper() == null) {
//                Looper.prepare();
//            }
//        }


        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.i("nConnectionStateChange", "device : " + device + " status : " + status + " new state : " + newState);
//            if (newState == 2) {
//                readCustomServerCharacteristic();
//            }
        }

        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            Log.i("ServiceAdded", "service : " + service.getUuid() + " status = " + status);
        }

        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.i("onCharacterReadRequest", "device : " + device.getAddress() + " request = " + requestId + " offset = " + offset + " characteristic = " + characteristic.getUuid() + ", Value : " + characteristic.getValue());
        }

        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.i("onCharacterWriteRequest", "device : " + device.getAddress() + " characteristic : " + characteristic.getUuid() + "Value = " + new String(value));
            Log.i(TAG, "DEVICE VALUE : " + byteArrayToString(value) + " on " + device.getName());
            GlobalMethod.write("DEVICE VALUE : " + byteArrayToString(value) + " on " + device.getName());
            if (hMapGatt != null && hMapGatt.containsKey(device.getAddress())) {

            } else {
                connect(device.getAddress());
            }
//            Intent i = new Intent(NOTIFY);
            Intent i = new Intent("ble.taps.notify");
            i.putExtra("text", byteArrayToString(value));
            i.putExtra("device", device.getName());
            i.putExtra("mac_address", device.getAddress());
//            + " taps on " + device.getName());
            sendBroadcast(i);
            GlobalMethod.write("ACTIVE DEVICE LIST : " + Landing_Activity.activeDevices);
            GlobalMethod.write("ACTIVE DEVICE LIST : DEVICE NAME : " + device.getName());

            if(Landing_Activity.activeDevices.contains(device.getName())) {
                Intent intent = new Intent(SIGNAL_STRENGTH);
                intent.putExtra("distance", device_distance + "");
                intent.putExtra("mac_address", device.getAddress());
                intent.putExtra("name", device.getName());
                sendBroadcast(intent);
            }
//            gattServer.sendResponse(device, requestId, 2, offset, characteristic.getValue());
//            readCustomCharacteristic();
//            readCustomServerCharacteristic();
//            DeviceControlActivity.openExitDialog();
/*
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (receiveNotificationCallback != null) {
                        GlobalMethod.write("+++inonreceive++===");
                        receiveNotificationCallback.onReceive();
                    }

                }
            }, 1000);
            received = true;*/
//            Handler h = new Handler(Looper.getMainLooper());
//            h.post(new Runnable() {
//                public void run() {
//                    GlobalMethod.write("+++inonreceive++===");
//                    receiveNotificationCallback.openExitDialog();
////                    if (receiveNotificationCallback != null) {
////
////                        receiveNotificationCallback.onReceive();
////                    }
//                }
//            });
//            received = true;
//            BluetoothLeService.updateText(new String(value));

        }

        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            Log.i("escriptorReadRequest", "device : " + device.getAddress() + " request = " + requestId + " offset = " + offset + " descriptor = " + descriptor.getUuid());
        }

        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            Log.i("nDescriptorWriteRequest", "device : " + device.getAddress() + " \n descriptor : " + descriptor.getUuid());
        }

        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
            Log.i("ExecuteWrite", "device : " + device.getAddress() + " request = " + requestId + " execute = " + true);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            Log.i(TAG, "NOTIFICATION SENT : " + device.getName() + ", STATUS : " + status);
            super.onNotificationSent(device, status);
        }
    }


    public static void updateText(String msgToSend) {
        GlobalMethod.write("value+=+=" + msgToSend);
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("myKey", msgToSend);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    /* renamed from: com.karson.jayconble.MyService.3 */
    class C00633 extends BluetoothGattServerCallback {
        C00633() {
        }

        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.i("onConnectionStateChange", "device : " + device + " status : " + status + " new state : " + newState);
        }

        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.i("onServiceAdded", "service : " + service.getUuid() + " status = " + status);
        }

        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.i("CharReadRequest", "device : " + device.getAddress() + " request = " + requestId + " offset = " + offset + " characteristic = " + characteristic.getUuid());
        }

        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.i("onCharacWriteRequest", "device : " + " characteristic : " + characteristic.getUuid() + "Value = " + new String(value));
        }

        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            Log.i("onDescriptorReadRequest", "device : " + device.getAddress() + " request = " + requestId + " offset = " + offset + " descriptor = " + descriptor.getUuid());
        }

        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.i("DescriptorWriteRequest", "device : " + device.getAddress() + " \n descriptor : " + descriptor.getUuid());
        }

        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            Log.i("onExecuteWrite", "device : " + device.getAddress() + " request = " + requestId + " execute = " + true);
        }
    }

    static {
        handler = new C00611();
    }

//    protected void showAlert(String string) {
//        try {
//            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//            alertBuilder.setTitle("MESSAGE FROM BLE DEVICE");
//            alertBuilder.setMessage(string).setCancelable(false).setPositiveButton("CLOSE", new C00587());
//            alertBuilder.create().show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /* renamed from: com.karson.jayconble.MainActivity.7 */
    class C00587 implements DialogInterface.OnClickListener {
        C00587() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            dialog.dismiss();
        }
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect(String address) {

//        if (mBleNotificationReceiver != null) {
//            unregisterReceiver(mBleNotificationReceiver);
//        }
        if (address == null || TextUtils.isEmpty(address)) {
            for (String key : hMapGatt.keySet()) {
                final BluetoothGatt gatt = hMapGatt.get(key);
                if (gatt != null) {
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            gatt.disconnect();
                        }
                    });
                    hMapGatt.remove(key);


                    close(gatt);
//                        }
//                    }, 500);
                }
            }
//            hMapGatt.clear();

//            for (final BluetoothGatt gattServer : hMapGatt.values()) {
//                if (gattServer != null) {
//                    gattServer.close();
//                }
//            }
        } else {

            final BluetoothGatt gatt = hMapGatt.containsKey(address) ? hMapGatt.get(address) : null;
            if (gatt != null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//            mBluetoothGatt.getDevice()
                gatt.disconnect();
//                hMapGatt.remove(address);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        close(gatt);
                    }
                }, 500);
            }

//            final BluetoothGattServer gattServer = hMapGattServer.containsKey(address) ? hMapGattServer.get(address) : null;
//            if (gattServer != null) {
////            Log.w(TAG, "BluetoothAdapter not initialized");
////            return;
////            mBluetoothGatt.getDevice()
//                gatt.disconnect();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        close(gatt);
//                    }
//                }, 500);
//            }
//
//
//            if (gattServer != null) {
//                gattServer.close();
//            }
        }

        if (gattServer != null) {
            GlobalMethod.write("DISCONNECT GATT SERVER ADDRESS : " + address);
//            GlobalMethod.write("DISCONNECT GATT SERVER ADDRESS : " + address + ", CONNECTION STATE  : " + mBluetoothManager.getConnectionState(mBluetoothAdapter.getRemoteDevice(address), BluetoothProfile.GATT_SERVER));
            if (!TextUtils.isEmpty(address)) {
                GlobalMethod.write("DISCONNECT GATT SERVER ADDRESS : CANCEL CONNECTION : " + address);
                BluetoothDevice dev = mBluetoothAdapter.getRemoteDevice(address);
                gattServer.cancelConnection(dev);
//                GlobalMethod.write("DISCONNECT GATT SERVER ADDRESS : CLOSE : " + address + ", CONNECTION STATE  : " + mBluetoothManager.getConnectionState(mBluetoothAdapter.getRemoteDevice(address), BluetoothProfile.GATT_SERVER));
            } else {
                GlobalMethod.write("DISCONNECT GATT SERVER ADDRESS : CLOSE : " + address);
                gattServer.close();
            }
//            stopAdvertising();
        }

        if (tTask != null) {
            tTask.cancel();
        }
        GlobalMethod.stopAlarm(context);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopAdvertising() {
        mBluetoothAdapter.getBluetoothLeAdvertiser().stopAdvertising(new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                GlobalMethod.write("AdvertiseCallback.onStartSuccess");
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                GlobalMethod.write("AdvertiseCallback.onStartFailure");
            }
        });
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     *
     * @param gatt
     */
    public void close(BluetoothGatt gatt) {
        try {
            if (gatt == null) {
                return;
            }
            final BluetoothGatt finalGatt = gatt;
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    finalGatt.close();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        gatt = null;
    }

//    /**
//     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
//     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
//     * callback.
//     *
//     * @param characteristic The characteristic to read from.
//     */
//    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }
//        mBluetoothGatt.readCharacteristic(characteristic);
//    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     * @param gatt
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled, BluetoothGatt gatt) {
        if (mBluetoothAdapter == null || gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        GlobalMethod.write("value+++" + characteristic.getService().getUuid().toString());
        GlobalMethod.write("value+++bat" + characteristic.getValue());
        gatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);

//        // This is specific to Heart Rate Measurement.
//        if (IMMEDIATE_ALERT_SERVICE.equals(characteristic.getService().getUuid())) {
//            GlobalMethod.write("in immediate alert service++++");
////            mBluetoothGatt.setCharacteristicNotification(characteristic, true);
//
////            UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
////            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
////            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
////            mBluetoothGatt.writeDescriptor(descriptor);
////            readCharacteristic(characteristic);
////            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
////            characteristic.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
////            mBluetoothGatt.writeDescriptor(descriptor);
////            readCustomCharacteristic();
////            readCustomServerCharacteristic();
//        }/* else {
//        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        mBluetoothGatt.writeDescriptor(descriptor);
//        }*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    }*/

    public void getbattery(BluetoothGatt gatt) {
        GlobalMethod.write("READ CHAR : " + "GET BATTERY CALLED");
        if (gatt != null) {
            BluetoothGattService batteryService = gatt.getService(Battery_Service_UUID);
            if (batteryService == null) {
                Log.d(TAG, "Battery service not found!");
                GlobalMethod.write("READ CHAR : " + "Battery service not found!");
                return;
            }

            batteryLevel = batteryService.getCharacteristic(Battery_Level_UUID);
            if (batteryLevel == null) {
                Log.d(TAG, "Battery level not found!");
                GlobalMethod.write("READ CHAR : " + "Battery level not found!");
                return;
            }

            setCharacteristicNotification(batteryLevel, true, gatt);

            GlobalMethod.write("READ CHAR : " + gatt.readCharacteristic(batteryLevel));
            Log.v(TAG, "batteryLevel = " + gatt.readCharacteristic(batteryLevel));
        }
    }
//
//    /**
//     * Retrieves a list of supported GATT services on the connected device. This should be
//     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
//     *
//     * @return A {@code List} of supported services.
//     */
//    public List<BluetoothGattService> getSupportedGattServices() {
//        if (mBluetoothGatt == null) return null;
//
//        return mBluetoothGatt.getServices();
//    }

    public static String byteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");

        return hex.toString();
    }

//    public void readCustomCharacteristic() {
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }
//        /*check if the service is available on the device*/
//        BluetoothGattService mCustomService = mBluetoothGatt.getService(IMMEDIATE_ALERT_SERVICE);
//        if (mCustomService == null) {
//            Log.w(TAG, "Custom BLE Service not found");
//            return;
//        }
//        /*get the read characteristic from the service*/
//        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(ALERT_LEVEL_CHAR);
//        Log.i(TAG, "PERMISSIONS : " + mReadCharacteristic.getPermissions());
//        Log.i(TAG, "PROPERTIES : " + mReadCharacteristic.getProperties());
//        if (mBluetoothGatt.readCharacteristic(mReadCharacteristic) == false) {
//            Log.w(TAG, "Failed to read characteristic");
//        } else {
//            Log.i(TAG, "Successfully registered read characteristic");
//        }
//    }
//
//    public void readCustomServerCharacteristic() {
//        if (mBluetoothAdapter == null || gattServer == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }
//        /*check if the service is available on the device*/
//        BluetoothGattService mCustomService = gattServer.getService(IMMEDIATE_ALERT_SERVICE);
//        if (mCustomService == null) {
//            Log.w(TAG, "Custom BLE Service not found");
//            return;
//        }
//        /*get the read characteristic from the service*/
//        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(ALERT_LEVEL_CHAR);
//
//        if (mReadCharacteristic != null) {
//            Log.i(TAG, "PERMISSIONS : " + mReadCharacteristic.getPermissions());
//            Log.i(TAG, "PROPERTIES : " + mReadCharacteristic.getProperties());
//            Log.i(TAG, "VALUE : " + byteArrayToString(mReadCharacteristic.getValue()));
//            if (mBluetoothGatt.writeCharacteristic(mReadCharacteristic) == false) {
//                Log.w(TAG, "Failed to write characteristic");
//            } else {
//                Log.i(TAG, "Successfully registered write characteristic");
//            }
//        } else {
//            Log.i(TAG, "mWriteCharacteristic NULL");
//        }
//    }

    /*
to calculate distance with respect to rssi.
*/
    public double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }
}
