/*
 *
 */
package com.globalclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.nio.charset.Charset;


public class AsyncTask_Multipart extends AsyncTask<Bundle, Void, String> {
    String URL = "", Action = "";
    Bundle Bundle;
    Activity context;
    ProgressDialog progressDialog;
    CallBackListenar callbacklistner;


    public AsyncTask_Multipart(CallBackListenar callbacklistner, Activity context,
                               String url, Bundle bundel, String action) {
        this.callbacklistner = callbacklistner;
        this.context = context;
        this.URL = url;
        this.Action = action;
        progressDialog = new ProgressDialog(context);
        this.Bundle = bundel;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Bundle... params) {

        MultipartEntity mp;
        URL = URL.replace(" ", "%20");

        try {
            mp = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (String key : params[0].keySet()) {
                Object value = params[0].get(key);

                if (value instanceof Integer||value instanceof Long) {
                    mp.addPart(key, new StringBody(String.valueOf(value), HTTP.PLAIN_TEXT_TYPE, Charset.forName(HTTP.UTF_8)));
                    GlobalMethod.write("key" + key + "value" + value);
                } else if (value instanceof String) {
                    mp.addPart(key, new StringBody((String) value, HTTP.PLAIN_TEXT_TYPE, Charset.forName(HTTP.UTF_8)));
                    GlobalMethod.write("key" + key + "value" + value);
                }
            }

            for (String key : Bundle.keySet()) {
                Object value = Bundle.get(key);
                if (!TextUtils.isEmpty((String) value)) {
                    mp.addPart(key, new FileBody(new File((String) value), "*"));
                    GlobalMethod.write("key" + key + "value" + value);
                }
            }

            return SimpleHTTPConnection.sendByPOST(URL,mp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        GlobalMethod.write("result=====" + result);
        if (result.equals(Constant.ConnectionTimeOut)) {
            GlobalMethod.showToast(context, "Request failed please try again later");
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
            progressDialog.cancel();
        }
        progressDialog.cancel();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
