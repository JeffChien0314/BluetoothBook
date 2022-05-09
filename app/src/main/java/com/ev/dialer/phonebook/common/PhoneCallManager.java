package com.ev.dialer.phonebook.common;

import android.content.Context;
import android.media.AudioManager;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.util.Log;

public class PhoneCallManager {

    public static Call call;
    private Context context;
    private AudioManager audioManager;

    public PhoneCallManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 接听电话
     */
    public void answer() {
        if (call != null) {
            call.answer(VideoProfile.STATE_AUDIO_ONLY);
            openSpeaker();
        }
    }

    /**
     * 断开电话，包括来电时的拒接以及接听后的挂断
     */
    public void disconnect() {
        if (call != null) {
            call.disconnect();
        }
    }

    /**
     * 打开免提
     */
    public void openSpeaker() {
        if (audioManager != null) {
            Log.d("twen_call", "openSpeaker, isSpeakerphoneOn == " + audioManager.isSpeakerphoneOn());
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        }
    }

    /**
     * 销毁资源
     * */
    public void destroy() {
        call = null;
        context = null;
        audioManager = null;
    }
}