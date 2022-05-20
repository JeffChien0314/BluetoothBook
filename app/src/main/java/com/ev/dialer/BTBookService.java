package com.ev.dialer;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleService;

public class BTBookService extends LifecycleService {
    private final String TAG = BTBookService.class.getSimpleName();

    private ServiceLifecycleObserver observer;

    private static BTBookService myService;
    public static boolean isAlive;


    public BTBookService() {
        observer = new ServiceLifecycleObserver();
        // 将观察者和被观察者绑定
        getLifecycle().addObserver(observer);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        isAlive = true;
        myService = this;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        Log.i(TAG, "onBind");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        isAlive = false;
        super.onDestroy();
    }

    public static LifecycleOwner getMyLifecycleOwner() {
        return myService;
    }


}
