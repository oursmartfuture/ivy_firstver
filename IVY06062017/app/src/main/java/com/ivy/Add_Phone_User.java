package com.ivy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.globalclasses.FetchContacts;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Class Name: Add_App_User.Class
 * Class description: This class will show the total app user register on application.
 * Here we will be able to search any app user across app.
 * And can add if and delete from the contact.
 * Created by Abhas Vohra(00213)
 * Created  on 23/11/2015.
 */

public class Add_Phone_User extends Activity implements View.OnClickListener, CallBackListenar {

    private static final int REQUEST_CONTACTS = 1222;
    ImageButton backBtn;
    FrameLayout notificationBtn;
    TextView headerText, edit_prof;
    EditText search_etxt;
    ImageView search_icon, add_new_user;
    ListView contactList;
    SharedPreferences preferences;
    List<Contact> update_contact;
    ArrayList<JSONObject> listData = new ArrayList<>();
    HashMap<String, String> listDataMap = new HashMap<>();
    String hasPhone = "";
    ListAdapters listAdapters;
    String contactname, contactnumber;
    public static final String INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED = "finishActivityOnSaveCompleted";
    int globaLPosition;
    Context context;
    DatabaseHandler db;
    FetchContacts fetchContacts;
    Bundle params;
    ArrayList<Bitmap> imagestore;
    String added = "0";
    String status = "";
    String uid = "";
    //    TextView no_record;
    List<Contact> searched_contact;
    Uri contactsUri;
    String action = "";
    View noRecordView;
    Handler handler;
    private ProgressDialog progressDialog;
    private int id;
    String sortOrder = "";
    Cursor cursor;
    private String contactId = "";
    private LruCache<String, Bitmap> arrBitmap;
    private Bitmap defaultBitmap;
    private boolean isAlreadyAdded = false;
    private SwipeRefreshLayout swipeRefresh;
    LinearLayout norecord_linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app_user);
        update_contact = new ArrayList<Contact>();
        searched_contact = new ArrayList<Contact>();
        context = Add_Phone_User.this;
        preferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        headerText = (TextView) findViewById(R.id.headerText);
        headerText = (TextView) findViewById(R.id.headerText);
        edit_prof = (TextView) findViewById(R.id.edit_prof);
        params = new Bundle();
        edit_prof.setVisibility(View.GONE);
        notificationBtn = (FrameLayout) findViewById(R.id.notificationBtn);
        notificationBtn.setVisibility(View.GONE);
        add_new_user = (ImageView) findViewById(R.id.add_new_user);
        add_new_user.setVisibility(View.VISIBLE);
        add_new_user.setOnClickListener(this);
        headerText.setText(getIntent().getStringExtra("header_name"));


        if (getIntent().hasExtra("listData") && !getIntent().getStringExtra("listData").isEmpty()) {
            JSONArray jsonObject = null;
            try {
                jsonObject = new JSONArray(getIntent().getStringExtra("listData"));
//                JSONArray productArray;
//                productArray = jsonObject.optJSONArray("data");
                if (jsonObject.length() > 0) {
                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject productObject = jsonObject.getJSONObject(i);
                        if (!productObject.getString("contact_type").equalsIgnoreCase("App User") || productObject.getString("is_phonebook_user").equalsIgnoreCase("1"))
                            listDataMap.put(productObject.getString("phone_number"), productObject.getString("id_contact"));
//                        listData.add(productObject);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            GlobalMethod.write("----listData" + getIntent().getStringExtra("listData"));
        }
        norecord_linear = (LinearLayout) findViewById(R.id.norecord_linear);
        search_etxt = (EditText) findViewById(R.id.search_etxt);
        search_icon = (ImageView) findViewById(R.id.search_icon);
        contactList = (ListView) findViewById(R.id.contactList);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        imagestore = new ArrayList<Bitmap>();
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 20;
        arrBitmap = new LruCache<>(cacheSize);
        defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);
//        noRecordView= View.inflate(Add_Phone_User.this,R.layout.no_rcord,null);

//        no_record = (TextView) findViewById(R.id.no_record);
//        no_record.setVisibility(View.GONE);
        db = new DatabaseHandler(Add_Phone_User.this);

        if (mayRequestPermission(READ_CONTACTS, REQUEST_CONTACTS, getString(R.string.contact_permission))) {
            initialize(true);
        }
    }

    private void initialize(boolean check) {
//        fetchContacts = new FetchContacts(Add_Phone_User.this, true);
//        update_contact = db.getAllContacts();
//        setContactList(update_contact);
        if (check) {
            GlobalMethod.write("IS CONTACT UPDATED: " + (preferences.getBoolean(Constant.isContactsUpdated, false) + ", " + db.getContactsCount()));
            if (preferences.getBoolean(Constant.isContactsUpdated, false) && Integer.parseInt(db.getContactsCount()) > 0) {
                new UpdateContact().execute();
            } else {
                fetchContacts = new FetchContacts(Add_Phone_User.this, false);
                fetchContacts.execute();
                progressDialog = new ProgressDialog(Add_Phone_User.this);
                progressDialog.setMessage("Fetching Contacts...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        } else {
            fetchContacts = new FetchContacts(Add_Phone_User.this, false);
            fetchContacts.execute();
            progressDialog = new ProgressDialog(Add_Phone_User.this);
            progressDialog.setMessage("Fetching Contacts...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

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
                    setContactList(db.getAllContacts());
                }
            }
        });

        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(search_etxt.getText().toString().trim())) {

                    searched_contact.clear();
                    searched_contact = db.getContactbyNameOrPhoneNumber(search_etxt.getText().toString().trim());
                    GlobalMethod.write("====searched_contactsize" + searched_contact.size());
                    setContactList(searched_contact);
                    GlobalMethod.hideSoftKeyboard(Add_Phone_User.this);
                }
            }
        });


        contactList.setDividerHeight(1);
