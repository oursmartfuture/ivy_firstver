package com.ivy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.CustomListView;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Singsys-0105 on 1/20/2016.
 */
public class Emergency_Activity extends FragmentActivity implements View.OnClickListener, CallBackListenar {
    FrameLayout notificationBtn;
    ImageView cross_icon, audio_record;
    CustomListView phone_list;
    Handler mServiceHandler;
    RoundedImageView notify_profile_icon;
    TextView noitem, notify_txt;
    SharedPreferences preferences;
    ScrollView scrollView;
    ArrayList<JSONObject> review_jsonobject_list = new ArrayList<JSONObject>();
    Bundle params;
    Button safe_btn, cancel_btn;
    CustomListAdapter CustomListAdapter;
    MediaPlayer mediaPlayer;
    private Context context;
    ImageButton backBtn;
    private Uri uriAudio;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_alert);
        notificationBtn = (FrameLayout) findViewById(R.id.notificationBtn);
        cross_icon = (ImageView) findViewById(R.id.cross_icon);
        cross_icon.setOnClickListener(this);
        audio_record = (ImageView) findViewById(R.id.audio_record);
        audio_record.setTag(false);
        notify_profile_icon = (RoundedImageView) findViewById(R.id.notify_profile_icon);
        notify_profile_icon.setVisibility(View.GONE);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        cross_icon.setVisibility(View.GONE);
        audio_record.setOnClickListener(this);
        notificationBtn.setVisibility(View.GONE);
        mediaPlayer = new MediaPlayer();
        params = new Bundle();
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        context = Emergency_Activity.this;
        noitem = (TextView) findViewById(R.id.noitem);
        safe_btn = (Button) findViewById(R.id.safe_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        notify_txt = (TextView) findViewById(R.id.notify_txt);
        safe_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        preferences = getSharedPreferences(Constant.pref_main, 0);
        phone_list = (CustomListView) findViewById(R.id.total_follow_list);
        phone_list.setExpanded(true);
        phone_list.setDividerHeight(1);
        TextView tvHeader = (TextView) findViewById(R.id.headerText);
        tvHeader.setText(R.string.emergencyAlert);
        /*phone_list.getRefreshableView();
        phone_list.getRefreshableView().setDividerHeight(1);*/
        if (SimpleHttpConnection.isNetworkAvailable(Emergency_Activity.this)) {
            params.putString("user_id", preferences.getString(Constant.id_user, ""));
            params.putString("alert_id", getIntent().getStringExtra("alert_id"));
            new AsyncTaskApp(this, Emergency_Activity.this, Urls.sender_alert_detail, "Sender_Alert_Details").execute(params);
        } else {
            GlobalMethod.showToast(Emergency_Activity.this, Constant.network_error);
        }
        try {
            if (getIntent().hasExtra("audiopath")) {
                mediaPlayer.reset();
                uriAudio = Uri.parse(getIntent().getStringExtra("audiopath"));
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(getIntent().getStringExtra("audiopath")));//setting the path of the recorded voice in media player.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audio_record:
                boolean b = (boolean) audio_record.getTag();
                if (!b) {
//                    try {
//                        GlobalMethod.write("IS FIRST : " + first);
//                        if (first) {
//                            mediaPlayer.prepare();
//                            first = false;
//                        }
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    try {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(getApplicationContext(), uriAudio);//setting the path of the recorded voice in media player.
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    audio_record.setImageResource(android.R.drawable.ic_media_pause);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                            if (audio_record != null) {
                                ((ImageView) audio_record).setImageResource(android.R.drawable.ic_media_play);
                                audio_record.setTag(false);
//                                audio_record = null;
                            }
                        }
                    });
                } else {
                    mediaPlayer.stop();
                    audio_record.setImageResource(android.R.drawable.ic_media_play);
                }
                audio_record.setTag(!b);
                break;
            case R.id.backBtn:
            case R.id.cross_icon:
                finish();
                break;
            case R.id.safe_btn:
                if (SimpleHttpConnection.isNetworkAvailable(Emergency_Activity.this)) {
                    params.putString("alert_id", getIntent().getStringExtra("alert_id"));
                    params.putString("mode", "");
                    new AsyncTaskApp(Emergency_Activity.this, Emergency_Activity.this, Urls.mark_safe, "Mark_Safe").execute(params);
                } else {
                    GlobalMethod.showToast(Emergency_Activity.this, Constant.network_error);
                }
                break;
            case R.id.cancel_btn:
                if (SimpleHttpConnection.isNetworkAvailable(Emergency_Activity.this)) {
                    params.putString("alert_id", getIntent().getStringExtra("alert_id"));
                    params.putString("mode", "");
                    new AsyncTaskApp(Emergency_Activity.this, Emergency_Activity.this, Urls.cancel_alert, "Cancel_Alert").execute(params);
                } else {
                    GlobalMethod.showToast(Emergency_Activity.this, Constant.network_error);
                }
                break;

        }
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {


        if (action == "Sender_Alert_Details") {
            Set_Result_view(result);
        } else if (action == "Mark_Safe") {
            Set_Result_safe(result);
        } else if (action == "Cancel_Alert") {
            Set_Result_cancel(result);
        }


    }

    void Set_Result_view(String result) {
        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray productArray, info;
            JSONObject data = jsonObject.getJSONObject("data");
            if (result != null) {
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    info = data.optJSONArray("info");
                    JSONObject info_object = info.getJSONObject(0);
//                    notify_txt.setText("Last message has been sent on " + info_object.getString("send_at") + " (IST) near " + info_object.getString("address") + " and " + info_object.getString("audio_duration") + " seconds recording.");
                    notify_txt.setText("Last message has been sent " + info_object.getString("time_age") + " near " + info_object.getString("address") + " and " + info_object.getString("audio_duration") + " seconds recording.");
                    GlobalMethod.write("Last message has been sent " + info_object.getString("time_age") + " near " + info_object.getString("address") + " and " + info_object.getString("audio_duration") + " seconds recording." + "\n\nContact: " + info_object.optString("sender_number"));

                    try {
                        initilizeMap(info_object.getString("address"), info_object.getString("latitude"), info_object.getString("longitude"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    productArray = data.optJSONArray("receivers");
                    if (productArray.length() > 0) {
                        noitem.setVisibility(View.GONE);
                        for (int i = 0; i < productArray.length(); i++) {
                            JSONObject productObject = productArray.getJSONObject(i);
                            review_jsonobject_list.add(productObject);
                        }
                    } else {
                        noitem.setVisibility(View.VISIBLE);
                        phone_list.setEmptyView(noitem);
                    }
                    if (CustomListAdapter == null) {
                        phone_list.setVisibility(View.VISIBLE);
                        CustomListAdapter = new CustomListAdapter(review_jsonobject_list);
                        phone_list.setAdapter(CustomListAdapter);
//                        phone_list.requestLayout();
                    } else {
                        CustomListAdapter.notifyDataSetChanged();
                    }

                    new AsyncTask<JSONObject, Void, Void>() {

                        @Override
                        protected Void doInBackground(JSONObject... params) {
                            if (!getIntent().hasExtra("audiopath")) {
                                try {
                                    JSONObject info_object = params[0];
                                    uriAudio = Uri.parse(info_object.optString("audio_file"));
                                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(info_object.optString("audio_file")));//setting the path of the recorded voice in media player.
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return null;
                        }
                    }.execute(info_object);


                } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                    JSONObject jobj_blank = new JSONObject();
                    review_jsonobject_list.add(jobj_blank);
                    review_jsonobject_list.clear();
                    noitem.setVisibility(View.VISIBLE);
                    phone_list.setEmptyView(noitem);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void Set_Result_safe(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    GlobalMethod.showToast(Emergency_Activity.this, jsonObject.getString("message"));
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                    GlobalMethod.showToast(Emergency_Activity.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void Set_Result_cancel(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    GlobalMethod.showToast(Emergency_Activity.this, jsonObject.getString("message"));
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                    GlobalMethod.showToast(Emergency_Activity.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap(final String title, final String latitude, final String longitutde) {

        GlobalMethod.write("==titleinitilizeMap" + title + "==" + latitude + "==" + longitutde);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mGoogleMap) {
                // create marker
                MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),
                        Double.parseDouble(longitutde))).snippet(title);
                GlobalMethod.write("==title" + title + "==" + latitude + "==" + longitutde);
//            marker.
                // Changing marker icon
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_dev));
                // adding marker
                mGoogleMap.addMarker(marker);
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        float container_height = getResources().getDimension(R.dimen.DIP);
                        Projection projection = mGoogleMap.getProjection();
                        Point markerScreenPosition = projection.toScreenLocation(marker.getPosition());
                        Point pointHalfScreenAbove = new Point(markerScreenPosition.x, (int) (markerScreenPosition.y - (container_height / 2)));
                        LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointHalfScreenAbove);
                        CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
                        mGoogleMap.animateCamera(center);
                        marker.showInfoWindow();
                        return true;
                    }
                });
                mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        LinearLayout info = new LinearLayout(Emergency_Activity.this);
                        info.setOrientation(LinearLayout.VERTICAL);
                        TextView snippet = new TextView(Emergency_Activity.this);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());
                        info.addView(snippet);
                        return info;
                    }
                });
                CameraPosition cameraPosition = new CameraPosition.Builder().target
                        (new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitutde))).zoom(12).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    public class CustomListAdapter extends BaseAdapter {
        ArrayList<JSONObject> innerJsonObj = new ArrayList<JSONObject>();
        LayoutInflater inflat;


        public CustomListAdapter(ArrayList<JSONObject> frndzlist) {
            innerJsonObj = frndzlist;
            inflat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        private class ViewHolder {
            RoundedImageView profile_pic;
            TextView name;
            LinearLayout linear_app_user;
            ImageView added_or_not;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflat.inflate(R.layout.view_my_contact, parent, false);
                holder = new ViewHolder();
                holder.profile_pic = (RoundedImageView) convertView.findViewById(R.id.app_user_pic);
                holder.added_or_not = (ImageView) convertView.findViewById(R.id.added_or_not);
                holder.linear_app_user = (LinearLayout) convertView.findViewById(R.id.linear_app_user);
                holder.linear_app_user.setVisibility(View.GONE);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {

                if (!TextUtils.isEmpty(innerJsonObj.get(position).getString("profile_image"))) {
                    UniversalImageDownloader.loadImageFromURL(Emergency_Activity.this,
                            GlobalMethod.encodeURL(innerJsonObj.get(position).getString("profile_image")),
                            holder.profile_pic, R.drawable.profile_pic);
                } else {
                    holder.profile_pic.setImageResource(R.drawable.profile_pic);
                }
                if (!TextUtils.isEmpty(innerJsonObj.get(position).getString("receiver_name"))) {
                    holder.name.setText(innerJsonObj.get(position).getString("receiver_name"));
                } else {
                    holder.name.setText("No Name");
                }

                if (innerJsonObj.get(position).getString("coming").equalsIgnoreCase("1")) {
//                    holder.name.setTextColor(getResources().getColor(R.color.general_heading));
                    holder.name.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
//                    holder.name.setTextColor(getResources().getColor(R.color.black));
                    holder.name.setTypeface(Typeface.DEFAULT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }


}
