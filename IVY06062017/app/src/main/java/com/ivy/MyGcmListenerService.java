package com.ivy;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;
import com.google.android.gms.gcm.GcmListenerService;

import static com.push_notifications.CommonUtilities.displayMessage;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    SharedPreferences preferences, preference_settings;
    final String SFS = "new_sfs", REVIEW = "new_review", FOLLOWER = "new_follower",
            PRODUCT = "new_product", REQUEST_OPEN = "new_open_request", REQUEST_CLOSE = "new_direct_request";

    String action = "";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Log.d(TAG, data.toString());
        Log.i(TAG, "Received message");
        GlobalMethod.write("MESSAGE RECEIVED");
        String message = data.getString("action");
        displayMessage(this, message);
        preferences = getSharedPreferences(Constant.pref_main, 0);
        GlobalMethod.write("total====" + data.getString("action"));
        preference_settings = getSharedPreferences(Constant.pref_settings, MODE_PRIVATE);


        if (preference_settings.getString(Constant.view_notification, "").equalsIgnoreCase("Yes"))
            GlobalMethod.sendNotificationNew(data,this);
    }


}