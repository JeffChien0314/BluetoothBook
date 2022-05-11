package com.ev.dialer.phonebook.common;

import android.content.Context;
import android.media.AudioManager;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.util.Log;

import com.ev.dialer.telecom.InCallServiceImpl;

public class PhoneCallManager {

    public static Call mCall;
    public static InCallServiceImpl.CallType mCallType= InCallServiceImpl.CallType.CALL_OUT;
    private Context context;
    private AudioManager audioManager;

    public PhoneCallManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static void setCall(Call call, InCallServiceImpl.CallType callType) {
        mCall = call;
        mCallType = callType;
    }

    public static InCallServiceImpl.CallType getCallType(){
        return mCallType;
    }

    /**
     * 接听电话
     */
    public void answer() {
        if (mCall != null) {
            mCall.answer(VideoProfile.STATE_AUDIO_ONLY);
            openSpeaker();
        }
    }

    /**
     * 断开电话，包括来电时的拒接以及接听后的挂断
     */
    public void disconnect() {
        if (mCall != null) {
            mCall.disconnect();
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
     */
    public void destroy() {
        mCall = null;
        context = null;
        audioManager = null;
    }
}