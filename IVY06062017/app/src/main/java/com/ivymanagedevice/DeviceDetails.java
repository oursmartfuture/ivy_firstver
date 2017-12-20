package com.ivymanagedevice;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ivy.Landing_Activity;
import com.ivy.R;
import com.ivy.RoundedImageView;
import com.ivy.SimpleHttpConnection;
import com.ivy.UniversalImageDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import static com.ivy.Landing_Activity.mBluetoothLeService;

/**
 * Class Name: DeviceDetails.Class
 * Class description: This class is use to show  device information.
 * Created by Pradeep Kumar(00181)
 * Created by Singsys-043 on 11/12/2015.
 */
public class DeviceDetails extends FragmentActivity implements CallBackListenar, View.OnClickListener {
    TextView headerText, name_text, status_text, location_text, time_text,
            device_name, on_off_text, location_name, time_edit;
    CheckBox status;
    ImageView notify_bell;
    FrameLayout notificationBtn;
    RoundedImageView prof_pic;
    SharedPreferences sharedPreferences;
    SharedPreferences preferences;
    private GoogleMap googleMap;
    ImageView delete_button;
    JSONObject jsonData;
    ImageView backBtn;
    LinearLayout detailslinear, maplinear;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_info);
        sharedPreferences = getSharedPreferences(Constant.pref_settings, Context.MODE_PRIVATE);
        preferences = getSharedPreferences(Constant.pref_main, Context.MODE_PRIVATE);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        notify_bell = (ImageView) findViewById(R.id.notify_bell);
        delete_button = (ImageView) findViewById(R.id.delete_button);
        notificationBtn = (FrameLayout) findViewById(R.id.notificationBtn);
        delete_button.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.GONE);
        delete_button.setOnClickListener(this);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        maplinear = (LinearLayout) findViewById(R.id.maplinear);
        detailslinear = (LinearLayout) findViewById(R.id.detailslinear);
        prof_pic = (RoundedImageView) findViewById(R.id.prof_pic);
        headerText = (TextView) findViewById(R.id.headerText);

        name_text = (TextView) findViewById(R.id.name_text);
        status_text = (TextView) findViewById(R.id.status_text);
        location_text = (TextView) findViewById(R.id.location_text);
        GlobalMethod.write("show map value" + sharedPreferences.getString(Constant.show_map, ""));
//        if (sharedPreferences.getString(Constant.show_map, "").equalsIgnoreCase("Yes")) {
//            maplinear.setVisibility(View.VISIBLE);
//        } else {
//            maplinear.setVisibility(View.GONE);
//        }
        time_text = (TextView) findViewById(R.id.time_text);
        DeviceDetails.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        device_name = (TextView) findViewById(R.id.device_name);
        on_off_text = (TextView) findViewById(R.id.on_off_text);
        location_name = (TextView) findViewById(R.id.location_name);
        time_edit = (TextView) findViewById(R.id.time_edit);
        status = (CheckBox) findViewById(R.id.status);
        notify_bell.setVisibility(View.GONE);
        headerText.setText(getIntent().getStringExtra("device_name"));
        detailslinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(DeviceDetails.this, DeviceInfoWithBigMap.class);
                    intent.putExtra("last_known_lat", jsonData.getString("last_known_lat"));
                    intent.putExtra("last_known_long", jsonData.getString("last_known_long"));
                    intent.putExtra("last_seen_time", time_edit.getText().toString().trim());
                    intent.putExtra("address", location_name.getText().toString().trim());
                    intent.putExtra("device_name", getIntent().getStringExtra("device_name"));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        location_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalMethod.write("+++=+==");
                detailslinear.performClick();
            }
        });

        View view = (View) findViewById(android.R.id.content);
        GlobalMethod.HelveticaCE35Thin(DeviceDetails.this, view);
        GlobalMethod.AcaslonProSemiBoldTextView(DeviceDetails.this, headerText);

        if (SimpleHttpConnection.isNetworkAvailable(DeviceDetails.this)) {
            viewDeviceInfo();
        } else {
            GlobalMethod.showToast(DeviceDetails.this, "Check Your Internet Connection.");
        }
    }

    /*
      This method is use to hit url for view device information.
     */

    public void viewDeviceInfo() {
        if (SimpleHttpConnection.isNetworkAvailable(DeviceDetails.this)) {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "");
            bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
            bundle.putString("device_id", getIntent().getStringExtra("device_id"));
            new AsyncTaskApp(this, DeviceDetails.this, Urls.VIEW_DEVICE, "DeviceDetail").execute(bundle);
        } else {
            GlobalMethod.showToast(DeviceDetails.this, Constant.network_error);
        }
    }


    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap(final String title, final String latitude, final String longitutde) {


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mGoogleMap) {

                googleMap = mGoogleMap;

                // create marker
                MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),
                        Double.parseDouble(longitutde))).snippet(title);
