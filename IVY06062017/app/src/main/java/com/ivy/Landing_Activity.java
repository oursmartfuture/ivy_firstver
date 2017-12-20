package com.ivy;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
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
import com.ivymanagedevice.AddDevice;
import com.ivymanagedevice.DeviceDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Class Name: Landing_Activity.Class
 * Class description: This class is use to show list of device
 * We can perform also delete device, edit device, navigation to AddDevice and Setting Screen.
 * Created by Abhas Vohra(00181)
 * Created by Singsys-106 on 10/12/2015.
 */
public class Landing_Activity extends Activity implements View.OnClickListener, CallBackListenar {
    private static final int REQUEST_ACCESS = 1230;
    private static final long SCAN_PERIOD = 30000;
    private static final int REQUEST_ENABLE_BT = 11;
    private static final int DEVICE_SCAN = 111;
    private static final int REQUEST_AUDIO = 123;
    private static final int REQUEST_TELEPHONE = 122;
    private static final int REQUEST_CAMERA_EXTERNAL_STORAGE = 1221;
    public static ArrayList<String> activeDevices = new ArrayList<>();
    boolean isAsking = false;
    ImageView settings, add_icon;
    TextView headerText;
    ImageButton backBtn;
    CustomListAdapter customListAdapter;
    public static ArrayList<JSONObject> total_devices = new ArrayList<JSONObject>();
    ListView total_device;
    private Context context;
    SharedPreferences mainPreferences;
    DatabaseHandler db;
    int ADD_DEVICE = 1001;
    String mac_address = "";
    private TextView no_record;
    boolean is_load_more = false;
    HashMap<String, Integer> pagelist = new HashMap<String, Integer>();
    int pagecount = 1;
    LinearLayout norecord_linear;
    int globaLPosition;
    SimpleLocation mSimpleLocation;
    public static BluetoothLeService mBluetoothLeService;
    int currentPos = 0;
    private Handler mHandler;
    FrameLayout notificationBtn;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    static Landing_Activity mActivity;

    HashMap<String, String> hDistanceMap = new HashMap<>();
    HashMap<String, TextView> hConnectedDeviceDistance = new HashMap<>();
    HashMap<String, TextView> hConnectedDeviceBattery = new HashMap<>();
    HashMap<String, ImageView> hConnectedDeviceBatteryImage = new HashMap<>();
    HashMap<String, LinearLayout> hConnectedDeviceBatteryLinearLayout = new HashMap<>();
    HashMap<String, ImageView> hConnectedDeviceImageView = new HashMap<>();
    private String previousSignal = "";
    private String previousBattery = "";
    private boolean firstTime = true;

//    private static final Class<?>[] mSetForegroundSignature = new Class[] {
//            boolean.class};
//    private static final Class<?>[] mStartForegroundSignature = new Class[] {
//            int.class, Notification.class};
//    private static final Class<?>[] mStopForegroundSignature = new Class[] {
//            boolean.class};
//
//    private NotificationManager mNM;
//    private Method mSetForeground;
//    private Method mStartForeground;
//    private Method mStopForeground;
//    private Object[] mSetForegroundArgs = new Object[1];
//    private Object[] mStartForegroundArgs = new Object[2];
//    private Object[] mStopForegroundArgs = new Object[1];
//
//    void invokeMethod(Method method, Object[] args) {
//        try {
//            method.invoke(this, args);
//        } catch (InvocationTargetException e) {
//            // Should not happen.
//            Log.w("ApiDemos", "Unable to invoke method", e);
//        } catch (IllegalAccessException e) {
//            // Should not happen.
//            Log.w("ApiDemos", "Unable to invoke method", e);
//        }
//    }
//
//    /**
//     * This is a wrapper around the new startForeground method, using the older
//     * APIs if it is not available.
//     */
//    void startForegroundCompat(int id, Notification notification) {
//        // If we have the new startForeground API, then use it.
//        if (mStartForeground != null) {
//            mStartForegroundArgs[0] = Integer.valueOf(id);
//            mStartForegroundArgs[1] = notification;
//            invokeMethod(mStartForeground, mStartForegroundArgs);
//            return;
//        }
//
//        // Fall back on the old API.
//        mSetForegroundArgs[0] = Boolean.TRUE;
//        invokeMethod(mSetForeground, mSetForegroundArgs);
//        mNM.notify(id, notification);
//    }
//
//    /**
//     * This is a wrapper around the new stopForeground method, using the older
//     * APIs if it is not available.
//     */
//    void stopForegroundCompat(int id) {
//        // If we have the new stopForeground API, then use it.
//        if (mStopForeground != null) {
//            mStopForegroundArgs[0] = Boolean.TRUE;
//            invokeMethod(mStopForeground, mStopForegroundArgs);
//            return;
//        }
//
//        // Fall back on the old API.  Note to cancel BEFORE changing the
//        // foreground state, since we could be killed at that point.
//        mNM.cancel(id);
//        mSetForegroundArgs[0] = Boolean.FALSE;
//        invokeMethod(mSetForeground, mSetForegroundArgs);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        try {
//            mStartForeground = getClass().getMethod("startForeground",
//                    mStartForegroundSignature);
//            mStopForeground = getClass().getMethod("stopForeground",
//                    mStopForegroundSignature);
//            return;
//        } catch (NoSuchMethodException e) {
//            // Running on an older platform.
//            mStartForeground = mStopForeground = null;
//        }
//        try {
//            mSetForeground = getClass().getMethod("setForeground",
//                    mSetForegroundSignature);
//        } catch (NoSuchMethodException e) {
//            throw new IllegalStateException(
//                    "OS doesn't have Service.startForeground OR Service.setForeground!");
//        }
//    }