//        contactList.setMode(PullToRefreshBase.Mode.DISABLED);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search_etxt.setText("");
                update_contact.clear();
                initialize(false);
//                new UpdateContact().execute();
            }
        });
        GlobalMethod.AcaslonProSemiBoldTextView(Add_Phone_User.this, headerText);
        GlobalMethod.AcaslonProSemiBoldEditText(Add_Phone_User.this, search_etxt);
    }

    private class UpdateContact extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(Add_Phone_User.this);
//            progressDialog.setMessage(getResources().getString(R.string.progress_msg));
//            progressDialog.setCancelable(false);
//            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            update_contact = db.getAllContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Collections.sort(update_contact, new Comparator<Contact>() {
                        public int compare(Contact s1, Contact s2) {
                            String name1 = "";
                            String name2 = "";
                            try {
                                name1 = s1.getName();
                                name2 = s2.getName();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return name1.compareToIgnoreCase(name2);
                        }
                    }

            );
            setContactList(update_contact);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                GlobalMethod.hideSoftKeyboard(Add_Phone_User.this);
                if (fetchContacts != null && fetchContacts.getStatus() == AsyncTask.Status.RUNNING) {
                    fetchContacts.cancel(true);
                }
                Intent intent = new Intent();
                intent.putExtra("added", added);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.add_new_user:
                GlobalMethod.hideSoftKeyboard(Add_Phone_User.this);
                Intent intent1 = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                intent1.putExtra(INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, true);
                action = "Add";
                startActivityForResult(intent1, 1);
                break;
        }
    }

    int cursor_count() {
        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Uri contactsUri = ContactsContract.Data.CONTENT_URI;
        Cursor cursor = getContentResolver().query(contactsUri, null, null, null, sortOrder);
        return (cursor.getCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GlobalMethod.write("====>= Build.VERSION_CODES.M");
            if (ActivityCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
//                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                GlobalMethod.write("====PERMISSION_GRANTED");
                return;
            }
        }

        GlobalMethod.write("contactid+++" + id);
        GlobalMethod.write("Contactcount++" + preferences.getString(Constant.contact_count, "") + "+++++" + String.valueOf(cursor_count()));

        if (requestCode == 1 && resultCode == RESULT_OK) {
            GlobalMethod.write("resultCode" + resultCode + "requestCode" + requestCode);
            GlobalMethod.write("data" + data);
            if (data != null) {
                Uri contactData = data.getData();
                Cursor c = managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {
//                    if (TextUtils.isEmpty(uid)) {
                    uid = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    GlobalMethod.write("uid" + uid + "===hasPhone" + hasPhone);
//                    }

                }
            }

            if (preferences.getString(Constant.contact_count, "").equals(String.valueOf(cursor_count()))) {
            } else {

                if (uid != null && !TextUtils.isEmpty(uid)) {
                   /* params.putString("user_id", preferences.getString(Constant.id_user, ""));
                    params.putString("action", action);
                    params.putString("id_contact", "");
                    params.putString("user_name", hasPhone);
                    params.putString("phone_number", GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, Long.parseLong(uid)));
                    params.putString("email", "");
                    params.putString("mode", "");
                    new AsyncTaskApp(this, Add_Phone_User.this, Urls.update_contact, "Add_Contact").execute(params);*/

                    boolean isContains = false;
                    for (int i = 0; i < update_contact.size(); i++) {
                        if (update_contact.get(i).getPhoneNumber().
                                equalsIgnoreCase(GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, Long.parseLong(uid)))) {
                            isContains = true;
                            break;
                        }
                    }

                    if (!hasPhone.isEmpty() && GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, Long.parseLong(uid)).length() >= 8
                            && !isContains) {
                        DatabaseHandler dbHandler = new DatabaseHandler(this);
                        dbHandler.addContact(new Contact("-1", hasPhone, "",
                                GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, Long.parseLong(uid)), "no"));
                        update_contact.add(0, new Contact("-1", hasPhone, "",
                                GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, Long.parseLong(uid)), "no"));

                        try {
                            GlobalMethod.write("====isalready_added before notify : " + update_contact.get(globaLPosition).getIs_already_added() + ", GLOBAL : " + globaLPosition);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        new UpdateContact().execute();
                    } else {
                        if (hasPhone.isEmpty()) {
                            GlobalMethod.showCustomToastInCenterLong(Add_Phone_User.this, "Please add valid contact name.");
                        } else if (GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, Long.parseLong(uid)).length() < 8) {
                            GlobalMethod.showCustomToastInCenterLong(Add_Phone_User.this, "Please add valid phone number.");
                        } else {
                            GlobalMethod.showCustomToastInCenterLong(Add_Phone_User.this, "Phone number already present.");
                        }
                    }
                }
            }
        }

        if (requestCode == 101) {
            GlobalMethod.write("contactname+++++" + contactname + "=====" + GlobalMethod.retrieveContactName(Add_Phone_User.this, id));
            GlobalMethod.write("contactnumber+++++" + contactnumber + "=====" + GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id));
            if (contactname != null && contactname.equalsIgnoreCase(GlobalMethod.retrieveContactName(Add_Phone_User.this, id))
                    && contactnumber.equalsIgnoreCase(GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id))) {

            } else {
               /* params.putString("user_id", preferences.getString(Constant.id_user, ""));
                params.putString("action", action);
                params.putString("id_contact", "" + contactId);
                params.putString("user_name", GlobalMethod.retrieveContactName(Add_Phone_User.this, id));
                params.putString("phone_number", GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id));
                params.putString("email", "");
                params.putString("mode", "");
                new AsyncTaskApp(this, Add_Phone_User.this, Urls.update_contact, "Update_Contact").execute(params);*/

                GlobalMethod.write("UPDATE CONTACT ID: " + update_contact.get(globaLPosition).getIdContact());

                boolean isContains = false;
                for (int i = 0; i < update_contact.size(); i++) {

                    if (globaLPosition != i && update_contact.get(i).getPhoneNumber().
                            equalsIgnoreCase(GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id))) {
                        isContains = true;
                        break;
                    }
                }

                if (GlobalMethod.retrieveContactName(Add_Phone_User.this, id) != null &&
                        !GlobalMethod.retrieveContactName(Add_Phone_User.this, id).isEmpty() &&
                        GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id) != null
                        && GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id).length() >= 8 && !isContains) {

                    if (isAlreadyAdded) {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                        bundle.putString("user_name", GlobalMethod.retrieveContactName(Add_Phone_User.this, id));
                        bundle.putString("phone_number", GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id));
                        bundle.putString("email", "");
                        bundle.putString("action", "Edit");
                        bundle.putString("mode", "");
                        bundle.putString("id_contact", update_contact.get(globaLPosition).getIdContact());
                        if (SimpleHttpConnection.isNetworkAvailable(Add_Phone_User.this)) {
                            new AsyncTaskApp(Add_Phone_User.this, Add_Phone_User.this, Urls.add_phonebookSingle, "Update_Contact").execute(bundle);
                        } else {
                            GlobalMethod.showToast(Add_Phone_User.this, Constant.network_error);
                        }

                    } else {
                        DatabaseHandler dbHandler = new DatabaseHandler(this);
                        String user_name = "", user_e_mail, phone_number = "", is_already_added = "";
                        user_name = GlobalMethod.retrieveContactName(Add_Phone_User.this, id);
                        user_e_mail = update_contact.get(globaLPosition).getE_mail();
                        phone_number = GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id);
                        is_already_added = update_contact.get(globaLPosition).getIs_already_added();
                        update_contact.get(globaLPosition).setName(GlobalMethod.retrieveContactName(Add_Phone_User.this, id));
                        update_contact.get(globaLPosition).setPhoneNumber(GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id));

                        if (dbHandler.updateContactId_Contact(update_contact.get(globaLPosition),
                                contactId, user_name, user_e_mail, phone_number, is_already_added) == -1) {
                            update_contact.remove(globaLPosition);
                        }

                        GlobalMethod.write("====isalready_added before notify : " + update_contact.get(globaLPosition) + "Position" + globaLPosition);
                        listAdapters.notifyDataSetInvalidated();
