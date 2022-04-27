package com.ev.dialer.phonebook.connectthread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.content.Context;

public class PbapConnectThread extends Thread{
    private static final String TAG = "PbapConnectThread";
    private BluetoothPbapClient mPbapClient;
    private BluetoothDevice bluetoothDevice;
    private Context context;

    public PbapConnectThread(Context context, BluetoothPbapClient mPbapClient){
        this.context = context;
        this.mPbapClient = mPbapClient;
    }

    @Override
    public void run() {
        mPbapClient.connect();
    }
}
