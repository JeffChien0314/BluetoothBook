package com.ev.bluetooth.phonebook.connectthread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

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
