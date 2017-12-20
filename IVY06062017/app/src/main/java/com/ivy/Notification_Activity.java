package com.ivy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Singsys-0105 on 1/18/2016.
 */
public class Notification_Activity extends Activity implements View.OnClickListener, CallBackListenar {
    private static final int MARKORCANCEL = 1211;
    FrameLayout notificationBtn;
    RelativeLayout menu_addlayout;
    ListView phone_list;
    private TextView no_record;
    SharedPreferences preferences;
    ArrayList<JSONObject> review_jsonobject_list = new ArrayList<JSONObject>();
    Bundle params;
    CustomListAdapter CustomListAdapter;
    LinearLayout norecord_linear;
    private Context context;
    ImageButton backBtn;
    int pageCount = 1;
    private int pos = -1;
    private SwipeRefreshLayout swipeRefresh, swipeRefreshNoRecord;
    private boolean isLoadMore = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header_withlistview);
        notificationBtn = (FrameLayout) findViewById(R.id.notificationBtn);
        menu_addlayout = (RelativeLayout) findViewById(R.id.menu_addlayout);
        notificationBtn.setVisibility(View.GONE);
        menu_addlayout.setVisibility(View.GONE);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        preferences = getSharedPreferences(Constant.pref_main, 0);
        phone_list = (ListView) findViewById(R.id.total_follow_list);
        params = new Bundle();
        context = Notification_Activity.this;
        phone_list.setDividerHeight(1);
