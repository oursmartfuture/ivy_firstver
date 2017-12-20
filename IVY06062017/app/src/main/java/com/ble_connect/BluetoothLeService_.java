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

import android.app.Activity;
import android.app.AlertDialog;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.globalclasses.GlobalMethod;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService_ extends Service {
    private final static String TAG = BluetoothLeService_.class.getSimpleName();
    private static Handler handler;
    //    private DeviceControlActivity receiveNotificationCallback;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final UUID ALERT_LEVEL_CHAR = UUID.fromString("00002A06-0000-1000-8000-00805f9b34fb");
    private static final UUID IMMEDIATE_ALERT_SERVICE = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    protected static final UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int STATE_CONNECTING = 1;
    private static final int ALERT_LEVEL_CHARACTERISTIC_FORMATTYPE = 17;
    private static final int ALERT_LEVEL_CHARACTERISTIC_OFFSET = 0;
    private static final int ALERT_LEVEL_CHARACTERISTIC_VALUE = 2;
    public final static String EXTRA_DATA_RAW = "com.example.bluetooth.le.EXTRA_DATA_RAW";
    private static final int STATE_CONNECTED = 2;
    private static final UUID Battery_Service_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");// UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    private static final UUID Battery_Level_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");// UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
    BluetoothGattCharacteristic batteryLevel;
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

    BluetoothLeService_(Activity receiveNotificationCallback) {
        this();
//        this.receiveNotificationCallback = receiveNotificationCallback;
    }

    public BluetoothLeService_() {
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

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            System.out.println("VALUE READ : " + descriptor.getValue());
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
//                Intent in=new Intent(intentAction);
                broadcastUpdate(intentAction, gatt.getDevice().getAddress());
//                getbattery();
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                close();
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction, gatt.getDevice().getAddress());
                connect(mBluetoothDeviceAddress);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, "");
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

      /*  public void setupGattServer()
        {
            Object obj = (BluetoothManager)getSystemService("bluetooth");
            Object obj1 = new BluetoothGattServerCallback() {

                final BluetoothLeService this$0;

                public void onCharacteristicReadRequest(BluetoothDevice bluetoothdevice, int i, int j, BluetoothGattCharacteristic bluetoothgattcharacteristic1)
                {
                    Log.i("BLEMainActivity:BluetoothGattServerCallback:onCharacteristicReadRequest", (new StringBuilder("device : ")).append(bluetoothdevice.getAddress()).append(" request = ").append(i).append(" offset = ").append(j).append(" characteristic = ").append(bluetoothgattcharacteristic1.getUuid()).toString());
                }

                public void onCharacteristicWriteRequest(BluetoothDevice bluetoothdevice, int i, BluetoothGattCharacteristic bluetoothgattcharacteristic1, boolean flag, boolean flag1, int j, byte abyte0[])
                {
                    super.onCharacteristicWriteRequest(bluetoothdevice, i, bluetoothgattcharacteristic1, flag, flag1, j, abyte0);
                    Log.i("BLEMainActivity:BluetoothGattServerCallback:onCharacteristicWriteRequest", (new StringBuilder("device : ")).append(bluetoothdevice.getAddress()).append(" characteristic : ").append(bluetoothgattcharacteristic1.getUuid()).append("Value = ").append(new String(abyte0)).toString());
//                    updateText(new String(abyte0));
//                    updateAllText(new String(abyte0));
                }

                public void onConnectionStateChange(BluetoothDevice bluetoothdevice, int i, int j)
                {
                    Log.i("BLEMainActivity:BluetoothGattServerCallback:onConnectionStateChange", (new StringBuilder("device : ")).append(bluetoothdevice).append(" status : ").append(i).append(" new state : ").append(j).toString());
                }

                public void onDescriptorReadRequest(BluetoothDevice bluetoothdevice, int i, int j, BluetoothGattDescriptor bluetoothgattdescriptor)
                {
                    Log.i("BLEMainActivity:BluetoothGattServerCallback:onDescriptorReadRequest", (new StringBuilder("device : ")).append(bluetoothdevice.getAddress()).append(" request = ").append(i).append(" offset = ").append(j).append(" descriptor = ").append(bluetoothgattdescriptor.getUuid()).toString());
                }

                public void onDescriptorWriteRequest(BluetoothDevice bluetoothdevice, int i, BluetoothGattDescriptor bluetoothgattdescriptor, boolean flag, boolean flag1, int j, byte abyte0[])
                {
                    Log.i("BLEMainActivity:BluetoothGattServerCallback:onDescriptorWriteRequest", (new StringBuilder("device : ")).append(bluetoothdevice.getAddress()).append(" \n descriptor : ").append(bluetoothgattdescriptor.getUuid()).toString());
                }

                public void onExecuteWrite(BluetoothDevice bluetoothdevice, int i, boolean flag)
                {
                    Log.i("BLEMainActivity:BluetoothGattServerCallback:onExecuteWrite", (new StringBuilder("device : ")).append(bluetoothdevice.getAddress()).append(" request = ").append(i).append(" execute = ").append(true).toString());
                }

                public void onServiceAdded(int i, BluetoothGattService bluetoothgattservice)
                {
                    Log.i("BLEMainActivity:BluetoothGattServerCallback:onServiceAdded", (new StringBuilder("service : ")).append(bluetoothgattservice.getUuid()).append(" status = ").append(i).toString());
                }


                {
                    this$0 = BluetoothLeService.this;

                }
            };
            obj = ((BluetoothManager) (obj)).openGattServer(getApplicationContext(), ((BluetoothGattServerCallback) (obj1)));
            obj1 = new BluetoothGattService(IMMEDIATE_ALERT_SERVICE, 0);
            BluetoothGattCharacteristic bluetoothgattcharacteristic = new BluetoothGattCharacteristic(ALERT_LEVEL_CHAR, 4, 16);
            bluetoothgattcharacteristic.setValue(2, 17, 0);
            ((BluetoothGattService) (obj1)).addCharacteristic(bluetoothgattcharacteristic);
            ((BluetoothGattServer) (obj)).addService(((BluetoothGattService) (obj1)));
            Log.i("BLEMainActivity:setupGattServer", (new StringBuilder("Gatt server setup complete : ")).append(obj.toString()).toString());
        }*/

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.i(TAG, "VALUE ON CHARACTERISTIC CHANGE : " + characteristic.getValue());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "VALUE ON CHARACTERISITC READ : " + characteristic.getValue());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

    };


    private void broadcastUpdate(final String action, String address) {
        final Intent intent = new Intent(action);
        if (!TextUtils.isEmpty(address)) {
            intent.putExtra("address", address);
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
        BluetoothLeService_ getService() {
            return BluetoothLeService_.this;
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
        close();
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

        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
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
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        setupGattServer();
        return true;
    }

    public void setupGattServer() {
        gattServer = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).openGattServer(getApplicationContext(), new C00609());
        BluetoothGattService service = new BluetoothGattService(IMMEDIATE_ALERT_SERVICE, ALERT_LEVEL_CHARACTERISTIC_OFFSET);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(ALERT_LEVEL_CHAR, 4, 16);
//        characteristic.setValue(new byte[]{0x02});
//        ALERT_LEVEL_CHARACTERISTIC_VALUE  , ALERT_LEVEL_CHARACTERISTIC_FORMATTYPE, ALERT_LEVEL_CHARACTERISTIC_OFFSET);
        service.addCharacteristic(characteristic);
        gattServer.addService(service);
//        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(ALERT_LEVEL_CHAR, 4, 16);
//        gattServer.sendResponse()
//        if (gattServer != null)
        Log.i("setupGattServer", "Gatt server setup complete : " + gattServer.toString());
    }

    class C00609 extends BluetoothGattServerCallback {
//        C00609() {
//            if (Looper.myLooper() == null) {
//                Looper.prepare();
//            }
//        }


        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.i("nConnectionStateChange", "device : " + device + " status : " + status + " new state : " + newState);
//            if (newState == 2) {
//                readCustomServerCharacteristic();
//            }
        }

        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.i("ServiceAdded", "service : " + service.getUuid() + " status = " + status);
        }

        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.i("onCharacterReadRequest", "device : " + device.getAddress() + " request = " + requestId + " offset = " + offset + " characteristic = " + characteristic.getUuid() + ", Value : " + characteristic.getValue());
        }

        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.i("onCharacterWriteRequest", "device : " + device.getAddress() + " characteristic : " + characteristic.getUuid() + "Value = " + new String(value));
            Log.i(TAG, "DEVICE VALUE : " + byteArrayToString(value));
            Intent i = new Intent(NOTIFY);
            i.putExtra("text", byteArrayToString(value) + " taps on " + device.getName());
            sendBroadcast(i);
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
            Log.i("escriptorReadRequest", "device : " + device.getAddress() + " request = " + requestId + " offset = " + offset + " descriptor = " + descriptor.getUuid());
        }

        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.i("nDescriptorWriteRequest", "device : " + device.getAddress() + " \n descriptor : " + descriptor.getUuid());
        }

        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
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

    protected void showAlert(String string) {
        try {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("MESSAGE FROM BLE DEVICE");
            alertBuilder.setMessage(string).setCancelable(false).setPositiveButton("CLOSE", new C00587());
            alertBuilder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        GlobalMethod.write("value+++" + characteristic.getService().getUuid().toString());
        GlobalMethod.write("value+++bat" + characteristic.getValue());
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        // This is specific to Heart Rate Measurement.
        if (IMMEDIATE_ALERT_SERVICE.equals(characteristic.getService().getUuid())) {
            GlobalMethod.write("in immediate alert service++++");
//            mBluetoothGatt.setCharacteristicNotification(characteristic, true);

//            UUID uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//            readCharacteristic(characteristic);
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            characteristic.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//            readCustomCharacteristic();
//            readCustomServerCharacteristic();
        }/* else {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }*/
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

    public void getbattery() {

        BluetoothGattService batteryService = mBluetoothGatt.getService(Battery_Service_UUID);
        if (batteryService == null) {
            Log.d(TAG, "Battery service not found!");
            return;
        }

        batteryLevel = batteryService.getCharacteristic(Battery_Level_UUID);
        if (batteryLevel == null) {
            Log.d(TAG, "Battery level not found!");
            return;
        }
        mBluetoothGatt.readCharacteristic(batteryLevel);
        Log.v(TAG, "batteryLevel = " + mBluetoothGatt.readCharacteristic(batteryLevel));
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public static String byteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");

        return hex.toString();
    }

    public void readCustomCharacteristic() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService mCustomService = mBluetoothGatt.getService(IMMEDIATE_ALERT_SERVICE);
        if (mCustomService == null) {
            Log.w(TAG, "Custom BLE Service not found");
            return;
        }
        /*get the read characteristic from the service*/
        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(ALERT_LEVEL_CHAR);
        Log.i(TAG, "PERMISSIONS : " + mReadCharacteristic.getPermissions());
        Log.i(TAG, "PROPERTIES : " + mReadCharacteristic.getProperties());
        if (mBluetoothGatt.readCharacteristic(mReadCharacteristic) == false) {
            Log.w(TAG, "Failed to read characteristic");
        } else {
            Log.i(TAG, "Successfully registered read characteristic");
        }
    }

    public void readCustomServerCharacteristic() {
        if (mBluetoothAdapter == null || gattServer == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService mCustomService = gattServer.getService(IMMEDIATE_ALERT_SERVICE);
        if (mCustomService == null) {
            Log.w(TAG, "Custom BLE Service not found");
            return;
        }
        /*get the read characteristic from the service*/
        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(ALERT_LEVEL_CHAR);

        if (mReadCharacteristic != null) {
            Log.i(TAG, "PERMISSIONS : " + mReadCharacteristic.getPermissions());
            Log.i(TAG, "PROPERTIES : " + mReadCharacteristic.getProperties());
            Log.i(TAG, "VALUE : " + byteArrayToString(mReadCharacteristic.getValue()));
            if (mBluetoothGatt.writeCharacteristic(mReadCharacteristic) == false) {
                Log.w(TAG, "Failed to write characteristic");
            } else {
                Log.i(TAG, "Successfully registered write characteristic");
            }
        } else {
            Log.i(TAG, "mWriteCharacteristic NULL");
        }
    }
}
