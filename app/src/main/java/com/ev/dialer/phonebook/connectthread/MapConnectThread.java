package com.ev.dialer.phonebook.connectthread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.map.BluetoothMasClient;
import android.content.Context;

public class MapConnectThread extends Thread{
    private static final String TAG = "MapConnectThread";
    private BluetoothMasClient mMasClient;
    private BluetoothDevice bluetoothDevice;
    private Context context;

    public MapConnectThread(Context context, BluetoothMasClient mMasClient){
        this.context = context;
        this.mMasClient = mMasClient;
    }

    @Override
    public void run() {
        mMasClient.connect();
    }
}
