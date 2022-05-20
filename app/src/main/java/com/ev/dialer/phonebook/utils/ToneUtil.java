package com.ev.dialer.phonebook.utils;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.view.KeyEvent;

import com.google.common.collect.ImmutableMap;

import androidx.annotation.VisibleForTesting;

public class ToneUtil {
    static ToneUtil mInstance;
    private ToneGenerator mToneGenerator;
    private int mCurrentlyPlayingTone = KeyEvent.KEYCODE_UNKNOWN;
    @VisibleForTesting
    static final int MAX_DIAL_NUMBER = 20;

    private static final int TONE_RELATIVE_VOLUME = 80;
    private static final int TONE_LENGTH_INFINITE = -1;
    private final ImmutableMap<Integer, Integer> mToneMap =
            ImmutableMap.<Integer, Integer>builder()
                    .put(KeyEvent.KEYCODE_1, ToneGenerator.TONE_DTMF_1)
                    .put(KeyEvent.KEYCODE_2, ToneGenerator.TONE_DTMF_2)
                    .put(KeyEvent.KEYCODE_3, ToneGenerator.TONE_DTMF_3)
                    .put(KeyEvent.KEYCODE_4, ToneGenerator.TONE_DTMF_4)
                    .put(KeyEvent.KEYCODE_5, ToneGenerator.TONE_DTMF_5)
                    .put(KeyEvent.KEYCODE_6, ToneGenerator.TONE_DTMF_6)
                    .put(KeyEvent.KEYCODE_7, ToneGenerator.TONE_DTMF_7)
                    .put(KeyEvent.KEYCODE_8, ToneGenerator.TONE_DTMF_8)
                    .put(KeyEvent.KEYCODE_9, ToneGenerator.TONE_DTMF_9)
                    .put(KeyEvent.KEYCODE_0, ToneGenerator.TONE_DTMF_0)
                    .put(KeyEvent.KEYCODE_STAR, ToneGenerator.TONE_DTMF_S)
                    .put(KeyEvent.KEYCODE_POUND, ToneGenerator.TONE_DTMF_P)
                    .build();

    public ToneUtil() {
        mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, TONE_RELATIVE_VOLUME);
    }

    public static ToneUtil getInstance() {
        if (null == mInstance) {
            mInstance = new ToneUtil();
        }
        return mInstance;
    }

    public void playTone(int keycode) {
        mCurrentlyPlayingTone = keycode;
        mToneGenerator.startTone(mToneMap.get(keycode), TONE_LENGTH_INFINITE);
    }

    public void stopAllTones() {
        mCurrentlyPlayingTone = KeyEvent.KEYCODE_UNKNOWN;
        mToneGenerator.stopTone();
    }

    public int getCurrentKeyCode() {
        return mCurrentlyPlayingTone;
    }
}
