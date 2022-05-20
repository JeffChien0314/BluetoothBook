package com.ev.dialer;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class ServiceLifecycleObserver implements LifecycleObserver {

    private final String TAG = ServiceLifecycleObserver.class.getSimpleName();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onServiceCreate() {
        // Service onCreate
        Log.d(TAG, "onServiceCreate: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onServiceStart() {
        // Service onStartCommand
        Log.d(TAG, "onServiceStart: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onServiceStop() {
        // Service stop
        Log.d(TAG, "onServiceStop: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onServiceDestroy() {
        // Service onDestroy
        Log.d(TAG, "onServiceDestroy: ");
    }

}
