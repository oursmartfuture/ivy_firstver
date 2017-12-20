package com.ivy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Name: Sign_Up_Activity.Class
 * Class description: This class contains methods and classes for signing up the app.
 * Created by Sobhit Gupta(00253)
 * Created by Singsys-043 on 10/20/2015.
 */
public class Sign_Up_Activity extends Activity implements View.OnClickListener, CallBackListenar {

    LinearLayout sign_up_button, parent_layout_signup;
    EditText name_txt, email_signup, password_sign_up, phone_sign_up, confirm_pass_sign_up;
    String errmsg = "";
    Bundle params;
    private static final String emailExpression = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$)";
    String emailPattern = "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+";
    TextView already_registered_text, logo_text, email1, country_code_sign_up;
    TextView norecord;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);
        parent_layout_signup = (LinearLayout) findViewById(R.id.parent_layout_signup);
        sign_up_button = (LinearLayout) findViewById(R.id.sign_up_button);
        name_txt = (EditText) findViewById(R.id.name_txt);
        email_signup = (EditText) findViewById(R.id.email_signup);
        phone_sign_up = (EditText) findViewById(R.id.phone_sign_up);
        password_sign_up = (EditText) findViewById(R.id.password_sign_up);
        confirm_pass_sign_up = (EditText) findViewById(R.id.confirm_pass_sign_up);
        country_code_sign_up = (TextView) findViewById(R.id.country_code_sign_up);
        country_code_sign_up.setOnClickListener(this);
        already_registered_text = (TextView) findViewById(R.id.already_registered_text);
        params = new Bundle();
        email1 = (TextView) findViewById(R.id.email1);
        logo_text = (TextView) findViewById(R.id.logo_text);
        sign_up_button.setOnClickListener(this);
        password_sign_up.setOnClickListener(this);
        parent_layout_signup.setOnClickListener(this);
        already_registered_text.setOnClickListener(this);
        logo_text.setOnClickListener(this);
        Typeface tf = Typeface.createFromAsset(Sign_Up_Activity.this.getAssets(), "HelveticaNeueLTPro-Lt.otf");
        logo_text.setTypeface(tf);
        Typeface tf1 = Typeface.createFromAsset(Sign_Up_Activity.this.getAssets(), "pala_0.ttf");
        email_signup.setTypeface(tf1);
        Typeface tf2 = Typeface.createFromAsset(Sign_Up_Activity.this.getAssets(), "pala_0.ttf");
        password_sign_up.setTypeface(tf2);
        Typeface tf3 = Typeface.createFromAsset(Sign_Up_Activity.this.getAssets(), "pala_0.ttf");
        email1.setTypeface(tf3);
        Typeface tf4 = Typeface.createFromAsset(Sign_Up_Activity.this.getAssets(), "HelveticaNeueLTPro-Lt.otf");
        phone_sign_up.setTypeface(tf4);
        country_code_sign_up.setTypeface(tf4);
        Typeface tf5 = Typeface.createFromAsset(Sign_Up_Activity.this.getAssets(), "pala_0.ttf");
        confirm_pass_sign_up.setTypeface(tf5);
        Typeface tf6 = Typeface.createFromAsset(Sign_Up_Activity.this.getAssets(), "palab_0.ttf");
        already_registered_text.setTypeface(tf6);
        email_signup.requestFocus();
    }

    // this method is for checking the validation regarding email and password.
    public boolean validation() {
        if (TextUtils.isEmpty(name_txt.getText().toString().trim())) {
            errmsg = getResources().getString(R.string.user_name_validation);
            name_txt.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email_signup.getText().toString().trim())) {
            errmsg = getResources().getString(R.string.email_validation);
            email_signup.requestFocus();
            return false;
        }
        else if (!isEmailValid(email_signup.getText().toString().trim())) {
//            !email_signup.getText().toString().trim().matches(emailPattern)
            errmsg = "Please enter valid Email.";
            email_signup.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(country_code_sign_up.getText().toString().trim())) {
            errmsg = "Please enter Country Code. ";
            country_code_sign_up.requestFocus();
            return false;

        } else if (TextUtils.isEmpty(phone_sign_up.getText().toString().trim())) {
            errmsg = "Please enter Phone Number. ";
            phone_sign_up.requestFocus();
            return false;

        }
        else if (phone_sign_up.getText().toString().trim().length() + country_code_sign_up.getText().toString().trim().length() < 8) {
            errmsg = "Please enter valid Phone Number.";
            phone_sign_up.requestFocus();
            return false;
        } else if (phone_sign_up.getText().toString().trim().length() + country_code_sign_up.getText().toString().trim().length() > 15) {
            errmsg = "Please enter valid Phone Number.";
            phone_sign_up.requestFocus();
            return false;
        } else if (isValidPhoneNumber(phone_sign_up.getText().toString().trim()) == false) {
            errmsg = "Please enter valid Phone Number.";
            phone_sign_up.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password_sign_up.getText().toString().trim())) {
            errmsg = getResources().getString(R.string.password_validation);
            password_sign_up.requestFocus();
            return false;
        } else if (password_sign_up.getText().toString().trim().length() < 8) {
            errmsg = "Please enter password of minimum 8 characters.";
            password_sign_up.requestFocus();
            return false;
        } else if (password_sign_up.getText().toString().trim().length() > 12) {
            errmsg = "Please enter password of minimum 8 and maximum 12 characters.";
            password_sign_up.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pass_sign_up.getText().toString().trim())) {
            errmsg = "Please enter Confirm Password.";
            confirm_pass_sign_up.requestFocus();
            return false;
        } else if (!password_sign_up.getText().toString().trim().equalsIgnoreCase(confirm_pass_sign_up.getText().toString().trim())) {
            errmsg = "Password and Confirm Password does not match.";
            confirm_pass_sign_up.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    /*
    this method is for setting up the value of message through web service .
     */
    void Set_Result(String result) {

        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    GlobalMethod.showToast(Sign_Up_Activity.this, jsonObject.getString("message"));
                    Intent intent = new Intent(Sign_Up_Activity.this, Pop_up_Sign_Up.class);
                    startActivity(intent);
                    finish();
                } else {
                    GlobalMethod.showToast(Sign_Up_Activity.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button:
                if (validation()) {
                    params.putString("name", String.valueOf(name_txt.getText().toString().trim()));
                    params.putString("email", String.valueOf(email_signup.getText().toString().trim()));
                    params.putString("phone_number", String.valueOf(country_code_sign_up.getText().toString().trim() + phone_sign_up.getText().toString().trim()));
                    params.putString("password", String.valueOf(password_sign_up.getText().toString().trim()));
                    if (SimpleHttpConnection.isNetworkAvailable(Sign_Up_Activity.this)) {
                        new AsyncTaskApp(this, Sign_Up_Activity.this, Urls.REGISTRATION_URL, "SIGNUP").execute(params);
                    } else {
                        GlobalMethod.showToast(Sign_Up_Activity.this, Constant.network_error);
                    }
                    GlobalMethod.hideSoftKeyboard(this);
                } else {
                    GlobalMethod.showToast(Sign_Up_Activity.this, errmsg);
                    GlobalMethod.hideSoftKeyboard(this);
                }
                break;
            case R.id.country_code_sign_up:
                Intent intentcountry = new Intent(Sign_Up_Activity.this, AddCountryList.class);
                startActivityForResult(intentcountry, 105);
                break;
            case R.id.password_sign_up:
                break;
            case R.id.already_registered_text:
                finish();
                break;
            case R.id.parent_layout_signup:
                GlobalMethod.hideSoftKeyboard(this);
                break;
            case R.id.logo_text:
                GlobalMethod.hideSoftKeyboard(this);
                break;

        }
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        Set_Result(result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 105 && resultCode == RESULT_OK) {
            GlobalMethod.hideSoftKeyboard(this);
            country_code_sign_up.setText(data.getStringExtra("country_code"));
        }
    }
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
//        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    /*
    end of class.
     */
}

