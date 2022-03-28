package com.ev.bluetooth.phonebook;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.ev.bluetooth.phonebook.service.BluetoothMapService;
import com.ev.bluetooth.phonebook.service.BluetoothRecentsService;
import com.ev.bluetooth.phonebook.utils.LogUtils;

import java.util.Set;

public class BTBookApplication extends Application {
    private static final String TAG = BTBookApplication.class.getSimpleName();
    public static BTBookApplication instance;

    public BluetoothAdapter mBluetoothAdapter;
    public boolean isPbapProfileReady = false;
    private BluetoothDevice bluetoothDevice;
    public static BluetoothRecentsService mPbapService;
    public static BluetoothMapService mMapService;
    public static String deviceMac;

    /*private final Handler mMapHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtils.d(TAG, "handleMessage,msg.what:"+msg.what);
            switch (msg.what) {
                case BluetoothMasClient.EVENT_GET_MESSAGES_LISTING:
                    ArrayList<BluetoothMapMessage> arrayList = (ArrayList<BluetoothMapMessage>) msg.obj;
                    break;
            }
        }

        ;
    };*/

    /*private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d(TAG, "onReceive,action:"+action);
            if (action.equals("android.bluetooth.device.action.SDP_RECORD")) {
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ParcelUuid uuid = intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);
                if (uuid.equals(BluetoothUuid.MAS)) {

                }
            }
        }
    };*/

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        /*IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.SDP_RECORD");
        registerReceiver(mReceiver, filter);*/

        //找到已配对的蓝牙设备
        initScanedBlueDevice();

    }

    public static BTBookApplication getInstance() {
        return instance;
    }

    private void initScanedBlueDevice() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            // 说明该设备不支持蓝牙
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                //蓝牙设备未打开
                //Toast.makeText(this, "请打开设备的蓝牙功能并完成设备配对！", Toast.LENGTH_LONG).show();
            } else {
                //PbapClientUtils.getConnectedBtDevice(mBluetoothAdapter);
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        BluetoothClass bluetoothClass = device.getBluetoothClass();
                        //设备类型，如手机，
                        //int deviceCategory = bluetoothClass.getDeviceClass();
                        //具体设备类型，如音箱类别
                        int deviceMajorCategory = bluetoothClass.getMajorDeviceClass();
                        LogUtils.i(TAG, "已配对设备名字:" + device.getName() + ",已配对设备地址:" + device.getAddress() + ",deviceMajorCategory:" + deviceMajorCategory);
                        //LogUtils.i(TAG, "BondState:" + device.getBondState());

                        if (deviceMajorCategory == BluetoothClass.Device.Major.PHONE && "78:85:F4:5A:E6:DC".equals(device.getAddress())) {
                            //LogUtils.i(TAG, "BondState:" + device.getUuids()[0].getUuid().toString());
                            deviceMac = device.getAddress();
                            bluetoothDevice = device;

                            //bindBluetoothPbapService();
                            //bindBluetoothMapService();

                            break;
                        } else if (deviceMajorCategory == BluetoothClass.Device.Major.UNCATEGORIZED) {
                            //Log.i(TAG,"已配对设备Uuid:"+device.getUuids()[0].getUuid().toString());
                            bluetoothDevice = device;

                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "没有找到已匹对的设备！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void bindBluetoothPbapService() {
        Intent serviceIntent = new Intent(this, BluetoothRecentsService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.d(TAG, "onServiceConnected, init the Service");
                mPbapService = ((BluetoothRecentsService.BluetoothServiceBinder) service).getService();
                mPbapService.connectPbapClient(deviceMac);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPbapService = null;
            }
        };
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private void bindBluetoothMapService() {
        Intent serviceIntent = new Intent(this, BluetoothMapService.class);

        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.d(TAG, "onServiceConnected, init the Service");
                mMapService = ((BluetoothMapService.BluetoothServiceBinder) service).getService();
                mMapService.connectMapClient(deviceMac);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mMapService = null;
            }
        };
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }
}