//            marker.
                // Changing marker icon
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_dev));
                // adding marker
                googleMap.addMarker(marker);
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
//                    View view=View.inflate(DeviceDetails.this, R.layout.no_rcord,null);
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        LinearLayout info = new LinearLayout(DeviceDetails.this);
                        info.setOrientation(LinearLayout.VERTICAL);
                        TextView snippet = new TextView(DeviceDetails.this);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());
                        info.addView(snippet);
                        return info;
                    }
                });

                CameraPosition cameraPosition = new CameraPosition.Builder().target
                        (new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitutde))).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        JSONObject jobj = new JSONObject(result);
        if (action.equalsIgnoreCase("DeviceDetail")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                jsonData = jobj.getJSONArray("data").getJSONObject(0);
                GlobalMethod.write("DEVICE NAME : " + jsonData.getString("pending_status"));
//                Glide.with(DeviceDetails.this).load("103.15.232.35/ivy/admin/media/profiles/1467148492_IvyCrop1467191765290.png").placeholder(R.drawable.profile_pic).into(prof_pic);
                UniversalImageDownloader.loadImageFromURL(DeviceDetails.this, jsonData.getString("device_image"),
                        prof_pic, R.drawable.profile_pic);
                device_name.setText("ID: " + getIntent().getStringExtra("unique_name"));
                if (jsonData.getString("pending_status").equalsIgnoreCase("Yes")) {
                    on_off_text.setText("Active");
                    status.setChecked(false);
                } else {
                    on_off_text.setText("Inactive");
                    status.setChecked(true);
                }
//            GlobalMethod.write("====address" + GlobalMethod.GetAddress
//                    (DeviceDetails.this, jsonData.getString("last_known_lat"), jsonData.getString("last_known_long")));
                location_name.setText(GlobalMethod.GetAddress
                        (DeviceDetails.this, jsonData.getString("last_known_lat"), jsonData.getString("last_known_long")));
                GlobalMethod.write("time++++" + jsonData.getString("last_seen_time"));
                time_edit.setText(GlobalMethod.convert_date_dd_MM_yyyy(jsonData.getString("last_seen_time")));

                initilizeMap(GlobalMethod.GetAddress
                                (DeviceDetails.this, jsonData.getString("last_known_lat"), jsonData.getString("last_known_long")),
                        jsonData.getString("last_known_lat"), jsonData.getString("last_known_long"));
            } else {
                GlobalMethod.showToast(DeviceDetails.this, jobj.getString("message"));
            }
        } else if (action.equalsIgnoreCase("Delete_Dev")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                try {
                    if (mBluetoothLeService != null) {
                        mBluetoothLeService.disconnect(getIntent().getStringExtra("mac_address"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Landing_Activity.hBatteryMap.containsKey(getIntent().getStringExtra("mac_address")))
                    Landing_Activity.hBatteryMap.remove(getIntent().getStringExtra("mac_address"));
                GlobalMethod.showToast(DeviceDetails.this, jobj.getString("message"));
                Intent intent = new Intent();
                intent.putExtra("added", "yes");
                setResult(RESULT_OK, intent);
                finish();
            }
        }

    }

    @Override
    public void onClick(View v) {
        toDeleteDevice(DeviceDetails.this);
    }

    public void toDeleteDevice(final Activity context) {
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
        account_created.setText(getIntent().getStringExtra("device_name"));
        GlobalMethod.AcaslonProSemiBoldTextView(DeviceDetails.this, account_created);
        oktext.setTypeface(Typeface.createFromAsset(context.getAssets(), "Helvetica Neue CE 35 Thin.ttf"));
        oktext.setText("Delete");
        alerttextlogout.setText("Are you sure you want to delete " + "\n this Device?");

        alertParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SimpleHttpConnection.isNetworkAvailable(DeviceDetails.this)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                    bundle.putString("device_id", getIntent().getStringExtra("device_id"));
                    bundle.putString("mode", "");
                    new AsyncTaskApp(DeviceDetails.this, DeviceDetails.this, Urls.DELETE_DEVICE, "Delete_Dev").execute(bundle);
                    dialog.dismiss();
                } else {
                    GlobalMethod.showToast(DeviceDetails.this, Constant.network_error);
                }
            }
        });
        dialog.show();
    }
}

/*
    End of class here.
*/