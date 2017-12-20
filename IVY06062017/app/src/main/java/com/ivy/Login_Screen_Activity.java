package com.ivy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Class Name: Login_Activity.Class
 * Class description: This class contains methods and classes for signing in the app.
 * Created by Sobhit Gupta(00253)
 * Created by Singsys-043 on 10/20/2015.
 */
public class Login_Screen_Activity extends Activity implements View.OnClickListener, CallBackListenar {
    private static final int REQUEST_ACCESS = 1230;
    LinearLayout sign_in_button, parent_layout_login;
    EditText email_login, password_login;
    String errmsg = "";
    Bundle params;
    SharedPreferences login_prference, login_password, checkbox_first_time, pref_settings;
    SharedPreferences.Editor login_edit, password_edit, edit_settings;
    String emailPattern = "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+";
    TextView forgot_pass_text, sign_up_new_account, logo_text, email1, remember_ext;
    CheckBox checkbox;

    private SharedPreferences mainPreferences;
    TextView norecord;
    AlertDialog alert;
    AlertDialog.Builder alertDialog;
    Activity mActivity;
    FrameLayout questionFrame;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = Login_Screen_Activity.this;
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.login_screen);
        parent_layout_login = (LinearLayout) findViewById(R.id.parent_layout_login);
        sign_in_button = (LinearLayout) findViewById(R.id.sign_in_button);
        email_login = (EditText) findViewById(R.id.email_login);
        password_login = (EditText) findViewById(R.id.password_login);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        questionFrame = (FrameLayout) findViewById(R.id.question);
        questionFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Screen_Activity.this, HelpScreen.class));
            }
        });
        pref_settings = getSharedPreferences(Constant.pref_settings, Activity.MODE_PRIVATE);
        login_prference = getSharedPreferences(Constant.remember_me, 0);
        login_password = getSharedPreferences(Constant.remember_me, 0);
//        checkbox_first_time = getSharedPreferences(Constant.checked_first_time,0);
        mainPreferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        edit_settings = pref_settings.edit();
        login_edit = login_prference.edit();
        password_edit = login_password.edit();
        Login_Screen_Activity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        login_edit.putString("firsttime", "1");
//        login_edit.putString("logoutissue", "1");
        password_edit.putString("firsttime", "1");

        login_edit.commit();
        password_edit.commit();

//        first_time_edit= checkbox_first_time.edit();
        forgot_pass_text = (TextView) findViewById(R.id.forgot_pass_text);
        params = new Bundle();
        logo_text = (TextView) findViewById(R.id.logo_text);
        logo_text.setOnClickListener(this);
        sign_up_new_account = (TextView) findViewById(R.id.sign_up_new_account);
        email1 = (TextView) findViewById(R.id.email1);
        // remember_ext=(TextView)findViewById(R.id.remember_ext);
        sign_in_button.setOnClickListener(this);
        parent_layout_login.setOnClickListener(this);
        sign_up_new_account.setOnClickListener(this);
        forgot_pass_text.setOnClickListener(this);
        GlobalMethod.write("logonvalue" + login_prference.getString("firsttime", ""));