//                    fetchContacts = new FetchContacts(Add_Phone_User.this, false);
//                    fetchContacts.execute();
//                    progressDialog = new ProgressDialog(Add_Phone_User.this);
//                    progressDialog.setMessage("Fetching Contacts...");
//                    progressDialog.setCancelable(false);
//                    progressDialog.show();
                    }
                } else {
//                    if (GlobalMethod.retrieveContactName(Add_Phone_User.this, id)!=null&&
//                            GlobalMethod.retrieveContactName(Add_Phone_User.this, id).isEmpty()) {
//                        GlobalMethod.showCustomToastInCenterLong(Add_Phone_User.this, "Please add valid contact name.");
//                    } else if (GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id)!=null&&
//                            GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id).length() < 8) {
//                        GlobalMethod.showCustomToastInCenterLong(Add_Phone_User.this, "Please add valid phone number.");
//                    } else {
//                        GlobalMethod.showCustomToastInCenterLong(Add_Phone_User.this, "Phone number already present in another contact.");
//                    }
                    fetchContacts = new FetchContacts(Add_Phone_User.this, false);
                    fetchContacts.execute();
                    progressDialog = new ProgressDialog(Add_Phone_User.this);
                    progressDialog.setMessage("Fetching Contacts...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }
        }
    }

    public class GetContactIdAsyntask extends AsyncTask<String, Void, String> {

        Context context;


        @Override
        protected String doInBackground(String... params) {
            id = getContactIDFromNumber(params[0], Add_Phone_User.this);
            return id + "";
        }


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Add_Phone_User.this);
            progressDialog.setMessage(getResources().getString(R.string.progress_msg));
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            GlobalMethod.write("result++" + result);
            progressDialog.dismiss();

            try {
                contactname = GlobalMethod.retrieveContactName(Add_Phone_User.this, id);
                contactnumber = GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id);
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                action = "Edit";
           /* editIntent.setData(Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/" + id));
            startActivity(editIntent);*/

                editIntent.setData(contactUri);
                startActivityForResult(editIntent, 101);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                GlobalMethod.showCustomToastInCenterLong(Add_Phone_User.this, "Sorry! Contact not found in phone book, maybe contact was deleted.");
            }


        }
    }

    public void updateCon(ArrayList<JSONObject> result) {
        GlobalMethod.write("UPDATE CON CALLED");
        update_contact.clear();
        new UpdateContact().execute();
    }

    @Override
    public void onBackPressed() {
        GlobalMethod.hideSoftKeyboard(Add_Phone_User.this);
        if (fetchContacts != null && fetchContacts.getStatus() == AsyncTask.Status.RUNNING) {
            fetchContacts.cancel(true);
        }
        Intent intent = new Intent();
        intent.putExtra("added", added);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void setContactList(List<Contact> contacts) {
        update_contact = contacts;
        upDatePull();
        if (update_contact.size() > 0) {
//            no_record.setVisibility(View.GONE);

            for (int i = 0; i < update_contact.size(); i++) {
                GlobalMethod.write("===update_contactNumber" + update_contact.get(i).getPhoneNumber());
                if (listDataMap.containsKey(update_contact.get(i).getPhoneNumber())) {

                    update_contact.get(i).setIs_already_added("yes");
                    update_contact.get(i).setIdContact(listDataMap.get(update_contact.get(i).getPhoneNumber()));
                    GlobalMethod.write("===update_contact" + update_contact.get(i));

                    /*for (Map.Entry<String, String> entry : listDataMap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (value.equalsIgnoreCase(update_contact.get(i).getPhoneNumber())) {
                            update_contact.get(i).setIs_already_added("yes");
                            update_contact.get(i).setIdContact(key);
                            GlobalMethod.write("===update_contact" + update_contact.get(i));
                        }
                    }*/
                } else {
                    update_contact.get(i).setIs_already_added("no");
                }

            }


            listAdapters = new ListAdapters(update_contact);
            contactList.setAdapter(listAdapters);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } else {
            listAdapters = new ListAdapters(update_contact);
            contactList.setAdapter(listAdapters);
//            no_record.setVisibility(View.VISIBLE);
//            contactList.setEmptyView(no_record);
            contactList.setEmptyView(norecord_linear);
//            contactList.onRefreshComplete();
            swipeRefresh.setRefreshing(false);
        }
    }


    private void upDatePull() {
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            contactList.onRefreshComplete();
            swipeRefresh.setRefreshing(false);
            if (swipeRefresh.isRefreshing()) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        JSONObject jobj = new JSONObject(result);
        if (jobj.getString("success").equalsIgnoreCase("1")) {
            if (action.equalsIgnoreCase("Update_Contact")) {
/*                String id_contact = "", user_name = "", user_e_mail, phone_number = "", is_already_added = "";
                JSONObject jsonObject = jobj.getJSONObject("data");

                // Inserting New Contacts
                user_name = jsonObject.getString("user_name");
                user_e_mail = jsonObject.getString("email");
                phone_number = jsonObject.getString("phone_number");
                is_already_added = jsonObject.getString("is_already_added");
                GlobalMethod.write("====isalready_added" + is_already_added);
                DatabaseHandler dbHandler = new DatabaseHandler(this);
                GlobalMethod.write("Contac ID : " + update_contact.get(globaLPosition).getIdContact() + ", " + contactId);
                dbHandler.updateContactId_Contact(update_contact.get(globaLPosition), contactId, user_name, user_e_mail, phone_number, is_already_added);
                update_contact.get(globaLPosition).setIs_already_added(is_already_added);
                update_contact.get(globaLPosition).setName(user_name);
                update_contact.get(globaLPosition).setE_mail(user_e_mail);
                update_contact.get(globaLPosition).setPhoneNumber(phone_number);
                GlobalMethod.write("====isalready_added before notify : " + update_contact.get(globaLPosition).getIs_already_added() + ", GLOBAL : " + globaLPosition);
                */

                DatabaseHandler dbHandler = new DatabaseHandler(this);
                String user_name = "", user_e_mail, phone_number = "", is_already_added = "";
                user_name = GlobalMethod.retrieveContactName(Add_Phone_User.this, id);
                user_e_mail = update_contact.get(globaLPosition).getE_mail();
                phone_number = GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id);
                is_already_added = update_contact.get(globaLPosition).getIs_already_added();

                dbHandler.updateContactId_Contact(update_contact.get(globaLPosition),
                        contactId, user_name, user_e_mail, phone_number, is_already_added);

                update_contact.get(globaLPosition).setName(GlobalMethod.retrieveContactName(Add_Phone_User.this, id));
                update_contact.get(globaLPosition).setPhoneNumber(GlobalMethod.retrieveContactNumbers(Add_Phone_User.this, id));
                GlobalMethod.write("====isalready_added before notify : " + update_contact.get(globaLPosition) + "Position" + globaLPosition);
                GlobalMethod.showToast(Add_Phone_User.this, jobj.getString("message"));
                listAdapters.notifyDataSetInvalidated();

//                fetchContacts = new FetchContacts(Add_Phone_User.this, false);
//                fetchContacts.execute();
//                progressDialog = new ProgressDialog(Add_Phone_User.this);
//                progressDialog.setMessage("Fetching Contacts...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
            }
            /*else if (action.equalsIgnoreCase("Add_Contact")) {
                DatabaseHandler db = new DatabaseHandler(Add_Phone_User.this);
                String id_contact = "", user_name = "", user_e_mail, phone_number = "", is_already_added = "";
                JSONObject jsonObject = jobj.getJSONObject("data");
                // Inserting New Contacts
                user_name = jsonObject.getString("user_name");
                user_e_mail = jsonObject.getString("email");
                phone_number = jsonObject.getString("phone_number");
                is_already_added = jsonObject.getString("is_already_added");
                id_contact = jsonObject.getString("id_contact");
                GlobalMethod.write("====isalready_added" + is_already_added);
                DatabaseHandler dbHandler = new DatabaseHandler(this);
                dbHandler.addContact(new Contact(id_contact, user_name, user_e_mail, phone_number, is_already_added));
                update_contact.add(0, new Contact(contactId, user_name, user_e_mail, phone_number, is_already_added));
                GlobalMethod.write("====isalready_added before notify : " + update_contact.get(globaLPosition).getIs_already_added() + ", GLOBAL : " + globaLPosition);
                new UpdateContact().execute();
            } */
            else if (action.equalsIgnoreCase("Add")) {

                JSONObject jsonObject = jobj.getJSONObject("data");
                DatabaseHandler dbHandler = new DatabaseHandler(this);
                dbHandler.updateContact(update_contact.get(globaLPosition), jsonObject.getString("id_contact"), "yes");
                update_contact.get(globaLPosition).setIs_already_added("yes");
                update_contact.get(globaLPosition).setIdContact(jsonObject.getString("id_contact"));
                listDataMap.put(update_contact.get(globaLPosition).getPhoneNumber(), jsonObject.getString("id_contact"));
                GlobalMethod.showToast(Add_Phone_User.this, jobj.getString("message"));
                listAdapters.notifyDataSetInvalidated();

            } else {

                DatabaseHandler dbHandler = new DatabaseHandler(this);
                dbHandler.updateContact(update_contact.get(globaLPosition), "no");
                update_contact.get(globaLPosition).setIs_already_added("no");
                listDataMap.remove(update_contact.get(globaLPosition).getPhoneNumber());
                listAdapters.notifyDataSetInvalidated();
            }
            added = "1";
        } else {
            GlobalMethod.showToast(Add_Phone_User.this, jobj.getString("message"));
        }
    }

    public static Bitmap retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

