package com.push_notifications;

import android.content.Context;
import android.content.Intent;

import com.globalclasses.Urls;

public final class CommonUtilities {
	// give your server registration url here
    static final String SERVER_URL = Urls.updateDeviceToken;

    // Google project id
    public static final String SENDER_ID = "885691363042";

    /**
     * Tag used on log messages.
     */
//    static final String TAG = "Ivy GCM";
    static final String TAG = "Ivy FCM";
 
    static final String DISPLAY_MESSAGE_ACTION =
            "com.ivy.DISPLAY_MESSAGE";
 
    static final String EXTRA_MESSAGE = "message";
 
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
