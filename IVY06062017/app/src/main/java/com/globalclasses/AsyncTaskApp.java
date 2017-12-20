/*
 * 
 */
package com.globalclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import java.net.URLEncoder;

/**
 * File Name: AsyncTaskApp.java
 * Description: This file contains classes and functions for the Products Management.
 *
 * @author singsys-016
 *         Date Created: 06/10/2015
 *         Date Released: 07/15/2016
 *         Created by Lalit Khandelwal Employee ID is 0212
 */

public class AsyncTaskApp extends AsyncTask<Bundle, Void, String> {
    String URL = "", Action = "";
    Activity context;
    //	Context context1;
    ProgressDialog progressDialog;
    CallBackListenar callbacklistner;
    boolean dialogShow=true;

    public AsyncTaskApp(CallBackListenar callbacklistner, Activity context,
                        String url, String action) {
        this.callbacklistner = callbacklistner;
        this.context = context;
        URL = url;
        Action = action;
        progressDialog = new ProgressDialog(context);
    }

    public AsyncTaskApp(CallBackListenar callbacklistner,
                        String url, String action) {
        this.callbacklistner = callbacklistner;
//		this.context1 = context;
        URL = url;
        Action = action;
//		progressDialog = new ProgressDialog(context1);
    }

    public AsyncTaskApp(CallBackListenar callbacklistner, Activity context,
                        String url, String action,boolean dialogShow) {
        this.callbacklistner = callbacklistner;
        this.context = context;
        URL = url;
        Action = action;
        progressDialog = new ProgressDialog(context);
        this.dialogShow = dialogShow;
    }

    @Override
    protected void onPreExecute() {

        if (progressDialog != null&&dialogShow) {
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Bundle... params) {

        try {
            URL = URL.replace(" ", "%20");
            StringBuilder postData = new StringBuilder();

            for (String key : params[0].keySet()) {
                Object value = params[0].get(key);
                if (value instanceof Integer || value instanceof Long ||
                        value instanceof Float || value instanceof Double) {
                    if (postData.length() != 0)
                        postData.append('&');
                    postData.append(URLEncoder.encode(key, "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(value), "UTF-8"));

                } else if (value instanceof String) {
                    if (postData.length() != 0)
                        postData.append('&');
                    postData.append(URLEncoder.encode(key, "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode((String) value, "UTF-8"));
                }
            }

            GlobalMethod.write("==--url" + URL + "?" + postData.toString());

            return SimpleHTTPConnection.sendByPOST(URL, postData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        GlobalMethod.write("result=====" + result);
        if (result.equals(Constant.ConnectionTimeOut)) {
            GlobalMethod.showToast(context, "Request failed please try again later");
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.cancel();
        }
        try {
            if (result != null) {
                if (callbacklistner != null) {
                    callbacklistner.callBackFunction(result, Action);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
