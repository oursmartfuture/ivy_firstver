package com.ivy;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.globalclasses.FetchContacts;

/**
 * Created by singsys-048 on 12/2/2015.
 */
public class MyServiceClass extends Service {


    public MyServiceClass(Activity activity) {
        this.activity = activity;
    }

    FetchContacts fetchContacts;
    Activity activity;

    @Override
    public void onCreate() {
        fetchContacts = new FetchContacts(activity, false);
        fetchContacts.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
