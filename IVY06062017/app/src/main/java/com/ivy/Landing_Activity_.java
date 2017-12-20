package com.ivy;

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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ble_connect.BluetoothLeService;
import com.ble_connect.DeviceScanActivity;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.DatabaseHandler;
import com.globalclasses.GlobalMethod;
import com.globalclasses.SimpleLocation;
import com.globalclasses.Urls;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ivymanagedevice.AddDevice;
import com.ivymanagedevice.DeviceDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.RECORD_AUDIO;

/**
 * Class Name: Landing_Activity.Class
 * Class description: This class is use to show list of device
 * We can perform also delete device, edit device, navigation to AddDevice and Setting Screen.
 * Created by Pradeep Kumar(00181)
 * Created by Singsys-043 on 10/12/2015.
 */
public class Landing_Activity_ extends Activity implements View.OnClickListener, CallBackListenar {
    private static final int REQUEST_ACCESS = 1230;
    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_ENABLE_BT = 11;
    private static final int DEVICE_SCAN = 111;
    private static final int REQUEST_AUDIO = 123;
    private static final int REQUEST_TELEPHONE = 122;
    ImageView settings, add_icon;
    TextView headerText;
    ImageButton backBtn;
    CustomListAdapter customListAdapter;
    ArrayList<JSONObject> total_devices = new ArrayList<JSONObject>();
    PullToRefreshListView total_device;
    private Context context;
    SharedPreferences mainPreferences;
    DatabaseHandler db;
    int ADD_DEVICE = 1001;
    String mac_address = "22:23:4:";
    private TextView no_record;
    boolean is_load_more = false;
    HashMap<String, Integer> pagelist = new HashMap<String, Integer>();
    int pagecount = 1;
    LinearLayout norecord_linear;
    int globaLPosition;
    SimpleLocation mSimpleLocation;
    static BluetoothLeService mBluetoothLeService = new BluetoothLeService();
    int currentPos = 0;
    private Handler mHandler;
    FrameLayout notificationBtn;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    Activity mActivity;

    HashMap<String, String> hDistanceMap = new HashMap<>();
    HashMap<String, TextView> hConnectedDeviceDistance = new HashMap<>();
    HashMap<String, TextView> hConnectedDeviceBattery = new HashMap<>();
    HashMap<String, ImageView> hConnectedDeviceImageView = new HashMap<>();
    private String previousSignal = "";
    private String previousBattery = "";
    private BroadcastReceiver signalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("distance") && intent.hasExtra("mac_address")) {
                hDistanceMap.put(intent.getStringExtra("mac_address").trim(), intent.getStringExtra("distance").trim());
//                customListAdapter.notifyDatasetChanged();

                if (!previousSignal.equals(intent.getStringExtra("distance").trim())) {
                    previousSignal = intent.getStringExtra("distance").trim();
                    GlobalMethod.write("===previousSignal" + previousSignal);

                    if (hConnectedDeviceDistance.containsKey(intent.getStringExtra("mac_address"))) {
                        hConnectedDeviceDistance.get(intent.getStringExtra("mac_address")).setText("Distance: " + intent.getStringExtra("distance"));
                    } else {
                        customListAdapter = new CustomListAdapter(total_devices, context);
                        total_device.setAdapter(customListAdapter);
                    }

                }


            }
        }
    };

    private HashMap<String, String> hBatteryMap = new HashMap<>();

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("battery") && intent.hasExtra("mac_address")) {
                hBatteryMap.put(intent.getStringExtra("mac_address").trim(), intent.getStringExtra("battery").trim());
//                customListAdapter.notifyDatasetChanged();

                if (!previousBattery.equals(intent.getStringExtra("battery").trim())) {
                    previousBattery = intent.getStringExtra("battery").trim();
                    GlobalMethod.write("===previousBattery" + previousBattery);
                    if (hConnectedDeviceBattery.containsKey(intent.getStringExtra("mac_address"))) {
                        hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setText("Battery: " + intent.getStringExtra("battery"));
                    } else {
                        customListAdapter = new CustomListAdapter(total_devices, context);
                        total_device.setAdapter(customListAdapter);
                    }
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_withlistview);
        mActivity = Landing_Activity_.this;
        mHandler = new Handler();
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        notificationBtn = (FrameLayout) findViewById(R.id.notificationBtn);
        notificationBtn.setVisibility(View.VISIBLE);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mainPreferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        settings = (ImageView) findViewById(R.id.settings);
        headerText = (TextView) findViewById(R.id.headerText);
        no_record = (TextView) findViewById(R.id.no_record);
        norecord_linear = (LinearLayout) findViewById(R.id.norecord_linear);
        headerText.setText("Manage Devices");
        context = Landing_Activity_.this;
        Landing_Activity_.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        total_device = (PullToRefreshListView) findViewById(R.id.total_follow_list);
        total_device.getRefreshableView();
//        total_device.getRefreshableView().setDividerHeight(1);
        settings.setOnClickListener(this);
        add_icon = (ImageView) findViewById(R.id.add_icon);
        add_icon.setOnClickListener(this);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);
        db = new DatabaseHandler(Landing_Activity_.this);
        pagelist.put("page", pagecount);
        GlobalMethod.AcaslonProSemiBoldTextView(Landing_Activity_.this, headerText);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Landing_Activity_.this, Notification_Activity.class);
                intent.putExtra("from_page", "from_landing");
                startActivity(intent);
            }
        });