    public static BleNotificationReceiver mBleNotificationReceiver = new BleNotificationReceiver();

    private boolean recent;
    BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    GlobalMethod.write("LANDING ACTIVITY BLUETOOTH OFF");
                    isAsking = false;
                    list.clear();
                    refreshList("","");
                    scanLeDevice(false);
                    if (mBluetoothLeService != null)
                        mBluetoothLeService.disconnect("");
                } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    GlobalMethod.write("LANDING ACTIVITY BLUETOOTH ON");
                    isAsking = false;
                    if (!recent) {
                        recent = true;
//                        loadContacts(true);
                        scanLeDevice(true);
                        currentPos = 0;
                        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (mayRequestLocation()) {
                                if (mayRequestPermission(RECORD_AUDIO, REQUEST_AUDIO, getString(R.string.mic_permisison))) {
                                    if (mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.phone_permission)))
                                        if (mayRequestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CAMERA_EXTERNAL_STORAGE, getString(R.string.storage_permission))) {
                                        }
                                } else {
//                        exit();
                                }
                            } else {
//                    exit();
                                GlobalMethod.checkGpsEnable(mActivity, true);
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recent = false;
                            }
                        }, 5 * 1000);
                    }

                }
            }
        }
    };

    BroadcastReceiver signalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GlobalMethod.write("SIGNAL RECEIVER : " + intent.getStringExtra("mac_address"));
            if (intent.hasExtra("distance") && intent.hasExtra("mac_address")) {
//                if (!hBatteryMap.containsKey(intent.getStringExtra("mac_address").trim())) {
//                    hBatteryMap.put(intent.getStringExtra("mac_address").trim(), "100");
//                }
//                if (!hConnectedDeviceBattery.containsKey(intent.getStringExtra("mac_address")) || !hConnectedDeviceBatteryLinearLayout.containsKey(intent.getStringExtra("mac_address")) || !hConnectedDeviceBatteryImage.containsKey(intent.getStringExtra("mac_address")) || !hConnectedDeviceDistance.containsKey(intent.getStringExtra("mac_address")) || !hConnectedDeviceImageView.containsKey(intent.getStringExtra("mac_address")) || !hConnectingMap.containsKey(intent.getStringExtra("mac_address"))) {
//                    customListAdapter = new CustomListAdapter(total_devices, context);
//                    total_device.setAdapter(customListAdapter);
//                } else {
                if (hConnectingMap != null && hConnectingMap.containsKey(intent.getStringExtra("mac_address"))) {
                    hConnectingMap.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
                }
                if (hConnectedDeviceImageView != null && hConnectedDeviceImageView.containsKey(intent.getStringExtra("name"))) {
                    if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled() && activeDevices.contains(intent.getStringExtra("name")))
                        hConnectedDeviceImageView.get(intent.getStringExtra("name")).setImageResource(R.drawable.diamondcolor);
                }
                hDistanceMap.put(intent.getStringExtra("mac_address").trim(), intent.getStringExtra("distance").trim());
                if (hConnectedDeviceBattery != null && hConnectedDeviceBattery.containsKey(intent.getStringExtra("mac_address"))) {
                    if (pref_settings != null && pref_settings.getString(Constant.left_battery, "No").equalsIgnoreCase("Yes")) {
//                        hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setVisibility(View.VISIBLE);
                        setBatteryImage(intent.getStringExtra("mac_address"), true);
                    } else {
                        hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
                        setBatteryImage(intent.getStringExtra("mac_address"), false);
                    }
                } else {
                    customListAdapter = new CustomListAdapter(total_devices, context);
                    total_device.setAdapter(customListAdapter);
                }
//                    if (!hBatteryMap.containsKey(intent.getStringExtra("mac_address").trim()))
//                        hBatteryMap.put(intent.getStringExtra("mac_address").trim(), "100");
//                    if (hConnectedDeviceBattery.containsKey(intent.getStringExtra("mac_address"))) {
//                        if (pref_settings != null && pref_settings.getString(Constant.left_battery, "No").equalsIgnoreCase("Yes")) {
//                            hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setText("Battery: " + hBatteryMap.get(intent.getStringExtra("mac_address").trim()));
//                            setBatteryImage(intent.getStringExtra("mac_address"), true);
////                        hConnectedDeviceBatteryImage.get(intent.getStringExtra("mac_address")).setVisibility(View.VISIBLE);
////                        hConnectedDeviceBatteryLinearLayout.get(intent.getStringExtra("mac_address")).setVisibility(View.VISIBLE);
//                        } else {
//                            hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
////                        hConnectedDeviceBatteryLinearLayout.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
//                            setBatteryImage(intent.getStringExtra("mac_address"), false);
//                        }
//
//                    }
                GlobalMethod.write("SIGNAL RECEIVER : Connected Devices : " + hConnectedDeviceDistance);
