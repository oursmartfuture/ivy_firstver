package com.ivy;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivy.R;


/**
 * Class Name: Pop_Up_Forget.Class
 * Class description: This class contains methods and classes for showing the pop up in case user forgets his/her password.
 * Created by Sobhit Gupta(00253)
 * Created by Singsys-043 on 10/20/2015.
 */
public class Pop_Up_Forget extends Activity implements View.OnClickListener {

    TextView account_created,account_created_below,verify_text,email1;
    LinearLayout Ok_button;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pop_up);
        account_created=(TextView)findViewById(R.id.account_created);
//        account_created_below=(TextView)findViewById(R.id.account_created_below);
        verify_text=(TextView)findViewById(R.id.verify_text);
        email1=(TextView)findViewById(R.id.email1);
        Ok_button=(LinearLayout)findViewById(R.id.Ok_button);
        Ok_button.setOnClickListener(this);
        Typeface tf6 = Typeface.createFromAsset(Pop_Up_Forget.this.getAssets(), "ACaslonPro-Semibold.otf");
        account_created.setTypeface(tf6);
//        account_created_below.setTypeface(tf6);
        Typeface tf7 = Typeface.createFromAsset(Pop_Up_Forget.this.getAssets(), "Helvetica Neue CE 35 Thin.ttf");
        verify_text.setTypeface(tf7);
        email1.setTypeface(tf6);

    }

    @Override
    public void onClick(View v) {
        finish();
    }
 /*
    end of class.
     */
}
