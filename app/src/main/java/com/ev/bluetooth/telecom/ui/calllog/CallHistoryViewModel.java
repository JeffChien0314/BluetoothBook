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

package com.ev.bluetooth.telecom.ui.calllog;

import android.app.Application;
import android.text.format.DateUtils;

import com.ev.bluetooth.phonebook.common.InMemoryPhoneBook;
import com.ev.bluetooth.livedata.CallHistoryLiveData;
import com.ev.bluetooth.livedata.HeartBeatLiveData;
import com.ev.bluetooth.telecom.ui.common.UiCallLogLiveData;
import com.ev.bluetooth.telecom.ui.common.entity.UiCallLog;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * View model for CallHistoryFragment which provides call history live data.
 */
public class CallHistoryViewModel extends AndroidViewModel {
    private UiCallLogLiveData mUiCallLogLiveData;

    public CallHistoryViewModel(@NonNull Application application) {
        super(application);
        mUiCallLogLiveData = new UiCallLogLiveData(application.getApplicationContext(),
                new HeartBeatLiveData(DateUtils.MINUTE_IN_MILLIS),
                CallHistoryLiveData.newInstance(application.getApplicationContext()),
                InMemoryPhoneBook.get().getContactsLiveData());
    }

    /**
     * Returns the live data for call history list.
     */
    public LiveData<List<UiCallLog>> getCallHistory() {
        return mUiCallLogLiveData;
    }
}
