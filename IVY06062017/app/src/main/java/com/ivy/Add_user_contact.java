package com.ivy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.Contact;
import com.globalclasses.DatabaseHandler;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Singsys-0105 on 11/25/2015.
 */
public class Add_user_contact extends Activity implements CallBackListenar, View.OnClickListener {
    ListView phone_list;
    ArrayList<JSONObject> review_jsonobject_list = new ArrayList<JSONObject>();
    public SharedPreferences preferences;
    CustomListAdapter CustomListAdapter;
//    TextView noitem;
    Button appUserbtn, appPhonebook;
    TextView headerText, addcontactstxt, addcontactstxt1;
    ImageButton backBtn;
    private Context context;
    Bundle params;
    int global_position;
    private String contact;
    LinearLayout norecord_linear;
    private SwipeRefreshLayout swipeRefresh, swipeRefreshNoRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_manage_contact);
        preferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        params = new Bundle();
        context = Add_user_contact.this;
        appUserbtn = (Button) findViewById(R.id.appUserbtn);
        appUserbtn.setOnClickListener(this);
        appPhonebook = (Button) findViewById(R.id.appPhonebook);
        appPhonebook.setOnClickListener(this);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        headerText = (TextView) findViewById(R.id.headerText);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshNoRecord = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshNoRecord);
        Add_user_contact.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        addcontactstxt = (TextView) findViewById(R.id.addcontactstxt);
        addcontactstxt1 = (TextView) findViewById(R.id.addcontactstxt1);
        headerText.setText(getIntent().getStringExtra("header_name"));
        GlobalMethod.AcaslonProSemiBoldTextView(Add_user_contact.this, headerText);
        GlobalMethod.AcaslonProSemiBoldButton(Add_user_contact.this, appPhonebook);
        GlobalMethod.AcaslonProSemiBoldButton(Add_user_contact.this, appUserbtn);
        GlobalMethod.AcaslonProSemiBoldTextView(Add_user_contact.this, addcontactstxt);
        GlobalMethod.AcaslonProSemiBoldTextView(Add_user_contact.this, addcontactstxt1);
        norecord_linear = (LinearLayout) findViewById(R.id.norecord_linear);
        phone_list = (ListView) findViewById(R.id.total_follow_list);
//       phone_list;
       phone_list.setDividerHeight(1);
