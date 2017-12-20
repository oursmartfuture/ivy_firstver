package com.ivymanagedevice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.AsyncTask_Multipart;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GPS;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;
import com.ivy.ChooseImageActivity;
import com.ivy.R;
import com.ivy.Record_Audio;
import com.ivy.RoundedImageView;
import com.ivy.SimpleHttpConnection;
import com.ivy.UniversalImageDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Class Name: AddDevice.Class
 * Class description: This class is use to add and edit device.
 * Created by Pradeep Kumar(00181)
 * Created by Singsys-043 on 09/12/2015.
 */
public class AddDevice extends Activity implements CallBackListenar, View.OnClickListener {
    TextView headerTxt, device_name_text, device_img_text, on_off_text, saveDevice;
    EditText device_name_edit;
    RoundedImageView device_img;
    CheckBox status_toggle;
    ImageButton backBtn;
    String added = "no";
    String filepath = "";
    SharedPreferences preferences;
    String errmsg;
    LinearLayout close_keyboard, parent_layout_login, save_device;
    String action = "Add";
    String mac_address = "", pending_status = "Yes";
    SharedPreferences.Editor editor;
    GPS gps;
    private String deviceName;
    private String deviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device);
        GlobalMethod.hideSoftKeyboard(AddDevice.this);
        preferences = getSharedPreferences(Constant.pref_main, 0);
        parent_layout_login = (LinearLayout) findViewById(R.id.parent_layout_login);
        close_keyboard = (LinearLayout) findViewById(R.id.close_keyboard);
        headerTxt = (TextView) findViewById(R.id.headerText);
        saveDevice = (TextView) findViewById(R.id.save_txt);
        device_name_text = (TextView) findViewById(R.id.device_name_text);
        device_img_text = (TextView) findViewById(R.id.device_img_text);
        device_name_edit = (EditText) findViewById(R.id.device_name);
        device_img = (RoundedImageView) findViewById(R.id.device_img);
        on_off_text = (TextView) findViewById(R.id.on_off_text);
        status_toggle = (CheckBox) findViewById(R.id.status_toggle);
        save_device = (LinearLayout) findViewById(R.id.save_device);
        save_device.setOnClickListener(this);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        if (getIntent().hasExtra("device_name"))
            deviceName = getIntent().getStringExtra("device_name");
        if (getIntent().hasExtra("mac_address"))
            deviceAddress = getIntent().getStringExtra("mac_address");

        device_name_edit.setText(deviceName);
        close_keyboard.setOnClickListener(this);
        parent_layout_login.setOnClickListener(this);
        saveDevice.setOnClickListener(this);
        device_img.setOnClickListener(this);
        if (!getIntent().hasExtra("edit_device")) {
            headerTxt.setText("Add Device");
            GlobalMethod.AcaslonProSemiBoldTextView(AddDevice.this, headerTxt);

            action = "Add";
            if (getIntent().getStringExtra("pending_status").equalsIgnoreCase("Yes")) {
                on_off_text.setText("Active");
                status_toggle.setChecked(true);
                status_toggle.setEnabled(true);
            } else {
                on_off_text.setText("Inactive");
                status_toggle.setChecked(false);
                status_toggle.setEnabled(true);
            }
//            mac_address = getIntent().getStringExtra("mac_address");
//            pending_status = getIntent().getStringExtra("pending_status");
        } else {
            headerTxt.setText("Edit Device");
            action = "Edit";
            deviceName = getIntent().getStringExtra("deviceName");
            viewDeviceInfo();
        }

        status_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    on_off_text.setText("Active");
                    pending_status = "Yes";
                } else {
                    on_off_text.setText("Inactive");
                    pending_status = "No";
                }
            }
        });

        gps = new GPS(AddDevice.this);
        View view = (View) findViewById(android.R.id.content);
        GlobalMethod.HelveticaCE35Thin(AddDevice.this, view);
        GlobalMethod.AcaslonProSemiBoldTextView(AddDevice.this, headerTxt);
        GlobalMethod.AcaslonProSemiBoldTextView(AddDevice.this, saveDevice);
    }

    /*
    this method is for validating the various fields of Add Device form.
  */
    public boolean isValidation() {
        if (TextUtils.isEmpty(device_name_edit.getText().toString().trim())) {
            errmsg = "Please enter Name of Device.";
            device_name_edit.requestFocus();
            return false;
        } else if (!getIntent().hasExtra("edit_device")) {
            if (TextUtils.isEmpty(filepath)) {
                errmsg = "Please choose Device Image.";
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("added", added);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        if (action.equalsIgnoreCase("Add") || action.equalsIgnoreCase("Edit")) {
            if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                GlobalMethod.showToast(AddDevice.this, jsonObject.getString("message"));
                added = "yes";
                Intent intent = new Intent();
                intent.putExtra("added", added);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                added = "no";
                GlobalMethod.showToast(AddDevice.this, jsonObject.getString("message"));
            }
        } else {
            if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                JSONObject jsonData = jsonObject.getJSONArray("data").getJSONObject(0);

                device_name_edit.setText(jsonData.getString("name"));

                if (jsonData.getString("pending_status").equalsIgnoreCase("Yes")) {
                    on_off_text.setText("Active");
                    status_toggle.setChecked(true);
                } else {
                    on_off_text.setText("Inactive");
                    status_toggle.setChecked(false);
                }

                pending_status = jsonData.getString("pending_status");

                if (!TextUtils.isEmpty(jsonData.getString("device_image")))
                    UniversalImageDownloader.loadImageFromURL(AddDevice.this, jsonData.getString("device_image"),
                            device_img, R.drawable.profile_pic);
                else
                    device_img.setImageResource(R.drawable.image_device);
                deviceAddress = jsonData.getString("mac_address");
            } else {
                GlobalMethod.showToast(AddDevice.this, jsonObject.getString("message"));
            }
        }
    }

