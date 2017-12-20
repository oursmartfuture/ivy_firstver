package com.ivy;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.globalclasses.AsyncTask_Multipart;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.GPS;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * Created by Singsys-0105 on 1/18/2016.
 */
public class Record_Audio extends Activity implements View.OnClickListener, CallBackListenar {
    private static final int REQUEST_AUDIO = 123;
    private static final long TIME_LIMIT = 5000;
    ImageView record, play_icon, stop_icon, pause_icon;
    TextView time_set, headerText;
    int c = 0;
    Button discard_btn, save_btn;
    boolean isSaving = false;
    Bundle params;
    SharedPreferences preferences;
    private MediaPlayer mediaPlayer;
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private MediaRecorder myAudioRecorder = null;
    Handler mServiceHandler;
    GPS gps;
    ImageButton backBtn;
    Context context;
    CountDownTimer timer;
    public static final long SCAN_PERIOD = 16000;
    private int currentFormat = 0;
    ProgressBar _progressBar;
    String fileaudio = "";
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};
    private Runnable runnable;
    private boolean isStopped;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_audio);
//        record= (ImageView)findViewById(R.id.record);
        play_icon = (ImageView) findViewById(R.id.play_icon);
        stop_icon = (ImageView) findViewById(R.id.stop_icon);
//        pause_icon = (ImageView) findViewById(R.id.pause_icon);
        discard_btn = (Button) findViewById(R.id.discard_btn);
        save_btn = (Button) findViewById(R.id.save_btn);
        discard_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        stop_icon.setOnClickListener(this);
        preferences = getSharedPreferences(Constant.pref_main, 0);
        gps = new GPS(Record_Audio.this);
//        pause_icon.setOnClickListener(this);
        context = Record_Audio.this;
        params = new Bundle();
        play_icon.setOnClickListener(this);
        stop_icon.setClickable(false);
        headerText = (TextView) findViewById(R.id.headerText);
        headerText.setText("Audio Record");
        GlobalMethod.AcaslonProSemiBoldTextView(Record_Audio.this, headerText);

        time_set = (TextView) findViewById(R.id.time_set);
        _progressBar = (ProgressBar) findViewById(R.id.circularProgressBar);
        _progressBar.setIndeterminate(false);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discard_btn.performClick();