//        alertDialog = new AlertDialog.Builder(Login_Screen_Activity.this);
//        alert=alertDialog.create();

        if (login_prference.contains("logoutissue") && login_prference.getString("logoutissue", "").equalsIgnoreCase("1")) {
            if (login_prference.getString(Constant.check_box, "").equals("1") &&
                    login_password.getString(Constant.check_box, "").equals("1")) {
                email_login.setText(login_prference.getString(Constant.email, ""));
                password_login.setText(login_password.getString(Constant.password, ""));
                email_login.setSelection(email_login.length());
                checkbox.setChecked(true);
            }
        } else if (login_prference.getString(Constant.check_box, "").equals("1") &&
                login_password.getString(Constant.check_box, "").equals("1")) {
            email_login.setText(login_prference.getString(Constant.email, ""));
            password_login.setText(login_password.getString(Constant.password, ""));
            email_login.setSelection(email_login.length());
            checkbox.setChecked(true);

        } else if (login_prference.getString("firsttime", "").equals("1")) {
            email_login.setText("");
            password_login.setText("");
            email_login.setSelection(email_login.length());
            checkbox.setChecked(true);

        } else {
            email_login.setText("");
            password_login.setText("");
            checkbox.setChecked(false);
        }
        Typeface tf = Typeface.createFromAsset(Login_Screen_Activity.this.getAssets(), "Helvetica Neue CE 35 Thin.ttf");
        logo_text.setTypeface(tf);

        Typeface tf7 = Typeface.createFromAsset(Login_Screen_Activity.this.getAssets(), "Helvetica Neue CE 35 Thin.ttf");
        checkbox.setTypeface(tf7);

        Typeface tf1 = Typeface.createFromAsset(Login_Screen_Activity.this.getAssets(), "ACaslonPro-Semibold.otf");
        email_login.setTypeface(tf1);

        Typeface tf2 = Typeface.createFromAsset(Login_Screen_Activity.this.getAssets(), "ACaslonPro-Semibold.otf");
        password_login.setTypeface(tf2);

        Typeface tf3 = Typeface.createFromAsset(Login_Screen_Activity.this.getAssets(), "ACaslonPro-Semibold.otf");
        email1.setTypeface(tf3);

        Typeface tf5 = Typeface.createFromAsset(Login_Screen_Activity.this.getAssets(), "ACaslonPro-Semibold.otf");
        forgot_pass_text.setTypeface(tf5);

        Typeface tf6 = Typeface.createFromAsset(Login_Screen_Activity.this.getAssets(), "ACaslonPro-Semibold.otf");
        sign_up_new_account.setTypeface(tf6);


        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (mayRequestLocation()) {
                GlobalMethod.checkGpsEnable(mActivity, true);
            }
        } else {
            GlobalMethod.checkGpsEnable(mActivity, true);
        }
    }

    public boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GlobalMethod.write("====<Build.VERSION_CODES.M");
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GlobalMethod.write("====>= Build.VERSION_CODES.M");
            if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                GlobalMethod.write("====PERMISSION_GRANTED");
                return true;
            } else if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                GlobalMethod.write("====ifPermissionRationale");
                requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_ACCESS);
            } else {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_ACCESS);
//                GlobalMethod.showCustomToastInCenter(mActivity, "We can't get your location. Please Enable Location Permission.");
                GlobalMethod.write("====elsePermission");
