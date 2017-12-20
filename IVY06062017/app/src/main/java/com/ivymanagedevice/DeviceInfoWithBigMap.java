package com.ivymanagedevice;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ivy.R;

/**
 * Class Name: DeviceInfoWithBigMap.Class
 * Class description: This class is use to show device information.
 * Created by Pradeep Kumar(00181)
 * Created by Singsys-043 on 12/12/2015.
 */
public class DeviceInfoWithBigMap extends FragmentActivity {
    TextView location_text, last_seen_time, headerText;
    private GoogleMap googleMap;
    SharedPreferences preferences;
    LinearLayout maplinear;
    SharedPreferences sharedPreferences;
    ImageView backBtn;
    SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deviceinfbigmap);
        preferences = getSharedPreferences(Constant.pref_main, Context.MODE_PRIVATE);

        headerText = (TextView) findViewById(R.id.headerText);
        location_text = (TextView) findViewById(R.id.location_text);
        last_seen_time = (TextView) findViewById(R.id.last_seen_time);
        sharedPreferences = getSharedPreferences(Constant.pref_settings, Context.MODE_PRIVATE);
        headerText.setText(getIntent().getStringExtra("device_name"));
        location_text.setText(getIntent().getStringExtra("address"));
        last_seen_time.setText(getIntent().getStringExtra("last_seen_time"));
        DeviceInfoWithBigMap.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        maplinear=(LinearLayout)findViewById(R.id.maplinear);
        View view = (View) findViewById(android.R.id.content);
        GlobalMethod.HelveticaCE35Thin(DeviceInfoWithBigMap.this, view);
        GlobalMethod.AcaslonProSemiBoldTextView(DeviceInfoWithBigMap.this, headerText);
        initilizeMap(getIntent().getStringExtra("address"), getIntent().getStringExtra("last_known_lat"),
                getIntent().getStringExtra("last_known_long"));

        if(sharedPreferences.getString(Constant.show_map, "").equalsIgnoreCase("Yes"))
        {
            maplinear.setVisibility(View.VISIBLE);
        }
        else
        {
            maplinear.setVisibility(View.GONE);
        }
        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                googleMap=mGoogleMap;
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
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        LinearLayout info = new LinearLayout(DeviceInfoWithBigMap.this);
                        info.setOrientation(LinearLayout.VERTICAL);
                        TextView snippet = new TextView(DeviceInfoWithBigMap.this);
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
}


    /*
      End of class here.
     */