//                customListAdapter.notifyDatasetChanged();
//                    if (hConnectedDeviceDistance.containsKey(intent.getStringExtra("mac_address"))) {
//                        if (pref_settings != null && pref_settings.getString(Constant.connection_fading, "No").equalsIgnoreCase("Yes")) {
//                            hConnectedDeviceDistance.get(intent.getStringExtra("mac_address")).setVisibility(View.VISIBLE);
//
//                        } else {
//                            hConnectedDeviceDistance.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
//                        }
//                    }
//                    if (!previousSignal.equals(intent.getStringExtra("distance").trim())) {
//                        previousSignal = intent.getStringExtra("distance").trim();
//                        GlobalMethod.write("===previousSignal" + previousSignal);
//
//                        if (hConnectedDeviceDistance.containsKey(intent.getStringExtra("mac_address"))) {
////                        if (pref_settings != null && pref_settings.getString(Constant.connection_fading, "No").equalsIgnoreCase("Yes")) {
////                            hConnectedDeviceDistance.get(intent.getStringExtra("mac_address")).setVisibility(View.VISIBLE);
////                            hConnectedDeviceDistance.get(intent.getStringExtra("mac_address")).setText("Distance: " + intent.getStringExtra("distance"));
////                        } else {
////                            hConnectedDeviceDistance.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
////                        }
//                            hConnectedDeviceDistance.get(intent.getStringExtra("mac_address")).setText("Distance: " + intent.getStringExtra("distance"));
//
//                        } else {
//                            customListAdapter = new CustomListAdapter(total_devices, context);
//                            total_device.setAdapter(customListAdapter);
//                        }
//
//                    }
//                }

            }
        }
    };

    private void setBatteryImage(String mac_address, boolean b) {
        if (b) {
            if (hConnectedDeviceBatteryLinearLayout != null && hConnectedDeviceBatteryLinearLayout.containsKey(mac_address))
                hConnectedDeviceBatteryLinearLayout.get(mac_address).setVisibility(View.VISIBLE);
            if (hConnectedDeviceBatteryImage != null && hConnectedDeviceBatteryImage.containsKey(mac_address))
                hConnectedDeviceBatteryImage.get(mac_address).setVisibility(View.VISIBLE);
            if (hBatteryMap != null && hBatteryMap.containsKey(mac_address)) {
                if (hConnectedDeviceBatteryImage != null && hConnectedDeviceBatteryImage.containsKey(mac_address)) {
                    int battery = Integer.parseInt(hBatteryMap.get(mac_address).trim());
                    if (battery >= 80) {
                        hConnectedDeviceBatteryImage.get(mac_address).setImageResource(R.drawable.battery_full);
                    } else if (battery >= 40) {
                        hConnectedDeviceBatteryImage.get(mac_address).setImageResource(R.drawable.battery_sixty);
                    } else {
                        hConnectedDeviceBatteryImage.get(mac_address).setImageResource(R.drawable.battery_low);
                    }
                }
            } else {
                if (hConnectedDeviceBatteryLinearLayout != null && hConnectedDeviceBatteryLinearLayout.containsKey(mac_address))
                    hConnectedDeviceBatteryLinearLayout.get(mac_address).setVisibility(View.GONE);
            }
        } else {
            if (hConnectedDeviceBatteryLinearLayout != null && hConnectedDeviceBatteryLinearLayout.containsKey(mac_address))
                hConnectedDeviceBatteryLinearLayout.get(mac_address).setVisibility(View.GONE);
        }
    }

    public static HashMap<String, String> hBatteryMap = new HashMap<>();

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("battery") && intent.hasExtra("mac_address")) {
                hBatteryMap.put(intent.getStringExtra("mac_address").trim(), intent.getStringExtra("battery").trim().isEmpty() ? "100" : intent.getStringExtra("battery").trim());
//                customListAdapter.notifyDatasetChanged();
                if (hConnectedDeviceImageView != null && hConnectedDeviceImageView.containsKey(intent.getStringExtra("name"))) {
                    if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled() && activeDevices.contains(intent.getStringExtra("name")))
                        hConnectedDeviceImageView.get(intent.getStringExtra("name")).setImageResource(R.drawable.diamondcolor);

                }
                if (hConnectedDeviceBattery != null && hConnectedDeviceBattery.containsKey(intent.getStringExtra("mac_address"))) {
                    if (pref_settings != null && pref_settings.getString(Constant.left_battery, "No").equalsIgnoreCase("Yes")) {
//                        hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setVisibility(View.VISIBLE);
                        setBatteryImage(intent.getStringExtra("mac_address"), true);
                    } else {
                        hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
                        setBatteryImage(intent.getStringExtra("mac_address"), false);
                    }
                } else {
                    customListAdapter = new CustomListAdapter(total_devices, context);
                    total_device.setAdapter(customListAdapter);
                }
//                if (!previousBattery.equals(intent.getStringExtra("battery").trim())) {
//                    previousBattery = intent.getStringExtra("battery").trim();
//                    GlobalMethod.write("===previousBattery" + previousBattery);
//                    if (hConnectedDeviceBattery.containsKey(intent.getStringExtra("mac_address"))) {
////                        if (pref_settings != null && pref_settings.getString(Constant.left_battery, "No").equalsIgnoreCase("Yes")) {
////                            hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setVisibility(View.VISIBLE);
////                            hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setText("Battery: " + intent.getStringExtra("battery"));
////                        } else {
////                            hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
////                        }
////                        hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setText("Battery: " + intent.getStringExtra("battery"));
////                        setBatteryImage(intent.getStringExtra("mac_address"), true);
//                        if (pref_settings != null && pref_settings.getString(Constant.left_battery, "No").equalsIgnoreCase("Yes")) {
//                            setBatteryImage(intent.getStringExtra("mac_address"), true);
//                        } else {
//                            hConnectedDeviceBattery.get(intent.getStringExtra("mac_address")).setVisibility(View.GONE);
//                            setBatteryImage(intent.getStringExtra("mac_address"), false);
//                        }
//
//                    } else {
//                        customListAdapter = new CustomListAdapter(total_devices, context);
//                        total_device.setAdapter(customListAdapter);
//                    }
//                }
            }
        }
    };
    private SharedPreferences pref_settings;
    private SwipeRefreshLayout swipeRefresh, swipeRefreshNoRecord;
    private HashMap<String, TextView> hConnectingMap = new HashMap<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_withlistview);
        mActivity = Landing_Activity.this;
        mHandler = new Handler();
        pref_settings = getSharedPreferences(Constant.pref_settings, Activity.MODE_PRIVATE);
        mBluetoothLeService = BluetoothLeService.getInstance(Landing_Activity.this);
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
        context = Landing_Activity.this;
        Landing_Activity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        total_device = (ListView) findViewById(R.id.total_follow_list);