//        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
//        Uri contactsUri = ContactsContract.Data.CONTENT_URI;
//        String[] projection = {
//                ContactsContract.Data.MIMETYPE,
//                ContactsContract.Data.CONTACT_ID,
//                ContactsContract.Contacts.DISPLAY_NAME,
//                ContactsContract.CommonDataKinds.Contactables.DATA,
//                ContactsContract.CommonDataKinds.Contactables.TYPE,
//        };

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        Bitmap photo = null;
//                BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.profile_pic);

        InputStream input = openThumbPhoto(context, Long.parseLong(contactId));
        if (input != null)
            photo = BitmapFactory.decodeStream(input);
        return photo;
    }


    public static InputStream openThumbPhoto(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private class ListAdapters extends BaseAdapter {
        List<Contact> contact;

        public ListAdapters(List<Contact> contact) {
            this.contact = contact;
        }

        public void notifyDataSetChanged(List<Contact> contactList) {
            contact = contactList;
            notifyDataSetChanged();
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
                convertView = View.inflate(Add_Phone_User.this, R.layout.view_my_contact, null);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.added_or_not = (ImageView) convertView.findViewById(R.id.added_or_not);
                holder.app_user_pic = (ImageView) convertView.findViewById(R.id.app_user_pic);
                holder.edit = (ImageView) convertView.findViewById(R.id.edit);
                holder.contactNumberTextView = (TextView) convertView.findViewById(R.id.contactNumberTextView);
                holder.contactNumberTextView.setVisibility(View.VISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.edit.setVisibility(View.VISIBLE);
            try {
                holder.name.setText(contact.get(position).getName());
                holder.contactNumberTextView.setText(contact.get(position).getPhoneNumber());

                holder.app_user_pic.setImageResource(R.drawable.profile_pic);
                Bitmap bmp;
                if (arrBitmap != null && arrBitmap.get(contact.get(position).getPhoneNumber()) != null) {
                    bmp = arrBitmap.get(contact.get(position).getPhoneNumber());
                } else {
                    bmp = retrieveContactPhoto(Add_Phone_User.this, contact.get(position).getPhoneNumber());
                    if (bmp != null) {
                        arrBitmap.put(contact.get(position).getPhoneNumber(), bmp);
                    } else {
                        arrBitmap.put(contact.get(position).getPhoneNumber(), defaultBitmap);
                        bmp = defaultBitmap;
                    }
                }
                GlobalMethod.write("ArrayBitmap : " + arrBitmap);
                holder.app_user_pic.setImageBitmap(bmp);
                if (position == globaLPosition)
                    GlobalMethod.write("====isalready_added after notify : " + contact.get(globaLPosition).getIs_already_added() + ", name : " + contact.get(globaLPosition).getName() + ", GLOBAL : " + globaLPosition);
                if (contact.get(position).getIs_already_added().equalsIgnoreCase("no")) {
                    holder.added_or_not.setImageResource(R.drawable.notadded);
                    holder.added_or_not.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            globaLPosition = position;
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                            bundle.putString("user_name", contact.get(position).getName());
                            bundle.putString("phone_number", contact.get(position).getPhoneNumber());
                            bundle.putString("email", "");
                            bundle.putString("action", "Add");
                            bundle.putString("mode", "");
//                            bundle.putString("id_contact", contact.get(position).getIdContact());
                            new AsyncTaskApp(Add_Phone_User.this, Add_Phone_User.this, Urls.add_phonebookSingle, "Add").execute(bundle);
                        }
                    });
                } else if (contact.get(position).getIs_already_added().equalsIgnoreCase("yes")) {
//                    globaLPosition = position;
                    holder.added_or_not.setImageResource(R.drawable.added);
                    holder.added_or_not.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openExitDialog(Add_Phone_User.this, position);
                        }
                    });
                }


                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactId = contact.get(position).getIdContact();
                        globaLPosition = position;
                        isAlreadyAdded = contact.get(position).getIs_already_added().equalsIgnoreCase("yes");
                        new GetContactIdAsyntask().execute(contact.get(position).getPhoneNumber());
