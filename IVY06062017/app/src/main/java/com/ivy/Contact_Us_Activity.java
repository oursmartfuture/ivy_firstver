package com.ivy;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
 * Class Name: Contact_Us_Activity.Class
 * Class description: This class contains methods and classes to send contact information.
 * Created by Monita Sonkar(00215)
 * Created by sing sys-118 on 05/11/2015.
 */
public class Contact_Us_Activity extends Activity implements View.OnClickListener, CallBackListenar {
    TextView headerText,description_tv,email_address_tv,write_msg;
    ImageButton backBtn;
    EditText email_address,description;
    Button submit;
    View view;
    LinearLayout parent_contact;
    String emailPattern = "^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+";
    String errmsg;
    Bundle params;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us_layout);
        preferences=getSharedPreferences(Constant.pref_main,0);
        backBtn= (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);
        parent_contact = (LinearLayout) findViewById(R.id.parent_contact);
        parent_contact.setOnClickListener(this);
        headerText=(TextView)findViewById(R.id.headerText);
        write_msg=(TextView)findViewById(R.id.write_msg);
        description_tv=(TextView)findViewById(R.id.description_tv);
        email_address_tv=(TextView)findViewById(R.id.email_address_tv);
        email_address=(EditText)findViewById(R.id.email_address);
        description=(EditText)findViewById(R.id.description);
        Contact_Us_Activity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        headerText.setText("Contact Us");
        GlobalMethod.AcaslonProSemiBoldTextView(Contact_Us_Activity.this, headerText);
        params=new Bundle();
        view=(View) findViewById(android.R.id.content);
        setFont();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.parent_contact:
                GlobalMethod.hideSoftKeyboard(this);
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.submit:
                params.putString("user_id",preferences.getString(Constant.id_user, ""));
                params.putString("email",email_address.getText().toString().trim());
                params.putString("description", description.getText().toString().trim());
                params.putString("mode","");
                if(validation()) {
                    if (SimpleHttpConnection.isNetworkAvailable(Contact_Us_Activity.this)) {
                        new AsyncTaskApp(this, Contact_Us_Activity.this, Urls.Contact_Us_Url, "Contact_Us").execute(params);
                    } else {
                        GlobalMethod.showToast(Contact_Us_Activity.this, Constant.network_error);

                    }
                }
                else
                {
                    GlobalMethod.showToast(Contact_Us_Activity.this,errmsg);
                }
                GlobalMethod.hideSoftKeyboard(this);
                break;
        }
    }
    /*
   This method is sued for setting font.
    */
    private void setFont() {
        GlobalMethod.HelveticaCE35Thin(Contact_Us_Activity.this, view);
        GlobalMethod.AcaslonProSemiBoldButton(Contact_Us_Activity.this, submit);
        GlobalMethod.AcaslonProSemiBoldEditText(Contact_Us_Activity.this, headerText);
    }

    // this method is for checking the validation regarding email and discription.
    public boolean validation() {
        if (TextUtils.isEmpty(email_address.getText().toString().trim())) {
            errmsg = getResources().getString(R.string.email_validation);
            email_address.requestFocus();
            return false;
        }else if (!email_address.getText().toString().trim().matches(emailPattern)) {
            errmsg = "Please enter valid Email.";
            email_address.requestFocus();
            return false;
        }
        else if (TextUtils.isEmpty(description.getText().toString().trim())) {
            errmsg = getResources().getString(R.string.descriptionmessage);
            description.requestFocus();
            return false;
        }
        return  true;
    }

    public void sendMessage(final Activity context){//}, final int position) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.forget_pop_up);
        TextView alertquery, oktext, message_send;
        LinearLayout alertParent;
        ImageView delete_icon;
        alertParent = (LinearLayout) dialog.findViewById(R.id.alertParent);
        delete_icon = (ImageView) dialog.findViewById(R.id.delete_icon);
//        delete_icon.setImageResource(R.drawable.delete_image);
        message_send = (TextView) dialog.findViewById(R.id.account_created);
        alertquery = (TextView) dialog.findViewById(R.id.verify_text);
        oktext = (TextView) dialog.findViewById(R.id.email1);
        message_send.setText("MESSAGE SENT");
        GlobalMethod.AcaslonProSemiBoldTextView(getApplicationContext(), message_send);
        oktext.setTypeface(Typeface.createFromAsset(context.getAssets(), "Helvetica Neue CE 35 Thin.ttf"));
        oktext.setText("Ok");
        alertquery.setText("Thanks for your Query. We"+"\n"+"will be in touch soon");
        oktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        alertParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        try {
//            account_created.setText(total_devices.get(position).getString("device_name"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        oktext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        dialog.show();
    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {


        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (result != null) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (jsonObject.getString("success").equalsIgnoreCase("1")) {

                    sendMessage(Contact_Us_Activity.this);
//                    GlobalMethod.showToast(Contact_Us_Activity.this, jsonObject.getString("message"));

                } else {
                    GlobalMethod.showToast(Contact_Us_Activity.this, jsonObject.getString("message"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
/*
 * End of Class
 */