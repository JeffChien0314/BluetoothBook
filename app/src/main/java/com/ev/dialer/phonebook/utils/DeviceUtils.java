package com.ev.dialer.phonebook.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class DeviceUtils {
    public static String getDeviceUUID(Context context) {
        String uniqueId = "";
        try {
            String tmDevice, tmSerial, androidId;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            uniqueId = deviceUuid.toString();
        } catch (Exception e) {

        }
        return uniqueId;
    }
}
