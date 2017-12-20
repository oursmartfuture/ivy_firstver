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
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.globalclasses.GlobalMethod;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ivy.R;
import com.ivymanagedevice.AddDevice;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends Activity {
    private static final int ADD_DEVICE = 1111;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    ImageButton backBtn;
    Button enableButton;
    ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 20000;
    //    private BroadcastReceiver broadcastReceiver;
    BluetoothLeService mBluetoothLeService = BluetoothLeService.getInstance(DeviceScanActivity.this);
    PullToRefreshListView listScannedDevice;
    private LinearLayout llNoRecordsFound;
    private LinearLayout enableBluetooth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_devices);
//        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        enableBluetooth = (LinearLayout) findViewById(R.id.enableBluetooth);

        llNoRecordsFound = (LinearLayout) findViewById(R.id.llNoRecordsFound);
        listScannedDevice = (PullToRefreshListView) findViewById(R.id.listScannedDevices);
        listScannedDevice.getRefreshableView().setDividerHeight(1);
        listScannedDevice.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                scanLeDevice(true);
            }
        });
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvHeader = (TextView) findViewById(R.id.headerText);
        tvHeader.setText(R.string.scan_header);
        GlobalMethod.AcaslonProSemiBoldTextView(DeviceScanActivity.this, tvHeader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    GlobalMethod.write("BLUETOOTH OFF");
                    TextView tvHeader = (TextView) findViewById(R.id.headerText);
                    tvHeader.setText(R.string.enable_device);

                    listScannedDevice.setVisibility(View.GONE);
                    enableBluetooth.setVisibility(View.VISIBLE);
                    Button enable = (Button) findViewById(R.id.enable);
                    enable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            if (!mBluetoothAdapter.isEnabled()) {
                                mBluetoothAdapter.enable();
                            }
                        }
                    });
                } else {
                    GlobalMethod.write("BLUETOOTH ON");
                    onResume();
//                    scanLeDevice(true);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        if (mBluetoothAdapter.isEnabled()) {

            TextView tvHeader = (TextView) findViewById(R.id.headerText);
            tvHeader.setText(R.string.scan_header);

            listScannedDevice.setVisibility(View.VISIBLE);
            enableBluetooth.setVisibility(View.GONE);
        }
        if (!mBluetoothAdapter.isEnabled()) {
            TextView tvHeader = (TextView) findViewById(R.id.headerText);
            tvHeader.setText(R.string.enable_device);

            listScannedDevice.setVisibility(View.GONE);
            enableBluetooth.setVisibility(View.VISIBLE);
            Button enable = (Button) findViewById(R.id.enable);
            enable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    if (!mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.enable();
                        onResume();
                    }
                }
            });
        }

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
//        if (!mBluetoothAdapter.isEnabled()) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listScannedDevice.setAdapter(mLeDeviceListAdapter);
        listScannedDevice.getRefreshableView().setEmptyView(llNoRecordsFound);
        listScannedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("====deviceinfoafterclick" + mLeDeviceListAdapter.getDevice(position - 1));
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position - 1);
                if (device == null) return;
                Intent intent = new Intent(DeviceScanActivity.this, AddDevice.class);
                intent.putExtra("mac_address", device.getAddress());
                intent.putExtra("device_name", device.getName());
                intent.putExtra("pending_status", "Yes");
//        startActivityForResult(intent, ADD_DEVICE);
//        final Intent intent = new Intent(this, DeviceControlActivity.class);
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                if (mScanning) {
                    scanLeDevice(false);
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    mScanning = false;
                }
                startActivityForResult(intent, ADD_DEVICE);
