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
import android.util.Log;
import android.widget.Toast;

import com.android.vcard.VCardEntry;
import com.ev.dialer.btManager.BtManager;
import com.ev.dialer.phonebook.connectthread.PbapConnectThread;
import com.ev.dialer.Constants;
import com.ev.dialer.phonebook.constants.PbapClientConstants;
import com.ev.dialer.phonebook.utils.LogUtils;
import com.ev.dialer.phonebook.utils.PbapClientUtils;

import java.util.Collection;
import java.util.List;

/**
 * Created by Jerry on 22/1/18.
 */
public class BluetoothRecentsService extends Service {
    private static final String TAG = BluetoothRecentsService.class.getSimpleName();
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
        public BluetoothRecentsService getService() {
            return BluetoothRecentsService.this;
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
        //  if (TextUtils.isEmpty(address)) return false;
        BluetoothDevice device = BtManager.getInstance(mContext).getConnectedDevice();
        if (null == device) return false;//无连接设备

        LogUtils.i(TAG, "mPbapClient=" + mPbapClient);
        if (mPbapClient != null) {
            mPbapClient.disconnect();
            mPbapClient = null;
        }
        if (mPbapClient == null) {
            Log.i(TAG, "connectPbapClient: device="+device.getName()+";MAC:"+device.getAddress());
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
                    sendBroadcast(new Intent(Constants.PBAP_RECENTS_READY));
                    LogUtils.i(TAG, "vCardEntry:" + vCardEntryList.size());
                    for (VCardEntry vCardEntry : vCardEntryList) {
                        //LogUtils.i(TAG, "vCardEntry:\n" + vCardEntry.toString());
                        Collection<String> collection = vCardEntry.getCallDate().getTypeCollection();
                        if (collection != null) {
                            for (String category : collection) {
                                LogUtils.i(TAG, "category:" + category + "," + vCardEntry.getPhoneList().get(0).getNumber());
                            }
                        }
                        LogUtils.i(TAG, "getCallDatetime:" + vCardEntry.getCallDate().getCallDatetime());
                    }
                    mPbapClient.disconnect();
                    mPbapClient = null;
                }
                break;
                case BluetoothPbapClient.EVENT_SESSION_CONNECTED: {
                    LogUtils.i(TAG, "EVENT_SESSION_CONNECTED");
                    Toast.makeText(mContext,"mPbapClient 连接成功，正在获取信息...",Toast.LENGTH_SHORT);
                    if (PbapClientUtils.getConnectState(mPbapClient)) {
                        LogUtils.i(TAG, "pulling the PhoneBook, it may take a long time ! ");
                        PbapClientUtils.pullPhonebook(mPbapClient, PbapClientConstants.CCH_PATH, Constants.PBAP_RECENTS_MAX_COUNT);
                    }
                }
                break;
                case BluetoothPbapClient.EVENT_SESSION_DISCONNECTED: {
                    LogUtils.i(TAG, "EVENT_SESSION_DISCONNECTED");
                    Toast.makeText(mContext,"mPbapClient 连接失败",Toast.LENGTH_SHORT);
                    sendBroadcast(new Intent(Constants.PBAP_RECENTS_FAILED));
                }
                break;
                default: {
                }
            }
        }
    }

}
