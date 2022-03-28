package com.ev.bluetooth.phonebook.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.client.pbap.BluetoothPbapClient;
import android.os.ParcelUuid;
import android.util.Log;

import com.ev.bluetooth.phonebook.constants.PbapClientConstants;

import java.lang.reflect.Method;
import java.util.Set;

public class PbapClientUtils {
    private static final String TAG = PbapClientUtils.class.getSimpleName();
    private static final long PBAP_FILTER_VERSION = 1 << 0;
    private static final long PBAP_FILTER_FN = 1 << 1;
    private static final long PBAP_FILTER_N = 1 << 2;
    private static final long PBAP_FILTER_PHOTO = 1 << 3;
    private static final long PBAP_FILTER_ADR = 1 << 5;
    private static final long PBAP_FILTER_TEL = 1 << 7;
    private static final long PBAP_FILTER_EMAIL = 1 << 8;
    private static final long PBAP_FILTER_NICKNAME = 1 << 23;

    private static final long PBAP_REQUESTED_FIELDS =
            PBAP_FILTER_VERSION | PBAP_FILTER_FN | PBAP_FILTER_N | PBAP_FILTER_PHOTO
                    | PBAP_FILTER_ADR | PBAP_FILTER_EMAIL | PBAP_FILTER_TEL | PBAP_FILTER_NICKNAME;

    //连接
    public static void connect(BluetoothPbapClient client){
        if(null!=client){
            client.connect();
        }
    }

    //断开连接
    public static void disConnect(BluetoothPbapClient client){
        if(null!=client){
            client.disconnect();
        }
    }

    //判断连接状态
    public static boolean getConnectState(BluetoothPbapClient client){
        LogUtils.i(TAG,"getConnectState:"+client.getState());
        if(null!=client && client.getState() == BluetoothPbapClient.ConnectionState.CONNECTED){
            return true;
        }
        return false;
    }

    //获取已连接的蓝牙设备
    public static void getConnectedBtDevice(BluetoothAdapter adapter) {
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {
            //得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = adapter.getBondedDevices(); //集合里面包括已绑定的设备和已连接的设备
                Log.i(TAG, "devices:" + devices.size());
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) { //根据状态来区分是已连接的还是已绑定的，isConnected为true表示是已连接状态。
                        Log.i(TAG, "connected:" + device.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean pullPhonebook(BluetoothPbapClient client,String path,int maxCount){
        if(null!=client){
            return client.pullPhoneBook(path,maxCount,0);
        }
        return false;
    }

    public void showAllAvailableUuids(BluetoothDevice device) {
        ParcelUuid[] uuids = device.getUuids();
        if (uuids == null || uuids.length == 0) {
            LogUtils.i(TAG,"get uuids failed");
        } else {
            String uuidString = device.getUuids()[0].getUuid().toString();
            LogUtils.i(TAG,"uuidString is : " + uuidString);
            for (ParcelUuid uuid : uuids) {
                LogUtils.i(TAG,"uuid is : " + uuid);
            }
        }
    }
}
