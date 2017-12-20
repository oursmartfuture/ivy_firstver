package com.ivy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.ivy.R;
import com.ivy.Record_Audio;
import com.push_notifications.WakeLocker;

import java.util.List;

import static android.Manifest.permission.CALL_PHONE;

public class NotifyActivity extends Activity {

    private Activity mActivity;
    private Handler handler;
    private Runnable runnable;
    private static final int REQUEST_TELEPHONE = 122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        mActivity = NotifyActivity.this;
        String text = getIntent().getStringExtra("text").trim();
        GlobalMethod.stopAlarm(mActivity);
        if (text.trim().equalsIgnoreCase("2")) {
            playAlarmDialog(mActivity);
            GlobalMethod.playAlarm(mActivity);
        } else if (text.trim().equalsIgnoreCase("3")) {
            String number = GlobalMethod.getSavedPreferences(NotifyActivity.this, Constant.NUMBERLIST, "");
            GlobalMethod.write("NUMBER LIST : " + number);
            if (!TextUtils.isEmpty(number.trim().replace(",", "").replace("(", "").replace(")", "").replace("[", "").replace("]", "")))
                openDangerDialogue(mActivity);
            else {
                addGuardianDialog(mActivity);
            }
        } else if (text.equalsIgnoreCase("4")) {
            String number = GlobalMethod.getSavedPreferences(NotifyActivity.this, Constant.DEFAULT_NUMBER, "0000000000");
            if (mayRequestPermission(CALL_PHONE, REQUEST_TELEPHONE, getString(R.string.phone_permission))) {
                makeACall(mActivity, TextUtils.isEmpty(number) ? "0000000000" : number);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e("Permission", "requestCode" + requestCode);
        switch (requestCode) {
            case REQUEST_TELEPHONE: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    String number = GlobalMethod.getSavedPreferences(NotifyActivity.this, Constant.DEFAULT_NUMBER, "0000000000");
                    makeACall(mActivity, TextUtils.isEmpty(number) ? "0000000000" : number);
                } else {
                    Log.e("Permission", "Denied");
                    finish();
                }
                break;
            }
        }

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
            // Here, this is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {
                GlobalMethod.write("====PERMISSION_NOT_GRANTED");

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission)) {
                    GlobalMethod.write("====ifPermissionRationale");
                    ActivityCompat.requestPermissions(this,
                            new String[]{permission},
                            requestCode);

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.
                    GlobalMethod.write("====elsePermission");
                    GlobalMethod.showCustomToastInCenter(this,errorMessage);
                    finish();

                    // permission is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else
            {
                GlobalMethod.write("====PERMISSION_GRANTED");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        WakeLocker.acquire(NotifyActivity.this);
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        w.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        w.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        w.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onResume();
    }

    private void makeACall(Context context, String s) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setPackage("com.android.phone");
        intent.setData(Uri.parse("tel:" + s));

        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        GlobalMethod.write("PACKAGES FOUND : " + activities);

        for (int j = 0; j < activities.size(); j++) {
            if (activities.get(j).toString().toLowerCase().contains("com.android.phone")) {
                intent.setPackage("com.android.phone");
                GlobalMethod.write("PACKAGE FOUND");
            } else if (activities.get(j).toString().toLowerCase().contains("com.android.server.telecom")) {
                intent.setPackage("com.android.server.telecom");
                GlobalMethod.write("PACKAGE FOUND");
            }

        }

        try {
            context.startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }


//    class NotifyUsers extends AsyncTask<Context, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Context... params) {
//            SharedPreferences preferences = params[0].getSharedPreferences(Constant.pref_main, 0);
//            GPS gps = new GPS(params[0]);
//            ArrayList<NameValuePair> arrNameValue = new ArrayList<>();
//            arrNameValue.add(new BasicNameValuePair("user_id", preferences.getString(Constant.id_user, "")));
//            if (TextUtils.isEmpty(preferences.getString("lat", "")) || TextUtils.isEmpty(preferences.getString("lng", ""))) {
//                arrNameValue.add(new BasicNameValuePair("latitude", gps.getLatitude() + "".toString().trim()));
//                arrNameValue.add(new BasicNameValuePair("longitude", gps.getLongitude() + "".toString().trim()));
//            } else {
//                arrNameValue.add(new BasicNameValuePair("latitude", preferences.getString("lat", "")));
//                arrNameValue.add(new BasicNameValuePair("longitude", preferences.getString("lng", "")));
//            }
////            arrNameValue.add(new BasicNameValuePair("user_id", preferences.getString(Constant.id_user, "")));
////            arrNameValue.add(new BasicNameValuePair("user_id", preferences.getString(Constant.id_user, "")));
////            arrNameValue.add(new BasicNameValuePair("user_id", preferences.getString(Constant.id_user, "")));
////            return SimpleHTTPConnection.sendByPOST();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//    }

    public void openDangerDialogue(final Context context) {

        handler = new Handler();

        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext;
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        final TextView oktext = (TextView) dialog.findViewById(R.id.oktext);
        canceltext.setText("No Need");
        oktext.setText("Notify Friends");
        alerttextlogout.setText("You Are in Danger!!!");
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null && runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                Intent intent = new Intent(context, Record_Audio.class);
                dialog.dismiss();
                context.startActivity(intent);
                NotifyActivity.this.finish();
            }
        });
        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null && runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                dialog.dismiss();
                NotifyActivity.this.finish();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                oktext.performClick();
            }
        }, 5 * 1000);
    }

    public void addGuardianDialog(final Context context) {

        handler = new Handler();

        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext;
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        canceltext.setVisibility(View.INVISIBLE);
        final TextView oktext = (TextView) dialog.findViewById(R.id.oktext);
//        canceltext.setText("No Need");
        oktext.setText("Ok");
        alerttextlogout.setText(Html.fromHtml(getString(R.string.add_guardian)));
        alerttextlogout.setGravity(Gravity.CENTER);
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                NotifyActivity.this.finish();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
//        handler.postDelayed(runnable = new Runnable() {
//            @Override
//            public void run() {
//                oktext.performClick();
//            }
//        }, 5 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WakeLocker.release();

    }


    public void playAlarmDialog(final Context context) {

        handler = new Handler();

        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext;
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        final TextView oktext = (TextView) dialog.findViewById(R.id.oktext);
        canceltext.setText("No");
        oktext.setText("Yes");
        alerttextlogout.setText("Stop Sound?");
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                GlobalMethod.stopAlarm(mActivity);
                NotifyActivity.this.finish();
            }
        });
        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }

}
