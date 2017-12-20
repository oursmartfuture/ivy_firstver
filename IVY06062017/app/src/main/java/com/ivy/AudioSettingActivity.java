package com.ivy;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.globalclasses.AsyncTaskApp;
import com.globalclasses.AudioModel;
import com.globalclasses.CallBackListenar;
import com.globalclasses.Constant;
import com.globalclasses.Contact;
import com.globalclasses.DatabaseHandler;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SS091 on 9/6/2016.
 */
public class AudioSettingActivity extends Activity implements View.OnClickListener, CallBackListenar {

    LinearLayout searchlinear, buttonupdatelinear;
    ListView mAudioListView;
    MediaPlayer mediaPlayer;
    AudioManager mAudioManager;
    int originalVolume;
    List<AudioModel> mAudioModalList = new ArrayList<>();
    AudioAdapter mAudioAdapter;
    TextView headerText;
    ImageButton backBtn;
    Activity mActivity;
    public View currentAudio;
    private int globalPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_country_list);
        mActivity = AudioSettingActivity.this;
        headerText = (TextView) findViewById(R.id.headerText);
        headerText.setText("Audio Setting");
        GlobalMethod.AcaslonProSemiBoldTextView(mActivity, headerText);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        searchlinear = (LinearLayout) findViewById(R.id.searchlinear);
        searchlinear.setVisibility(View.GONE);
        buttonupdatelinear = (LinearLayout) findViewById(R.id.buttonupdatelinear);
        buttonupdatelinear.setVisibility(View.GONE);
        addAudioFile();
        mAudioListView = (ListView) findViewById(R.id.listView_extra_features);
        mAudioAdapter = new AudioAdapter();
        mAudioListView.setAdapter(mAudioAdapter);
    }

    /**
     * Method to add audio file from raw folder.
     */
    void addAudioFile() {
        mAudioModalList.add(new AudioModel(0, R.raw.emergency_1, "Emergency 1"));
        mAudioModalList.add(new AudioModel(1, R.raw.emergency_2, "Emergency 2"));
        mAudioModalList.add(new AudioModel(2, R.raw.emergency_3, "Emergency 3"));
        mAudioModalList.add(new AudioModel(3, R.raw.ringtone_1, "Ringtone 1"));
        mAudioModalList.add(new AudioModel(4, R.raw.ringtone_2, "Ringtone 2"));
    }

    /**
     * Method to play an alarm sound to signal half time or full time.
     */
    private void playAudio(int audioId) {
        GlobalMethod.write("PLAY ALARM");
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        mediaPlayer = MediaPlayer.create(this, audioId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                if (currentAudio != null) {
                    ((ImageView) currentAudio).setImageResource(android.R.drawable.ic_media_play);
                    currentAudio.setTag(true);
                    currentAudio = null;
                }
            }
        });
        mediaPlayer.start();
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        if (action.equalsIgnoreCase("Default")) {
            JSONObject jobj = new JSONObject(result);
            if (jobj.getString("success").equalsIgnoreCase("1")) {
                GlobalMethod.savePreferences(mActivity, Constant.AUDIOFILE, "" + mAudioModalList.get(globalPosition).getAudiofile());
                GlobalMethod.savePreferences(mActivity, Constant.AUDIOFILESEQUENCE, "" + mAudioModalList.get(globalPosition).getAudioSequence());
                if (mAudioAdapter != null) {
                    mAudioAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    public class AudioAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAudioModalList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            view = View.inflate(mActivity, R.layout.audio_raw_layout, null);

            TextView audioName = (TextView) view.findViewById(R.id.audioName);
            final ImageView audioPlayIcon = (ImageView) view.findViewById(R.id.audioPlayIcon);
            ImageView tick = (ImageView) view.findViewById(R.id.tick);
            if (Integer.parseInt(GlobalMethod.getSavedPreferences(mActivity, Constant.AUDIOFILESEQUENCE, "0")) == i) {
                tick.setVisibility(View.VISIBLE);
            } else {
                tick.setVisibility(View.GONE);
            }
//            if (GlobalMethod.getSavedPreferences(mActivity, Constant.AUDIOFILE, String.valueOf(R.raw.buzzer)).equalsIgnoreCase(String.valueOf(mAudioModalList.get(i).getAudiofile())))
//                tick.setVisibility(View.VISIBLE);
//            else {
//                tick.setVisibility(View.GONE);
//            }
            audioName.setText(mAudioModalList.get(i).getAudioName());
            GlobalMethod.AcaslonProSemiBoldTextView(mActivity, audioName);
            audioPlayIcon.setTag(true);
            audioPlayIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean b = (boolean) view.getTag();
                    if (b) {
                        if (currentAudio != null) {
//                            pauseAudio();
                            currentAudio.setTag(true);
                            ((ImageView) currentAudio).setImageResource(android.R.drawable.ic_media_play);
                            currentAudio = null;
                        }
                        playAudio(mAudioModalList.get(i).getAudiofile());
                        audioPlayIcon.setImageResource(android.R.drawable.ic_media_pause);
                        currentAudio = view;
                    } else {
                        pauseAudio();
                        currentAudio = null;
                        audioPlayIcon.setImageResource(android.R.drawable.ic_media_play);
                    }
                    view.setTag(!b);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(mActivity, R.style.Theme_Dialog);
                    dialog.setContentView(R.layout.logout_pop_up);
                    TextView alerttextlogout, canceltext, oktext;
                    alerttextlogout = (TextView) dialog.findViewById(R.id.alerttextlogout);
                    canceltext = (TextView) dialog.findViewById(R.id.canceltext);
                    oktext = (TextView) dialog.findViewById(R.id.oktext);
                    alerttextlogout.setText("Do you want to change audio for emergency alert?");
                    oktext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            GlobalMethod.write("===AudioFile" + GlobalMethod.getSavedPreferences(mActivity, Constant.AUDIOFILE, ""));
                            dialog.dismiss();
                            Bundle bundle = new Bundle();
                            SharedPreferences preferences = getSharedPreferences(Constant.pref_main, Activity.MODE_PRIVATE);
                            bundle.putString("user_id", preferences.getString(Constant.id_user, ""));
                            bundle.putString("default_ringtone", i + "");
                            new AsyncTaskApp(AudioSettingActivity.this, AudioSettingActivity.this,
                                    Urls.DEFAULT_RING, "Default").execute(bundle);
                            globalPosition = mAudioModalList.get(i).getAudioSequence();

                        }
                    });
                    canceltext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            return view;
        }
    }
}
