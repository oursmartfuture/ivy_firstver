package com.ivy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.globalclasses.GlobalMethod;

/**
 * Class Name: LoadStaticPages.Class
 * Class description: This class contains methods and classes for loading the static pages.
 * Created by Sobhit Gupta
 * Created by sing sys-118 on 16/11/2015.
 */
public class LoadStaticPages extends Activity {
	WebView webView;
	ProgressBar loading_bar;
	TextView titletextview;
    View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_custom);
		webView = (WebView) findViewById(R.id.web_browser);
		loading_bar = (ProgressBar) findViewById(R.id.progressBar1);
		titletextview = (TextView) findViewById(R.id.headerText);
		if(!TextUtils.isEmpty(getIntent().getStringExtra("header_name")))
		titletextview.setText(getIntent().getStringExtra("header_name"));
		GlobalMethod.AcaslonProSemiBoldTextView(LoadStaticPages.this, titletextview);
		ImageView back_btn = (ImageView) findViewById(R.id.backBtn);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (webView != null) {
					webView.loadUrl("");
					webView.stopLoading();
				}

				finish();
			}
		});
		
		webView.loadUrl(getIntent().getStringExtra("url"));
		webView.setWebViewClient(new HelloWebViewClient());
//		GlobalMethod.AcaslonProSemiBoldTextView(LoadStaticPages.this, titletextview);
	}

	private class HelloWebViewClient extends WebViewClient {
		public void onPageStarted(WebView view, String url, Bitmap bitmap) {
			loading_bar.setVisibility(View.VISIBLE);
		}

		public void onPageFinished(WebView view, String url) {
			loading_bar.setVisibility(View.GONE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return true;
		}
	}
	/*
	end of class.
	 */
}
