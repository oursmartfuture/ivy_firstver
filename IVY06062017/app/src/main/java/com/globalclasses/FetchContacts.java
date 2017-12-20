package com.globalclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.ivy.Add_Phone_User;
import com.ivy.ContactServices;
import com.ivy.SimpleHttpConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Singsys-126 on 10/20/2015.
 */
public class FetchContacts extends AsyncTask<Void, Void, ArrayList<JSONObject>> implements CallBackListenar {

    Context activity;
    Fragment fragment;
    boolean isLoader;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    private Add_Phone_User activityRef;
    private long startTime;
    ArrayList<JSONObject> contactList = new ArrayList<>();
    DatabaseHandler db;

    public FetchContacts(Context activity, boolean isLoader) {
        this.activity = activity;
        this.isLoader = isLoader;
        db = new DatabaseHandler(activity);
        db.clearDataBase();
    }

    public FetchContacts(Fragment fragment, boolean isLoader) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.isLoader = isLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
        if (isLoader) {
            GlobalMethod.write("loading ====" + isLoader);
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


    }

    @Override
    protected ArrayList<JSONObject> doInBackground(Void... params) {
        preferences = activity.getSharedPreferences(Constant.pref_main, 0);
        contactList = new ArrayList<>();
        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Uri contactsUri = ContactsContract.Data.CONTENT_URI;
        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE,
        };
        String selection = ContactsContract.Data.MIMETYPE + " in (?)";
        String[] selectionArgs = {
//                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
//                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
        };
        Cursor cursor = activity.getContentResolver().query(contactsUri, projection, selection, selectionArgs, sortOrder);
        SharedPreferences.Editor main_edit = preferences.edit();
        main_edit.putString(Constant.contact_count, String.valueOf(cursor.getCount()));
//        main_edit.putString(Constant.phone, getPhoneNumber(cursor));
        main_edit.commit();
        if (cursor.getCount() > 0) {
            ArrayList<String> numbers = new ArrayList<String>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
//                GlobalMethod.write("IDeees : " + id);
                String number = getPhoneNumber(cursor);
//                String e_mail = getEmailId(cursor);
                number = getNumber(number);
//                GlobalMethod.write("e_mail====" + "/n" + e_mail);
                if (number.length() >= 8 && isValidNumber(number) && !isDuplicate(numbers, number) &&
                        !TextUtils.isEmpty(name) && !TextUtils.isEmpty(number)) {
                    try {
//                        if(contactList.size()<=50){
                        JSONObject objContact = new JSONObject();
                        objContact.put("user_id", preferences.getString(Constant.id_user, ""));
                        objContact.put("email", "");
                        objContact.put("phone_number", number);
                        objContact.put("id", id);
//                        GlobalMethod.write("phone_number=+====++"+number);
                        objContact.put("user_name", name);
                        numbers.add(number);
                        contactList.add(objContact);
//                        String id_contact,String user_name,String email, String phone_number,
//                                String is_already_added
                        db.addContact(new Contact(id, name, "", number, "no"));

//                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            cursor.close();
            GlobalMethod.write("Contacts size == " + contactList.size());
            GlobalMethod.write("Contacts_List====" + contactList.toString());
        }
        return contactList;
    }

    private String getEmailId(Cursor cursor) {
        String email = "";
        if (cursor.getString(cursor.getColumnIndex("mimetype"))
                .equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
            switch (cursor.getInt(cursor.getColumnIndex("data2"))) {
                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                    email = cursor.getString(cursor.getColumnIndex("data1"));
                    break;
                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                    email = cursor.getString(cursor.getColumnIndex("data1"));
                    break;
            }
        }
        return email;
    }

    private String getPhoneNumber(Cursor cursor) {
        String number = "";
        if (cursor.getString(cursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
            switch (cursor.getInt(cursor.getColumnIndex("data2"))) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    number = cursor.getString(cursor.getColumnIndex("data1"));
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    number = cursor.getString(cursor.getColumnIndex("data1"));
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    number = cursor.getString(cursor.getColumnIndex("data1"));
                    break;
            }
        }
        return number;
    }


    /*
    * Method to remove characters and get contact.
    * @number : number from which remove characters.
    *
    * */


    private String getNumber(String number) {
        String num = number.replaceAll("\\s+", "");
        num = num.replaceAll("-", "").replace("(","").replace(")","").replace(" ","");
        return num.trim();
    }

    private void hideLoder() {
        if (isLoader)
            progressDialog.dismiss();
    }

    /*
    * Method to check duplicate contact.
    * @numbers : array list of numbers in which duplicate number check.
    * @number : number to check duplicate.
    *
    * */


    private boolean isDuplicate(ArrayList<String> numbers, String number) {
        boolean duplicate = false;
        for (String num : numbers) {
            if (num.contains(number) || number.contains(num)) {
                duplicate = true;
                break;
            }
        }
        return duplicate;
    }


      /*
    * Method to check valid contact.
    * @number : number to check validity.
    *
    * */

    private boolean isValidNumber(String number) {
        String invalid[] = {"@", "#", "*", "%", "$"};
        for (int i = 0; i < invalid.length; i++) {
            if (number.contains(invalid[i])) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onPostExecute(ArrayList<JSONObject> result) {
        super.onPostExecute(result);
//        float total = (System.currentTimeMillis() - startTime) / 1000f;

//        GlobalMethod.showCustomToastInCenter((Activity) activity, total + " seconds taken");
        preferences.edit().putString(Constant.SEND_CONTACT_JSON_RESULT, result.toString()).commit();
        hideLoder();
        if (TextUtils.isEmpty(preferences.getString(Constant.SEND_CONTACT_JSON_RESULT, ""))) {
            preferences.edit().putString(Constant.SEND_CONTACT_JSON_RESULT, preferences.getString(Constant.SEND_CONTACT_JSON_RESULT, "").replace("?", "")).commit();
        }
//        SharedPreferences mainPreferences = activity.getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        preferences.edit().putBoolean(Constant.isContactsUpdated, true).commit();
//        GlobalMethod.write("Json_contact+++++" + preferences.getString(Constant.SEND_CONTACT_JSON_RESULT, "").replace("?", ""));
//        loadContacts();
        String str = preferences.getString(Constant.SEND_CONTACT_JSON_RESULT, "").replace("?", "");
        for (int i = 0; i < str.length() / 250; i++) {
            if ((i + 1) * 250 < str.length())
                GlobalMethod.write(str.substring(i * 250, (i + 1) * 250));
            else {
                GlobalMethod.write(str.substring(i * 250, str.length()));
            }
        }
        if (activity instanceof Add_Phone_User)
            activityRef = ((Add_Phone_User) this.activity);
        else {
            activityRef = null;
        }

        if (activityRef != null) {
            activityRef.updateCon(result);
        }
//            activity.stopService(new Intent("com.contactOfServices"));
        Intent intent = new Intent(activity, ContactServices.class);
        activity.stopService(intent);

//            ((Add_Phone_User) this.activity).updateCon();
    }

    public void loadContacts() {
        if (SimpleHttpConnection.isNetworkAvailable(activity)) {
            Bundle bundle = new Bundle();
//            bundle.putString("mode", "");

            GlobalMethod.write("===json_data" + preferences.getString(Constant.SEND_CONTACT_JSON_RESULT, "").replace("?", ""));

            bundle.putString("json_data", preferences.getString(Constant.SEND_CONTACT_JSON_RESULT, "").replace("?", ""));
//            String str = preferences.getString(Constant.SEND_CONTACT_JSON_RESULT, "");
          /*  int count = str.length() / 3500;
            for (int i = 0; i < count; i++) {
                GlobalMethod.write("jsondata+++++++" + str.substring(i * 3500, (i + 1) * 3500));
            }*/

            new AsyncTaskApp(this, Urls.my_contact, "Fetch_Contact").execute(bundle);
        } else {
            GlobalMethod.showToast((Activity) activity, Constant.network_error);
        }
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        GlobalMethod.write("====result" + result);
        JSONObject jobj = new JSONObject(result);
        if (jobj.getString("success").equalsIgnoreCase("1")) {
            DatabaseHandler db = new DatabaseHandler(activity);
            String id_contact = "", user_name = "", user_e_mail, phone_number = "", is_already_added = "";
            db.clearDataBase();
            SharedPreferences mainPreferences = activity.getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
            mainPreferences.edit().putBoolean(Constant.isContactsUpdated, true).commit();
            JSONArray jsonArray = jobj.getJSONArray("data");
            {
                // Inserting Contacts
                for (int i = 0; i < jsonArray.length(); i++) {
                    id_contact = jsonArray.getJSONObject(i).getString("id_contact");
                    user_name = jsonArray.getJSONObject(i).getString("user_name");
                    user_e_mail = jsonArray.getJSONObject(i).getString("email");
                    phone_number = jsonArray.getJSONObject(i).getString("phone_number");
                    phone_number.replace("?", "");
                    is_already_added = jsonArray.getJSONObject(i).getString("is_already_added");
                    db.addContact(new Contact(id_contact, user_name, user_e_mail, phone_number, is_already_added));
//                    db.addContactofLoginUser(new Contact(preferences.getString(Constant.id_user,""),id_contact,is_already_added));
                }
            }
            Log.d("Reading: ", "Reading all contacts..");

//            List<Contact> contacts = db.getAllContacts();
//            if (activity != null && activity instanceof Add_Phone_User)
//                ((Add_Phone_User) activity).setContactList(contacts);
            if (activityRef != null) {
                activityRef.updateCon(contactList);
            }
//            activity.stopService(new Intent("com.contactOfServices"));
            Intent intent = new Intent(activity, ContactServices.class);
            activity.stopService(intent);
        }
    }


}