//                        id = contact.get(position).getIdContact();

                    }
                });

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return convertView;
        }
    }

    public static int getContactIDFromNumber(String contactNumber, Context context) {

        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        if (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();
        return phoneContactID;
    }

    public class ViewHolder {
        TextView name, contactNumberTextView;
        ImageView added_or_not, app_user_pic;
        public ImageView edit;
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
                Bundle bundle = new Bundle();
                bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                bundle.putString("id_contact", listAdapters.contact.get(position).getIdContact());
                new AsyncTaskApp(Add_Phone_User.this, Add_Phone_User.this,
                        Urls.delete_phonebook, "Delete").execute(bundle);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * @param permission   that has to be accessed in the app
     * @param requestCode  for callback purpose
     * @param errorMessage to display in case of error
     * @return
     */
    public boolean mayRequestPermission(String permission, int requestCode, String errorMessage) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GlobalMethod.write("====<Build.VERSION_CODES.M");
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GlobalMethod.write("====>= Build.VERSION_CODES.M");
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
//                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                GlobalMethod.write("====PERMISSION_GRANTED");
                return true;
            } else if (shouldShowRequestPermissionRationale(permission)) {
                GlobalMethod.write("====ifPermissionRationale");
                requestPermissions(new String[]{permission}, requestCode);
            } else {
                requestPermissions(new String[]{permission}, requestCode);
//                GlobalMethod.showCustomToastInCenter(this, errorMessage);
                GlobalMethod.write("====elsePermission");
//                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialize(true);
            } else {
                Log.e("Permission", "Denied");
            }
        }
    }
}
/**
 * This class end here.
 */