//        startService();
        loadContacts();
        total_device.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // TODO Auto-generated method stub
                pagecount = 1;
                pagelist.put("page", pagecount);
                if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity_.this)) {
                    GlobalMethod.write("CALLED FROM REFRESH");
                    currentPos = 0;
                    total_devices.clear();
                    loadContacts();
                } else {
                    GlobalMethod.showToast(Landing_Activity_.this, "Check Your Internet Connection.");
                }
            }
        });

        total_device.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                // TODO Auto-generated method stub
                if (is_load_more) {
                    pagecount = pagecount + 1;
                    pagelist.put("page", pagecount);
                    if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity_.this)) {
                        GlobalMethod.write("CALLED FROM LOAD MORE");
                        loadContacts();
                    } else {
                        GlobalMethod.showToast(Landing_Activity_.this, "Check Your Internet Connection.");
                    }
                }
            }
        });

        if (mBluetoothAdapter.isEnabled()) {
//            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (mayRequestLocation()) {
                if (mayRequestPermission(RECORD_AUDIO, REQUEST_AUDIO, getString(R.string.mic_permisison))) {
                    if (mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.phone_permission))) {
                        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            toShowDialogueWhetherGPSisEnabledorNot();
                        }
//                        toShowDialogueWhetherGPSisEnabledorNot();
                    }
                } else {
//                        exit();
                }
//                } else {
////                    exit();
//                }
            }
        }

    }

    private void exit() {
//        GlobalMethod.showCustomToastInCenter(this, "We can't get enough permissions to continue. Please enable them manually to continue. \nIVY Exitting.");
//        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        registerReceiver(new BleNotificationReceiver(), new IntentFilter("ble.taps.notify"));
        registerReceiver(signalReceiver, new IntentFilter(BluetoothLeService.SIGNAL_STRENGTH));
        registerReceiver(batteryReceiver, new IntentFilter(BluetoothLeService.BATTERY_LEVEL_INTENT));
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }


    public boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GlobalMethod.write("====<Build.VERSION_CODES.M");
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GlobalMethod.write("====>= Build.VERSION_CODES.M");
            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                GlobalMethod.write("====PERMISSION_GRANTED");
                return true;
            } else if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                GlobalMethod.write("====ifPermissionRationale");
                requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_ACCESS);
            } else {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_ACCESS);
//                GlobalMethod.showCustomToastInCenter(mActivity, "We can't get your location. Please Enable Location Permission.");
                GlobalMethod.write("====elsePermission");