//                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e("Permission", "requestCode" + requestCode);
        switch (requestCode) {
            case REQUEST_ACCESS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    GlobalMethod.checkGpsEnable(mActivity, true);
                } else {
                    Log.e("Permission", "Denied");
                    GlobalMethod.showCustomToastInCenter(mActivity, "We can't get your location. Please Enable Location Permission.");
                }

                return;
            }
        }
    }


    // this method is for checking the validation regarding email and password .
    public boolean validation() {
        if (TextUtils.isEmpty(email_login.getText().toString().trim())) {
            errmsg = getResources().getString(R.string.email_validation);
            email_login.requestFocus();
            return false;
        } else if (!email_login.getText().toString().trim().matches(emailPattern)) {
            errmsg = "Please enter valid E-mail.";
            email_login.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password_login.getText().toString().trim())) {
            errmsg = getResources().getString(R.string.password_validation);
            password_login.requestFocus();
            return false;
        }
        return true;
    }

    void Set_Result(String result) {

        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    if (checkbox.isChecked()) {
                        login_edit.putString(Constant.email, email_login.getText().toString().trim());
                        password_edit.putString(Constant.password, password_login.getText().toString().trim());
                        login_edit.putString(Constant.check_box, "1");
                        password_edit.putString(Constant.check_box, "1");
                        login_edit.putString("firsttime", "0");
                        login_edit.putString("logoutissue", "1");
                        login_edit.commit();
                        password_edit.commit();
                    } else {
                        login_edit.putString(Constant.email, "");
                        password_edit.putString(Constant.password, "");
                        login_edit.putString(Constant.check_box, "0");
                        password_edit.putString(Constant.check_box, "0");
                        login_edit.putString("firsttime", "0");
                        login_edit.putString("logoutissue", "1");
                        login_edit.commit();
                        password_edit.commit();
                    }

                   /*
                   to write edit_settings preference code afterwards....
                    */
                    edit_settings.putString(Constant.view_notification, data.getString("view_notification"));
                    edit_settings.putString(Constant.alert_type_ring, data.getString("alert_type_ring"));
                    edit_settings.putString(Constant.left_battery, data.getString("left_battery"));
                    edit_settings.putString(Constant.show_map, data.getString("show_map"));
                    edit_settings.putString(Constant.connection, data.getString("connection"));
                    edit_settings.putString(Constant.connection_fading, "No");
//                            data.getString("connection_fading"));
                    edit_settings.commit();
                    JSONArray arrContact = jsonObject.getJSONArray("contact_number");
                    if (arrContact.length() > 0) {
                        String commaSeperated = arrContact.toString().replace("[", "").replace("]", "").trim();
                        GlobalMethod.savePreferences(Login_Screen_Activity.this, Constant.NUMBERLIST, commaSeperated);
                    }
                    SharedPreferences.Editor main_edit = mainPreferences.edit();
                    GlobalMethod.savePreferences(this, Constant.NAME, data.optString("name"));
                    main_edit.putString(Constant.email, email_login.getText().toString().trim());
                    main_edit.putBoolean(Constant.isContactsUpdated, false);
                    main_edit.putString(Constant.password, password_login.getText().toString().trim());
                    main_edit.putString(Constant.id_user, data.getString("id_user"));
                    if (!TextUtils.isEmpty(data.optString("default_number")))
                        GlobalMethod.savePreferences(Login_Screen_Activity.this, Constant.DEFAULT_NUMBER, data.getString("default_number"));
                    if (!TextUtils.isEmpty(data.optString("default_ringtone")))
                        GlobalMethod.savePreferences(Login_Screen_Activity.this, Constant.AUDIOFILESEQUENCE, data.getString("default_ringtone"));
                    main_edit.commit();

                    GlobalMethod.setRingtone(Login_Screen_Activity.this, GlobalMethod.getSavedPreferences(Login_Screen_Activity.this, Constant.AUDIOFILESEQUENCE, "0"));

                    if (jsonObject.has("notification")) {
                        JSONArray notiJsonArray = jsonObject.optJSONArray("notification");
                        if (notiJsonArray != null && notiJsonArray.length() > 0) {
                            for (int i = 0; i < notiJsonArray.length(); i++) {
                                GlobalMethod.sendNotificationNew(jsonStringToBundle(notiJsonArray.getJSONObject(i).getString("notification_data")), Login_Screen_Activity.this);
                            }
                        }
                    }

                    Intent intent = new Intent(Login_Screen_Activity.this, Landing_Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    GlobalMethod.showToast(Login_Screen_Activity.this, jsonObject.getString("message"));
                    password_login.setText("");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static Bundle jsonStringToBundle(String jsonString) {
        try {
            JSONObject jsonObject = toJsonObject(jsonString);
            return jsonToBundle(jsonObject);
        } catch (JSONException ignored) {

        }
        return null;
    }

    public static JSONObject toJsonObject(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }

    public static Bundle jsonToBundle(JSONObject jsonObject) throws JSONException {
        Bundle bundle = new Bundle();
        Iterator iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = jsonObject.getString(key);
            GlobalMethod.write("==key" + key);
            GlobalMethod.write("==value" + value);

            bundle.putString(key, value);
        }
        return bundle;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                if (validation()) {
                    params.putString("email", String.valueOf(email_login.getText().toString().trim()));
                    params.putString("password", String.valueOf(password_login.getText().toString().trim()));
                    params.putString("device_type", "1");
                    params.putString("device_token", GlobalMethod.getSavedPreferences(Login_Screen_Activity.this, Constant.RegisterationId, ""));
                    params.putString("app_version", GlobalMethod.appp_version(Login_Screen_Activity.this));
                    params.putString("mode", "");
                    GlobalMethod.write("params_login" + params.toString());
                    if (SimpleHttpConnection.isNetworkAvailable(Login_Screen_Activity.this)) {
                        new AsyncTaskApp(this, Login_Screen_Activity.this, Urls.Sign_In_URL, "LOGIN").execute(params);
                    } else {
                        GlobalMethod.showToast(Login_Screen_Activity.this, Constant.network_error);
                    }
                    GlobalMethod.hideSoftKeyboard(this);
                } else {
                    GlobalMethod.showToast(Login_Screen_Activity.this, errmsg);
                    GlobalMethod.hideSoftKeyboard(this);
                }
                break;
            case R.id.sign_up_new_account:
                Intent intent = new Intent(Login_Screen_Activity.this, Sign_Up_Activity.class);
                startActivity(intent);
                break;
            case R.id.forgot_pass_text:
                Intent intent1 = new Intent(Login_Screen_Activity.this, Forget_Password.class);
                startActivity(intent1);
                break;
            case R.id.parent_layout_login:
                GlobalMethod.hideSoftKeyboard(this);
                break;
            case R.id.logo_text:
                GlobalMethod.hideSoftKeyboard(this);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (login_prference.getString(Constant.check_box, "").equals("1"))
                && (login_password.getString(Constant.check_box, "").equals("1"))) {
            email_login.setText(login_prference.getString(Constant.email, ""));
            password_login.setText(login_password.getString(Constant.password, ""));
            password_login.setSelection(password_login.length());
            checkbox.setChecked(true);
        } else {

            email_login.setText("");
            password_login.setText("");
            checkbox.setChecked(false);
        }
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        Set_Result(result);
    }

    @Override
    public void onBackPressed() {
        GlobalMethod.openExitDialog(this);
    }

    /*
    end of class .
     */
}
