package com.ivy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.HashMap;

/**
 * Class Name: Add_App_User.Class
 * Class description: This class will show the total app user register on application.
 * Here we will be able to search any app user across app.
 * And can add if and delete from the contact.
 * Created by Pradeep Kunar(00181)
 * Created  on 23/11/2015.
 */

public class Add_App_User extends Activity implements View.OnClickListener, CallBackListenar {
    ImageButton backBtn;
    TextView headerText;
    EditText search_etxt;
    ImageView search_icon;
    SharedPreferences preferences;
    HashMap<String, Integer> pagelist;
    ListView contactList;
    int page = 1;
    boolean is_load_more = false;
    //    TextView no_record;
    ArrayList<JSONObject> appUserList;
    int globaLPosition;
    ListAdapters listAdapters;
    String added = "0";
    LinearLayout norecord_linear;
    private SwipeRefreshLayout swipeRefresh;
    private TextView number_format, scrollToRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app_user);

        preferences = getSharedPreferences(Constant.pref_main, 0);

        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        headerText = (TextView) findViewById(R.id.headerText);
        scrollToRefresh = (TextView) findViewById(R.id.scrollToRefresh);
        number_format = (TextView) findViewById(R.id.number_format);
        number_format.setVisibility(View.GONE);
        scrollToRefresh.setVisibility(View.GONE);
        headerText.setText(getIntent().getStringExtra("header_name"));
        GlobalMethod.AcaslonProSemiBoldTextView(Add_App_User.this, headerText);
        search_etxt = (EditText) findViewById(R.id.search_etxt);
        search_icon = (ImageView) findViewById(R.id.search_icon);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        norecord_linear = (LinearLayout) findViewById(R.id.norecord_linear);
        contactList = (ListView) findViewById(R.id.contactList);
//        no_record = (TextView) findViewById(R.id.no_record);
        Add_App_User.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        pagelist = new HashMap<>();
        pagelist.put("page", page);

        appUserList = new ArrayList<>();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                page = 1;
                pagelist.put("page", page);
                is_load_more = true;

                if (SimpleHttpConnection.isNetworkAvailable(Add_App_User.this)) {
                    loadContacts();
                } else {
                    GlobalMethod.showToast(Add_App_User.this, "Check Your Internet Connection.");
                }
            }
        });

//        contactList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
//            @Override
//            public void onLastItemVisible() {
//                // TODO Auto-generated method stub
//                if (is_load_more == false) {
//                    page = page + 1;
//                    pagelist.put("page", page);
//                    if (SimpleHttpConnection.isNetworkAvailable(Add_App_User.this)) {
//                        loadContacts();
//                    } else {
//                        GlobalMethod.showToast(Add_App_User.this, "Check Your Internet Connection.");
//                    }
//                }
//            }
//        });

        if (SimpleHttpConnection.isNetworkAvailable(Add_App_User.this)) {
            loadContacts();
        } else {
            GlobalMethod.showToast(Add_App_User.this, "Check Your Internet Connection.");
        }

        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(search_etxt.getText().toString().trim())) {
                    if (SimpleHttpConnection.isNetworkAvailable(Add_App_User.this)) {
                        page = 1;
                        pagelist.put("page", page);
                        loadContacts();
                        GlobalMethod.hideSoftKeyboard(Add_App_User.this);
                    } else {
                        GlobalMethod.showToast(Add_App_User.this, "Check Your Internet Connection.");
                    }
                }
            }
        });

        contactList.setDividerHeight(1);
//        contactList.setMode(PullToRefreshBase.Mode.DISABLED);

        search_etxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    if (SimpleHttpConnection.isNetworkAvailable(Add_App_User.this)) {
                        page = 1;
                        pagelist.put("page", page);
                        loadContacts();
                    } else {
                        GlobalMethod.showToast(Add_App_User.this, "Check Your Internet Connection.");
                    }
                }
            }
        });


        GlobalMethod.AcaslonProSemiBoldTextView(Add_App_User.this, headerText);