//        total_device;
//        total_device.setEnabled(false);
//        total_device.setMode(PullToRefreshBase.Mode.DISABLED);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshNoRecord = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshNoRecord);
//        swipeRefresh.setBackgroundColor(Color.BLACK);
//        swipeRefresh.setColorSchemeColors(new int[]{R.color.header_background});

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pagecount = 1;
                pagelist.put("page", pagecount);
                if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity.this)) {
                    GlobalMethod.write("CALLED FROM REFRESH");
                    currentPos = 0;
                    total_devices.clear();
                    loadContacts(false);
                } else {
                    GlobalMethod.showToast(Landing_Activity.this, "Check Your Internet Connection.");
                    try {
                        Thread.sleep(1000);
//                        total_device.onRefreshComplete();
                        swipeRefresh.setRefreshing(false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        swipeRefreshNoRecord.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pagecount = 1;
                pagelist.put("page", pagecount);
                if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity.this)) {
                    GlobalMethod.write("CALLED FROM REFRESH");
                    currentPos = 0;
                    total_devices.clear();
                    loadContacts(false);
                } else {
                    GlobalMethod.showToast(Landing_Activity.this, "Check Your Internet Connection.");
                    try {
                        Thread.sleep(1000);
//                        total_device.onRefreshComplete();
                        swipeRefreshNoRecord.setRefreshing(false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
//        total_device.setDividerHeight(1);
        settings.setOnClickListener(this);
        add_icon = (ImageView) findViewById(R.id.add_icon);
        add_icon.setOnClickListener(this);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setVisibility(View.GONE);
        db = new DatabaseHandler(Landing_Activity.this);
        pagelist.put("page", pagecount);
        GlobalMethod.AcaslonProSemiBoldTextView(Landing_Activity.this, headerText);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Landing_Activity.this, Notification_Activity.class);
                intent.putExtra("from_page", "from_landing");
                startActivity(intent);
            }
        });
//        startService();
        loadContacts(true);


//        total_device.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                GlobalMethod.write("====OnRefresh");
//                // TODO Auto-generated method stub
//                pagecount = 1;
//                pagelist.put("page", pagecount);
//                if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity.this)) {
//                    GlobalMethod.write("CALLED FROM REFRESH");
//                    currentPos = 0;
//                    total_devices.clear();
//                    loadContacts(false);
//                } else {
//                    GlobalMethod.showToast(Landing_Activity.this, "Check Your Internet Connection.");
//                    try {
//                        Thread.sleep(1000);
//                        total_device.onRefreshComplete();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//            }
//        });


        /*total_device.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {



            }
        });*/

//        total_device.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
//            @Override
//            public void onLastItemVisible() {
//                // TODO Auto-generated method stub
//                if (is_load_more) {
//                    pagecount = pagecount + 1;
//                    pagelist.put("page", pagecount);
//                    if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity.this)) {
//                        GlobalMethod.write("CALLED FROM LOAD MORE");
//                        loadContacts(true);
//                    } else {
//                        GlobalMethod.showToast(Landing_Activity.this, "Check Your Internet Connection.");
//                    }
//                }
//            }
//        });


        if (mayRequestLocation()) {
            if (mayRequestPermission(RECORD_AUDIO, REQUEST_AUDIO, getString(R.string.mic_permisison))) {
                if (mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.phone_permission))) {
                    if (mayRequestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CAMERA_EXTERNAL_STORAGE, getString(R.string.storage_permission))) {
                        GlobalMethod.checkGpsEnable(mActivity, true);
                    }
                }
            }
        }
    }

    private void exit() {
//        GlobalMethod.showCustomToastInCenter(this, "We can't get enough permissions to continue. Please enable them manually to continue. \nIVY Exitting.");
//        finish();
    }

    public static void addToList(String address) {
        if (!list.contains(address)) {
            list.add(address);
            if (mActivity != null)
                mActivity.refreshList(address,"");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (total_devices.size() > 0) {
            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                enableBt(true);
//                mBluetoothAdapter.enable();
                refreshList("","");
                isAsking = true;
            } else {
//                if (isAsking) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onBackPressed();
//                        }
//                    }, 300);
//                }
//                if (list.size() != total_devices.size())
                scanLeDevice(true);
            }
        } else {
            if (mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                enableBt(false);
//                mBluetoothAdapter.disable();
//                isAsking = true;
            }
        }


        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mBleNotificationReceiver, new IntentFilter("ble.taps.notify"));
//        if (pref_settings.getString(Constant.connection_fading, "No").equalsIgnoreCase("Yes"))
        registerReceiver(signalReceiver, new IntentFilter(BluetoothLeService.SIGNAL_STRENGTH));
