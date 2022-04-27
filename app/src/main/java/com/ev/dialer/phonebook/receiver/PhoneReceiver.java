package com.ev.dialer.phonebook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ev.dialer.phonebook.utils.LogUtils;

public class PhoneReceiver extends BroadcastReceiver {
    private static final String TAG = PhoneReceiver.class.getSimpleName();
    private final String ACTION_PHONE_STATE="android.intent.action.PHONE_STATE";
    private final String ACTION_OUTGOING_CALL="android.intent.action.NEW_OUTGOING_CALL";
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "onReceive,action:"+action);
      //  Toast.makeText(context,"action="+action,Toast.LENGTH_SHORT);
        switch (action){
            case TelephonyManager.ACTION_PHONE_STATE_CHANGED:
               // TelephoneManager.ACTION_PHONE_STATE_CHANGED
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                Log.i(TAG, "静态广播,当前状态state="+state);
                String inNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);// 来电号码
                Log.i(TAG, "onReceive: inNumber="+inNumber);
                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {// 电话正在响铃
                    Toast.makeText(context,"静态广播，电话正在振铃",Toast.LENGTH_SHORT).show();
                } else if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {// 挂断
                    Toast.makeText(context,"静态广播，电话已挂断",Toast.LENGTH_SHORT).show();
                } else if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {// 摘机，通话状态
                    Toast.makeText(context,"静态广播，电话正在通话",Toast.LENGTH_SHORT).show();
                }else
                Toast.makeText(context,"静态广播，状态改变，没有extra ",Toast.LENGTH_SHORT).show();
                break;
            case ACTION_OUTGOING_CALL:
                if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                    savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                }
                break;

        }
    }
}
