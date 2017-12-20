package com.ivy;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.AsyncTask_Multipart;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Class Name: My_Profile.Class
 * Class description: This class contains methods and classes for viewing and editing the profile fields in the app.
 * Created by Sobhit Gupta(00253)
 * Created by Singsys-043 on 10/20/2015.
 */
public class My_profile extends Activity implements View.OnClickListener,CallBackListenar {
    TextView name_text,email_text,address_text,phone_text,change_pswd,headerText,edit_prof;
    ImageView edit_image,edit_pencil,notify_bell;
    RoundedImageView prof_pic;
    EditText name,email,address,phone;
    ImageButton backBtn;
    FrameLayout image_capture,notificationBtn;
    Activity context;
    Bundle params;
    String pattern = "[A-Za-z\\s]{0,}$";
    String errmsg;
    String filepath = "";
    private ObjectAnimator animation;
    boolean text = false;
    public SharedPreferences preferences;
    LinearLayout parent_layout_login,header_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        intialize();
        onCLick();
        edit_prof.setVisibility(View.VISIBLE);
        preferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
        headerText.setText("My Profile");
        GlobalMethod.AcaslonProSemiBoldTextView(My_profile.this, headerText);
        params = new Bundle();
        My_profile.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        if (SimpleHttpConnection.isNetworkAvailable(My_profile.this)) {
            params.putString("id_user", preferences.getString(Constant.id_user, ""));
            params.putString("mode","");
            new AsyncTaskApp(this, My_profile.this, Urls.view_profile, "View_Profile").execute(params);
        }
    }
    /*
    this method is for validating the phone number.
     */
    private boolean isValidPhoneNumber (CharSequence phoneNumber){
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }




    private void onCLick() {
        edit_image.setOnClickListener(this);
        change_pswd.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        parent_layout_login.setOnClickListener(this);
    }

/*
this method is for initializing the values used in the entire class .
 */

    private void intialize() {
        name_text = (TextView) findViewById(R.id.name_text);
        context = this;
        notificationBtn = (FrameLayout) findViewById(R.id.notificationBtn);
        image_capture = (FrameLayout) findViewById(R.id.image_capture);
        name = (EditText) findViewById(R.id.name);
        email_text = (TextView) findViewById(R.id.email_text);
        email = (EditText) findViewById(R.id.email);
        address = (EditText) findViewById(R.id.address);
        address_text = (TextView) findViewById(R.id.address_text);
        phone_text = (TextView) findViewById(R.id.phone_text);
        context = this;
        phone = (EditText) findViewById(R.id.phone);
        change_pswd = (TextView) findViewById(R.id.change_pswd);
        headerText = (TextView) findViewById(R.id.headerText);
        edit_prof = (TextView) findViewById(R.id.edit_prof);
        edit_prof.setOnClickListener(this);
        prof_pic = (RoundedImageView) findViewById(R.id.prof_pic);
        notify_bell = (ImageView) findViewById(R.id.notify_bell);
        edit_image = (ImageView) findViewById(R.id.edit_image);
        edit_pencil = (ImageView) findViewById(R.id.edit_pencil);
        edit_pencil.setVisibility(View.VISIBLE);
        notify_bell.setVisibility(View.GONE);
        notificationBtn.setVisibility(View.GONE);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        parent_layout_login = (LinearLayout) findViewById(R.id.parent_layout_login);
        GlobalMethod.AcaslonProSemiBoldTextView(My_profile.this, headerText);
    }

    @Override

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_pswd:
                Intent intent = new Intent(My_profile.this, ChangePassword.class);
                startActivity(intent);
                break;
            case R.id.backBtn:
                onBackPressed();
                break;
            case R.id.parent_layout_login:
                GlobalMethod.hideSoftKeyboard(this);
                break;
            case R.id.edit_prof:
                if (!text) {
                    name.setEnabled(true);
                    address.setEnabled(true);
                    phone.setEnabled(true);
                    image_capture.setEnabled(true);
                    image_capture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(My_profile.this, ChooseImageActivity.class), 5);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                        }
                    });
                    edit_prof.setText("Done");
                    edit_image.setVisibility(View.VISIBLE);
                    edit_pencil.setImageResource(R.drawable.done_prof);
                    text = true;

                } else {

                    if (validation()) {
                        if (SimpleHttpConnection.isNetworkAvailable(this)) {
                            params.putString("id_user", preferences.getString(Constant.id_user, ""));
                            params.putString("name", name.getText().toString().trim());
                            params.putString("email", email.getText().toString().trim());
                            params.putString("address", address.getText().toString().trim());
                            params.putString("phone_number", phone.getText().toString().trim());
                            params.putString("password", "");
                            params.putString("mode", "");
                            Bundle image_path = new Bundle();
                            image_path.putString("profile_pic", filepath);
                            new AsyncTask_Multipart(My_profile.this, context, Urls.my_profile, image_path, "Edit_Profile").execute(params);
                        } else {
                            GlobalMethod.showToast(this, Constant.network_error);
                        }
                    } else {
                        GlobalMethod.showToast(this, errmsg);
                    }
                }
                GlobalMethod.hideSoftKeyboard(this);
                    break;
        }
    }