/*
      This method is use to hit url for view device information.
     */

    public void viewDeviceInfo() {
        if (SimpleHttpConnection.isNetworkAvailable(AddDevice.this)) {
            Bundle bundle = new Bundle();
            bundle.putString("showHtml", "no");
            bundle.putString("mode", "");
            bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
            bundle.putString("device_id", getIntent().getStringExtra("device_id"));
            new AsyncTaskApp(this, AddDevice.this, Urls.VIEW_DEVICE, "EDIT_DEVICE").execute(bundle);
        } else {
            GlobalMethod.showToast(AddDevice.this, Constant.network_error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            try {
                GlobalMethod.write("====saveimage" + data.getStringExtra("filepath"));
                File file = new File(data.getStringExtra("filepath"));
                filepath = data.getStringExtra("filepath");
//                prof_pic.setImageURI(Uri.fromFile(file));
//                Glide.with(AddDevice.this).load("file://" + filepath).placeholder(R.drawable.default_ptr_flip).into(device_img);
                UniversalImageDownloader.loadImageFromLocalUrl(AddDevice.this, filepath, device_img);
            } catch (OutOfMemoryError e) {
//                finish();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.backBtn:
                Intent intent = new Intent();
                intent.putExtra("added", added);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.close_keyboard:
                GlobalMethod.hideSoftKeyboard(AddDevice.this);
                break;
            case R.id.save_txt:
                if (isValidation()) {
                    if (SimpleHttpConnection.isNetworkAvailable(AddDevice.this)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("showHtml", "no");
                        bundle.putString("mode", "");
                        bundle.putString("action", action);
                        bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                        bundle.putString("name", device_name_edit.getText().toString().trim());
                        bundle.putString("unique_name", deviceName);
                        bundle.putString("mac_address", deviceAddress);
                        bundle.putString("pending_status", pending_status);
                        if (TextUtils.isEmpty(preferences.getString("lat", "")) || TextUtils.isEmpty(preferences.getString("lng", ""))) {
                            bundle.putString("last_known_lat", gps.getLatitude() + "".toString().trim());
                            bundle.putString("last_known_long", gps.getLongitude() + "".toString().trim());
                        } else {
                            bundle.putString("last_known_lat", preferences.getString("lat", ""));
                            bundle.putString("last_known_long", preferences.getString("lng", ""));
                        }
                        Bundle image_path = new Bundle();
                        image_path.putString("device_image", filepath);
                        new AsyncTask_Multipart(AddDevice.this, AddDevice.this, Urls.add_device, image_path, action).execute(bundle);
                    } else {
                        GlobalMethod.showToast(AddDevice.this, Constant.network_error);
                    }
                } else {
                    GlobalMethod.showToast(AddDevice.this, errmsg);
                }
                break;
            case R.id.save_device:
                if (isValidation()) {
                    if (SimpleHttpConnection.isNetworkAvailable(AddDevice.this)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("showHtml", "no");
                        bundle.putString("mode", "");
                        bundle.putString("action", action);
                        bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                        bundle.putString("name", device_name_edit.getText().toString().trim());
                        bundle.putString("unique_name", deviceName);
                        bundle.putString("mac_address", deviceAddress);
                        bundle.putString("pending_status", pending_status);
                        if (TextUtils.isEmpty(preferences.getString("lat", "")) || TextUtils.isEmpty(preferences.getString("lng", ""))) {
                            bundle.putString("last_known_lat", gps.getLatitude() + "".toString().trim());
                            bundle.putString("last_known_long", gps.getLongitude() + "".toString().trim());
                        } else {
                            bundle.putString("last_known_lat", preferences.getString("lat", ""));
                            bundle.putString("last_known_long", preferences.getString("lng", ""));
                        }
                        Bundle image_path = new Bundle();
                        image_path.putString("device_image", filepath);
                        new AsyncTask_Multipart(AddDevice.this, AddDevice.this, Urls.add_device, image_path, action).execute(bundle);
                    } else {
                        GlobalMethod.showToast(AddDevice.this, Constant.network_error);
                    }
                } else {
                    GlobalMethod.showToast(AddDevice.this, errmsg);
                }
                break;
            case R.id.parent_layout_login:
                GlobalMethod.hideSoftKeyboard(AddDevice.this);
                break;
            case R.id.device_img:
                startActivityForResult(new Intent(AddDevice.this, ChooseImageActivity.class), 5);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                break;
        }

    }
}


/*
 end of class here
 */
