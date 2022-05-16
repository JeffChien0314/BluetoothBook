/*
 * Copyright (C) 2018 The Android Open Source Project
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

import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.ui.common.KeypadView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Fragment which displays a pad of keys.
 */
public class KeypadFragment extends Fragment {

    private KeypadView keypadView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.keypad_fragment, container, false);
        keypadView = new KeypadView(getContext());
        ((FrameLayout) fragmentView.findViewById(R.id.keypad_fragment)).addView(keypadView);
        if (getParentFragment() instanceof KeypadView.KeypadCallback) {
            keypadView.setKeypadCallback((KeypadView.KeypadCallback) getParentFragment());
        } else if (getHost() instanceof KeypadView.KeypadCallback) {
            keypadView.setKeypadCallback((KeypadView.KeypadCallback) getHost());
        }
        return fragmentView;
    }

}
