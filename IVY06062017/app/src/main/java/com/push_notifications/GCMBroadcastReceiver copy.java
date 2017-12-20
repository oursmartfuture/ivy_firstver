package com.push_notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.push_notifications.CommonUtilities.EXTRA_MESSAGE;

public class GCMBroadcastReceiver extends BroadcastReceiver{
	@Override
    public void onReceive(Context context, Intent intent) {
        String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
        // Waking up mobile if it is sleeping
        WakeLocker.acquire(context);
        
        // Releasing wake lock
        WakeLocker.release();
    }
}
