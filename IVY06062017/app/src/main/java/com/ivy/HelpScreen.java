package com.ivy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.globalclasses.Urls;

public class HelpScreen extends Activity implements View.OnClickListener {

    private RelativeLayout rlPrivacyPolicy;
    private RelativeLayout rlAboutUs;
    private RelativeLayout rlTermsAndCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_privacy_tnc_about);
        rlPrivacyPolicy = (RelativeLayout) findViewById(R.id.rlPrivacyPolicy);
        rlAboutUs = (RelativeLayout) findViewById(R.id.rlAboutUs);
        rlAboutUs.setOnClickListener(this);
        rlTermsAndCondition = (RelativeLayout) findViewById(R.id.rlTermsAndCondition);
        rlTermsAndCondition.setOnClickListener(this);
        rlPrivacyPolicy.setOnClickListener(this);
        findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlAboutUs:
                Intent intent_static = new Intent(HelpScreen.this, LoadStaticPages.class);
                intent_static.putExtra("header_name", "About Us");
                intent_static.putExtra("url", Urls.BASE_URL +"static-pages.php?page_key=about_us");
                startActivity(intent_static);
                finish();
                break;
            case R.id.rlTermsAndCondition:
                Intent intent_terms_and_conditions = new Intent(HelpScreen.this, LoadStaticPages.class);
                intent_terms_and_conditions.putExtra("header_name", "Terms & Conditions");
                intent_terms_and_conditions.putExtra("url", Urls.BASE_URL +"static-pages.php?page_key=terms_and_conditions");
                startActivity(intent_terms_and_conditions);
                finish();
//                GlobalMethod.checkTap(HelpScreen.this, "2");

                break;
            case R.id.rlPrivacyPolicy:
                Intent intent_privacy = new Intent(HelpScreen.this, LoadStaticPages.class);
                intent_privacy.putExtra("header_name", "Privacy Policy");
                intent_privacy.putExtra("url", Urls.BASE_URL +"static-pages.php?page_key=privacy_policy");
                startActivity(intent_privacy);
                finish();
//                GlobalMethod.checkTap(HelpScreen.this, "4");

                break;
        }
    }
}