//        if (pref_settings.getString(Constant.left_battery, "No").equalsIgnoreCase("Yes"))
        registerReceiver(batteryReceiver, new IntentFilter(BluetoothLeService.BATTERY_LEVEL_INTENT));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mBleNotificationReceiver);
    }

    private void enableBt(final boolean b) {
        String str = b ? "Would you like to <b>enable bluetooth</b>?" : "Would you like to <b>Disable bluetooth</b>, as no device is attached to this account?";
//        if (b) {
//            if (mBluetoothAdapter != null)
//                mBluetoothAdapter.enable();
//        }
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext;
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        oktext = (TextView) dialog.findViewById(R.id.oktext);
        alerttextlogout.setText(Html.fromHtml(str));
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b1 = b ? mBluetoothAdapter.enable() : mBluetoothAdapter.disable();
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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        unregisterReceiver(mBluetoothReceiver);

        SharedPreferences pref_settings = getSharedPreferences(Constant.pref_settings, Activity.MODE_PRIVATE);
//        if (pref_settings.getString(Constant.connection_fading, "No").equalsIgnoreCase("Yes"))
        unregisterReceiver(signalReceiver);
//        if (pref_settings.getString(Constant.left_battery, "No").equalsIgnoreCase("Yes"))
        unregisterReceiver(batteryReceiver);
    }


    public boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GlobalMethod.write("====<Build.VERSION_CODES.M");
            GlobalMethod.checkGpsEnable(mActivity, true);
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
                if (mayRequestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CAMERA_EXTERNAL_STORAGE, getString(R.string.phone_permission))) {
                } else {
//                        exit();
                }

                break;
            }
            case REQUEST_CAMERA_EXTERNAL_STORAGE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Log.e("Permission", "Denied");
                    }
                }
                GlobalMethod.checkGpsEnable(mActivity, true);
                break;
            }

        }
