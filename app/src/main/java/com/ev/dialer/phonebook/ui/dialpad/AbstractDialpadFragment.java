/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ev.dialer.phonebook.ui.dialpad;

import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ev.dialer.log.L;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.ui.common.KeypadView;
import com.ev.dialer.phonebook.utils.ToneUtil;

/**
 * Fragment that controls the dialpad.
 */
public abstract class AbstractDialpadFragment extends Fragment implements
        KeypadView.KeypadCallback {
    private static final String TAG = "AbsDialpadFragment";
    private static final String DIAL_NUMBER_KEY = "DIAL_NUMBER_KEY";
    private static final int PLAY_DTMF_TONE = 1;

    static final SparseArray<Character> sDialValueMap = new SparseArray<>();

    static {
        sDialValueMap.put(KeyEvent.KEYCODE_1, '1');
        sDialValueMap.put(KeyEvent.KEYCODE_2, '2');
        sDialValueMap.put(KeyEvent.KEYCODE_3, '3');
        sDialValueMap.put(KeyEvent.KEYCODE_4, '4');
        sDialValueMap.put(KeyEvent.KEYCODE_5, '5');
        sDialValueMap.put(KeyEvent.KEYCODE_6, '6');
        sDialValueMap.put(KeyEvent.KEYCODE_7, '7');
        sDialValueMap.put(KeyEvent.KEYCODE_8, '8');
        sDialValueMap.put(KeyEvent.KEYCODE_9, '9');
        sDialValueMap.put(KeyEvent.KEYCODE_0, '0');
        sDialValueMap.put(KeyEvent.KEYCODE_STAR, '*');
        sDialValueMap.put(KeyEvent.KEYCODE_POUND, '#');
    }

    private boolean mDTMFToneEnabled;
    private final StringBuffer mNumber = new StringBuffer();
    private ValueAnimator mInputMotionAnimator;
    private ScaleSpan mScaleSpan;
    private TextView dialNumber;
    private int mCurrentlyPlayingTone = KeyEvent.KEYCODE_UNKNOWN;

    /**
     * Defines how the dialed number should be presented.
     */
    abstract void presentDialedNumber(@NonNull StringBuffer number);

    /**
     * Plays the tone for the pressed keycode when "play DTMF tone" is enabled in settings.
     */
    abstract void playTone(int keycode);

    /**
     * Stops playing all tones when "play DTMF tone" is enabled in settings.
     */
    abstract void stopAllTones();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mNumber.append(savedInstanceState.getCharSequence(DIAL_NUMBER_KEY));
        }
        L.d(TAG, "onCreate, number: %s", mNumber);
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialNumber = view.findViewById(R.id.dial_number);
        if (dialNumber != null) {
            mInputMotionAnimator = (ValueAnimator) AnimatorInflater.loadAnimator(getContext(),
                    R.animator.scale_down);
            float startTextSize = (float) (dialNumber.getTextSize() * 1.5);
            mScaleSpan = new ScaleSpan(startTextSize);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDTMFToneEnabled = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.DTMF_TONE_WHEN_DIALING, 1) == PLAY_DTMF_TONE;
        L.d(TAG, "DTMF tone enabled = %s", String.valueOf(mDTMFToneEnabled));

        presentDialedNumber();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAllTones();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(DIAL_NUMBER_KEY, mNumber);
    }

    @Override
    public void onKeypadKeyDown(@KeypadView.DialKeyCode int keycode) {
        String digit = sDialValueMap.get(keycode).toString();
        appendDialedNumber(digit);

        if (mDTMFToneEnabled) {
            //  mCurrentlyPlayingTone = keycode;
            playTone(keycode);
        }
    }

    @Override
    public void onKeypadKeyUp(@KeypadView.DialKeyCode int keycode) {
        if (mDTMFToneEnabled && keycode == /*mCurrentlyPlayingTone*/ToneUtil.getInstance().getCurrentKeyCode()) {
            //   mCurrentlyPlayingTone = KeyEvent.KEYCODE_UNKNOWN;
            stopAllTones();
        }
    }

    /**
     * Set the dialed number to the given number. Must be called after the fragment is added.
     */
    public void setDialedNumber(String number) {
        mNumber.setLength(0);
        if (!TextUtils.isEmpty(number)) {
            mNumber.append(number);
        }
        presentDialedNumber();
    }

    void clearDialedNumber() {
        mNumber.setLength(0);
        presentDialedNumber();
    }

    void removeLastDigit() {
        if (mNumber.length() != 0) {
            mNumber.deleteCharAt(mNumber.length() - 1);
        }
        presentDialedNumber();
    }

    void appendDialedNumber(String number) {
        mNumber.append(number);
        presentDialedNumber();

        if (TextUtils.isEmpty(number)) {
            return;
        }

        if (mInputMotionAnimator != null) {
            final String currentText = dialNumber.getText().toString();
            final SpannableString spannableString = new SpannableString(currentText);
            mInputMotionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float textSize =
                            (float) valueAnimator.getAnimatedValue() * dialNumber.getTextSize();
                    mScaleSpan.setTextSize(textSize);
                    spannableString.setSpan(mScaleSpan, currentText.length() - number.length(),
                            currentText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    dialNumber.setText(spannableString, TextView.BufferType.SPANNABLE);
                }
            });
            mInputMotionAnimator.start();
        }
    }

    private void presentDialedNumber() {
        if (mInputMotionAnimator != null) {
            mInputMotionAnimator.cancel();
            mInputMotionAnimator.removeAllUpdateListeners();
        }

        presentDialedNumber(mNumber);
    }

    @NonNull
    StringBuffer getNumber() {
        return mNumber;
    }
}
