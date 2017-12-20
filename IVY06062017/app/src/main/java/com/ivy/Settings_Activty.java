package com.ivy;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.DatabaseHandler;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class Name: Settings_Activity.Class
 * Class description: This class contains methods and classes for managing the
 * different settings related to profile,contacts,general settings,notifications,etc.
 * Created by Sobhit Gupta(00253)
 * Created by Singsys-043 on 10/21/2015.
 */
public class Settings_Activty extends Activity implements View.OnClickListener, CallBackListenar {

    RelativeLayout rlGeneralSettings, rlAudio, rlNotifications, rlMyProfile, rlAboutUs,
            rlContactUs, rlTermsAndCondition, rlPrivacyPolicy, rlLogout, rlManageContacts;
    ImageButton backBtn;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editPreferences;
    TextView headerText;
    private RelativeLayout rlAlert;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Settings_Activty.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        initialze();
        onCLick();
    }

    private void onCLick() {
        rlMyProfile.setOnClickListener(this);
    }

    private void initialze() {
        sharedPreferences = getSharedPreferences(Constant.pref_main, 0);
        editPreferences = sharedPreferences.edit();
        rlManageContacts = (RelativeLayout) findViewById(R.id.rlManageContacts);
        rlManageContacts.setOnClickListener(this);
        rlGeneralSettings = (RelativeLayout) findViewById(R.id.rlGeneralSettings);
        rlGeneralSettings.setOnClickListener(this);
        rlAudio = (RelativeLayout) findViewById(R.id.rlAudio);
        rlAudio.setOnClickListener(this);
        rlNotifications = (RelativeLayout) findViewById(R.id.rlNotifications);
        rlMyProfile = (RelativeLayout) findViewById(R.id.rlMyProfile);
        rlAboutUs = (RelativeLayout) findViewById(R.id.rlAboutUs);
        rlAboutUs.setOnClickListener(this);
        rlNotifications.setOnClickListener(this);
        rlContactUs = (RelativeLayout) findViewById(R.id.rlContactUs);
        rlContactUs.setOnClickListener(this);
        rlAlert = (RelativeLayout) findViewById(R.id.rlAlert);
        rlAlert.setOnClickListener(this);
        rlTermsAndCondition = (RelativeLayout) findViewById(R.id.rlTermsAndCondition);
        rlTermsAndCondition.setOnClickListener(this);
        rlPrivacyPolicy = (RelativeLayout) findViewById(R.id.rlPrivacyPolicy);
        rlPrivacyPolicy.setOnClickListener(this);
        rlLogout = (RelativeLayout) findViewById(R.id.rlLogout);
        rlLogout.setOnClickListener(this);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        headerText = (TextView) findViewById(R.id.headerText);
        GlobalMethod.AcaslonProSemiBoldTextView(Settings_Activty.this, headerText);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlManageContacts:
                Intent manageContact = new Intent(Settings_Activty.this, Add_user_contact.class);
                manageContact.putExtra("header_name", "Manage Contacts");
                startActivity(manageContact);
                break;
            case R.id.rlNotifications:
                Intent intent = new Intent(Settings_Activty.this, Notification_Activity.class);
                intent.putExtra("from_page", "from_settings");
                startActivity(intent);
//                openDangerDialogue(Settings_Activty.this);
                break;
            case R.id.rlAlert:
                Intent intent1 = new Intent(Settings_Activty.this, My_Alert_Activity.class);
                intent1.putExtra("from_page", "from_settings");
                startActivity(intent1);
//                openDangerDialogue(Settings_Activty.this);
                break;
            case R.id.rlAboutUs:
                Intent intent_static = new Intent(Settings_Activty.this, LoadStaticPages.class);
                intent_static.putExtra("header_name", "About Us");
                intent_static.putExtra("url", Urls.BASE_URL + "static-pages.php?page_key=about_us");
                startActivity(intent_static);
                break;
            case R.id.rlTermsAndCondition:
                Intent intent_terms_and_conditions = new Intent(Settings_Activty.this, LoadStaticPages.class);
                intent_terms_and_conditions.putExtra("header_name", "Terms & Conditions");
                intent_terms_and_conditions.putExtra("url", Urls.BASE_URL + "static-pages.php?page_key=terms_and_conditions");
                startActivity(intent_terms_and_conditions);
//                GlobalMethod.checkTap(Settings_Activty.this, "2");

                break;
            case R.id.rlPrivacyPolicy:
                Intent intent_privacy = new Intent(Settings_Activty.this, LoadStaticPages.class);
                intent_privacy.putExtra("header_name", "Privacy Policy");
                intent_privacy.putExtra("url", Urls.BASE_URL + "static-pages.php?page_key=privacy_policy");
                startActivity(intent_privacy);
//                GlobalMethod.checkTap(Settings_Activty.this, "4");

                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.rlMyProfile:
                Intent myprofie = new Intent(Settings_Activty.this, My_profile.class);
                startActivity(myprofie);
                break;
            case R.id.rlGeneralSettings:
                Intent genral_settings = new Intent(Settings_Activty.this, GeneralSetting.class);
                startActivity(genral_settings);
                break;

            case R.id.rlAudio:
                Intent rlAudioIntent1 = new Intent(Settings_Activty.this, AudioSettingActivity.class);
                startActivity(rlAudioIntent1);
                break;
            case R.id.rlContactUs:
                Intent rlContactUs = new Intent(Settings_Activty.this, Contact_Us_Activity.class);
                startActivity(rlContactUs);
//                GlobalMethod.checkTap(Settings_Activty.this, "3");

                break;
            case R.id.rlLogout:
//                GlobalMethod.checkTap(Settings_Activty.this, "3");
                openExitDialog(Settings_Activty.this);
                break;
        }
    }

    /*
    this method is for going back to the home screen when back pressed.
     */
    @Override
    public void onBackPressed() {
        finish();
    }


    /*
    this method is for opening up the pop up for exiting the app if back button is pressed .
       */
    public void openExitDialog(final Activity context) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext;
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        oktext = (TextView) dialog.findViewById(R.id.oktext);
        alerttextlogout.setText("Do you want to logout from this app?");
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                SharedPreferences mainPreferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
                String userId = mainPreferences.getString(Constant.id_user, "");
                bundle.putString("user_id", userId);
                dialog.dismiss();
                new AsyncTaskApp(Settings_Activty.this, Settings_Activty.this, Urls.LOGOUT, "logout").execute(bundle);
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

    /*
 this method is for opening up the pop up for exiting the app if back button is pressed .
  */
    public void openDangerDialogue(final Activity context) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.logout_pop_up);
        TextView alerttextlogout, canceltext, oktext;
        alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
        canceltext = (TextView) dialog.findViewById(R.id.canceltext);
        oktext = (TextView) dialog.findViewById(R.id.oktext);
        canceltext.setText("No Need");
        oktext.setText("Notify Friends");
        alerttextlogout.setText("You Are in Danger!!!");
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Activty.this, Record_Audio.class);
                startActivity(intent);
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

    /***
     * this method will use to clear all notification from mobile if user has logout from app.
     *
     * @param context we need to pass context when you are calling.
     */
    public void cancel_All_Notification(final Context context) {
        try {
            NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        if (action.equalsIgnoreCase("logout")) {

            JSONObject obj = new JSONObject(result);
            String status = obj.getString("success");
            if (status.equalsIgnoreCase("1")) {
                if (Landing_Activity.mBluetoothLeService != null) {
                    Landing_Activity.mBluetoothLeService.disconnect("");
                }
//
                editPreferences.clear().commit();
                GlobalMethod.clearPreferences(Settings_Activty.this);
                cancel_All_Notification(Settings_Activty.this);
                DatabaseHandler cleardb = new DatabaseHandler(Settings_Activty.this);
                cleardb.clearDataBase();
                Intent i = new Intent(Settings_Activty.this, Login_Screen_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            } else {
                GlobalMethod.showCustomToastInCenter(Settings_Activty.this, obj.getString("message"));
            }
        }
    }
    /*
    end of class .
     */
}