/*
This method is for setting up the values to the various fields trough web service .
 */
    void Set_Result_view(String result) {

        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    name.setText(data.getString("name"));
                    email.setText(data.getString("email"));
                    if(data.getString("address").equalsIgnoreCase("null"))
                        address.setText("");
                    else
                    address.setText(data.getString("address"));
                    phone.setText(data.getString("phone_number"));
                    UniversalImageDownloader.loadImageFromURL(My_profile.this,
                            GlobalMethod.encodeURL(data.getString("profile_pic")), prof_pic, R.drawable.profile_pic);
                } else {
                    GlobalMethod.showToast(My_profile.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
/*
this method is for validating the various fields of profile .
 */
    public boolean validation() {
        if (TextUtils.isEmpty(name.getText().toString().trim())) {
            errmsg = "Please enter Name.";
            name.requestFocus();
            return false;
        }/* else if (!name.getText().toString().trim().matches(pattern)) {
            errmsg = "Please enter valid Name.";
            name.requestFocus();
            return false;
        }*/
        if (TextUtils.isEmpty(address.getText().toString().trim())) {
            errmsg = "Please enter Address.";
            address.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone.getText().toString().trim())) {
            errmsg = "Please enter Phone Number.";
            phone.requestFocus();
            return false;
        } else if (phone.getText().toString().trim().length() <8) {
            errmsg = "Please enter valid Phone Number.";
            phone.requestFocus();
            return false;
        }  else if(phone.getText().toString().trim().length()>15)
        {
            errmsg = "Please enter valid Phone Number.";
            phone.requestFocus();
            return false;
        }else if(isValidPhoneNumber(phone.getText().toString().trim())==false)
        {
            errmsg = "Please enter valid Phone Number.";
            phone.requestFocus();
            return false;
        }
        return true;
    }
    /*
    This method is for setting up the values to the various fields trough web service .
     */
    void Set_Result_edit(String result) {

        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    name.setEnabled(false);
                    address.setEnabled(false);
                    phone.setEnabled(false);
                    image_capture.setEnabled(false);
                    edit_prof.setText("Edit");
                    edit_image.setVisibility(View.GONE);
                    edit_pencil.setImageResource(R.drawable.edit);
                    text = false;
                    GlobalMethod.showToast(My_profile.this, jsonObject.getString("message"));
                } else {
                    GlobalMethod.showToast(My_profile.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            try {
                GlobalMethod.write("====saveimage" + data.getStringExtra("filepath"));
                File file = new File(data.getStringExtra("filepath"));
                filepath = data.getStringExtra("filepath");
//                prof_pic.setImageURI(Uri.fromFile(file));
                UniversalImageDownloader.loadImageFromLocalUrl(context, filepath, prof_pic);
            } catch (OutOfMemoryError e) {
                finish();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        if (action == "View_Profile") {
            Set_Result_view(result);
        } else {
            Set_Result_edit(result);
        }
    }
/*
end of class.
 */

}
