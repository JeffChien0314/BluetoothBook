/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.ev.dialer.telecom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.util.Log;

import com.ev.dialer.log.L;
import com.ev.dialer.phonebook.common.PhoneCallManager;
import com.ev.dialer.telecom.ui.WindowService;

import static com.ev.dialer.telecom.Constants.Action.CALL_END_ACTION;
import static com.ev.dialer.telecom.Constants.Action.CALL_STATE_CHANGE_ACTION;


/**
 * An implementation of {@link InCallService}. This service is bounded by android telecom and
 * {@link UiCallManager}. For incoming calls it will launch Dialer app.
 */
public class InCallServiceImpl extends InCallService {
    private final String TAG = InCallServiceImpl.class.getName();
    public static final String ACTION_LOCAL_BIND = "local_bind";

    private BroadcastReceiver headsetReceiver = null;
    // private static PhoneCallManager phoneCallManager;

    private Call.Callback callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            Log.d(TAG, "onStateChanged, state == " + state);
            switch (state) {
                case Call.STATE_RINGING:
                case Call.STATE_CONNECTING:
                    //    break;
                case Call.STATE_ACTIVE:// 通话中
                    if (PhoneCallManager.getCallType() == CallType.CALL_OUT) {
                        Intent intent=new Intent();
                        intent.setAction(CALL_STATE_CHANGE_ACTION);
                        intent.putExtra("state",Call.STATE_ACTIVE);
                        sendBroadcast(intent);
                    }
                    break;
                case Call.STATE_DISCONNECTED: // 通话结束
                    Intent intent = new Intent();
                    intent.setAction(CALL_END_ACTION);
                    sendBroadcast(intent);
                    break;
            }
        }
    };

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.d(TAG, "onCallAdded");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            Log.d(TAG, "isWiredHeadsetOn");
            this.setAudioRoute(CallAudioState.ROUTE_WIRED_HEADSET);
        } else {
            this.setAudioRoute(CallAudioState.ROUTE_SPEAKER);
        }
        call.registerCallback(callback);

        CallType callType = null;
        if (call.getState() == Call.STATE_RINGING) {
            callType = CallType.CALL_IN;
        } else if (call.getState() == Call.STATE_CONNECTING) {
            callType = CallType.CALL_OUT;
        }
        PhoneCallManager.setCall(call, callType); // 传入call
        if (callType != null) {
            Call.Details details = call.getDetails();
            String phoneNumber = details.getHandle().toString().substring(4)
                    .replaceAll("%20", ""); // 去除拨出电话中的空格

            Intent intent = new Intent(getBaseContext(), WindowService.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, callType);
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
            Log.d(TAG, "onCallAdded, callType : " + callType + ", phoneNumber : " + phoneNumber);
            startService(intent);
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.d(TAG, "onCallRemoved");
        call.unregisterCallback(callback);
        PhoneCallManager.mCall = null;
    }

    public enum CallType {
        CALL_IN,
        CALL_OUT,
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState audioState) {
        super.onCallAudioStateChanged(audioState);
        Log.d(TAG, "onCallAudioStateChanged, audioState == " + audioState);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        registerHeadsetReceiver();
    }

    private void registerHeadsetReceiver() {
        headsetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, "registerHeadsetReceiver, action == " + action);
                if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                    if (intent.hasExtra("state")) {
                        if (intent.getIntExtra("state", 0) == 0) {
                            Log.d(TAG, "headset not connected");
                            InCallServiceImpl.this.setAudioRoute(CallAudioState.ROUTE_SPEAKER);
                        } else if (intent.getIntExtra("state", 0) == 1) {
                            Log.d(TAG, "headset connected");
                            InCallServiceImpl.this.setAudioRoute(CallAudioState.ROUTE_WIRED_HEADSET);
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, filter);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (headsetReceiver != null) {
            unregisterReceiver(headsetReceiver);
        }
        super.onDestroy();
    }


    /* private static final String TAG = "CD.InCallService";

     *//** An action which indicates a bind is from local component. *//*


    private CopyOnWriteArrayList<Callback> mCallbacks = new CopyOnWriteArrayList<>();

    private InCallRouter mInCallRouter;

    *//** Listens to active call list changes. Callbacks will be called on main thread. *//*
    public interface ActiveCallListChangedCallback {

        *//**
     * Called when a new call is added.
     *
     * @return if the given call has been handled by this callback.
     *//*
        boolean onTelecomCallAdded(Call telecomCall);

        */

    /**
     * Called when an existing call is removed.
     *
     * @return if the given call has been handled by this callback.
     *//*
        boolean onTelecomCallRemoved(Call telecomCall);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInCallRouter = new InCallRouter(getApplicationContext());
        mInCallRouter.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInCallRouter.stop();
        mInCallRouter = null;
    }

    @Override
    public void onCallAdded(Call telecomCall) {
        L.d(TAG, "onCallAdded: %s", telecomCall);

        for (Callback callback : mCallbacks) {
            callback.onTelecomCallAdded(telecomCall);
        }

        mInCallRouter.onCallAdded(telecomCall);
    }

    @Override
    public void onCallRemoved(Call telecomCall) {
        L.d(TAG, "onCallRemoved: %s", telecomCall);
        for (Callback callback : mCallbacks) {
            callback.onTelecomCallRemoved(telecomCall);
        }

        mInCallRouter.onCallRemoved(telecomCall);
    }
*/
    @Override
    public IBinder onBind(Intent intent) {
        L.d(TAG, "onBind: %s", intent);
        return ACTION_LOCAL_BIND.equals(intent.getAction())
                ? new LocalBinder()
                : super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        L.d(TAG, "onUnbind, intent: %s", intent);
        if (ACTION_LOCAL_BIND.equals(intent.getAction())) {
            return false;
        }
        return super.onUnbind(intent);
    }
/*
    @Override
    public void onCallAudioStateChanged(CallAudioState audioState) {
        for (Callback callback : mCallbacks) {
            callback.onCallAudioStateChanged(audioState);
        }
    }

    public void registerCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    public void addActiveCallListChangedCallback(ActiveCallListChangedCallback callback) {
        mInCallRouter.registerActiveCallListChangedCallback(callback);
    }

    public void removeActiveCallListChangedCallback(ActiveCallListChangedCallback callback) {
        mInCallRouter.unregisterActiveCallHandler(callback);
    }

    @Deprecated
    interface Callback {
        void onTelecomCallAdded(Call telecomCall);

        void onTelecomCallRemoved(Call telecomCall);

        void onCallAudioStateChanged(CallAudioState audioState);
    }
*/

    /**
     * Local binder only available for Car Dialer package.
     */
    public class LocalBinder extends Binder {

        /**
         * Returns a reference to {@link InCallServiceImpl}. Any process other than Dialer
         * process won't be able to get a reference.
         */
        public InCallServiceImpl getService() {
            if (getCallingPid() == Process.myPid()) {
                return InCallServiceImpl.this;
            }
            return null;
        }
    }
}