//        GlobalMethod.AcaslonProSemiBoldTextView(Add_App_User.this, no_record);
        GlobalMethod.AcaslonProSemiBoldEditText(Add_App_User.this, search_etxt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:

                GlobalMethod.hideSoftKeyboard(Add_App_User.this);

                Intent intent = new Intent();
                intent.putExtra("added", added);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }


    public void loadContacts() {
        if (SimpleHttpConnection.isNetworkAvailable(Add_App_User.this)) {
            Bundle bundle = new Bundle();
            bundle.putString("showHtml", "no");
            bundle.putString("mode", "");
            bundle.putString("search_key", search_etxt.getText().toString().trim());
            bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
            bundle.putString("page_number", "" + pagelist.get("page"));
//            bundle.putString("mode", "debug");
            new AsyncTaskApp(this, Add_App_User.this, Urls.getListOfAppUser, "LOGIN").execute(bundle);
        } else {
            GlobalMethod.showToast(Add_App_User.this, Constant.network_error);
        }
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {

//        contactList.onRefreshComplete();
        swipeRefresh.setRefreshing(false);
        GlobalMethod.write("RESULT: " + result);
        JSONObject jobj = new JSONObject(result);
        if (action.equalsIgnoreCase("Login")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                JSONArray jsonArray = jobj.getJSONArray("data");
                if (jsonArray.length() > 0) {
                    contactList.setVisibility(View.VISIBLE);
                    norecord_linear.setVisibility(View.GONE);
                    is_load_more = false;
                    if (pagelist.get("page") == 1) {
                        appUserList.clear();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        appUserList.add(jsonArray.getJSONObject(i));
                    }
                    int index = contactList.getFirstVisiblePosition();
                    View v = contactList.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    listAdapters = new ListAdapters(appUserList);
                    contactList.setAdapter(listAdapters);
//                    contactList.setEmptyView(no_record);
                    contactList.setEmptyView(norecord_linear);
                    contactList.setSelectionFromTop(index, top);
                }
            } else {
                is_load_more = true;
                if (pagelist.get("page") == 1) {
                    appUserList.clear();
                    listAdapters = new ListAdapters(appUserList);
                    contactList.setAdapter(listAdapters);
//                    no_record.setVisibility(View.VISIBLE);
                    contactList.setEmptyView(norecord_linear);
                }
            }
        } else if (action.equalsIgnoreCase("Add")) {
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                added = "1";
                appUserList.get(globaLPosition).put("is_already_added", "yes");
                listAdapters.notifyDataSetInvalidated();
//                String num = GlobalMethod.getSavedPreferences(Add_App_User.this, Constant.NUMBERLIST, "");
//                if (num.length() > 0) {
//                    num += ", " + appUserList.get(globaLPosition).getString("");
//                }
            }
            GlobalMethod.showToast(Add_App_User.this, jobj.getString("message"));
        } else {
            added = "1";
            appUserList.get(globaLPosition).put("is_already_added", "no");
            listAdapters.notifyDataSetInvalidated();
        }
    }


    private class ListAdapters extends BaseAdapter {
        ArrayList<JSONObject> contact;

        public ListAdapters(ArrayList<JSONObject> jobj) {
            contact = jobj;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return contact.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(Add_App_User.this, R.layout.view_my_contact, null);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.added_or_not = (ImageView) convertView.findViewById(R.id.added_or_not);
                holder.app_user_pic = (ImageView) convertView.findViewById(R.id.app_user_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try {
                if (!TextUtils.isEmpty(contact.get(position).getString("user_name")))
                    holder.name.setText(contact.get(position).getString("user_name"));
                else
                    holder.name.setText("No Name");
                if (!TextUtils.isEmpty(contact.get(position).getString("user_image"))) {
                    UniversalImageDownloader.loadImageFromURL(Add_App_User.this,
                            GlobalMethod.encodeURL(contact.get(position).getString("user_image")),
                            holder.app_user_pic, R.drawable.profile_pic);
                } else {
                    holder.app_user_pic.setImageResource(R.drawable.profile_pic);
                }

                if ((contact.get(position).getString("is_pre_ngo").trim().equalsIgnoreCase("1"))) {
                    holder.name.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    holder.name.setTypeface(Typeface.DEFAULT);
                }

                if (contact.get(position).getString("is_already_added").equalsIgnoreCase("no")) {
                    holder.added_or_not.setImageResource(R.drawable.notadded);
                    holder.added_or_not.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            globaLPosition = position;
                            try {
                                Bundle bundle = new Bundle();
                                bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                                bundle.putString("id_contact", contact.get(position).getString("id_contact"));
                                new AsyncTaskApp(Add_App_User.this, Add_App_User.this, Urls.add_user, "Add").execute(bundle);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    holder.added_or_not.setImageResource(R.drawable.added);
                    holder.added_or_not.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openExitDialog(Add_App_User.this, position);
                       /*     globaLPosition = position;
                            try
                            {
                                Bundle bundle=new Bundle();
                                bundle.putString("user_id",preferences.getString(Constant.id_user,""));
                                bundle.putString("id_contact",contact.get(position).getString("id_contact"));
                                new AsyncTaskApp(Add_App_User.this, Add_App_User.this , Urls.delete_user,"Delete").execute(bundle);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }*/
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (position == contact.size() - 1){
                if (is_load_more == false) {
                    page = page + 1;
                    pagelist.put("page", page);
                    if (SimpleHttpConnection.isNetworkAvailable(Add_App_User.this)) {
                        loadContacts();
                    } else {
                        GlobalMethod.showToast(Add_App_User.this, "Check Your Internet Connection.");
                    }
                }
            }


                return convertView;
        }

    }

    public class ViewHolder {
        TextView name;
        ImageView added_or_not, app_user_pic;
    }

    public void openExitDialog(final Activity context, final int position) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext, clickmarkettext;
        clickmarkettext = (TextView) dialog.findViewById(R.id.clickmarkettext);
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        oktext = (TextView) dialog.findViewById(R.id.oktext);
        canceltext.setText("Yes");
        oktext.setText("No");
        alerttextlogout.setText("Do you want to remove this contact?");
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globaLPosition = position;
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                    bundle.putString("id_contact", listAdapters.contact.get(position).getString("id_contact"));
                    new AsyncTaskApp(Add_App_User.this, Add_App_User.this, Urls.delete_user, "Delete").execute(bundle);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        GlobalMethod.hideSoftKeyboard(Add_App_User.this);
        Intent intent = new Intent();
        intent.putExtra("added", added);
        setResult(RESULT_OK, intent);
        finish();
    }
}

/**
 * This class end here.
 */