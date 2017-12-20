package com.ivy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.globalclasses.Constant.longitude;

/**
 * Created by Singsys-0105 on 5/27/2016.
 */
public class Emergency_Alert_Receiver extends FragmentActivity implements View.OnClickListener, CallBackListenar {
    public static final String ALERT_RECEIVER = "ALERT_RECEIVER";
    FrameLayout notificationBtn;
    private AsyncTask mAsyncTask;
    LatLng origin, dest;
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
    private Button coming_btn;
    private boolean first = true;
    private Uri uriAudio;
    String alert_id;
    private Button disable_coming_btn;
    private Button already_marked_safe;
    GoogleMap googleMap;
    LinearLayout maplinear;
    int height = 150, width = 150;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_alert);
        notificationBtn = (FrameLayout) findViewById(R.id.notificationBtn);
        maplinear = (LinearLayout) findViewById(R.id.maplinear);
        //inside of onCreate method should work
        maplinear.post(new Runnable() {
            @Override
            public void run() {
                //maybe also works height = ll.getLayoutParams().height;

                height = maplinear.getHeight();

            }
        });

        cross_icon = (ImageView) findViewById(R.id.cross_icon);
        cross_icon.setOnClickListener(this);
        audio_record = (ImageView) findViewById(R.id.audio_record);
        audio_record.setTag(false);
        notify_profile_icon = (RoundedImageView) findViewById(R.id.notify_profile_icon);
        notify_profile_icon.setVisibility(View.VISIBLE);
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
        context = Emergency_Alert_Receiver.this;
        noitem = (TextView) findViewById(R.id.noitem);
        safe_btn = (Button) findViewById(R.id.safe_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        notify_txt = (TextView) findViewById(R.id.notify_txt);
        coming_btn = (Button) findViewById(R.id.coming_btn);
        coming_btn.setOnClickListener(this);
        coming_btn.setVisibility(View.VISIBLE);

        disable_coming_btn = (Button) findViewById(R.id.disable_coming_btn);
        already_marked_safe = (Button) findViewById(R.id.already_marked_safe);
        already_marked_safe.setVisibility(View.GONE);
        disable_coming_btn.setVisibility(View.GONE);
        safe_btn.setVisibility(View.GONE);
//        cancel_btn.setOnClickListener(this);
        cancel_btn.setVisibility(View.GONE);
        preferences = getSharedPreferences(Constant.pref_main, 0);
        try {
            origin = new LatLng(Double.parseDouble(GlobalMethod.getSavedPreferences(this, Constant.lalitude, "0.0")), Double.parseDouble(GlobalMethod.getSavedPreferences(this, longitude, "0.0")));
        } catch (Exception e) {
            e.printStackTrace();
            origin = new LatLng(0.0, 0.0);
        }
        phone_list = (CustomListView) findViewById(R.id.total_follow_list);
        phone_list.setExpanded(true);
        phone_list.setDividerHeight(1);
        TextView tvHeader = (TextView) findViewById(R.id.headerText);
//        findViewById(R.id.header_bg).setVisibility(View.GONE);
//        findViewById(R.id.list_layout).setVisibility(View.GONE);
        tvHeader.setText(R.string.emergencyAlert);
        GlobalMethod.AcaslonProSemiBoldTextView(Emergency_Alert_Receiver.this, tvHeader);
        /*phone_list.getRefreshableView();
        phone_list.getRefreshableView().setDividerHeight(1);*/
        alert_id = getIntent().getStringExtra("alert_id");
        onNewIntent(getIntent());

    }

    BroadcastReceiver alertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GlobalMethod.write("RECEIVER : " + alert_id + ", RECEIVED : " + intent.getStringExtra("alert_id"));
            if (alert_id.equalsIgnoreCase(intent.getStringExtra("alert_id"))) {
                coming_btn.setVisibility(View.GONE);
                already_marked_safe.setVisibility(View.VISIBLE);
//                coming_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        GlobalMethod.showCustomToastInCenter(Emergency_Alert_Receiver.this, "This alert has been Cancelled or Marked as Safe");
//                        finish();
//                    }
//                });
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(alertReceiver, new IntentFilter(ALERT_RECEIVER));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alertReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        alert_id = intent.getStringExtra("alert_id");
        GlobalMethod.write("====alert_id" + alert_id);
        if (SimpleHttpConnection.isNetworkAvailable(Emergency_Alert_Receiver.this)) {
            params.putString("user_id", preferences.getString(Constant.id_user, ""));
            params.putString("alert_id", alert_id);
            new AsyncTaskApp(this, Emergency_Alert_Receiver.this, Urls.receiver_alert_detail, "Sender_Alert_Details").execute(params);
        } else {
            GlobalMethod.showToast(Emergency_Alert_Receiver.this, Constant.network_error);
        }
        if (audio_record != null) {
            audio_record.setImageResource(android.R.drawable.ic_media_play);
            audio_record.setTag(false);
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
                            mediaPlayer.reset();
                            mediaPlayer.release();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        mediaPlayer = null;
                    }

                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(getApplicationContext(), uriAudio);//setting the path of the recorded voice in media player.
                        mediaPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mediaPlayer != null && uriAudio != null) {
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
                    }
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        audio_record.setImageResource(android.R.drawable.ic_media_play);
                    }
                }
                audio_record.setTag(!b);
                break;
            case R.id.backBtn:
            case R.id.cross_icon:
                finish();
                break;
            case R.id.coming_btn:
                if (SimpleHttpConnection.isNetworkAvailable(this)) {
                    params.putString("alert_id", alert_id);
                    params.putString("user_id", preferences.getString(Constant.id_user, ""));
                    params.putString("mode", "");
                    new AsyncTaskApp(this, this, Urls.iAmComing, "Mark_Safe").execute(params);

                } else {
                    GlobalMethod.showToast(this, Constant.network_error);
                }
                break;
