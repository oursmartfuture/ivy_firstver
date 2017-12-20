package com.globalclasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by sing sys-118 on 8/3/2015.
 */
public class Constant {
    public static final String NUMBERLIST = "NUMBERLIST";
    public static boolean running = false;
    public static final String SMS_SENT = "com.ivy.sent";
    public static final String SMS_DELIVERED = "com.ivy.delivery";
    public static final String DEFAULT_NUMBER = "DEFAULT_NUMBER";
    public static final String AUDIOFILE = "AUDIOFILE";
    public static final String NAME = "NAME";
    public static final String AUDIOFILESEQUENCE = "AUDIOFILESEQUENCE";
    public static String pref_main = "Main_Preference_Name";
    public static String network_error = "No internet connection available.Please check your internet connection.";
    public static String email = "email";
    public static String check_box = "checked";
    public static String contact_count = "contact_count";
    public static String name = "name";
    public static String phone = "phone";
    public static String password = "password";
    public static String id_user = "id_user";
    public static String default_ringtone = "default_ringtone";
    public static String default_number = "default_number";
    public static String view_notification = "view_notification";
    public static String alert_type_ring = "alert_type_ring";
    public static String left_battery = "left_battery";
    public static String show_map = "show_map";
    public static String connection = "connection";
    public static String connection_fading = "connection_fading";
    public static String remember_me = "email_password";
    public static String pref_settings = "pref_settings";
    public static String RegisterationId = "RegisterationId";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int RESULT_LOAD_IMAGE = 101;
    public static final int CROP_IMAGE = 100;
    public static String ConnectionTimeOut = "Connection Time Out Error";
    public static String SEND_CONTACT_JSON_RESULT = "SEND_CONTACT_JSON_RESULT";
    public static ArrayList<Activity> ActivitiLifeCycel = new ArrayList<>();
    public static String isContactsUpdated = "isContactsUpdated";
    public static String lalitude = "lat";
    public static String longitude = "long";

    /**
     * Create static Get Preference method
     *
     * @param: Three paramaters are used in this method Context is for class, key is for String in which value will be stored.
     */
    public static String getPreferenceValues(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(pref_main, context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    /**
     * Create static Save Preference method
     *
     * @param: Three paramaters are used in this method Context is for class, key is for String in which value will be stored and value is for that value which will be stored.
     */
    public static void savePreferenceValues(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(pref_main, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
