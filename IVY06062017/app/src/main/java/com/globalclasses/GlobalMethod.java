package com.globalclasses;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ble_connect.NotifyActivity;
import com.ivy.Emergency_Alert_Receiver;
import com.ivy.Landing_Activity;
import com.ivy.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationChannel;

/**
 * * Name Of class : GlobalMethod.class
 * Class description : The classes  and methods under this class is for declaring the global methods for use in several classes.
 * Created by : Sobhit Gupta(00253)
 * Created by sing sys-118 on 7/24/2015.
 */
public class GlobalMethod extends Activity {
    static Toast toast;
    private final static String PREF_KEY = "MyPrefDeviceToken";
    static MediaPlayer mp;
    private static ArrayList<AudioModel> mAudioModalList;
    private static Dialog dialog;
    private static Handler handler;
    private static java.lang.Runnable r;

    /**
     * *****
     * to get version
     */
    public static String appp_version(Context act) {
        try {
            PackageInfo pInfo = act.getPackageManager().getPackageInfo(
                    act.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }

    }


    public static void showToast(Activity ctx, String msg) {
        showCustomToastInCenter(ctx, msg);
    }


    public static void showCustomToastInCenter(Activity context, String message) {
        LayoutInflater inflater = (context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_ingrey, (ViewGroup) (context).findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        toSetFontstoSignIn(context, text);
        if (toast != null && toast.getView().isShown()) {
            GlobalMethod.write("====true");
        } else {
            toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER, 0, 110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            GlobalMethod.write("====false");
        }


    }

    public static void showCustomToastInCenterLong(Activity context, String message) {
        LayoutInflater inflater = (context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_ingrey, (ViewGroup) (context).findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        toSetFontstoSignIn(context, text);
        if (toast != null && toast.getView().isShown()) {
            GlobalMethod.write("====true");
        } else {
            toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER, 0, 110);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            GlobalMethod.write("====false");
        }


    }

    public static String encodeURL(String urlStr) {

        URL url = null;
        try {
            System.out.println("url==" + urlStr);
            if (urlStr != null) {
                if (urlStr.length() > 4) {
                    if (urlStr.startsWith("http") || urlStr.contains("http://")) {
                    } else {
                        urlStr = "http://" + urlStr;
                    }
                    url = new URL(urlStr);
                    return url.toString().replaceAll("&amp;", "&");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlStr;
    }

    public static void checkTap(Context context, String taps) {
        context.startActivity(new Intent(context, NotifyActivity.class).putExtra("text", taps).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static boolean savePreferences(Context c, String key, String value) {
        SharedPreferences sp = initializeSharedPreferences(c);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean clearPreference(Context c, String key) {
        SharedPreferences sp = initializeSharedPreferences(c);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }

    public static boolean clearPreferences(Context c) {
        SharedPreferences sp = initializeSharedPreferences(c);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(Constant.AUDIOFILE);
        editor.remove(Constant.AUDIOFILESEQUENCE);
        editor.remove(Constant.DEFAULT_NUMBER);
        return editor.commit();
    }

    public static String getSavedPreferences(Context c, String key, String defValue) {
        SharedPreferences sp = initializeSharedPreferences(c);
        return sp.getString(key, defValue);
    }

    private static SharedPreferences initializeSharedPreferences(Context c) {
        return c.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    /*
    class description : this class is for setting fonts to the home screen.
 */
    public static void toSetFontstohomescreen(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    toSetFontstohomescreen(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Lt.otf"));
            } else if (v instanceof CheckBox) {
                ((CheckBox) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Lt.otf"));
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Lt.otf"));
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Lt.otf"));
            } else if (v instanceof RadioButton) {
                ((RadioButton) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Lt.otf"));
            }
        } catch (Exception e) {
        }
    }

    /*
    class description : this class is for setting fonts to the sign in screen.
 */
    public static void toSetFontstoSignIn(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    toSetFontstohomescreen(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Th.otf"));
            } else if (v instanceof CheckBox) {
                ((CheckBox) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Th.otf"));
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Th.otf"));
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Th.otf"));
            } else if (v instanceof RadioButton) {
                ((RadioButton) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLTPro-Th.otf"));
            }
        } catch (Exception e) {
        }
    }

    public static Bitmap retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_pic);
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

    /**
     * to hide default keyboard if open .
     *
     * @param activity we have to pass only activity.
     */
    public static void hideSoftKeyboard(Activity activity) {
        try {
            if (activity != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                View v = activity.getCurrentFocus();
                if (v != null) {
                    IBinder binder = activity.getCurrentFocus()
                            .getWindowToken();
                    if (binder != null) {
                        inputMethodManager.hideSoftInputFromWindow(binder, 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void write(String value) {
//        System.out.println(value);
    }

    public static void openExitDialog(final Activity context) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext;
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        oktext = (TextView) dialog.findViewById(R.id.oktext);
        alerttextlogout.setText("Do you want to exit?");
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
                context.finish();
            }
        });
        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    /**
     * Method to play an alarm sound to signal half time or full time.
     */
    public static void playAlarm(final Activity context) {
        GlobalMethod.write("PLAY ALARM");
        final AudioManager mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        try {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (GlobalMethod.getSavedPreferences(context, Constant.AUDIOFILE, "").isEmpty()) {
            mp = MediaPlayer.create(context, R.raw.emergency_1);
        } else {
            mp = MediaPlayer.create(context, Integer.valueOf(GlobalMethod.getSavedPreferences(context, Constant.AUDIOFILE, "")));
        }
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                context.finish();
            }
        });

    }

    /**
     * Method to stop an alarm sound
     */
    public static void stopAlarm(Activity context) {
        GlobalMethod.write("STOP ALARM");

        try {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    public static void HelveticaCE35Thin(final Context context, final View v) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                toSetFontstohomescreen(context, child);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "Helvetica Neue CE 35 Thin.ttf"));
        } else if (v instanceof CheckBox) {
            ((CheckBox) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "Helvetica Neue CE 35 Thin.ttf"));
        } else if (v instanceof EditText) {
            ((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "Helvetica Neue CE 35 Thin.ttf"));
        } else if (v instanceof Button) {
            ((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "Helvetica Neue CE 35 Thin.ttf"));
        } else if (v instanceof RadioButton) {
            ((RadioButton) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "Helvetica Neue CE 35 Thin.ttf"));
        }
    }

    public static void AcaslonProSemiBoldButton(final Context context, final Button v) {
        try {
            v.setTypeface(Typeface.createFromAsset(context.getAssets(), "ACaslonPro-Semibold.otf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void AcaslonProSemiBoldTextView(final Context context, final TextView v) {
        try {
            v.setTypeface(Typeface.createFromAsset(context.getAssets(), "ACaslonPro-Semibold.otf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void AcaslonProSemiBoldEditText(final Context context, final TextView v) {
        try {
            v.setTypeface(Typeface.createFromAsset(context.getAssets(), "ACaslonPro-Semibold.otf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    Getting an address from Latitude and Longitude
     */
    public static String GetAddress(Activity activity, String lat, String lon) {
        Geocoder geocoder = new Geocoder(activity, Locale.ENGLISH);
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    if (i < returnedAddress.getMaxAddressLineIndex() - 1)
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("," + "\t");
                    else
                        strReturnedAddress.append(returnedAddress.getAddressLine(i));
                }
                ret = strReturnedAddress.toString();
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "Can't get Address!";
        }
        return ret;
    }

    /*
     to get required time format
     */
    public static String convert_date_dd_MM_yyyy(String date) {
        String strCurrentDate = date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
//            2015-12-14 21:34:20
            //2015-12-14 18:52:13
            newDate = format.parse(strCurrentDate);
            format = new SimpleDateFormat("dd MMMM yyyy, hh:mm a");
            return format.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String retrieveContactNumbers(Activity context, long contactId) {
        String str = "";
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId + ""}, null);
        if (cursor.getCount() >= 1) {

            while (cursor.moveToNext()) {
                // store the numbers in an array
                str = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

//                if (str != null && str.trim().length() > 0) {
//                    phoneNum.add(str);
//                }

            }
        }
        cursor.close();
        if (str == null) {
            str = "";
        }
        return str.replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
//        textView.setText(phoneNum.get(0));
//        return phoneNum;
    }

    public static String retrieveContactName(Activity context, long contactId) {
        String contactName = "";

        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                ContactsContract.Contacts._ID + " = ?", new String[]{String.valueOf(contactId)}, null);

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        if (contactName == null) {
            contactName = "";
        }
        return contactName;
//        textView.setText(contactName);
    }

    /**
     * Method to add audio file from raw folder.
     */
    static void addAudioFile() {
        mAudioModalList = new ArrayList<>();
        mAudioModalList.add(new AudioModel(0, R.raw.emergency_1, "Emergency 1"));
        mAudioModalList.add(new AudioModel(1, R.raw.emergency_2, "Emergency 2"));
        mAudioModalList.add(new AudioModel(2, R.raw.emergency_3, "Emergency 3"));
        mAudioModalList.add(new AudioModel(3, R.raw.ringtone_1, "Ringtone 1"));
        mAudioModalList.add(new AudioModel(4, R.raw.ringtone_2, "Ringtone 2"));
    }

    public static void setRingtone(Context context, String i) {
        addAudioFile();
        GlobalMethod.savePreferences(context, Constant.AUDIOFILE, String.valueOf(mAudioModalList.get(Integer.parseInt(i)).getAudiofile()));
    }

    static int locationMode;

    public static void checkGpsEnable(Activity mActivity, boolean showDialogue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(mActivity.getContentResolver(), Settings.Secure.LOCATION_MODE);
                switch (locationMode) {
                    case Settings.Secure.LOCATION_MODE_HIGH_ACCURACY:
                        GlobalMethod.write("----====LOCATION_MODE_HIGH_ACCURACY");
                        break;
                    case Settings.Secure.LOCATION_MODE_SENSORS_ONLY:
                        GlobalMethod.write("----====LOCATION_MODE_SENSORS_ONLY");
                        break;
                    case Settings.Secure.LOCATION_MODE_BATTERY_SAVING:
                        GlobalMethod.write("----====LOCATION_MODE_BATTERY_SAVING");
                        break;
                    case Settings.Secure.LOCATION_MODE_OFF:
                        GlobalMethod.write("----====LOCATION_MODE_OFF");
                        if (showDialogue)
                            toShowDialogueWhetherGPSisEnabledorNot(mActivity);
                        break;
                    default:
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                LocationManager manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (showDialogue)
                        toShowDialogueWhetherGPSisEnabledorNot(mActivity);
                }
            }

        } else {
            LocationManager manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (showDialogue)
                    toShowDialogueWhetherGPSisEnabledorNot(mActivity);
            }
        }
    }

    public static void toShowDialogueWhetherGPSisEnabledorNot(final Activity mActivity) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(mActivity, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext, clickmarkettext;
        clickmarkettext = (TextView) dialog.findViewById(R.id.clickmarkettext);
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        canceltext.setVisibility(View.VISIBLE);
        oktext = (TextView) dialog.findViewById(R.id.oktext);

        alerttextlogout.setText("Please enable GPS location service from device setting.");
        clickmarkettext.setText("Enable GPS");
        oktext.setText("Settings");
        canceltext.setText("Not Now");

        GlobalMethod.AcaslonProSemiBoldTextView(mActivity, clickmarkettext);
        GlobalMethod.AcaslonProSemiBoldTextView(mActivity, oktext);
        GlobalMethod.AcaslonProSemiBoldTextView(mActivity, canceltext);
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mActivity.startActivity(intent);
                dialog.dismiss();
            }
        });
        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomToastInCenter(mActivity, mActivity.getResources().getString(R.string.closesLocation));
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //This method is generating a notification and displaying the notification
    public static void sendNotificationNew(Bundle data, Context mActivity) {
        int ringerSound = -100;
        SharedPreferences preference_settings = mActivity.getSharedPreferences(Constant.pref_settings, MODE_PRIVATE);
        String messageDisplay = data.getString("message");
        int requestID = (int) System.currentTimeMillis();
        String title = mActivity.getString(R.string.app_name);
        String message = "";
        int icon = R.mipmap.ic_launcher;
        Bitmap background = BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.ic_launcher);
        Intent notificationIntent = null;

        NotificationManager mnotificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M
                && !mnotificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            mActivity.startActivity(intent);
//            Toast.makeText(mActivity,"Please Enable Do no Disturb access", Toast.LENGTH_SHORT ).show();
            return;
        }

        GlobalMethod.write("totaldata" + data);
//        if (getIntent.getExtras() != null) {
//
//            if (getIntent.getExtras().containsKey("action"))
//                action = getIntent.getExtras().getString("action").trim();
//            if (action.equalsIgnoreCase(SFS)) {
//                message = getIntent.getExtras().getString("sname").trim() + " posted SFS for you.";
//            }

//        Notification notification = new Notification(icon, message, when);
        notificationIntent = new Intent(mActivity, Emergency_Alert_Receiver.class);
        message = data.getString("message");
        GlobalMethod.write("ACTION : " + data.containsKey("action"));
        if (data.containsKey("action") && (data.getString("action").trim().equalsIgnoreCase("cancel") || data.getString("action").trim().equalsIgnoreCase("safe"))) {
            Intent notification = new Intent(Emergency_Alert_Receiver.ALERT_RECEIVER);
            notification.putExtra("alert_id", data.getString("alert_id"));
            mActivity.sendBroadcast(notification);
        }

        if (data.containsKey("action") && data.getString("action").trim().equalsIgnoreCase("alert")) {

            GlobalMethod.write("ACTION : MESSAGE : " + data.containsKey("message"));
            if (message.length() > 0) {
                notificationIntent.putExtra("alert_id", data.getString("alert_id"));
                notificationIntent.putExtra("sender_name", data.getString("sender_name"));

//                notificationIntent.putExtra("device_id", getIntent.getExtras().getString("device_id"));
//  if(action.equalsIgnoreCase(FOLLOWER)
//                    notificationIntent = new Intent(context,ProductDetailActivity.class);
//                notificationIntent.putExtra("productid", getIntent.getExtras().getString("notification_id"));
            }
        } else if (data.containsKey("action") && (data.getString("action").trim().equalsIgnoreCase("cancel") || data.getString("action").trim().equalsIgnoreCase("safe") || data.getString("action").trim().equalsIgnoreCase("notify") || data.getString("action").trim().equalsIgnoreCase("coming"))) {
            notificationIntent = new Intent(mActivity, Landing_Activity.class);
        } else {
            message = data.getString("device_name") + " was succesfully found.";
            if (message.length() > 1) {
                notificationIntent.putExtra("device_name", data.getString("device_name"));
                notificationIntent.putExtra("mac_address", data.getString("mac_address"));
                notificationIntent.putExtra("device_id", data.getString("device_id"));
//  if(action.equalsIgnoreCase(FOLLOWER)
//                    notificationIntent = new Intent(context,ProductDetailActivity.class);
                notificationIntent.putExtra("productid", data.getString("notification_id"));
            }
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(mActivity, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mActivity)
                .setSmallIcon(icon)
                .setLargeIcon(background)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageDisplay))
                .setLights(238 - 221 - 130, 1, 1)
                .setContentText(messageDisplay).setAutoCancel(true);


        if (preference_settings.getString(Constant.alert_type_ring, "").equalsIgnoreCase("Yes")) {
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            // Vibrate if vibrate is enabled
            mBuilder.setVibrate(new long[]{1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000});
        } else {
            mBuilder.setVibrate(new long[]{1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000});
        }

        if (data.containsKey("action") && (data.getString("action").trim().equalsIgnoreCase("alert"))) {
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            ringerSound = enableSound(mActivity);
            write("ALERT : " + ringerSound);
//            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

        if (data.containsKey("action") && data.getString("action").trim().equalsIgnoreCase("coming")) {
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            ringerSound = enableVibrate(mActivity);
            write("ALERT : " + ringerSound);
//            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }

//        enableSound(mActivity);

        //For Oreo change
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      /* Create or update. */
            NotificationChannel channel = new NotificationChannel("my_channel_01",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mnotificationManager.createNotificationChannel(channel);
        }
        mBuilder.setContentIntent(contentIntent);

        mnotificationManager.notify(requestID, mBuilder.build());
//        if (ringerSound != -100) {
//            backToNormalRing(mActivity, ringerSound);
//        }
    }

    private static int enableSound(Context context) {
        final AudioManager mobilemode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int ringerMode = mobilemode.getRingerMode();
        final int previousNotificationVolume = mobilemode.getStreamVolume(AudioManager.RINGER_MODE_NORMAL);
//        if (mobilemode.getRingerMode() == AudioManager.RINGER_MODE_SILENT || mobilemode.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
        mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                mobilemode.adjustStreamVolume(AudioManager.RINGER_MODE_NORMAL, AudioManager.ADJUST_RAISE, 0);
        final int max = mobilemode.getStreamMaxVolume(AudioManager.RINGER_MODE_NORMAL);
        mobilemode.setStreamVolume(AudioManager.RINGER_MODE_NORMAL, max, 0);

        if (handler != null && r != null)
            handler.removeCallbacks(r);
        handler = new Handler(context.getMainLooper());
        handler.postDelayed(r = new Runnable() {
            @Override
            public void run() {
                mobilemode.setRingerMode(ringerMode);
                mobilemode.setStreamVolume(ringerMode, previousNotificationVolume, 0);
            }
        }, 7000);

        write("ALERT : MAX : " + mobilemode.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
//        AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0
//        mobilemode.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_RAISE | AudioManager.ADJUST_UNMUTE, 0);
//        mobilemode.adjustStreamVolume(AudioManager.RINGER_MODE_VIBRATE, mobilemode.getStreamMaxVolume(AudioManager.RINGER_MODE_VIBRATE), AudioManager.ADJUST_UNMUTE);
        return previousNotificationVolume;
        // Play notification sound


        // Set notification sound to its previous

    }

    private static int enableVibrate(Context context) {
        final AudioManager mobilemode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int ringerMode = mobilemode.getRingerMode();
        final int previousNotificationVolume = mobilemode.getStreamVolume(AudioManager.RINGER_MODE_NORMAL);
//        if (mobilemode.getRingerMode() == AudioManager.RINGER_MODE_SILENT || mobilemode.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
        mobilemode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//                mobilemode.adjustStreamVolume(AudioManager.RINGER_MODE_NORMAL, AudioManager.ADJUST_RAISE, 0);
//        final int max = mobilemode.getStreamMaxVolume(AudioManager.RINGER_MODE_NORMAL);
//        mobilemode.setStreamVolume(AudioManager.RINGER_MODE_NORMAL, max, 0);

        if (handler != null && r != null)
            handler.removeCallbacks(r);
        handler = new Handler(context.getMainLooper());
        handler.postDelayed(r = new Runnable() {
            @Override
            public void run() {
                mobilemode.setRingerMode(ringerMode);
                mobilemode.setStreamVolume(ringerMode, previousNotificationVolume, 0);
            }
        }, 7000);

        write("ALERT : MAX : " + mobilemode.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
//        AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0
//        mobilemode.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_RAISE | AudioManager.ADJUST_UNMUTE, 0);
//        mobilemode.adjustStreamVolume(AudioManager.RINGER_MODE_VIBRATE, mobilemode.getStreamMaxVolume(AudioManager.RINGER_MODE_VIBRATE), AudioManager.ADJUST_UNMUTE);
        return previousNotificationVolume;
        // Play notification sound


        // Set notification sound to its previous

    }

    private static void backToNormalRing(Context context, int previousNotificationVolume) {
        AudioManager mobilemode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mobilemode.setStreamVolume(AudioManager.STREAM_NOTIFICATION, previousNotificationVolume, 0);
//        mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }


    public static void openMap(Activity activity, float currentLat, float currentLong, float destLat, float destLong, int zoom) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d&q=%f,%f",
                currentLat, currentLong, zoom, destLat, destLong);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        activity.startActivity(intent);
    }

//    Uri gmmIntentUri = Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia");
//    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//    mapIntent.setPackage("com.google.android.apps.maps");
//
//    startActivity(mapIntent);


}
