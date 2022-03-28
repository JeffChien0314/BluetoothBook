package com.ev.bluetooth.phonebook.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.SdpMasRecord;
import android.bluetooth.client.map.BluetoothMapMessage;
import android.bluetooth.client.map.BluetoothMasClient;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.ev.bluetooth.phonebook.BTBookApplication;
import com.ev.bluetooth.phonebook.connectthread.MapConnectThread;
import com.ev.bluetooth.phonebook.connectthread.PbapConnectThread;
import com.ev.bluetooth.phonebook.utils.LogUtils;

import java.util.ArrayList;

public class BluetoothMapService extends Service {
    private static final String TAG = BluetoothMapService.class.getSimpleName();
    public BluetoothMasClient mMasClient;
    private BluetoothAdapter mBluetoothAdapter;
    public BluetoothMapService.BluetoothServiceHandler mHandler = new BluetoothMapService.BluetoothServiceHandler();
    public Context mContext;

    //private SdpMasRecord masRecord;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, " onCreate ");

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BTBookApplication.getInstance().mBluetoothAdapter;
        }

        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i(TAG, " onBind ");
        return new BluetoothMapService.BluetoothServiceBinder();
    }

    public class BluetoothServiceBinder extends Binder {
        public BluetoothMapService getService() {
            return BluetoothMapService.this;
        }
    }

    public void connectMapClient(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        LogUtils.i(TAG, "mMasClient=" + mMasClient);
        if (mMasClient != null) {
            mMasClient.disconnect();
            mMasClient = null;
        }
        if (mMasClient == null) {
            //mMasClient = new BluetoothMasClient(device, masRecord, mHandler);
            LogUtils.i(TAG, "mMasClient.ConnectState:" + mMasClient.getState());
            if (mMasClient != null) {
                MapConnectThread connectThread = new MapConnectThread(this, mMasClient);
                connectThread.start();
            } else {
                LogUtils.i(TAG, "mMasClient is not ready ! ");
                LogUtils.i(TAG, mMasClient.getState().toString());
            }
        }
    }

    private class BluetoothServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            LogUtils.i(TAG, "BluetoothServiceHandler-msg.what:" + msg.what);
            switch (msg.what) {
                case BluetoothMasClient.EVENT_GET_MESSAGES_LISTING:
                    ArrayList<BluetoothMapMessage> arrayList = (ArrayList<BluetoothMapMessage>) msg.obj;
                    for(BluetoothMapMessage mapMessage:arrayList){
                        LogUtils.i(TAG, "SenderName:" + mapMessage.getSenderName());
                        LogUtils.i(TAG, "SenderAddressing:" + mapMessage.getSenderAddressing());
                    }
                    break;
            }
        }
    }
}