//            case R.id.cancel_btn:
//                if (SimpleHttpConnection.isNetworkAvailable(Emergency_Alert_Receiver.this)) {
//                    params.putString("alert_id", getIntent().getStringExtra("alert_id"));
//                    params.putString("mode", "");
//                    new AsyncTaskApp(Emergency_Alert_Receiver.this, Emergency_Alert_Receiver.this, Urls.cancel_alert, "Cancel_Alert").execute(params);
//                } else {
//                    GlobalMethod.showToast(Emergency_Alert_Receiver.this, Constant.network_error);
//                }
//                break;
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
                if (jsonObject.getString("success").trim().equalsIgnoreCase("1")) {
                    JSONObject info_object = data.optJSONObject("info");
                    if (info_object != null) {
//                        JSONObject info_object = mJsonArray.getJSONObject(0);

                        if (info_object.has("is_safe") && info_object.getString("is_safe").equalsIgnoreCase("1")) {
                            GlobalMethod.showToast(Emergency_Alert_Receiver.this, "This alert has already been Cancelled or Marked as Safe");
                            finish();
                            return;
                        }

                        coming_btn.setVisibility(View.VISIBLE);
                        already_marked_safe.setVisibility(View.GONE);

                        if (info_object.has("is_coming") && info_object.getString("is_coming").equalsIgnoreCase("1")) {
                            coming_btn.setVisibility(View.GONE);
                            disable_coming_btn.setVisibility(View.VISIBLE);
                        }
//                        GlobalMethod.write("Last message has been sent on " + info_object.getString("send_at") + " (IST) near " + info_object.getString("address") + " and " + info_object.getString("audio_duration") + " seconds recording." + "\n\nContact: " + info_object.optString("sender_number"));
                        GlobalMethod.write("Last message has been sent " + info_object.getString("time_age") + " near " + info_object.getString("address") + " and " + info_object.getString("audio_duration") + " seconds recording." + "\n\nContact: " + info_object.optString("sender_number"));

                        new AsyncTask<JSONObject, Void, Void>() {

                            @Override
                            protected Void doInBackground(JSONObject... params) {
//                                if (!getIntent().hasExtra("audiopath")) {
                                try {
                                    JSONObject info_object = params[0];
                                    try {
                                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                            mediaPlayer.stop();
                                            mediaPlayer.reset();
                                            mediaPlayer.release();
                                        }
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                        mediaPlayer = null;
                                    }

                                    try {
                                        uriAudio = Uri.parse(info_object.optString("audio_file").replace(".m4a", ".mp3"));
                                        mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(info_object.optString("audio_file").replace(".m4a", ".mp3")));//setting the path of the recorded voice in media player.
                                        mediaPlayer.prepare();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                }
                                return null;
                            }
                        }.execute(info_object);

//                        try {
//                            uriAudio = Uri.parse(info_object.optString("audio_file"));
//                            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(info_object.optString("audio_file")));//setting the path of the recorded voice in media player.
//                            mediaPlayer.prepare();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        try {
                            UniversalImageDownloader.loadImageFromURL(Emergency_Alert_Receiver.this, info_object.getString("sender_profile_img"),
                                    notify_profile_icon, R.drawable.profile_pic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        notify_txt.setText("Last message has been sent " + info_object.getString("time_age") + " near " + info_object.getString("address") + " and " + info_object.getString("audio_duration") + " seconds recording." + "\n\nContact: " + info_object.optString("sender_number"));
                        GlobalMethod.write("Last message has been sent " + info_object.getString("time_age") + " near " + info_object.getString("address") + " and " + info_object.getString("audio_duration") + " seconds recording." + "\n\nContact: " + info_object.optString("sender_number"));

                        try {
                            initilizeMap(info_object.getString("address"), info_object.getString("latitude"), info_object.getString("longitude"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    review_jsonobject_list.clear();
                    productArray = data.optJSONArray("receivers");
                    if (productArray != null && productArray.length() > 0) {
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


                } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                    JSONObject jobj_blank = new JSONObject();
                    review_jsonobject_list.add(jobj_blank);
                    review_jsonobject_list.clear();
                    noitem.setVisibility(View.VISIBLE);
                    phone_list.setEmptyView(noitem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void Set_Result_safe(String result) {
        try {
            if (result != null) {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    GlobalMethod.showToast(Emergency_Alert_Receiver.this, jsonObject.getString("message"));
                    setResult(RESULT_OK, new Intent());
                    finish();
//                    finish();
                } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                    GlobalMethod.showToast(Emergency_Alert_Receiver.this, jsonObject.getString("message"));
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
                    GlobalMethod.showToast(Emergency_Alert_Receiver.this, jsonObject.getString("message"));

                } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                    GlobalMethod.showToast(Emergency_Alert_Receiver.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                    UniversalImageDownloader.loadImageFromURL(Emergency_Alert_Receiver.this,
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

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap(final String title, final String latitude, final String longitutde) {

        GlobalMethod.write("==titleinitilizeMap" + title + "==" + latitude + "==" + longitutde);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mGoogleMap) {
                // create marker
                if (googleMap != null) {
                    googleMap.clear();
                }
                googleMap = mGoogleMap;

//                origin = new LatLng(26.8470, 80.8763);


                MarkerOptions markerOptions1 = new MarkerOptions().position(origin);
                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_dev));
                MarkerOptions marker = new MarkerOptions().position(dest = new LatLng(Double.parseDouble(latitude),
                        Double.parseDouble(longitutde))).snippet(title);
                GlobalMethod.write("==title" + title + "==" + latitude + "==" + longitutde);

                try {
                    if (getDistance(Double.parseDouble(GlobalMethod.getSavedPreferences(Emergency_Alert_Receiver.this, Constant.lalitude, "0.0")), Double.parseDouble(GlobalMethod.getSavedPreferences(Emergency_Alert_Receiver.this, longitude, "0.0")), Double.parseDouble(latitude),
                            Double.parseDouble(longitutde)) > 50) {
                        mGoogleMap.addMarker(markerOptions1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//            marker.
                // Changing marker icon

//                Drawable myIcon = getResources().getDrawable( R.drawable.location_dev);
//                marker.icon(BitmapDescriptorFactory.fromBitmap(adjust(myIcon)));

                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_gray));
                // adding marker
                mGoogleMap.addMarker(marker);

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //Please use fix height popup
                        float container_height = getResources().getDimension(R.dimen.DIP);
                        Projection projection = googleMap.getProjection();
                        Point markerScreenPosition = projection.toScreenLocation(marker.getPosition());
                        Point pointHalfScreenAbove = new Point(markerScreenPosition.x, (int) (markerScreenPosition.y - (container_height / 2)));
                        LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointHalfScreenAbove);
                        CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
                        googleMap.animateCamera(center);
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
                        GlobalMethod.write("===marker" + marker.getSnippet());
                        if (marker.getSnippet() != null) {
                            LinearLayout info = new LinearLayout(Emergency_Alert_Receiver.this);
                            info.setOrientation(LinearLayout.VERTICAL);
                            TextView snippet = new TextView(Emergency_Alert_Receiver.this);
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());
                            info.addView(snippet);
                            return info;
                        } else {
                            return null;
                        }
                    }
                });


                CameraPosition cameraPosition = new CameraPosition.Builder().target
                        (new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitutde))).zoom(12).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // Getting URL to the Google Directions API


                if (origin != null && dest != null) {
//                    origin = new LatLng(26.8470, 80.8763);
                    String url = getDirectionsUrl(origin, dest);
                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    mAsyncTask = downloadTask.execute(url);
                }

            }
        });

    }

    private Bitmap adjust(Drawable d) {
        int to = Color.RED;

        //Need to copy to ensure that the bitmap is mutable.
        Bitmap src = ((BitmapDrawable) d).getBitmap();
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
        for (int x = 0; x < bitmap.getWidth(); x++)
            for (int y = 0; y < bitmap.getHeight(); y++)
                if (match(bitmap.getPixel(x, y)))
                    bitmap.setPixel(x, y, to);

        return bitmap;
    }

    private static final int[] FROM_COLOR = new int[]{49, 179, 110};
    private static final int THRESHOLD = 3;

    private boolean match(int pixel) {
        //There may be a better way to match, but I wanted to do a comparison ignoring
        //transparency, so I couldn't just do a direct integer compare.
        return Math.abs(Color.red(pixel) - FROM_COLOR[0]) < THRESHOLD &&
                Math.abs(Color.green(pixel) - FROM_COLOR[1]) < THRESHOLD &&
                Math.abs(Color.blue(pixel) - FROM_COLOR[2]) < THRESHOLD;
    }

    //addeed by arpit
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";
            GlobalMethod.write("url" + url[0]);
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            GlobalMethod.write("result" + result);

            mAsyncTask = parserTask.execute(result);

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing datas
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();
//            Utils.SOP("=================result" + result);
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));

                        LatLng position = new LatLng(lat, lng);
                        GlobalMethod.write("LAT : " + lat + ", j = " + j);
                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLUE);
//                lineOptions.color(R.color.background_color);

                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null && points.size() >= 5) {
                    googleMap.addPolyline(lineOptions);
                    if (origin != null && dest != null) {
                        try {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(origin);
                            builder.include(dest);
                            LatLngBounds bounds = builder.build();
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                            width = getResources().getDisplayMetrics().widthPixels;
//                        int height = getResources().getDisplayMetrics().heightPixels;
                            int padding = (int) (height * 0.30); // offset from edges of the map 12% of screen
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                            googleMap.animateCamera(cu);
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(dest);
                                builder.include(origin);
                                LatLngBounds bounds = builder.build();
                                width = getResources().getDisplayMetrics().widthPixels;
//                            int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (height * 0.30); // offset from edges of the map 12% of screen
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                                googleMap.animateCamera(cu);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;

    }

    public static float getDistance(double startLati, double startLongi, double goalLati, double goalLongi) {
        float[] resultArray = new float[99];
        Location.distanceBetween(startLati, startLongi, goalLati, goalLongi, resultArray);
        GlobalMethod.write("====getDistance" + resultArray[0]);
        return resultArray[0];
    }
}

