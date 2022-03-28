package com.ev.bluetooth.phonebook.constants;

import android.bluetooth.client.pbap.BluetoothPbapClient;

public class PbapClientConstants {
    public static String SIM_PB_PATH = BluetoothPbapClient.SIM_PB_PATH;//存储在SIM卡联系人
    public static String PB_PATH = BluetoothPbapClient.PB_PATH;//存储在手机端联系人
    public static String MCH_PATH = BluetoothPbapClient.MCH_PATH;//未接来电记录
    public static String ICH_PATH = BluetoothPbapClient.ICH_PATH;//来电记录
    public static String OCH_PATH = BluetoothPbapClient.OCH_PATH;//呼叫记录
    public static String CCH_PATH = BluetoothPbapClient.CCH_PATH;//3种通话记录一起
    public static String SIM_MCH_PATH = BluetoothPbapClient.SIM_MCH_PATH;//SIM卡未接来电记录
    public static String SIM_ICH_PATH = BluetoothPbapClient.SIM_ICH_PATH;//SIM卡来电记录
    public static String SIM_OCH_PATH = BluetoothPbapClient.SIM_OCH_PATH;//SIM卡呼叫记录
    public static String SIM_CCH_PATH = BluetoothPbapClient.SIM_CCH_PATH;//SIM卡3种通话记录一起

    public static final String FAV_PATH = "telecom/fav.vcf";
}
