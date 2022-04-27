package com.ev.dialer.phonebook.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.android.vcard.VCardEntry;
import com.ev.dialer.btManager.BtManager;
import com.ev.dialer.phonebook.connectthread.PbapConnectThread;
import com.ev.dialer.Constants;
import com.ev.dialer.phonebook.constants.PbapClientConstants;
import com.ev.dialer.phonebook.utils.LogUtils;
import com.ev.dialer.phonebook.utils.PbapClientUtils;

import java.util.List;

/**
 * Created by Jerry on 22/2/7.
 */
public class BluetoothContactsService extends Service {
    private static final String TAG = BluetoothContactsService.class.getSimpleName();
    public BluetoothPbapClient mPbapClient;
    private BluetoothAdapter mBluetoothAdapter;
    public BluetoothServiceHandler mHandler = new BluetoothServiceHandler();
    public Context mContext;
    private String path;
    public List<VCardEntry> vCardEntryList;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, " onCreate ");
        mContext = this;
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BtManager.getInstance(mContext).mBluetoothAdapter;
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i(TAG, " onBind ");
        return new BluetoothServiceBinder();
    }

    public class BluetoothServiceBinder extends Binder {
        public BluetoothContactsService getService() {
            return BluetoothContactsService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, " onStartCommand ");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setRequestPbapPath(String path) {
        this.path = path;
    }

    public List<VCardEntry> getVCardEntryList() {
        return vCardEntryList;
    }

    public boolean connectPbapClient() {
        BluetoothDevice device = BtManager.getInstance(mContext).getConnectedDevice();
        if (null == device) return false;//无连接设备
        LogUtils.i(TAG, "mPbapClient=" + mPbapClient);
        if (mPbapClient != null) {
            mPbapClient.disconnect();
            mPbapClient = null;
        }
        if (mPbapClient == null) {
            mPbapClient = new BluetoothPbapClient(device, mHandler);
            //LogUtils.i(TAG, "PbapClientUtils.getConnectState:" + PbapClientUtils.getConnectState(mPbapClient));
            if (mPbapClient != null) {
                PbapConnectThread connectThread = new PbapConnectThread(this, mPbapClient);
                connectThread.start();
            } else {
                LogUtils.i(TAG, "mPbapClient is not ready ! ");
                LogUtils.i(TAG, mPbapClient.getState().toString());
            }
        }
        return true;
    }

    private class BluetoothServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            LogUtils.i(TAG, "BluetoothServiceHandler-msg.what:" + msg.what);

            switch (msg.what) {
                case BluetoothPbapClient.EVENT_PULL_PHONE_BOOK_DONE: {
                    LogUtils.i(TAG, "EVENT_PULL_PHONE_BOOK_DONE");
                    //LogUtils.i(TAG, "BluetoothServiceHandler-msg.obj:" + msg.toString());
                    vCardEntryList = (List<VCardEntry>) msg.obj;
                    sendBroadcast(new Intent(Constants.PBAP_CONTACTS_READY));
                    for (VCardEntry vCardEntry : vCardEntryList) {
                        //LogUtils.i(TAG, "vCardEntry:\n" + vCardEntry.toString());
                        if (vCardEntry.getPhoneList() != null && vCardEntry.getPhoneList().size() > 0) {
                            LogUtils.i(TAG, "PhoneList:" + vCardEntry.getPhoneList().size() + ",***" + vCardEntry.getPhoneList().get(0).getNumber());
                        }
                    }
                    mPbapClient.disconnect();
                    mPbapClient = null;
                }
                break;
                case BluetoothPbapClient.EVENT_SESSION_CONNECTED: {
                    LogUtils.i(TAG, "EVENT_SESSION_CONNECTED");
                    if (PbapClientUtils.getConnectState(mPbapClient)) {
                        LogUtils.i(TAG, "pulling the PhoneBook, it may take a long time ! ");
                        PbapClientUtils.pullPhonebook(mPbapClient, PbapClientConstants.PB_PATH, Constants.PBAP_CONTACTS_MAX_COUNT);
                    }
                }
                break;
                case BluetoothPbapClient.EVENT_SESSION_DISCONNECTED: {
                    LogUtils.i(TAG, "EVENT_SESSION_DISCONNECTED");
                }
                break;
                default: {
                }
            }
        }
    }

}
