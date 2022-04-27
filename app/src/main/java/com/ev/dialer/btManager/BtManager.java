package com.ev.dialer.btManager;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAvrcpController;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ev.dialer.phonebook.service.BluetoothMapService;
import com.ev.dialer.phonebook.service.BluetoothRecentsService;
import com.ev.dialer.phonebook.utils.LogUtils;

import java.util.Set;

import androidx.annotation.NonNull;

import static android.security.KeyStore.getApplicationContext;
import static android.telephony.PhoneStateListener.LISTEN_CALL_STATE;

public class BtManager {
    private final String TAG = BtManager.class.getSimpleName();
    private final int HEADSET_CLIENT = 16;
    private final int PBAP_CLIENT = 17;


    public BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadsetClient mBluetoothHeadsetClient;
    private android.bluetooth.BluetoothPbapClient mBluetoothPbapClient;
    public boolean isPbapProfileReady = false;
    // private BluetoothDevice mConnectedDevice;
    public static BluetoothRecentsService mPbapService;
    public static BluetoothMapService mMapService;
    //   public static String deviceMac;
    private static Context mContext;
    private static BtManager mInstance;
    private final int UPDATE_DEVICE = 1;
    public BluetoothPbapClient mPbapClient;
    private BluetoothDevice mConnectedDevice;
    private Set<BluetoothDevice> mBondedDevices;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_DEVICE:
                    initScanedBlueDevice();
                    break;
            }
        }
    };
    private BluetoothProfile.ServiceListener profileServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            //   Toast.makeText(mContext, "onServiceConnected: profile=" + profile + ",BluetoothProfile=" + proxy, Toast.LENGTH_LONG).show();
            Log.i(TAG, "onServiceConnected: profile=" + profile + ",BluetoothProfile=" + proxy);
            switch (profile) {
                case HEADSET_CLIENT:
                    Log.i(TAG, "onServiceConnected: ");
                    mBluetoothHeadsetClient = (BluetoothHeadsetClient) proxy;
                    Toast.makeText(mContext, "profile connect successfully", Toast.LENGTH_SHORT).show();
                    break;
                case PBAP_CLIENT:
                    mBluetoothPbapClient = (android.bluetooth.BluetoothPbapClient) proxy;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + profile);
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            Log.i(TAG, "onServiceDisconnected: profile=" + profile);
            switch (profile) {

                default:
                    throw new IllegalStateException("Unexpected value: " + profile);
            }
        }
    };

    public BtManager(Context context) {
        mContext = context;
        initScanedBlueDevice();

        if(null != mBluetoothAdapter) {
            registerProfile(context);
            registeReceiver();
        }

        getTelephonyManager();
    }

    public static BtManager getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new BtManager(context);
        }
        return mInstance;
    }

    public void registerProfile(Context context) {
        if (mBluetoothAdapter.getProfileProxy(context, profileServiceListener, HEADSET_CLIENT)) {

            Log.i(TAG, "registerProfile: HEADSET_CLIENT success");
        } else {
            Log.e(TAG, "registerProfile: HEADSET_CLIENT failed");
        }

    }


    private void initScanedBlueDevice() {

        if (null == mBluetoothAdapter) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (null != mBluetoothAdapter) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                //蓝牙设备未打开
                //Toast.makeText(this, "请打开设备的蓝牙功能并完成设备配对！", Toast.LENGTH_LONG).show();
            } else {
                //PbapClientUtils.getConnectedBtDevice(mBluetoothAdapter);
                // Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                mBondedDevices = mBluetoothAdapter.getBondedDevices();
                mConnectedDevice = null;
                // If there are paired devices
                if (mBondedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : mBondedDevices) {
                        BluetoothClass bluetoothClass = device.getBluetoothClass();
                        //设备类型，如手机，
                        //int deviceCategory = bluetoothClass.getDeviceClass();
                        //具体设备类型，如音箱类别
                        int deviceMajorCategory = bluetoothClass.getMajorDeviceClass();
                        LogUtils.i(TAG, "已配对设备名字:" + device.getName() + ",已配对设备地址:" + device.getAddress() + ",deviceMajorCategory:" + deviceMajorCategory);
                        //LogUtils.i(TAG, "BondState:" + device.getBondState());


                        if (deviceMajorCategory == BluetoothClass.Device.Major.PHONE && device.isConnected()) {
                            LogUtils.i(TAG, "已连接的设备名字:" + device.getName() + ",已连接设备地址:" + device.getAddress() + ",deviceMajorCategory:" + deviceMajorCategory);

                            //LogUtils.i(TAG, "BondState:" + device.getUuids()[0].getUuid().toString());
                            //    deviceMac = device.getAddress();
                            mConnectedDevice = device;

                            //bindBluetoothPbapService();
                            //bindBluetoothMapService();

                            break;
                        } else if (deviceMajorCategory == BluetoothClass.Device.Major.UNCATEGORIZED) {
                            //Log.i(TAG,"已配对设备Uuid:"+device.getUuids()[0].getUuid().toString());
                            //   mConnectedDevice = device;

                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "没有找到已匹对的设备！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public BluetoothDevice getConnectedDevice() {
        return mConnectedDevice;
    }

    private void getTelephonyManager() {
        TelephonyManager manager = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE));
        MyPhoneStateListener mpsListener = new MyPhoneStateListener();
        manager.listen(mpsListener, LISTEN_CALL_STATE);
    }

    public class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String number) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Toast.makeText(mContext, "***空闲状态中****", Toast.LENGTH_SHORT);
                    Log.d(TAG, "***空闲状态中****");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Toast.makeText(mContext, "***振铃中****", Toast.LENGTH_SHORT);
                    Log.d(TAG, "***振铃中****");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(mContext, "***通话中****", Toast.LENGTH_SHORT);
                    Log.d(TAG, "***通话中****");
                    break;
                default:
                    Log.i(TAG, "onCallStateChanged: state=" + state);
                    break;
            }
        }
    }


    public void getContacts(BluetoothDevice device){
       // mBluetoothPbapClient.connect(device)
    }
    public void connectAudio(String number) {
        if (null != mBluetoothHeadsetClient) {
            mBluetoothHeadsetClient.dial(mConnectedDevice, number);
        }
    }

    private void registeReceiver() {
        //蓝牙相关action
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);//A2DP连接状态改变
        filter.addAction(BluetoothA2dpSink.ACTION_PLAYING_STATE_CHANGED);//A2DP播放状态改变
        filter.addAction(BluetoothAvrcpController.ACTION_CONNECTION_STATE_CHANGED);//连接状态
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(bluetoothReceiver, filter);
    }


    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: action=" + action);
            switch (action) {
                case BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothDevice.ACTION_NAME_CHANGED:
                case BluetoothDevice.ACTION_UUID:
                    mHandler.sendEmptyMessageDelayed(UPDATE_DEVICE, 100);
                    //  btA2dpContentStatus(intent);
                    break;
                case BluetoothA2dpSink.ACTION_PLAYING_STATE_CHANGED:
                    Log.e(TAG, "mBtReceiver，BluetoothA2dpSink.ACTION_PLAYING_STATE_CHANGED");
                    //控制蓝牙的播放状态,启动这个作为播放状态更新，时序太慢
                    //       playState(intent);
                    break;

                case BluetoothAvrcpController.ACTION_CONNECTION_STATE_CHANGED:
                    Log.e(TAG, "mBtReceiver，BluetoothAvrcpController.ACTION_CONNECTION_STATE_CHANGED");
                    break;
               /* case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED: //A2DP连接状态改变
                    Toast.makeText(context, "A2DP连接状态改变", Toast.LENGTH_SHORT);
                    break;
                case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED: //A2DP播放状态改变
                    Toast.makeText(context, "A2DP播放状态改变", Toast.LENGTH_SHORT);
                    break;*/
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    //    handler.sendEmptyMessage(UPDATE_BT_STATE);
                    Log.e(TAG, "mBtReceiver，BluetoothAdapter.ACTION_STATE_CHANGED");
                    break;
             /*   case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    handler.sendEmptyMessageDelayed(UPDATE_DEVICE_LIST, 100);
                    Log.e(TAG, "mBtReceiver，BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED");
                    //用这个广播判断蓝牙连接状态
                    break;
                case BluetoothDevice.ACTION_NAME_CHANGED:
                    handler.sendEmptyMessage(UPDATE_DEVICE_LIST);
                    Log.e(TAG, "mBtReceiver， BluetoothDevice.ACTION_NAME_CHANGED");
                    break;*/
                default:
                    break;
            }
        }
    };

}
