package com.globalclasses;

/**
 * Created by ss106 on 9/15/2016.
 */

public class AudioModel {

    public AudioModel(int audioSequence, int audiofile, String audioName) {
        this.audiofile = audiofile;
        this.audioName = audioName;
        this.audioSequence = audioSequence;
    }

    public int getAudiofile() {
        return audiofile;
    }

    int audiofile;

    public int getAudioSequence() {
        return audioSequence;
    }

    int audioSequence;

    public String getAudioName() {
        return audioName;
    }

    String audioName;

}