//                finish();
            }
        });
        mServiceHandler = new Handler();
        play_icon.performClick();
    }

    /*
   making a directory for voice recording in the device.
 */
    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "IvyVoiceRecording");
        if (!file.exists()) {
            file.mkdirs();
        }
        fileaudio = file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat];
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
    }

    /*
       In case if caught any error.
     */
    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(Record_Audio.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };


    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(Record_Audio.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    /*
        this method is for starting the audio recording.
     */

    private void startRecording() {

        play_icon.setClickable(false);
        try {
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myAudioRecorder.setOutputFile(getFilename());
            myAudioRecorder.setOnErrorListener(errorListener);
            myAudioRecorder.setOnInfoListener(infoListener);
            myAudioRecorder.prepare();//inbuilt method of media reccorder.
            myAudioRecorder.start();//inbuilt method of media reccorder.
//            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            GlobalMethod.showToast(this, "Recording started");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mServiceHandler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    myAudioRecorder.stop();//inbuilt method of media reccorder.
                    play_icon.setClickable(true);
                    myAudioRecorder.reset();//inbuilt method of media reccorder.
                    myAudioRecorder.release();//inbuilt method of media reccorder.
                    myAudioRecorder = null;
                    GlobalMethod.showToast(Record_Audio.this, "Audio recorded successfully");
                    stop_icon.setClickable(false);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }, SCAN_PERIOD);

    }

    long prev = -1;

    void increase_counter() {

        startRecording();
        timer = new CountDownTimer(TIME_LIMIT, 50) {
            public void onTick(long millisUntilFinished) {
                long x = (TIME_LIMIT - millisUntilFinished) / 1000;
                if (x != prev) {
                    time_set.setText("00:" + padding(Long.toString((TIME_LIMIT - millisUntilFinished) / 1000)));
                    prev = (TIME_LIMIT - millisUntilFinished) / 1000;
                }
                _progressBar.setProgress(((int) ((long) (TIME_LIMIT - millisUntilFinished) / 100)) * 2);
            }

            private String padding(String s) {
                if (s.length() > 1) {
                    return s;
                } else {
                    return "0" + s;
                }
            }

            public void onFinish() {
                play_icon.setClickable(true);
                time_set.setText("00:05");
                _progressBar.setProgress(100);

                save_btn.performClick();
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e("Permission", "requestCode" + requestCode);
        switch (requestCode) {

            case REQUEST_AUDIO: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    play_icon.performClick();
                } else {
                    Log.e("Permission", "Denied");
                    finish();
                }
                break;
            }
        }

    }

    /**
     * @param permission   that has to be accessed in the app
     * @param requestCode  for callback purpose
     * @param errorMessage to display in case of error
     * @return
     */
    public boolean mayRequestPermission(String permission, int requestCode, String errorMessage) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GlobalMethod.write("====<Build.VERSION_CODES.M");
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GlobalMethod.write("====>= Build.VERSION_CODES.M");
            // Here, this is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {
                GlobalMethod.write("====PERMISSION_NOT_GRANTED");

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission)) {
                    GlobalMethod.write("====ifPermissionRationale");
                    ActivityCompat.requestPermissions(this,
                            new String[]{permission},
                            requestCode);

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.
                    GlobalMethod.write("====elsePermission");
                    GlobalMethod.showCustomToastInCenter(this,errorMessage);
                    finish();
                    // permission is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else
            {
                GlobalMethod.write("====PERMISSION_GRANTED");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_icon:
                if (mayRequestPermission(RECORD_AUDIO, REQUEST_AUDIO, getString(R.string.mic_permisison))) {
                    stop_icon.setClickable(true);
                    increase_counter();
                }
                break;
           /* case R.id.pause_icon:
                break;*/
            case R.id.discard_btn:
                if (timer != null) {
                    timer.cancel();
                }
                finish();
                break;
            case R.id.save_btn:
                if (!isStopped) {
                    stop_icon.performClick();
                }
                if (!isSaving) {
                    params.putString("user_id", preferences.getString(Constant.id_user, ""));
                    params.putString("audio_duration", time_set.getText().toString().trim().replace("00:0", ""));
                    params.putString("mode", "");
//                params.putString("latitude", gps.getLatitude()+"".toString().trim());
//                params.putString("longitude", gps.getLongitude()+"".toString().trim());
                    if (TextUtils.isEmpty(preferences.getString("lat", "")) || TextUtils.isEmpty(preferences.getString("lng", ""))) {
                        params.putString("latitude", gps.getLatitude() + "".toString().trim());
                        params.putString("longitude", gps.getLongitude() + "".toString().trim());
                    } else {
                        params.putString("latitude", preferences.getString("lat", ""));
                        params.putString("longitude", preferences.getString("lng", ""));
                    }
//                params.putString("latitude","73");
//                params.putString("longitude", "73");
                    GlobalMethod.write("audiopath++==+=====" + fileaudio);
//                params.putString("input_audio",fileaudio);
                    Bundle audio_path = new Bundle();
                    audio_path.putString("input_audio", fileaudio);
                    if (SimpleHttpConnection.isNetworkAvailable(Record_Audio.this)) {
//                    new AsyncTaskApp(this, Record_Audio.this, Urls.send_alert, "Send_Alert").execute(params);
                        new AsyncTask_Multipart(Record_Audio.this, Record_Audio.this, Urls.send_alert, audio_path, "Send_Alert").execute(params);

                    } else {

                        if (!GlobalMethod.getSavedPreferences(this, Constant.DEFAULT_NUMBER, "").isEmpty()) {
                            sendSms(GlobalMethod.getSavedPreferences(this, Constant.DEFAULT_NUMBER, ""));
                        }
//                        GlobalMethod.showToast(Record_Audio.this, Constant.network_error);
                    }
                    isSaving = true;
                } else {

                }
                break;
            case R.id.stop_icon:
                isStopped = true;
                play_icon.setClickable(true);
                timer.cancel();
                mServiceHandler.removeCallbacks(runnable);
                try {
                    if (myAudioRecorder != null)
                        myAudioRecorder.stop();//inbuilt method of media reccorder.
                    myAudioRecorder.reset();//inbuilt method of media reccorder.
                    myAudioRecorder.release();//inbuilt method of media reccorder.
                    stop_icon.setClickable(false);
                    myAudioRecorder = null;
                    Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mServiceHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (receiverSmsSent != null) {
                unregisterReceiver(receiverSmsSent);
                unregisterReceiver(receiverSmsDelivery);
            }
        }catch (Exception e) {
           e.printStackTrace();
        }

        if (mServiceHandler!=null)
        mServiceHandler.removeCallbacks(runnable);
    }

    @Override
    public void callBackFunction(String result, String action) {


        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                GlobalMethod.showToast(Record_Audio.this, jsonObject.getString("message"));
                Intent intent = new Intent(Record_Audio.this, Emergency_Activity.class);
                intent.putExtra("audiopath", "file://" + fileaudio);
                intent.putExtra("alert_id", data.getString("alert_id"));
                startActivity(intent);
                finish();
            } else {
                if (!GlobalMethod.getSavedPreferences(this, Constant.DEFAULT_NUMBER, "").isEmpty()) {
                    sendSms(GlobalMethod.getSavedPreferences(this, Constant.DEFAULT_NUMBER, ""));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (!GlobalMethod.getSavedPreferences(this, Constant.DEFAULT_NUMBER, "").isEmpty()) {
                sendSms(GlobalMethod.getSavedPreferences(this, Constant.DEFAULT_NUMBER, ""));
            }
        }

    }

    private void sendSms(String phonenumber) {
        String message = GlobalMethod.getSavedPreferences(Record_Audio.this, Constant.NAME, "")
//                getSharedPreferences(Constant.pref_main, MODE_PRIVATE).getString(Constant.NAME, "")
                + " is in trouble.";
//        List<String> phonenumberList=new ArrayList<>();
//        phonenumberList.add(" 954966366");
//        phonenumberList.add("954966326");
//        phonenumberList.add("954966336");
//        phonenumberList.add("954966346");
//        phonenumberList.add("954966356");
//        phonenumberList.add("954966376");
        SmsManager manager = SmsManager.getDefault();
        String number = GlobalMethod.getSavedPreferences(Record_Audio.this, Constant.NUMBERLIST, "");
        GlobalMethod.write("NUMBERS : " + number);
        if (!TextUtils.isEmpty(number)) {

            String[] arrPhone = number.split(",");
            for (int i = 0; i < arrPhone.length; i++) {
                GlobalMethod.write("====phonenumber" + arrPhone[i]);
                PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(Constant.SMS_SENT), 0);
                PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(Constant.SMS_DELIVERED), 0);
                manager.sendTextMessage(arrPhone[i], null, message, piSend, piDelivered);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
////        for (int i=0;i<phonenumberList.size();i++)
////        {
////            GlobalMethod.write("====phonenumber" + phonenumberList.get(i));
//        PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(Constant.SMS_SENT), 0);
//        PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(Constant.SMS_DELIVERED), 0);
//        manager.sendTextMessage(phonenumber.toString(), null, message, piSend, piDelivered);
////            try {
////                Thread.sleep(2000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiverSmsSent, new IntentFilter(Constant.SMS_SENT));
        registerReceiver(receiverSmsDelivery, new IntentFilter(Constant.SMS_DELIVERED));
    }

    private BroadcastReceiver receiverSmsSent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = null;
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    message = "SMS sent";
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    message = "Generic failure";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    message = "No service";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    message = "Null PDU";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    message = "Radio off";
                    break;
            }
            GlobalMethod.write("====receiverSmsSent" + message);
            GlobalMethod.showCustomToastInCenter(Record_Audio.this, message);
        }
    };

    private BroadcastReceiver receiverSmsDelivery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = null;
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    message = "SMS delivered";
                    break;
                case Activity.RESULT_CANCELED:
                    message = "SMS not delivered";
                    break;
            }
            GlobalMethod.write("====receiverSmsDelivery" + message);
            GlobalMethod.showCustomToastInCenter(Record_Audio.this, message);
        }
    };

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        discard_btn.performClick();
    }
}
