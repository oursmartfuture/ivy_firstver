package com.ivy;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.ble_connect.NotifyActivity;
import com.crashlytics.android.Crashlytics;
import com.globalclasses.Constant;
import com.globalclasses.GPSTrackerNew;
import com.globalclasses.GlobalMethod;
import com.globalclasses.RegisterDevice;
import com.globalclasses.SimpleLocation;

import io.fabric.sdk.android.Fabric;


public class Applicationclass extends Application {
    SimpleLocation mSimpleLocation;
    private static Context mContext;
    private static Applicationclass instance;
    AsyncTask mAsyncTask;
    Intent intent;
    GPSTrackerNew gpsTrackerNew;


    public static Applicationclass getInstance() {
        return instance;
    }

    public static Context getContext() {
        //  return instance.getApplicationContext();
        return mContext;
    }

    @Override
    public void onCreate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new LifeCycleHandler());
        }
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = getApplicationContext();


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private class LifeCycleHandler implements ActivityLifecycleCallbacks {

        private int resumed;
        private int paused;
        private int started;
        private int stopped;

        @Override
        public void onActivityStopped(Activity activity) {
            ++stopped;
        }

        @Override
        public void onActivityStarted(Activity activity) {
            ++started;
        }

        @Override
        public void onActivitySaveInstanceState(final Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityResumed(final Activity activity) {
            ++resumed;

            if (TextUtils.isEmpty(GlobalMethod.getSavedPreferences(activity, Constant.RegisterationId, ""))) {
                if (mAsyncTask != null && mAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                } else {
                    mAsyncTask = new RegisterDevice(activity).execute();
                }
            }

            GlobalMethod.write("===Constant.running"+Constant.running);
            if (!Constant.running) {
                if (intent!=null)
                {
                    stopService(intent);
                }
                gpsTrackerNew=new GPSTrackerNew(activity);
                intent=new Intent(activity,GPSTrackerNew.class);
                gpsTrackerNew.onBind(intent);
                startService(intent);
            }

        }

        @Override
        public void onActivityPaused(Activity activity) {
            ++paused;
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Constant.ActivitiLifeCycel.remove(activity);
        }

        // @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {


            for (int i = 0; i < Constant.ActivitiLifeCycel.size(); i++) {
                if (Constant.ActivitiLifeCycel.get(i) instanceof NotifyActivity) {
                    GlobalMethod.write("instanceof NotifyActivity");
                    Constant.ActivitiLifeCycel.get(i).finish();
                    break;
                }
            }

            Constant.ActivitiLifeCycel.add(activity);
            mSimpleLocation = new SimpleLocation(activity);
            mSimpleLocation.beginUpdates();
        }
    }
}

