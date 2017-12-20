package com.ivy;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Singsys-043 on 11/23/2015.
 */
public class ChangePassword extends Activity implements CallBackListenar, View.OnClickListener {
    EditText old_password,new_password,re_type_password;
    Bundle params;
    String errmsg="";
    LinearLayout parent_layout_login,header;
    ImageButton backBtn;
    public SharedPreferences preferences;
    TextView email1,headerText;
    private ObjectAnimator animation;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        old_password=(EditText)findViewById(R.id.old_password);
        new_password=(EditText)findViewById(R.id.new_password);
        backBtn=(ImageButton)findViewById(R.id.backBtn);
        re_type_password=(EditText)findViewById(R.id.re_type_password);
        email1=(TextView)findViewById(R.id.save_txt);
        parent_layout_login=(LinearLayout)findViewById(R.id.parent_layout_login);
        header=(LinearLayout)findViewById(R.id.header);
        headerText = (TextView) findViewById(R.id.headerText);
        headerText.setText("Change Password");
        GlobalMethod.AcaslonProSemiBoldTextView(ChangePassword.this, headerText);
        preferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        email1.setOnClickListener(this);
        ChangePassword.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        parent_layout_login.setOnClickListener(this);
        header.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        params = new Bundle();
        GlobalMethod.hideSoftKeyboard(this);
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        Set_Result_view(result);
    }
    void Set_Result_view(String result) {

        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    GlobalMethod.showToast(ChangePassword.this, jsonObject.getString("message"));
                    finish();
                } else {
                    GlobalMethod.showToast(ChangePassword.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public boolean validation() {
        if (TextUtils.isEmpty(old_password.getText().toString().trim())) {
            errmsg = "Please enter Old Password.";
            old_password.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(new_password.getText().toString().trim())) {
            errmsg = "Please enter New Password.";
            new_password.requestFocus();
            return false;
        }
        else if (new_password.getText().toString().trim().length() > 0 && new_password.getText().toString().trim().length() < 8) {
            errmsg = "Please enter password of minimum 8 characters.";
            new_password.requestFocus();
            return false;
        } else if(new_password.getText().toString().trim().length() >12)
        {
            errmsg = "Please enter password of minimum 8 and maximum 12 characters.";
            new_password.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(re_type_password.getText().toString().trim())) {
            errmsg = "Please enter Re-Type Password.";
            re_type_password.requestFocus();
            return false;
        }
        else if (!(new_password.getText().toString().trim()).equalsIgnoreCase(re_type_password.getText().toString().trim()))
        {
            errmsg="New Password does not match Re-Type Password.";
            re_type_password.requestFocus();
            return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_txt:
                if (validation()) {

                    if (SimpleHttpConnection.isNetworkAvailable(this)) {

                        params.putString("user_id", preferences.getString(Constant.id_user, ""));
                        params.putString("old_password", old_password.getText().toString().trim());
                        params.putString("new_password", new_password.getText().toString().trim());
                        params.putString("confirm_password", re_type_password.getText().toString().trim());
                        params.putString("mode", "");
                        new AsyncTaskApp(this, ChangePassword.this, Urls.Change_Password, "Change_Password").execute(params);


                    }
                    else {
                        GlobalMethod.showToast(this, Constant.network_error);
                    }

                }
                else {
                    GlobalMethod.showToast(this, errmsg);
                }
                GlobalMethod.hideSoftKeyboard(this);
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.header:
                GlobalMethod.hideSoftKeyboard(this);
                break;
            case R.id.parent_layout_login:
                GlobalMethod.hideSoftKeyboard(this);
                break;
        }
    }

}