//                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e("Permission", "requestCode" + requestCode);
        switch (requestCode) {
            case REQUEST_ACCESS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");

                } else {
                    Log.e("Permission", "Denied");
                    exit();
//                    GlobalMethod.showCustomToastInCenter(mActivity, "We can't get your location. Please enable Location permission.");
                }
                if (mayRequestPermission(RECORD_AUDIO, REQUEST_AUDIO, getString(R.string.mic_permisison))) {
                } else {
//                        exit();
                }
                break;
            }
            case REQUEST_AUDIO: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");

                } else {
                    Log.e("Permission", "Denied");
                    exit();
//                    GlobalMethod.showCustomToastInCenter(mActivity, getString(R.string.phone_permission));
                }
                if (mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.phone_permission))) {
                } else {
//                        exit();
                }
                break;
            }
            case REQUEST_TELEPHONE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");

                } else {
                    Log.e("Permission", "Denied");
                    exit();
//                    GlobalMethod.showCustomToastInCenter(mActivity, getString(R.string.phone_permission));
                }
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    toShowDialogueWhetherGPSisEnabledorNot();
                }
//                toShowDialogueWhetherGPSisEnabledorNot();
                break;
            }
//                if (mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.mic_permisison))) {
//                } else {
//                    exit();
//                }
//                break;
        }
    }


    // Start the service
    public void startService() {
        if (mainPreferences.getBoolean(Constant.isContactsUpdated, false) && Integer.parseInt(db.getContactsCount()) > 0) {
            GlobalMethod.write("====inifstopService");
        } else {
            GlobalMethod.write("====inelsestartService");
            Intent intent = new Intent(this, ContactServices.class);
//            intent.setAction("com.contactOfServices");
            startService(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings:
                Intent intentSetting = new Intent(this, Settings_Activty.class);
                startActivity(intentSetting);
                break;
            case R.id.add_icon:
                GlobalMethod.write("mac+++++" + mac_address + getRandomMacAddress());
                Intent intent = new Intent(this, DeviceScanActivity.class);
//                intent.putExtra("mac_address", mac_address + getRandomMacAddress());
//                intent.putExtra("pending_status", "Yes");
                startActivityForResult(intent, DEVICE_SCAN);
//                Intent intent = new Intent(this, AddDevice.class);
//                intent.putExtra("mac_address", mac_address + getRandomMacAddress());
//                intent.putExtra("pending_status", "Yes");
//                startActivityForResult(intent, ADD_DEVICE);
                break;
        }

    }


    /**
     * This method generates random numbers
     *
     * @return int
     */
    private String getRandomMacAddress() {
        Random generator = new Random();
        int rand = 99;
        String mac_address = "";
        for (int i = 0; i <= 2; i++) {
            if (i == 0)
                mac_address += generator.nextInt(rand);
            else
                mac_address = mac_address + ":" + generator.nextInt(rand);
        }
        return mac_address;
    }

    public void loadContacts() {
        if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity_.this)) {
            Bundle bundle = new Bundle();
            bundle.putString("showHtml", "no");
            bundle.putString("mode", "");
            bundle.putString("user_id", mainPreferences.getString(Constant.id_user, ""));
            bundle.putString("page", "" + pagelist.get("page"));
            new AsyncTaskApp(this, Landing_Activity_.this, Urls.VIEW_DEVICE_LIST, "LOGIN").execute(bundle);
        } else {
            GlobalMethod.showToast(Landing_Activity_.this, Constant.network_error);
        }
    }


    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        total_device.onRefreshComplete();
        JSONObject jobj = new JSONObject(result);
        if (action.equalsIgnoreCase("Login")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                JSONArray jsonArray = jobj.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    total_device.setVisibility(View.VISIBLE);
                    norecord_linear.setVisibility(View.GONE);
                    no_record.setVisibility(View.GONE);

                    if (jsonArray.length() < 10)
                        is_load_more = false;
                    else {
                        is_load_more = true;
                    }
                    if (pagelist.get("page") == 1) {
//                        activeDevices.clear();
                        total_devices.clear();
                        hDistanceMap.clear();
                        hBatteryMap.clear();
                        previousSignal = "";
                        previousBattery = "";
//                        hConnectedDeviceDistance.clear();
//                        hConnectedDeviceBattery.clear();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        total_devices.add(jsonArray.getJSONObject(i));

                    }

                    int index = total_device.getRefreshableView().getFirstVisiblePosition();
                    View v = total_device.getRefreshableView().getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    customListAdapter = new CustomListAdapter(total_devices, context);
                    total_device.setAdapter(customListAdapter);
                    total_device.setEmptyView(no_record);
                    total_device.getRefreshableView().setSelectionFromTop(index, top);
//                    currentPos = 0;
                    for (JSONObject deviceAddress : total_devices) {
                        hMap.put(deviceAddress.getString("mac_address"), 0);
                    }
                    scanLeDevice(true
                    );
//                    if (currentPos < total_devices.size() &&
//                            (mLeDeviceList.contains(total_devices.get(currentPos).getString("mac_address").trim())))
//                        makeConnection(total_devices.get(currentPos).getString("mac_address"));
//                    if (currentPos < total_devices.size())
//                        makeConnection(total_devices.get(currentPos).getString("mac_address"));
                }
            } else {
                is_load_more = false;
                if (pagelist.get("page") == 1) {
                    total_devices.clear();
                    customListAdapter = new CustomListAdapter(total_devices, context);
                    total_device.setAdapter(customListAdapter);
                    norecord_linear.setVisibility(View.VISIBLE);
                    no_record.setVisibility(View.VISIBLE);
                    total_device.setEmptyView(no_record);
                }
            }
        } else if (action.equalsIgnoreCase("Delete")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                total_devices.remove(globaLPosition);
                customListAdapter = new CustomListAdapter(total_devices, context);
                total_device.setAdapter(customListAdapter);
                total_device.getRefreshableView().setSelection(globaLPosition - 1);
//                customListAdapter.notifyDataSetChanged();
                GlobalMethod.showToast(Landing_Activity_.this, jobj.getString("message"));
            } else {
                GlobalMethod.showToast(Landing_Activity_.this, jobj.getString("message"));
            }
        }
        total_device.requestLayout();
    }

    public class CustomListAdapter extends BaseSwipeAdapter {
        ArrayList<JSONObject> innerJsonObj = new ArrayList<JSONObject>();
        LayoutInflater inflat;
        Context context;

        public CustomListAdapter(ArrayList<JSONObject> innerJsonObj, Context context) {
            this.innerJsonObj = innerJsonObj;
            inflat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        private class ViewHolder {
            ImageView diamond;
            TextView device_name, delete_text, edit_text;
            LinearLayout edit_button;
            TextView deviceId;
            SwipeLayout swipeLayout;
            RelativeLayout firstlayout;
            public TextView tvBattery, tvDistance;
        }

        @Override
        public View generateView(final int position, ViewGroup parent) {
            // view lookup cache stored in tag
            View v = null;
            ViewHolder viewHolder;
            if (v == null) {
                viewHolder = new ViewHolder();
                v = LayoutInflater.from(context).inflate(R.layout.landing_layout, null);
                viewHolder.diamond = (ImageView) v.findViewById(R.id.diamond);
                viewHolder.device_name = (TextView) v.findViewById(R.id.name_text);
                viewHolder.deviceId = (TextView) v.findViewById(R.id.deviceId);
                viewHolder.delete_text = (TextView) v.findViewById(R.id.delete_text);
                viewHolder.edit_text = (TextView) v.findViewById(R.id.edit_text);
                viewHolder.tvBattery = (TextView) v.findViewById(R.id.tvBattery);
                viewHolder.tvDistance = (TextView) v.findViewById(R.id.tvDistance);
                viewHolder.firstlayout = (RelativeLayout) v.findViewById(R.id.firstlayout);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }
            try {
                String address = innerJsonObj.get(position).getString("mac_address").trim();
//                GlobalMethod.write("ADDRESS : " + address);
                if (hDistanceMap.containsKey(address)) {

                    if (!TextUtils.isEmpty(address)) {
                        viewHolder.tvDistance.setVisibility(View.VISIBLE);
                        viewHolder.tvDistance.append(hDistanceMap.get(address));

                        hConnectedDeviceDistance.put(address, viewHolder.tvDistance);
                        viewHolder.diamond.setImageResource(R.drawable.diamondcolor);
                        hConnectedDeviceImageView.put(address, viewHolder.diamond);
                    } else {
                        viewHolder.tvDistance.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.tvDistance.setVisibility(View.GONE);
                    viewHolder.diamond.setImageResource(R.drawable.daimond_fade);
                }


//                String battery = innerJsonObj.get(position).getString("mac_address").trim();
//                GlobalMethod.write("BATTERY : " + battery);
                if (hBatteryMap.containsKey(address)) {
//                    GlobalMethod.write("BATTERY : INSIDE : " + address + " BATTERY : " + hBatteryMap.get(address));
                    if (!TextUtils.isEmpty(address)) {
                        viewHolder.tvBattery.setVisibility(View.VISIBLE);
                        viewHolder.tvBattery.append(hBatteryMap.get(address));
                        hConnectedDeviceBattery.put(address, viewHolder.tvBattery);

                    } else {
                        viewHolder.tvBattery.setVisibility(View.GONE);
                    }
                } else {

                    viewHolder.tvBattery.setVisibility(View.GONE);
                }

                viewHolder.device_name.setText(innerJsonObj.get(position).getString("device_name"));
                viewHolder.deviceId.setText("ID:  " + innerJsonObj.get(position).getString("unique_name"));
//                if (innerJsonObj.get(position).getString("pending_status").equalsIgnoreCase("yes")) {
//                    viewHolder.diamond.setImageResource(R.drawable.diamondcolor);
//                } else {
//                    viewHolder.diamond.setImageResource(R.drawable.daimond_fade);
//                }
//                GlobalMethod.write("LISt : " + list);
//                GlobalMethod.write("LISt ITEM : " + innerJsonObj.get(position).getString("mac_address"));

//                if (list.contains(innerJsonObj.get(position).getString("mac_address"))) {
//                    viewHolder.diamond.setImageResource(R.drawable.diamondcolor);
//                } else {
//                    viewHolder.diamond.setImageResource(R.drawable.daimond_fade);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewHolder.delete_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalMethod.write("===pos" + position);
                    toDeleteDevice(Landing_Activity_.this, position);
                }
            });
            viewHolder.edit_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Landing_Activity_.this, AddDevice.class);
                        intent.putExtra("edit_device", "edit_device");
                        intent.putExtra("deviceName", total_devices.get(position).getString("unique_name"));
                        intent.putExtra("device_id", total_devices.get(position).getString("device_id"));
                        startActivityForResult(intent, ADD_DEVICE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            viewHolder.firstlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Landing_Activity_.this, DeviceDetails.class);
                        intent.putExtra("device_name", innerJsonObj.get(position).getString("device_name"));
                        intent.putExtra("device_mac", "DevD:  " + innerJsonObj.get(position).getString("mac_address"));
                        intent.putExtra("device_id", innerJsonObj.get(position).getString("device_id"));
                        intent.putExtra("unique_name", total_devices.get(position).getString("unique_name"));
                        startActivityForResult(intent, ADD_DEVICE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            GlobalMethod.HelveticaCE35Thin(Landing_Activity_.this, v);
            GlobalMethod.AcaslonProSemiBoldTextView(Landing_Activity_.this, viewHolder.device_name);
            return v;
        }


        @Override
        public void fillValues(final int position, View convertView) {

        }

        @Override
        public int getCount() {
            return innerJsonObj.size();
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == DEVICE_SCAN || requestCode == ADD_DEVICE) && data != null && data.getStringExtra("added").equalsIgnoreCase("Yes")) {
            if (customListAdapter != null) {
                total_devices.clear();
                customListAdapter.notifyDataSetChanged();
                pagecount = 1;
                pagelist.put("page", pagecount);
                loadContacts();
            }
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish();
            return;
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
//            finish();
            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (mayRequestLocation()) {
                    if (mayRequestPermission(RECORD_AUDIO, REQUEST_AUDIO, getString(R.string.mic_permisison))) {
                        mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.phone_permission));
                    } else {
//                        exit();
                    }
                } else {
//                    exit();
//                    toShowDialogueWhetherGPSisEnabledorNot();
                }
            }
            return;
        }

        JSONObject obj = new JSONObject();
        Iterator<String> iterator = obj.keys();
        while (iterator.hasNext()) {

        }

        if (requestCode == ADD_DEVICE) {
            if (customListAdapter != null) {
                customListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        GlobalMethod.openExitDialog(Landing_Activity_.this);
    }


    public void toDeleteDevice(final Activity context, final int position) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.forget_pop_up);
        TextView alerttextlogout, oktext, account_created;
        LinearLayout alertParent;
        ImageView delete_icon;
        alertParent = (LinearLayout) dialog.findViewById(R.id.alertParent);
        delete_icon = (ImageView) dialog.findViewById(R.id.delete_icon);
        delete_icon.setImageResource(R.drawable.delete_image);
        account_created = (TextView) dialog.findViewById(R.id.account_created);
        alerttextlogout = (TextView) dialog.findViewById(R.id.verify_text);
        oktext = (TextView) dialog.findViewById(R.id.email1);

        GlobalMethod.AcaslonProSemiBoldTextView(Landing_Activity_.this, account_created);
        oktext.setTypeface(Typeface.createFromAsset(context.getAssets(), "Helvetica Neue CE 35 Thin.ttf"));
        oktext.setText("Delete");
        alerttextlogout.setText("Are you sure you want to delete " + "\n this Device?");

        alertParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        try {
            account_created.setText(total_devices.get(position).getString("device_name"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globaLPosition = position;
                try {
                    if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity_.this)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", mainPreferences.getString(Constant.id_user, ""));
                        bundle.putString("device_id", total_devices.get(position).getString("device_id"));
                        new AsyncTaskApp(Landing_Activity_.this, Landing_Activity_.this, Urls.DELETE_DEVICE, "Delete").execute(bundle);
                        dialog.dismiss();
                    } else {
                        GlobalMethod.showToast(Landing_Activity_.this, Constant.network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
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
                    finish();
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
        Intent gattServiceIntent = new Intent(Landing_Activity_.this, BluetoothLeService.class);
        mBluetoothLeService.onBind(gattServiceIntent);
        //        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private ArrayList<String> list = new ArrayList<>();
    private HashMap<String, Integer> hMap = new HashMap<>();
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String deviceAddress = intent.getStringExtra("address");
            GlobalMethod.write("RECIEVER : " + action + ", ADDRESS : " + deviceAddress);
            int count = hMap.get(deviceAddress) != null ? hMap.get(deviceAddress) : 0;
            count++;
            hMap.put(deviceAddress, count);
            if (count == 1) {
                try {
                    currentPos++;
                    if (currentPos < total_devices.size() &&
                            (mLeDeviceList.contains(total_devices.get(currentPos).getString("mac_address").trim())))
                        makeConnection(total_devices.get(currentPos).getString("mac_address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                updateConnectionState(R.string.connected);
                if (!list.contains(deviceAddress)) {
                    list.add(deviceAddress);
                    refreshList(deviceAddress);
                }
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                GlobalMethod.write("++++actiondisconnected+++");
                if (list.contains(deviceAddress)) {
                    list.remove(deviceAddress);
                    refreshList(deviceAddress);
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

    private void refreshList(String deviceAddress) {
        Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        if (list != null && list.size() > 0)
            set.addAll(list);
        list = new ArrayList<String>(set);

        if (list.contains(deviceAddress)) {
            if (hConnectedDeviceDistance.containsKey(deviceAddress)) {
                GlobalMethod.write("======Connected");
                hConnectedDeviceDistance.get(deviceAddress).setVisibility(View.VISIBLE);
                hConnectedDeviceImageView.get(deviceAddress).setImageResource(R.drawable.diamondcolor);
            }
            if (hConnectedDeviceBattery.containsKey(deviceAddress))
                hConnectedDeviceBattery.get(deviceAddress).setVisibility(View.VISIBLE);
        } else {
            if (hConnectedDeviceDistance.containsKey(deviceAddress)) {
                GlobalMethod.write("======DisConnected");
                hConnectedDeviceDistance.get(deviceAddress).setVisibility(View.GONE);
                hConnectedDeviceImageView.get(deviceAddress).setImageResource(R.drawable.daimond_fade);
            }
            if (hConnectedDeviceBattery.containsKey(deviceAddress))
                hConnectedDeviceBattery.get(deviceAddress).setVisibility(View.GONE);
        }

        customListAdapter.notifyDataSetChanged();


//        mLeDeviceListAdapter = new LeDeviceListAdapter();
//        listScannedDevice.setAdapter(mLeDeviceListAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
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

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
//            listScannedDevice.onRefreshComplete();
            try {
                GlobalMethod.write("BLE DEVICES : " + mLeDeviceList);
                if (total_devices.size() > currentPos) {
                    if (mLeDeviceList.contains(total_devices.get(currentPos).getString("mac_address").trim())) {
                        makeConnection(total_devices.get(currentPos).getString("mac_address"));
                    }
                    currentPos++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        invalidateOptionsMenu();

    }

    private ArrayList<String> mLeDeviceList = new ArrayList<>();
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
                            if (!mLeDeviceList.contains(device.getAddress()))
                                mLeDeviceList.add(device.getAddress());
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
            Log.d("DEBUG", "decoded String : " + ByteArrayToString(scanRecord));
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

    public static String ByteArrayToString(byte[] ba) {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");

        return hex.toString();
    }

    /**
     * @param permission   that has to be accessed in the app
     * @param requestCode  for callback purpose
     * @param errorMessage to display in case of error
     * @return
     */
    public boolean mayRequestPermission(String permission, int requestCode, String errorMessage) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GlobalMethod.write("====<Build.VERSION_CODES.M");
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GlobalMethod.write("====>= Build.VERSION_CODES.M");
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
//                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                GlobalMethod.write("====PERMISSION_GRANTED");
                return true;
            } else if (shouldShowRequestPermissionRationale(permission)) {
                GlobalMethod.write("====ifPermissionRationale");
                requestPermissions(new String[]{permission}, requestCode);
            } else {
                requestPermissions(new String[]{permission}, requestCode);
//                GlobalMethod.showCustomToastInCenter(this, errorMessage);
                GlobalMethod.write("====elsePermission");
//                return true;
            }
        }
        return false;
    }

    public void toShowDialogueWhetherGPSisEnabledorNot() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext, clickmarkettext;
        clickmarkettext = (TextView) dialog.findViewById(R.id.clickmarkettext);
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        canceltext.setVisibility(View.GONE);
        oktext = (TextView) dialog.findViewById(R.id.oktext);

        alerttextlogout.setText("Please enable GPS location service from device setting.");
        clickmarkettext.setText("Enable GPS");
        oktext.setText("Settings");

        GlobalMethod.AcaslonProSemiBoldTextView(this, clickmarkettext);
        GlobalMethod.AcaslonProSemiBoldTextView(this, oktext);
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        });
//        canceltext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        dialog.show();
    }


    /*
      End of class here.
     */
}