//                if (mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.mic_permisison))) {
//                } else {
//                    exit();
//                break;
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
//                GlobalMethod.checkTap(this, "2");

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

    public void loadContacts(boolean dialogeShow) {
        if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity.this)) {
            Bundle bundle = new Bundle();
            bundle.putString("showHtml", "no");
            bundle.putString("mode", "");
            bundle.putString("user_id", mainPreferences.getString(Constant.id_user, ""));
            bundle.putString("page", "" + pagelist.get("page"));
            new AsyncTaskApp(this, Landing_Activity.this, Urls.VIEW_DEVICE_LIST, "LOGIN", dialogeShow).execute(bundle);
        } else {
            GlobalMethod.showToast(Landing_Activity.this, Constant.network_error);
        }
    }


    @Override
    public void callBackFunction(String result, String action) throws JSONException {
//        total_device.onRefreshComplete();
        swipeRefreshNoRecord.setRefreshing(false);
        swipeRefresh.setRefreshing(false);
        JSONObject jobj = new JSONObject(result);
        if (action.equalsIgnoreCase("Login")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                JSONArray jsonArray = jobj.getJSONArray("data");
                if (jsonArray.length() > 0) {
//                    total_device.setVisibility(View.VISIBLE);
//                    norecord_linear.setVisibility(View.GONE);
//                    no_record.setVisibility(View.GONE);
                    if (jsonArray.length() < 10)
                        is_load_more = false;
                    else {
                        is_load_more = true;
                    }
                    if (pagelist.get("page") == 1) {
                        total_devices.clear();
                        hDistanceMap.clear();
                        activeDevices.clear();
//                        hBatteryMap.clear();
                        previousSignal = "";
                        previousBattery = "";
//                        hConnectedDeviceDistance.clear();
//                        hConnectedDeviceBattery.clear();
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        total_devices.add(jsonArray.getJSONObject(i));
                        if(jsonArray.getJSONObject(i).getString("pending_status").equalsIgnoreCase("yes"))
                            activeDevices.add(jsonArray.getJSONObject(i).getString("unique_name"));
                    }

                    if (total_devices.size() > 0) {
                        swipeRefresh.setVisibility(View.VISIBLE);
                        swipeRefreshNoRecord.setVisibility(View.GONE);
                        if (!mBluetoothAdapter.isEnabled()) {
                            if (!mBluetoothAdapter.isEnabled()) {
//                                if (firstTime) {
//                                    firstTime = false;
//                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                enableBt(true);
//                                mBluetoothAdapter.enable();
                                isAsking = true;

//                                }
                            }
                        } else {
                            scanLeDevice(true);
                        }
                    } else {
                        swipeRefresh.setVisibility(View.GONE);
                        swipeRefreshNoRecord.setVisibility(View.VISIBLE);
//                        total_device.setEmptyView(norecord_linear);
                        if (mBluetoothLeService != null) {
                            mBluetoothLeService.disconnect("");
                        }
                    }

                    int index = total_device.getFirstVisiblePosition();
                    View v = total_device.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    customListAdapter = new CustomListAdapter(total_devices, context);
                    total_device.setAdapter(customListAdapter);
//                    total_device.setEmptyView(no_record);
                    total_device.setSelectionFromTop(index, top);
//                    currentPos = 0;
                    for (JSONObject deviceAddress : total_devices) {
                        hMap.put(deviceAddress.getString("mac_address"), 0);
                    }

//                    if (currentPos < total_devices.size() &&
//                            (mLeDeviceList.contains(total_devices.get(currentPos).getString("mac_address").trim())))
//                        makeConnection(total_devices.get(currentPos).getString("mac_address"));
//                    if (currentPos < total_devices.size())
//                        makeConnection(total_devices.get(currentPos).getString("mac_address"));
                }
            } else {
                if (mBluetoothLeService != null) {
                    try {
                        mBluetoothLeService.disconnect("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                is_load_more = false;
                if (pagelist.get("page") == 1) {
                    total_devices.clear();
                    customListAdapter = new CustomListAdapter(total_devices, context);
                    total_device.setAdapter(customListAdapter);
//                    total_device.setVisibility(View.GONE);
//                    norecord_linear.setVisibility(View.VISIBLE);
//                    no_record.setVisibility(View.VISIBLE);
                    swipeRefresh.setVisibility(View.GONE);
                    swipeRefreshNoRecord.setVisibility(View.VISIBLE);
//                    total_device.setEmptyView(norecord_linear);
//                    total_device.setEmptyView(no_record);
                }
            }
        } else if (action.equalsIgnoreCase("Delete")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                String unique_name = total_devices.get(globaLPosition).getString("unique_name");
                mLeDeviceList.remove(unique_name);
                if (hBatteryMap.containsKey(mLeDeviceListStable.get(unique_name)))
                    hBatteryMap.remove(mLeDeviceListStable.get(unique_name));
                total_devices.remove(globaLPosition);
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.disconnect(mLeDeviceListStable.get(unique_name));
                }

                customListAdapter = new CustomListAdapter(total_devices, context);
                total_device.setAdapter(customListAdapter);
                total_device.setSelection(globaLPosition - 1);
//                customListAdapter.notifyDataSetChanged();
                GlobalMethod.showToast(Landing_Activity.this, jobj.getString("message"));
                if (total_devices.size() == 0) {
                    if (mBluetoothAdapter.isEnabled()) {
                        enableBt(false);
//                        mBluetoothAdapter.disable();
                    }
//                    total_device.setVisibility(View.GONE);
//                    norecord_linear.setVisibility(View.VISIBLE);
//                    no_record.setVisibility(View.VISIBLE);
                    swipeRefresh.setVisibility(View.GONE);
                    swipeRefreshNoRecord.setVisibility(View.VISIBLE);
//                    total_device.setEmptyView(norecord_linear);
                }
            } else {
                GlobalMethod.showToast(Landing_Activity.this, jobj.getString("message"));
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
            public TextView tvConnecting;
            public LinearLayout llBattery;
            public ImageView imgBattery;
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
                viewHolder.tvConnecting = (TextView) v.findViewById(R.id.tvConnecting);
                viewHolder.firstlayout = (RelativeLayout) v.findViewById(R.id.firstlayout);
                viewHolder.llBattery = (LinearLayout) v.findViewById(R.id.llBattery);
                viewHolder.imgBattery = (ImageView) v.findViewById(R.id.imgBattery);
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) v.getTag();
            }
            try {
                viewHolder.device_name.setText(innerJsonObj.get(position).getString("device_name"));
                viewHolder.deviceId.setText("ID:  " + innerJsonObj.get(position).getString("unique_name"));
                String address = mLeDeviceList.containsKey(innerJsonObj.get(position).getString("unique_name").trim()) ? mLeDeviceList.get(innerJsonObj.get(position).getString("unique_name").trim()) : innerJsonObj.get(position).getString("mac_address").trim();
//                GlobalMethod.write("ADDRESS : " + address);
                if (hDistanceMap.containsKey(address)) {

                    if (!TextUtils.isEmpty(address)) {
                        if (pref_settings != null && pref_settings.getString(Constant.connection_fading, "No").equalsIgnoreCase("Yes"))
                            viewHolder.tvDistance.setVisibility(View.VISIBLE);
                        else {
                            viewHolder.tvDistance.setVisibility(View.GONE);
                        }
                        viewHolder.tvDistance.append(hDistanceMap.get(address));
                        if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled())
                            viewHolder.diamond.setImageResource(R.drawable.diamondcolor);
                    } else {
                        viewHolder.tvDistance.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.tvDistance.setVisibility(View.GONE);
                    viewHolder.diamond.setImageResource(R.drawable.daimond_fade);
                }

                hConnectedDeviceDistance.put(address, viewHolder.tvDistance);
                hConnectingMap.put(address, viewHolder.tvConnecting);
                hConnectedDeviceImageView.put(innerJsonObj.get(position).getString("unique_name"), viewHolder.diamond);
                //Working

                hConnectedDeviceBattery.put(address, viewHolder.tvBattery);
                hConnectedDeviceBatteryImage.put(address, viewHolder.imgBattery);
                hConnectedDeviceBatteryLinearLayout.put(address, viewHolder.llBattery);
//                String battery = innerJsonObj.get(position).getString("mac_address").trim();
//                GlobalMethod.write("BATTERY : " + battery);
                if (hBatteryMap.containsKey(address)) {
//                    GlobalMethod.write("BATTERY : INSIDE : " + address + " BATTERY : " + hBatteryMap.get(address));
                    if (!TextUtils.isEmpty(address)) {
                        if (pref_settings != null && pref_settings.getString(Constant.left_battery, "No").equalsIgnoreCase("Yes"))
                            setBatteryImage(address, true);

//                            viewHolder.tvBattery.setVisibility(View.VISIBLE);

                        else {
                            setBatteryImage(address, false);

                            viewHolder.tvBattery.setVisibility(View.GONE);
                        }
                        viewHolder.tvBattery.append(hBatteryMap.get(address));

                    } else {
                        viewHolder.tvBattery.setVisibility(View.GONE);
                    }
                    if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled())
                        viewHolder.diamond.setImageResource(R.drawable.diamondcolor);
                } else {
                    viewHolder.diamond.setImageResource(R.drawable.daimond_fade);
                    viewHolder.tvBattery.setVisibility(View.GONE);
                }


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
                    toDeleteDevice(Landing_Activity.this, position);
                }
            });
            viewHolder.edit_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Landing_Activity.this, AddDevice.class);
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
                        Intent intent = new Intent(Landing_Activity.this, DeviceDetails.class);
                        intent.putExtra("device_name", innerJsonObj.get(position).getString("device_name"));
                        intent.putExtra("device_mac", "DevD:  " + innerJsonObj.get(position).getString("mac_address"));
                        intent.putExtra("device_id", innerJsonObj.get(position).getString("device_id"));
                        intent.putExtra("unique_name", total_devices.get(position).getString("unique_name"));
                        intent.putExtra("mac_address", mLeDeviceListStable.get(total_devices.get(position).getString("unique_name")));
                        startActivityForResult(intent, ADD_DEVICE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            GlobalMethod.HelveticaCE35Thin(Landing_Activity.this, v);
            GlobalMethod.AcaslonProSemiBoldTextView(Landing_Activity.this, viewHolder.device_name);
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

    public void enableBt() {
        onActivityResult(REQUEST_ENABLE_BT, RESULT_OK, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == DEVICE_SCAN || requestCode == ADD_DEVICE) && data != null && data.getStringExtra("added").equalsIgnoreCase("Yes")) {
            if (customListAdapter != null) {
                currentPos = 0;
                total_devices.clear();
                customListAdapter.notifyDataSetChanged();
                pagecount = 1;
                pagelist.put("page", pagecount);
                loadContacts(true);
            }
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            isAsking = false;
//            finish();
            return;
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
//            finish();
            isAsking = false;
            scanLeDevice(true);
            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (mayRequestLocation()) {
                    if (mayRequestPermission(RECORD_AUDIO, REQUEST_AUDIO, getString(R.string.mic_permisison))) {
                        if (mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.phone_permission)))
                            if (mayRequestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CAMERA_EXTERNAL_STORAGE, getString(R.string.storage_permission))) {
                            }
                    } else {
//                        exit();
                    }
                } else {
//                    exit();
                    GlobalMethod.checkGpsEnable(mActivity, true);
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
        GlobalMethod.openExitDialog(Landing_Activity.this);
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

        GlobalMethod.AcaslonProSemiBoldTextView(Landing_Activity.this, account_created);
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
                    if (SimpleHttpConnection.isNetworkAvailable(Landing_Activity.this)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", mainPreferences.getString(Constant.id_user, ""));
                        bundle.putString("device_id", total_devices.get(position).getString("device_id"));
                        new AsyncTaskApp(Landing_Activity.this, Landing_Activity.this, Urls.DELETE_DEVICE, "Delete").execute(bundle);
                        dialog.dismiss();
                    } else {
                        GlobalMethod.showToast(Landing_Activity.this, Constant.network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    public static Landing_Activity getInstance() {
        return mActivity;
    }

    public void makeConnection(final String mDeviceAddress) {
        GlobalMethod.write("MAKE CONNECTION : ADDRESS : " + mDeviceAddress);
//        if (!list.contains(mDeviceAddress)) {
        try {
            if (hConnectingMap != null && hConnectingMap.containsKey(mDeviceAddress)) {
                hConnectingMap.get(mDeviceAddress).setVisibility(View.VISIBLE);
                setBatteryImage(mDeviceAddress, false);
            } else {
                customListAdapter = new CustomListAdapter(total_devices, context);
                total_device.setAdapter(customListAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            customListAdapter = new CustomListAdapter(total_devices, context);
            total_device.setAdapter(customListAdapter);
            if (hConnectingMap != null && hConnectingMap.containsKey(mDeviceAddress)) {
                hConnectingMap.get(mDeviceAddress).setVisibility(View.VISIBLE);
                setBatteryImage(mDeviceAddress, false);
            }
        }
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
        Intent gattServiceIntent = new Intent(Landing_Activity.this, BluetoothLeService.class);
        mBluetoothLeService.onBind(gattServiceIntent);

        //        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//        }
    }

    private static ArrayList<String> list = new ArrayList<>();
    private HashMap<String, Integer> hMap = new HashMap<>();
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String deviceAddress = intent.getStringExtra("address");
            String deviceName = intent.getStringExtra("name");
            GlobalMethod.write("RECIEVER : " + action + ", ADDRESS : " + deviceAddress);
//            int count = hMap.get(deviceAddress) != null ? hMap.get(deviceAddress) : 0;
//            count++;
//            hMap.put(deviceAddress, count);
//            if (count == 1) {
//                try {
//                    if (currentPos < total_devices.size() &&
//                            (mLeDeviceList.containsKey(total_devices.get(currentPos).getString("unique_name").trim()))) {
//                        GlobalMethod.write("TOTAL DEVICES GATT UPDATE: OUTSIDE WHILE currentPos: " + currentPos + ", LIST SIZE: " + total_devices.size());
//                        while (!total_devices.get(currentPos).getString("pending_status").equalsIgnoreCase("Yes") && currentPos < (total_devices.size())) {
//                            currentPos++;
//                            GlobalMethod.write("TOTAL DEVICES GATT UPDATE: INSIDE WHILE currentPos: " + currentPos + ", LIST SIZE: " + total_devices.size());
//                        }
//                        if (currentPos < total_devices.size()) {
//                            GlobalMethod.write("TOTAL DEVICES GATT UPDATE: MAKE CONNECTION currentPos: " + currentPos + ", LIST SIZE: " + total_devices.size());
//                            makeConnection(mLeDeviceList.get(total_devices.get(currentPos).getString("unique_name")));
//                            currentPos++;
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                updateConnectionState(R.string.connected);
                if (!list.contains(deviceAddress)) {
                    list.add(deviceAddress);
                    refreshList(deviceAddress, deviceName);
                }

                if (hConnectingMap != null && hConnectingMap.containsKey(deviceAddress))
                    hConnectingMap.get(deviceAddress).setVisibility(View.GONE);
//                setBatteryImage(deviceAddress, true);

//                if (mLeDeviceList.containsKey(deviceAddress)) {
//                    mLeDeviceList.remove(deviceAddress);
//                }
//                currentPos = 0;
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                GlobalMethod.write("++++actiondisconnected+++");
                if (list.contains(deviceAddress)) {
                    list.remove(deviceAddress);
                    refreshList(deviceAddress, deviceName);
                }
                setBatteryImage(deviceAddress, false);
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

    private void refreshList(String deviceAddress, String uniqueName) {
        if (TextUtils.isEmpty(deviceAddress)) {
            for (ImageView img : hConnectedDeviceImageView.values()) {
                if (img != null) {
                    img.setImageResource(R.drawable.daimond_fade);
                }
            }
            for (TextView img : hConnectedDeviceBattery.values()) {
                if (img != null) {
                    img.setVisibility(View.GONE);
                }
            }
            for (LinearLayout img : hConnectedDeviceBatteryLinearLayout.values()) {
                if (img != null) {
                    img.setVisibility(View.GONE);
                }
            }
            for (TextView img : hConnectedDeviceDistance.values()) {
                if (img != null) {
                    img.setVisibility(View.GONE);
                }
            }

            for (TextView img : hConnectingMap.values()) {
                if (img != null) {
                    img.setVisibility(View.GONE);
                }
            }
        } else {
            Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            if (list != null && list.size() > 0)
                set.addAll(list);
            list = new ArrayList<String>(set);
            if (list.contains(deviceAddress)) {
                if (hConnectedDeviceDistance.containsKey(deviceAddress)) {
                    GlobalMethod.write("======Connected");
                    if (pref_settings != null && pref_settings.getString(Constant.connection_fading, "No").equalsIgnoreCase("Yes"))
                        hConnectedDeviceDistance.get(deviceAddress).setVisibility(View.VISIBLE);
                    else {
                        hConnectedDeviceDistance.get(deviceAddress).setVisibility(View.GONE);
                    }
                    if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled() && activeDevices.contains(uniqueName))
                        hConnectedDeviceImageView.get(uniqueName).setImageResource(R.drawable.diamondcolor);
//                    else{
//                        hConnectedDeviceImageView.get(uniqueName).setImageResource(R.drawable.daimond_fade);
//                    }
                }
                if (hConnectedDeviceBattery.containsKey(deviceAddress))
                    setBatteryImage(deviceAddress, true);

//                hConnectedDeviceBattery.get(deviceAddress).setVisibility(View.VISIBLE);
            } else {
                if (hConnectedDeviceDistance.containsKey(deviceAddress)) {
                    GlobalMethod.write("======DisConnected");
                    hConnectedDeviceDistance.get(deviceAddress).setVisibility(View.GONE);
                    hConnectedDeviceImageView.get(uniqueName).setImageResource(R.drawable.daimond_fade);
                }
                if (hConnectedDeviceBattery.containsKey(deviceAddress))
                    setBatteryImage(deviceAddress, false);
//                    hConnectedDeviceBattery.get(deviceAddress).setVisibility(View.GONE);
            }
            customListAdapter.notifyDataSetChanged();
        }
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

            currentPos = 0;
            mLeDeviceList.clear();
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
//            listScannedDevice.onRefreshComplete();
//            try {
//                GlobalMethod.write("BLE DEVICES : " + mLeDeviceList);
//                GlobalMethod.write("BLE DEVICES : POSITION : " + currentPos);
//                if (total_devices.size() > currentPos) {
//                    if (mLeDeviceList.containsKey(total_devices.get(currentPos).getString("unique_name").trim())) {
//                        while (!total_devices.get(currentPos).getString("pending_status").equalsIgnoreCase("Yes") && currentPos < (total_devices.size() - 1)) {
//                            currentPos++;
//                        }
//                        makeConnection(mLeDeviceList.get(total_devices.get(currentPos).getString("unique_name")));
//                        currentPos++;
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        invalidateOptionsMenu();

    }

    private HashMap<String, String> mLeDeviceList = new HashMap<>();
    private HashMap<String, String> mLeDeviceListStable = new HashMap<>();
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

                            if (device.getName() != null && device.getName().trim().startsWith("IVY")) {
                                if (!list.contains(device.getName().trim())) {
                                    mLeDeviceList.put(device.getName(), device.getAddress());
                                    mLeDeviceListStable.put(device.getName(), device.getAddress());
                                    try {
                                        GlobalMethod.write("BLE DEVICES : " + mLeDeviceList);
                                        GlobalMethod.write("BLE DEVICES : POSITION : " + currentPos);

////                                        for (int i = 0; i < total_devices.size(); i++) {
//                                        if (total_devices.toString().trim().contains(device.getName().trim())) {
////                                                while (!total_devices.get(i).getString("pending_status").equalsIgnoreCase("Yes") && currentPos < (total_devices.size() - 1)) {
////                                                    i++;
////                                                }
//
////                                            if (total_devices.get(i).getString("pending_status").equalsIgnoreCase("Yes")) {
//                                                makeConnection(device.getAddress().trim());
//
////                                            }
////                                                break;
////                                            currentPos++;
////                                            }
//                                        }
                                        if (mLeDeviceList.size() > currentPos) {
                                            currentPos = 0;
                                            while (total_devices.size() > currentPos) {

                                                if (mLeDeviceList.containsKey(total_devices.get(currentPos).getString("unique_name").trim())) {
                                                    while (!total_devices.get(currentPos).getString("pending_status").equalsIgnoreCase("Yes") && currentPos < (total_devices.size() - 1) || list.contains(total_devices.get(currentPos))) {
                                                        currentPos++;
                                                    }
                                                    makeConnection(mLeDeviceList.get(total_devices.get(currentPos).getString("unique_name")));
                                                    currentPos++;
                                                }
                                                currentPos++;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (list.size() == total_devices.size()) {
                                scanLeDevice(false);
                                return;
                            }
//                            }
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




    /*
      End of class here.
     */
}
