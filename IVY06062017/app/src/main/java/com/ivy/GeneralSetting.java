package com.ivy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class Name: GeneralSetting.Class
 * Class description: This class contains methods and classes for general settings related to notifications,battery,phone ring,to show map,connection/disconnection alert,connection fading in the app.
 * Created by Sobhit Gupta(00253)
 * Created by Singsys-018 on 03-Nov-15.
 */
public class GeneralSetting extends Activity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, CallBackListenar {
    CheckBox view_notification, phn_ring, battry_prcnt, shw_map, connt_discontn, connection_fading;
    TextView headerText;
    ImageButton backBtn;
    FrameLayout notificationBtn;
    SharedPreferences.Editor edit_setting;
    public SharedPreferences preferences, preference_settings;
    Bundle params;
    private CheckBox view_visibility;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_settings);
        view_notification = (CheckBox) findViewById(R.id.view_notification);
        phn_ring = (CheckBox) findViewById(R.id.phn_ring);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        battry_prcnt = (CheckBox) findViewById(R.id.battry_prcnt);
        shw_map = (CheckBox) findViewById(R.id.shw_map);
        connt_discontn = (CheckBox) findViewById(R.id.connt_discontn);
        notificationBtn = (FrameLayout) findViewById(R.id.notificationBtn);
        connection_fading = (CheckBox) findViewById(R.id.connection_fading);
        view_visibility = (CheckBox) findViewById(R.id.view_visibility);
        headerText = (TextView) findViewById(R.id.headerText);
        headerText.setText("General Settings");
        GlobalMethod.AcaslonProSemiBoldTextView(GeneralSetting.this, headerText);
        notificationBtn.setVisibility(View.GONE);
        view_notification.setOnCheckedChangeListener(this);
        backBtn.setOnClickListener(this);
        params = new Bundle();
        GeneralSetting.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        preferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        preference_settings = getSharedPreferences(Constant.pref_settings, Activity.MODE_PRIVATE);
        edit_setting = preference_settings.edit();
        View view = (View) findViewById(android.R.id.content);
        GlobalMethod.HelveticaCE35Thin(GeneralSetting.this, view);
        GlobalMethod.AcaslonProSemiBoldTextView(GeneralSetting.this, headerText);


        if (SimpleHttpConnection.isNetworkAvailable(GeneralSetting.this)) {
            params.putString("user_id", preferences.getString(Constant.id_user, ""));
            params.putString("mode", "");
            new AsyncTaskApp(this, GeneralSetting.this, Urls.View_General_Settings, "View_General_Settings").execute(params);
        } else {
            GlobalMethod.showToast(GeneralSetting.this, Constant.network_error);
            GlobalMethod.hideSoftKeyboard(this);
        }
    }
/*
    this method is for deciding what happens when the check of toggle button is changed .
 */

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.view_notification:
                if (isChecked) {
                } else {
                }
                break;
            case R.id.phn_ring:
                if (isChecked) {
                } else {
                }
                break;
            case R.id.connection_fading:
                if (isChecked) {
                } else {
                }
                break;
            case R.id.battry_prcnt:
                if (isChecked) {
                } else {
                }
                break;
            case R.id.shw_map:
                if (isChecked) {
                } else {
                }
                break;
            case R.id.connt_discontn:
                if (isChecked) {
                } else {
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (SimpleHttpConnection.isNetworkAvailable(GeneralSetting.this)) {
            params = new Bundle();
            params.putString("user_id", preferences.getString(Constant.id_user, ""));
            params.putString(getString(R.string.view_votification_general_setting), view_notification.isChecked() ? "No" : "Yes");
            params.putString(getString(R.string.alert_type_general_setting), phn_ring.isChecked() ? "No" : "Yes");
            params.putString(getString(R.string.left_battery_general_setting), battry_prcnt.isChecked() ? "No" : "Yes");
            params.putString(getString(R.string.show_map_general_setting), shw_map.isChecked() ? "No" : "Yes");
            params.putString(getString(R.string.connection_general_setting), connt_discontn.isChecked() ? "No" : "Yes");
            params.putString(getString(R.string.connection_fading_general_setting), connection_fading.isChecked() ? "No" : "Yes");
            params.putString(getString(R.string.visibility_general_setting), view_visibility.isChecked() ? "Yes" : "No");
//            params.putString("mode", "");
            new AsyncTaskApp(this, GeneralSetting.this, Urls.General_Settings, "General_Settings").execute(params);
        } else {
            GlobalMethod.showToast(GeneralSetting.this, Constant.network_error);
            GlobalMethod.hideSoftKeyboard(this);
        }
    }

    /*
    this method is for setting the toggle buttons either checked or unchecked.
     */
    void Set_Result(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    if (data.getString("set_privacy").equalsIgnoreCase("no")) {
                        view_visibility.setChecked(false);
                    } else {
                        view_visibility.setChecked(true);
                    }
                    if (data.getString("view_notification").equalsIgnoreCase("yes")) {
                        view_notification.setChecked(false);
                    } else {
                        view_notification.setChecked(true);
                    }
                    if (data.getString("alert_type_ring").equalsIgnoreCase("Yes")) {
                        phn_ring.setChecked(false);
                    } else {
                        phn_ring.setChecked(true);
                    }
                    if (data.getString("left_battery").equalsIgnoreCase("Yes")) {
                        battry_prcnt.setChecked(false);
                    } else {
                        battry_prcnt.setChecked(true);
                    }
                    if (data.getString("show_map").equalsIgnoreCase("Yes")) {
                        shw_map.setChecked(false);
                    } else {
                        shw_map.setChecked(true);
                    }
                    if (data.getString("connection").equalsIgnoreCase("Yes")) {
                        connt_discontn.setChecked(false);
                    } else {
                        connt_discontn.setChecked(true);
                    }
//                    if (data.getString("connection_fading").equalsIgnoreCase("Yes")) {
//                        connection_fading.setChecked(false);
//                    } else {
//                        connection_fading.setChecked(true);
//                    }
                    save_preference_settings();
                } else {
                    GlobalMethod.showToast(GeneralSetting.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    void save_preference_settings() {
        edit_setting.putString(Constant.view_notification, view_notification.isChecked() ? "No" : "Yes");
        edit_setting.putString(Constant.alert_type_ring, phn_ring.isChecked() ? "No" : "Yes");
        edit_setting.putString(Constant.left_battery, battry_prcnt.isChecked() ? "No" : "Yes");
        edit_setting.putString(Constant.show_map, shw_map.isChecked() ? "No" : "Yes");
        edit_setting.putString(Constant.connection, connt_discontn.isChecked() ? "No" : "Yes");
        edit_setting.putString(Constant.connection_fading, connection_fading.isChecked() ? "No" : "Yes");
        edit_setting.commit();
    }


    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        if (action == "General_Settings") {
            save_preference_settings();
            finish();
        } else {
            Set_Result(result);
        }
    }
     /*
    end of class .
     */
}