//        phone_list.setMode(PullToRefreshBase.Mode.DISABLED);
//        noitem = (TextView) findViewById(R.id.noitem);

        if (SimpleHttpConnection.isNetworkAvailable(Add_user_contact.this)) {
            params.putString("user_id", preferences.getString(Constant.id_user, ""));
            params.putString("mode", "");
            new AsyncTaskApp(this, Add_user_contact.this, Urls.user_added_contact, "Add_User_Contact").execute(params);
        } else {
            GlobalMethod.showToast(Add_user_contact.this, Constant.network_error);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (SimpleHttpConnection.isNetworkAvailable(Add_user_contact.this)) {
                    review_jsonobject_list.clear();
                    params.putString("user_id", preferences.getString(Constant.id_user, ""));
                    params.putString("mode", "");
                    new AsyncTaskApp(Add_user_contact.this, Add_user_contact.this, Urls.user_added_contact, "Add_User_Contact").execute(params);
                } else {
                    GlobalMethod.showToast(Add_user_contact.this, Constant.network_error);
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
                if (SimpleHttpConnection.isNetworkAvailable(Add_user_contact.this)) {
                    review_jsonobject_list.clear();
                    params.putString("user_id", preferences.getString(Constant.id_user, ""));
                    params.putString("mode", "");
                    new AsyncTaskApp(Add_user_contact.this, Add_user_contact.this, Urls.user_added_contact, "Add_User_Contact").execute(params);
                } else {
                    GlobalMethod.showToast(Add_user_contact.this, Constant.network_error);
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


//        phone_list.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
//            @Override
//            public void onLastItemVisible() {
//                if (SimpleHttpConnection.isNetworkAvailable(Add_user_contact.this)) {
////                    if (Is_Load_more == false) {
////                        page = page + 1;
//                    new AsyncTaskApp(Add_user_contact.this, Add_user_contact.this, Urls.user_added_contact, "Add_User_Contact").execute(params);
////                    }
//                } else {
//                    GlobalMethod.showToast(Add_user_contact.this, Constant.network_error);
//                }
//            }
//        });

    }

    void Set_Result_view(String result) {
//        phone_list.onRefreshComplete();
        swipeRefresh.setRefreshing(false);
        swipeRefreshNoRecord.setRefreshing(false);
        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray productArray;
            if (result != null) {
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {

                    productArray = jsonObject.optJSONArray("data");
                    String numbers = "";


                    if (productArray.length() > 0) {
                        swipeRefresh.setVisibility(View.VISIBLE);
                        swipeRefreshNoRecord.setVisibility(View.GONE);
                        for (int i = 0; i < productArray.length(); i++) {
                            JSONObject productObject = productArray.getJSONObject(i);
                            review_jsonobject_list.add(productObject);
                            numbers += productObject.getString("phone_number");
                            if (i < productArray.length() - 1) {
                                numbers += ", ";
                            }
                        }
//                        if (productArray.length() >= 10 || productArray.length() >= 20)
//                            Is_Load_more = false;

//                            Is_Load_more = true;
                    } else {
//                        Is_Load_more = true;
                    }
                    GlobalMethod.write("====DefaultNumbers"+GlobalMethod.getSavedPreferences(Add_user_contact.this, Constant.DEFAULT_NUMBER,""));
                    GlobalMethod.write("====review_jsonobject_list"+review_jsonobject_list);
                    if (!review_jsonobject_list.toString().contains(GlobalMethod.getSavedPreferences(Add_user_contact.this, Constant.DEFAULT_NUMBER, ""))) {
                        GlobalMethod.clearPreference(Add_user_contact.this, Constant.DEFAULT_NUMBER);
                    }
                    GlobalMethod.savePreferences(Add_user_contact.this, Constant.NUMBERLIST, numbers);
                    GlobalMethod.write("====DefaultNumbers"+GlobalMethod.getSavedPreferences(Add_user_contact.this, Constant.DEFAULT_NUMBER,""));
                    if (CustomListAdapter == null) {
                        phone_list.setVisibility(View.VISIBLE);
//                        noitem.setVisibility(View.GONE);
                        CustomListAdapter = new CustomListAdapter(review_jsonobject_list);
                        phone_list.setAdapter(CustomListAdapter);
                    } else {
                        CustomListAdapter.notifyDataSetChanged();
                    }
                } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                    JSONObject jobj_blank = new JSONObject();
                    review_jsonobject_list.add(jobj_blank);
                    review_jsonobject_list.clear();
                    GlobalMethod.savePreferences(Add_user_contact.this, Constant.NUMBERLIST, "");
//                    noitem.setVisibility(View.VISIBLE);
//                   phone_list.setEmptyView(noitem);
                    swipeRefresh.setVisibility(View.GONE);
                    swipeRefreshNoRecord.setVisibility(View.VISIBLE);
//                   phone_list.setEmptyView(norecord_linear);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appUserbtn:
                Intent addAppUser = new Intent(Add_user_contact.this, Add_App_User.class);
                addAppUser.putExtra("header_name", "Add App Users");
                startActivityForResult(addAppUser, 1001);
                break;
            case R.id.appPhonebook:
//                Intent appPhonebook=new Intent(Add_user_contact.this,Add_Phone_User.class);
                Intent appPhonebook = new Intent(Add_user_contact.this, Add_Phone_User.class);
                appPhonebook.putExtra("header_name", "Add Contacts From Phonebook");
                appPhonebook.putExtra("listData",review_jsonobject_list.toString());

                startActivityForResult(appPhonebook, 1001);
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 1001 && data.getStringExtra("added").equalsIgnoreCase("1")) {
            if (SimpleHttpConnection.isNetworkAvailable(Add_user_contact.this)) {
                review_jsonobject_list.clear();
                params.putString("user_id", preferences.getString(Constant.id_user, ""));
                params.putString("mode", "");
                new AsyncTaskApp(this, Add_user_contact.this, Urls.user_added_contact, "Add_User_Contact").execute(params);
            } else {
                GlobalMethod.showToast(Add_user_contact.this, Constant.network_error);
            }
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
            TextView name,contactNumberTextView;
            ImageView added_or_not;
            public ImageView tick;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflat.inflate(R.layout.view_my_contact, parent, false);
                holder = new ViewHolder();
                holder.profile_pic = (RoundedImageView) convertView.findViewById(R.id.app_user_pic);
                holder.added_or_not = (ImageView) convertView.findViewById(R.id.added_or_not);
                holder.tick = (ImageView) convertView.findViewById(R.id.tick);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.contactNumberTextView = (TextView) convertView.findViewById(R.id.contactNumberTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {

                if (!TextUtils.isEmpty(innerJsonObj.get(position).getString("user_image"))) {
                    UniversalImageDownloader.loadImageFromURL(Add_user_contact.this,
                            GlobalMethod.encodeURL(innerJsonObj.get(position).getString("user_image")),
                            holder.profile_pic, R.drawable.profile_pic);
                } else {
                    holder.profile_pic.setImageResource(R.drawable.profile_pic);
                }
                if (!TextUtils.isEmpty(innerJsonObj.get(position).getString("user_name"))) {
                    holder.name.setText(innerJsonObj.get(position).getString("user_name"));
                } else {
                    holder.name.setText("No Name");
                }


                if (innerJsonObj.get(position).getString("contact_type").equalsIgnoreCase("App User")) {
                    holder.contactNumberTextView.setVisibility(View.GONE);
                    holder.added_or_not.setImageResource(R.drawable.app_user);
                    convertView.setOnClickListener(null);
                    if ((innerJsonObj.get(position).getString("is_pre_ngo").trim().equalsIgnoreCase("1"))) {
                        holder.name.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        holder.name.setTypeface(Typeface.DEFAULT);
                    }
                } else {
                    holder.contactNumberTextView.setVisibility(View.VISIBLE);
                    holder.contactNumberTextView.setText(innerJsonObj.get(position).getString("phone_number"));
                    holder.added_or_not.setImageResource(R.drawable.phone_boook);

                }
                if (!innerJsonObj.get(position).getString("contact_type").equalsIgnoreCase("App User") || innerJsonObj.get(position).getString("is_phonebook_user").equalsIgnoreCase("1")) {

                    if (!innerJsonObj.get(position).getString("phone_number").equalsIgnoreCase(GlobalMethod.getSavedPreferences(Add_user_contact.this, Constant.DEFAULT_NUMBER, ""))) {
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    openDefaultDialog(Add_user_contact.this, position, innerJsonObj.get(position).getString("phone_number"), innerJsonObj.get(position).getString("id_contact"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        holder.tick.setVisibility(View.INVISIBLE);
                    } else {
                        holder.tick.setVisibility(View.VISIBLE);
                    }
                }

                if (!innerJsonObj.get(position).getString("phone_number").equalsIgnoreCase(GlobalMethod.getSavedPreferences(Add_user_contact.this, Constant.DEFAULT_NUMBER, ""))) {
                    holder.tick.setVisibility(View.INVISIBLE);
                } else {
                    holder.tick.setVisibility(View.VISIBLE);
                }

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            openExitDialog(Add_user_contact.this, position, innerJsonObj.get(position).getString("id_contact"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }


    public void openExitDialog(final Activity context, final int position, final String id_contact) {
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
                global_position = position;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                bundle.putString("id_contact", id_contact);
                new AsyncTaskApp(Add_user_contact.this, Add_user_contact.this,
                        Urls.delete_phonebook, "Delete").execute(bundle);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openDefaultDialog(final Activity context, final int position, final String contact_number, String id_contact) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext, clickmarkettext;
        clickmarkettext = (TextView) dialog.findViewById(R.id.clickmarkettext);
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        oktext = (TextView) dialog.findViewById(R.id.oktext);
        canceltext.setText("Yes");
        oktext.setText("No");
        alerttextlogout.setText("Do you want to mark this contact as Default Contact?");
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global_position = position;
                contact = contact_number;
                Bundle bundle = new Bundle();
                bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                bundle.putString("default_number", contact_number);
                new AsyncTaskApp(Add_user_contact.this, Add_user_contact.this,
                        Urls.DEFAULT_NUMBER, "Default").execute(bundle);
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        if (action.equalsIgnoreCase("Delete")) {
            JSONObject jobj = new JSONObject(result);
            if (jobj.getString("success").equalsIgnoreCase("1")) {

                GlobalMethod.write("JSON OBJECT : " + review_jsonobject_list.get(global_position));
                if (!(review_jsonobject_list.get(global_position).getString("contact_type").equalsIgnoreCase("App User"))) {
                    DatabaseHandler dbHandler = new DatabaseHandler(this);
                    Contact contact = new Contact();
                    contact.setIdContact(review_jsonobject_list.get(global_position).getString("id_contact"));
                    dbHandler.updateContact(contact, "no");
                    if (GlobalMethod.getSavedPreferences(this, Constant.DEFAULT_NUMBER, "0").trim().
                            equalsIgnoreCase(review_jsonobject_list.get(global_position).getString("phone_number").trim()))
                        GlobalMethod.clearPreference(Add_user_contact.this, Constant.DEFAULT_NUMBER);
                }

                GlobalMethod.showToast(Add_user_contact.this, jobj.getString("message"));
                review_jsonobject_list.remove(global_position);
                if(review_jsonobject_list.size()==0){
                    GlobalMethod.savePreferences(Add_user_contact.this, Constant.NUMBERLIST, "");
                    swipeRefresh.setVisibility(View.GONE);
                    swipeRefreshNoRecord.setVisibility(View.VISIBLE);
                }
                CustomListAdapter.notifyDataSetChanged();
            } else {
                GlobalMethod.showToast(Add_user_contact.this, jobj.getString("message"));
            }
        } else if (action.equalsIgnoreCase("Default")) {
            JSONObject jobj = new JSONObject(result);
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                GlobalMethod.savePreferences(Add_user_contact.this, Constant.DEFAULT_NUMBER, contact);
                CustomListAdapter.notifyDataSetChanged();
            }
        } else {
            Set_Result_view(result);
        }
    }
}