//        phone_list.setMode(PullToRefreshBase.Mode.DISABLED);
        norecord_linear = (LinearLayout) findViewById(R.id.norecord_linear);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshNoRecord = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshNoRecord);
        no_record = (TextView) findViewById(R.id.no_record);
        TextView headerText = (TextView) findViewById(R.id.headerText);
        headerText.setText(R.string.notification);
        GlobalMethod.AcaslonProSemiBoldTextView(Notification_Activity.this, headerText);

        if (SimpleHttpConnection.isNetworkAvailable(Notification_Activity.this)) {
            params.putString("user_id", preferences.getString(Constant.id_user, ""));
            if (getIntent().getStringExtra("from_page").equalsIgnoreCase("from_landing")) {
                params.putString("alert_type", "1");

            } else if (getIntent().getStringExtra("from_page").equalsIgnoreCase("from_settings")) {
                params.putString("alert_type", "2");
            }

            params.putString("page_no", "" + pageCount);
            new AsyncTaskApp(Notification_Activity.this, Notification_Activity.this, Urls.notification_list, "Listing_Alerts").execute(params);
        } else {
            GlobalMethod.showToast(Notification_Activity.this, Constant.network_error);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (SimpleHttpConnection.isNetworkAvailable(Notification_Activity.this)) {
                    review_jsonobject_list.clear();
                    pageCount = 1;
                    isLoadMore = true;
                    params.putString("user_id", preferences.getString(Constant.id_user, ""));
                    if (getIntent().getStringExtra("from_page").equalsIgnoreCase("from_landing")) {
                        params.putString("alert_type", "1");
                    } else if (getIntent().getStringExtra("from_page").equalsIgnoreCase("from_settings")) {
                        params.putString("alert_type", "2");
                    }
                    params.putString("page_no", "" + pageCount);
                    new AsyncTaskApp(Notification_Activity.this, Notification_Activity.this, Urls.notification_list, "Listing_Alerts").execute(params);
                } else {
                    GlobalMethod.showToast(Notification_Activity.this, Constant.network_error);
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
                if (SimpleHttpConnection.isNetworkAvailable(Notification_Activity.this)) {
                    review_jsonobject_list.clear();
                    pageCount = 1;
                    isLoadMore = true;
                    params.putString("user_id", preferences.getString(Constant.id_user, ""));
                    if (getIntent().getStringExtra("from_page").equalsIgnoreCase("from_landing")) {
                        params.putString("alert_type", "1");
                    } else if (getIntent().getStringExtra("from_page").equalsIgnoreCase("from_settings")) {
                        params.putString("alert_type", "2");
                    }
                    params.putString("page_no", "" + pageCount);
                    new AsyncTaskApp(Notification_Activity.this, Notification_Activity.this, Urls.notification_list, "Listing_Alerts").execute(params);
                } else {
                    GlobalMethod.showToast(Notification_Activity.this, Constant.network_error);
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
        }
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        Set_Result_view(result);
    }

    void Set_Result_view(String result) {
//        phone_list.onRefreshComplete();
        swipeRefresh.setRefreshing(false);
        swipeRefreshNoRecord.setRefreshing(false);
        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray productArray, info;
            if (result != null) {
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    productArray = jsonObject.optJSONArray("data");
                    if (productArray.length() > 0) {
                        swipeRefresh.setVisibility(View.VISIBLE);
                        swipeRefreshNoRecord.setVisibility(View.GONE);
                        if (productArray.length() < 10) {
                            isLoadMore = false;
                        } else if (productArray.length() == 10) {
                            isLoadMore = true;
                        }
//                        norecord_linear.setVisibility(View.GONE);
                        no_record.setVisibility(View.GONE);
                        for (int i = 0; i < productArray.length(); i++) {
                            JSONObject productObject = productArray.getJSONObject(i);
                            review_jsonobject_list.add(productObject);
                        }
                    } else {
//                        phone_list.setEmptyView(norecord_linear);
                        if (pageCount == 1) {
                            swipeRefresh.setVisibility(View.GONE);
                            swipeRefreshNoRecord.setVisibility(View.VISIBLE);
                        }
//                        norecord_linear.setVisibility(View.VISIBLE);
//                        no_record.setVisibility(View.VISIBLE);
                    }
                    if (CustomListAdapter == null) {
                        phone_list.setVisibility(View.VISIBLE);
                        CustomListAdapter = new CustomListAdapter(review_jsonobject_list);
                        phone_list.setAdapter(CustomListAdapter);
//                        phone_list.setEmptyView(norecord_linear);
                    } else {
                        CustomListAdapter.notifyDataSetChanged();
                    }
                } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                    JSONObject jobj_blank = new JSONObject();
                    review_jsonobject_list.add(jobj_blank);
                    review_jsonobject_list.clear();
                    norecord_linear.setVisibility(View.VISIBLE);
                    no_record.setVisibility(View.VISIBLE);
//                    phone_list.setEmptyView(norecord_linear);
                    swipeRefresh.setVisibility(View.GONE);
                    swipeRefreshNoRecord.setVisibility(View.VISIBLE);
//                    phone_list.setEmptyView(no_record);
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
            return innerJsonObj.get(arg0);
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        private class ViewHolder {
            RoundedImageView notify_profile_icon;
            TextView notify_txt, notify_time;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = inflat.inflate(R.layout.notification_view, parent, false);
                holder = new ViewHolder();
                holder.notify_profile_icon = (RoundedImageView) convertView.findViewById(R.id.notify_profile_icon);
                holder.notify_profile_icon.setVisibility(View.GONE);
                holder.notify_txt = (TextView) convertView.findViewById(R.id.notify_txt);
                holder.notify_time = (TextView) convertView.findViewById(R.id.notify_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                if (!TextUtils.isEmpty(innerJsonObj.get(position).getString("notification_msg"))) {
                    holder.notify_txt.setText(innerJsonObj.get(position).getString("notification_msg"));
                }
//
                if (!TextUtils.isEmpty(innerJsonObj.get(position).getString("addedon"))) {
                    holder.notify_time.setText(innerJsonObj.get(position).getString("addedon"));
                } else {
                    holder.notify_time.setText("");
                }

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (innerJsonObj.get(position).getString("redirection").equalsIgnoreCase("0")) {
                                pos = position;
                                openActivity(innerJsonObj.get(position).getString("notification_data"));
                            } else {
                                GlobalMethod.showToast(Notification_Activity.this, getString(R.string.already_safe));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (position == innerJsonObj.size() - 1) {
                if (isLoadMore) {
                    if (SimpleHttpConnection.isNetworkAvailable(Notification_Activity.this)) {
                        pageCount++;
                        params.putString("user_id", preferences.getString(Constant.id_user, ""));
                        if (getIntent().getStringExtra("from_page").equalsIgnoreCase("from_landing")) {
                            params.putString("alert_type", "1");
                        } else if (getIntent().getStringExtra("from_page").equalsIgnoreCase("from_settings")) {
                            params.putString("alert_type", "2");
                        }
                        params.putString("page_no", "" + pageCount);
                        new AsyncTaskApp(Notification_Activity.this, Notification_Activity.this, Urls.notification_list, "Listing_Alerts").execute(params);
                    } else {
                        GlobalMethod.showToast(Notification_Activity.this, Constant.network_error);
                    }
                }
            }

            return convertView;
        }
    }

    private void openActivity(String json) {
        JSONObject data = null;
        try {
            data = new JSONObject(json);
            Intent notificationIntent = new Intent(Notification_Activity.this, Emergency_Alert_Receiver.class);
            GlobalMethod.write("ACTION : " + data.has("action"));
            if (data.has("action") && data.getString("action").trim().equalsIgnoreCase("alert")) {
                GlobalMethod.write("ACTION : MESSAGE : " + data.has("message"));
                notificationIntent.putExtra("alert_id", data.getString("alert_id"));
                notificationIntent.putExtra("sender_name", data.getString("sender_name"));
                startActivityForResult(notificationIntent, MARKORCANCEL);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MARKORCANCEL) {
//            try {
//                review_jsonobject_list.get(pos).put("redirection", "1");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
}