//                finish();
            }
        });
        scanLeDevice(true);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if (mBluetoothLeService != null) {
//            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//            Log.d(TAG, "Connect request result=" + result);
//        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            openExitDialog(intent.getStringExtra("text"));
        }
    };


    public void openExitDialog(String text) {
        final Dialog dialog = new Dialog(DeviceScanActivity.this);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext;
        dialog.setTitle("Strontium DEMO");
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        oktext = (TextView) dialog.findViewById(R.id.oktext);
        if (text != null) {
            alerttextlogout.setText(text);
        } else {
            alerttextlogout.setText("Tap Detected");
        }
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else if (requestCode == ADD_DEVICE && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("added", "Yes");
            setResult(RESULT_OK, intent);
            finish();
            ;
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
        unregisterReceiver(mGattUpdateReceiver);
        unregisterReceiver(mBluetoothReceiver);
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        System.out.println("====deviceinfoafterclick" + mLeDeviceListAdapter.getDevice(position));
//        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
//        if (device == null) return;
//        Intent intent = new Intent(this, AddDevice.class);
//        intent.putExtra("mac_address", device.getAddress());
//        intent.putExtra("device_name", device.getName());
//        intent.putExtra("pending_status", "Yes");
////        startActivityForResult(intent, ADD_DEVICE);
////        final Intent intent = new Intent(this, DeviceControlActivity.class);
////        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
////        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//        if (mScanning) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mScanning = false;
//        }
//        startActivity(intent);
//    }

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

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            listScannedDevice.onRefreshComplete();
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(BluetoothLeService.NOTIFY));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        //        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;


        public LeDeviceListAdapter() {
            super();
//            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.btnConnect = (Button) view.findViewById(R.id.btnConnect);
                viewHolder.btnConnect.setVisibility(View.GONE);
                viewHolder.loader = (ImageView) view.findViewById(R.id.loader);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.deviceName.setText(mLeDevices.get(i).getName());
//            viewHolder.deviceAddress.setText(mLeDevices.get(i).getAddress());

            viewHolder.loader.setVisibility(View.INVISIBLE);
            if (list.contains(mLeDevices.get(i).getAddress())) {
                viewHolder.btnConnect.setText(R.string.already_connected);
            } else {
                viewHolder.btnConnect.setTag(mLeDevices.get(i).getAddress());
                viewHolder.btnConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView img = (ImageView) ((View) v.getParent()).findViewById(R.id.loader);
                        img.setVisibility(View.VISIBLE);
                        Glide.with(DeviceScanActivity.this).load(R.drawable.rolling).asGif().into(img);
                        final String mDeviceAddress = (String) v.getTag();
                        GlobalMethod.write("DEVICE ADDRESS : " + mDeviceAddress);
                        ServiceConnection mServiceConnection = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName componentName, IBinder service) {
                                mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                                if (!mBluetoothLeService.initialize()) {
                                    Log.e("DEVICESCANACTIVITY", "Unable to initialize Bluetooth");
                                    finish();
                                }
                                // Automatically connects to the device upon successful start-up initialization.
                                GlobalMethod.write("CONNECTED : " + mBluetoothLeService.connect(mDeviceAddress));
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName componentName) {
                                mBluetoothLeService = null;
                            }
                        };


                        //        mBluetoothLeService.setCallback(this);
                        Intent gattServiceIntent = new Intent(DeviceScanActivity.this, BluetoothLeService.class);
                        mBluetoothLeService.onBind(gattServiceIntent);
                        //        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                    }
                });
            }
            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    System.out.println("====device" + device + "====rssi" + rssi + "====scanRecord" + scanRecord);
                    printScanRecord(scanRecord);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (device != null && device.getName() != null && device.getName().trim().startsWith("IVY")) {
                                mLeDeviceListAdapter.addDevice(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
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
            Log.d("DEBUG", "decoded String : " + ByteArrayToString(scanRecord));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Parse data bytes into individual records
        List<AdRecord> records = AdRecord.parseScanRecord(scanRecord);


        // Print individual records
        if (records.size() == 0) {
            Log.i("DEBUG", "Scan Record Empty");
        } else {
            Log.i("DEBUG", "Scan Record: " + TextUtils.join(",", records));
        }

    }


    public static String ByteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");

        return hex.toString();
    }


    public static class AdRecord {

        public AdRecord(int length, int type, byte[] data) {
            String decodedRecord = "";
            try {
                decodedRecord = new String(data, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Log.d("DEBUG", "Length: " + length + " Type : " + type + " Data : " + ByteArrayToString(data));
        }

        // ...

        public static List<AdRecord> parseScanRecord(byte[] scanRecord) {
            List<AdRecord> records = new ArrayList<AdRecord>();

            int index = 0;
            while (index < scanRecord.length) {
                int length = scanRecord[index++];
                //Done once we run out of records
                if (length == 0) break;
                int type = scanRecord[index];
                //Done if our record isn't a valid type
                if (type == 0) break;
                byte[] data = Arrays.copyOfRange(scanRecord, index + 1, index + length);
                records.add(new AdRecord(length, type, data));
                GlobalMethod.write("recorslist+====++++" + records.toString());
                //Advance
                index += length;
            }

            return records;
        }

        // ...
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        public Button btnConnect;
        public ImageView loader;
    }

    private ArrayList<String> list = new ArrayList<>();

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String deviceAddress = intent.getStringExtra("address");
            Log.e("RECIEVER", action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                updateConnectionState(R.string.connected);
                if (!list.contains(deviceAddress)) {
                    list.add(deviceAddress);
                    refreshList();
                }
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                GlobalMethod.write("++++actiondisconnected+++");
                if (list.contains(deviceAddress)) {
                    list.remove(deviceAddress);
                    refreshList();
                }
//                mConnected = false;
//                updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();
//                clearUI();
            }
//            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                // Show all the supported services and characteristics on the user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());
//            }
//            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                if (intent.getBooleanExtra("isNotify", false) && isNotify)
//                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                else if (!intent.getBooleanExtra("isNotify", false) && !isNotify)
//                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//            }
        }
    };

    private void refreshList() {
        Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(list);
        list = new ArrayList<String>(set);
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listScannedDevice.setAdapter(mLeDeviceListAdapter);
        listScannedDevice.getRefreshableView().setEmptyView(llNoRecordsFound);
    }

//    private void updateConnectionState(final int resourceId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mConnectionState.setText(resourceId);
//            }
//        });
//    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mBluetoothLeService != null) {
//            mBluetoothLeService.disconnect();
//        }
    }
}