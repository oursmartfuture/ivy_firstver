package com.globalclasses;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.push_notifications.CommonUtilities;

import java.io.IOException;

/**
 * Created by SS-097 on 01-08-2016.
 */


public class RegisterDevice extends AsyncTask<Void, Void, Void> {
    Activity mActivity;
    public RegisterDevice(Activity activity) {
        mActivity=activity;
    }
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration id, app versionCode, and expiration time in the
     * application's shared preferences.
     */

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        InstanceID instanceID = InstanceID.getInstance(mActivity);
        String token = "";
        try {
            token = instanceID.getToken(CommonUtilities.SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            GlobalMethod.savePreferences(mActivity, Constant.RegisterationId, token);
            GlobalMethod.write("====GCMRegistrationToken:"+token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}