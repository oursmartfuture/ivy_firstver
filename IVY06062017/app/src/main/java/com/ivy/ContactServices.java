package com.ivy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.globalclasses.FetchContacts;
import com.globalclasses.GlobalMethod;

public class ContactServices extends Service{
     private Looper mServiceLooper;
     private ServiceHandler mServiceHandler;
     
      // Handler that receives messages from the thread
      private final class ServiceHandler extends Handler {
          public ServiceHandler(Looper looper) {
              super(looper);
          }
          @Override
          public void handleMessage(Message msg) {
              downloadFile();
          }
      }

    @Override
        public void onCreate() {

            HandlerThread thread = new HandlerThread("ServiceStartArguments",1);
            thread.start();
        
            // Get the HandlerThread's Looper and use it for our Handler 
            mServiceLooper = thread.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);

        }

     @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
              Message msg = mServiceHandler.obtainMessage();
              msg.arg1 = startId;
              mServiceHandler.sendMessage(msg);
              // If we get killed, after returning from here, restart
              return START_STICKY;
          }
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		GlobalMethod.write("---==destroyContactservice");
		super.onDestroy();
	}
	@Override
      public IBinder onBind(Intent intent) {
          // We don't provide binding, so return null
          return null;
      }


      public void downloadFile(){
			if (isNetworkConnected()) {

				GlobalMethod.write("---==startcontactAsynTask");
				FetchContacts fetchContacts = new FetchContacts(getApplicationContext(), false);
				fetchContacts.execute();
			}
    	  	}
        private boolean isNetworkConnected() {
        	  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        	  NetworkInfo ni = cm.getActiveNetworkInfo();
        	  if (ni == null) {
        	   // There are no active networks.
        	   return false;
        	  } else
        	   return true;
        	 }
}
  