package com.ivy;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ble_connect.BluetoothBroadcastReceiver;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Class Name: Splash_Screen_Activity.Class
 * Class description: This class contains methods and classes for showing the splash screen of the app.
 * Created by Sobhit Gupta(00253)
 * Created by Singsys-043 on 10/20/2015.
 */
public class Splash_Screen_Activity extends Activity {
    SharedPreferences login_prference, pref_settings;
    private static final int REQUEST_CAMERA = 123321;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen_);
        pref_settings = getSharedPreferences(Constant.pref_settings, Activity.MODE_PRIVATE);
        login_prference = getSharedPreferences(Constant.pref_main, 0);
//        registerReceiver(new BluetoothBroadcastReceiver(), new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
//        GlobalMethod.hideSoftKeyboard(Splash_Screen_Activity.this);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!Settings.System.canWrite(Splash_Screen_Activity.this)) {
//                        if (!checkWriteExternalPermission() || !checkCameraPermission() || !checkBluetooth() || !checkBluetoothAdmin() || !checkReadContacts() || !checkRecordAudio() || !checkCallPhone() || !checkLocation() || !checkLocation_()) {
//                            requestPermissions(new String[]{CAMERA,
//                                    WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, BLUETOOTH, BLUETOOTH_ADMIN, READ_CONTACTS, CALL_PHONE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_CAMERA);
//                        } else {
//                            initialize();
//                        }
//                    } else {
//                        initialize();
//                    }
//                } else {
//                    initialize();
//                }
//            }
//        }, 500);

        initialize();
//        initialize();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialize();
            } else {
                Log.e("Permission", "Denied");
            }
        }
    }

    private boolean checkWriteExternalPermission() {
        String permission = WRITE_EXTERNAL_STORAGE;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCameraPermission() {
        String permission = Manifest.permission.CAMERA;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkRecordAudio() {
        String permission = Manifest.permission.RECORD_AUDIO;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkBluetooth() {
        String permission = Manifest.permission.BLUETOOTH;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkBluetoothAdmin() {
        String permission = Manifest.permission.BLUETOOTH_ADMIN;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkReadContacts() {
        String permission = Manifest.permission.READ_CONTACTS;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCallPhone() {
        String permission = Manifest.permission.CALL_PHONE;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkLocation() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkLocation_() {
        String permission = Manifest.permission.ACCESS_COARSE_LOCATION;
        int res = Splash_Screen_Activity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void initialize() {
        GlobalMethod.write("INITIALIZE 1");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GlobalMethod.write("INITIALIZE 2");
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    GlobalMethod.write("INITIALIZE 3");

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Splash_Screen_Activity.this, "BLE is not supported on this device.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else if (!TextUtils.isEmpty(login_prference.getString(Constant.email, ""))) {
                    GlobalMethod.write("INITIALIZE 4");
                    Intent i = new Intent(Splash_Screen_Activity.this, Landing_Activity.class);
                    startActivity(i);
                } else {
                    GlobalMethod.write("INITIALIZE 5");
                    Intent i = new Intent(Splash_Screen_Activity.this, Login_Screen_Activity.class);
                    startActivity(i);
                }
            }
        }, 2000);
//        Thread background = new Thread() {
//            public void run() {
//
//                try {
//                    // Thread will sleep for 2 seconds
//                    sleep(2 * 1000);
//                    // After 2 seconds redirect to another intent
//
//                    GlobalMethod.write("INITIALIZE 2");
//                    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//                        GlobalMethod.write("INITIALIZE 3");
//
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(Splash_Screen_Activity.this, "BLE is not supported on this device.", Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        });
//                    } else if (!TextUtils.isEmpty(login_prference.getString(Constant.email, ""))) {
//                        GlobalMethod.write("INITIALIZE 4");
//                        Intent i = new Intent(Splash_Screen_Activity.this, Landing_Activity.class);
//                        startActivity(i);
//                    } else {
//                        GlobalMethod.write("INITIALIZE 5");
//                        Intent i = new Intent(Splash_Screen_Activity.this, Login_Screen_Activity.class);
//                        startActivity(i);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        background.start();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    /*
    end of class.
     */
}
