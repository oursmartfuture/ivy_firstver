package com.ivy;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
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

/**
 * Class Name: Forget_Pasword.Class
 * Class description: This class contains methods and classes for resetting the password in case user forgets the password.
 * Created by Sobhit Gupta(00253)
 * Created by Singsys-043 on 10/26/2015.
 */
public class Forget_Password extends Activity implements View.OnClickListener,CallBackListenar {

    TextView header_text, email1,back;
    EditText email_forget;
    String emailPattern ="^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+";
    String errmsg;
    private ObjectAnimator animation;
    Bundle params;
    LinearLayout submit_button,parent_layout_login;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        email_forget = (EditText) findViewById(R.id.email_forget);
        header_text = (TextView) findViewById(R.id.header_text);
        back = (TextView) findViewById(R.id.back);
        email1 = (TextView) findViewById(R.id.email1);
        params = new Bundle();
        GlobalMethod.AcaslonProSemiBoldTextView(Forget_Password.this, header_text);
        submit_button=(LinearLayout)findViewById(R.id.submit_button);
        parent_layout_login=(LinearLayout)findViewById(R.id.parent_layout_login);
        parent_layout_login.setOnClickListener(this);
        submit_button.setOnClickListener(this);
        Forget_Password.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        Typeface tf = Typeface.createFromAsset(Forget_Password.this.getAssets(), "HelveticaNeueLTPro-Th.otf");
        header_text.setTypeface(tf);
        Typeface tf2 = Typeface.createFromAsset(Forget_Password.this.getAssets(), "HelveticaNeueLTPro-Lt.otf");
        email_forget.setTypeface(tf2);
        Typeface tf4 = Typeface.createFromAsset(Forget_Password.this.getAssets(), "HelveticaNeueLTPro-Lt.otf");
        back.setTypeface(tf4);
        header_text.setOnClickListener(this);
        back.setOnClickListener(this);

    }
    /*
    this method is for checking the validation to various fields .
     */
    public boolean validation() {
        if (TextUtils.isEmpty(email_forget.getText().toString().trim())) {
            errmsg = "Please enter Email.";
            return false;
        } else if (!email_forget.getText().toString().trim().matches(emailPattern))
        {
            errmsg = "Please enter valid Email.";
            return false;
        }
        return true;
    }
    /*
    this method is for setting up the value of message through web service.
     */
    void Set_Result(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                    Intent intent = new Intent(Forget_Password.this,Pop_Up_Forget.class);
                    startActivity(intent);
                    finish();
                } else {
                    GlobalMethod.showToast(Forget_Password.this, jsonObject .getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit_button :
                if(validation())
                {
                    params.putString("email", String.valueOf(email_forget.getText().toString().trim()));
                    params.putString("mode", String.valueOf(""));
                    if (SimpleHttpConnection.isNetworkAvailable(Forget_Password.this)) {
                    new AsyncTaskApp(this, Forget_Password.this, Urls.Forgot_Pass_URL, "FORGET_PASS").execute(params);

                    }
                    else
                    {
                        GlobalMethod.showToast(Forget_Password.this, Constant.network_error);
                        GlobalMethod.hideSoftKeyboard(this);
                    }
                }
                else
                {
                    GlobalMethod.showToast(Forget_Password.this,errmsg);
                    GlobalMethod.hideSoftKeyboard(this);
                }
                break;
            case R.id.parent_layout_login:
                GlobalMethod.hideSoftKeyboard(this);
                break;
            case R.id.header_text:
                GlobalMethod.hideSoftKeyboard(this);
                break;
            case R.id.back:
                finish();
        }
    }
    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        Set_Result(result);

    }
}
