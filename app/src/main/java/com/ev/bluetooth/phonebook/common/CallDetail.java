package com.ev.bluetooth.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.net.Uri;
import android.telecom.DisconnectCause;
import android.telecom.GatewayInfo;
import android.telecom.Call.Details;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CallDetail {
    private final String mNumber;
    private final CharSequence mDisconnectCause;
    private final Uri mGatewayInfoOriginalAddress;
    private final long mConnectTimeMillis;

    private CallDetail(String number, CharSequence disconnectCause, Uri gatewayInfoOriginalAddress, long connectTimeMillis) {
        this.mNumber = number;
        this.mDisconnectCause = disconnectCause;
        this.mGatewayInfoOriginalAddress = gatewayInfoOriginalAddress;
        this.mConnectTimeMillis = connectTimeMillis;
    }

    public static CallDetail fromTelecomCallDetail(@Nullable Details callDetail) {
        return new CallDetail(getNumber(callDetail), getDisconnectCause(callDetail), getGatewayInfoOriginalAddress(callDetail), getConnectTimeMillis(callDetail));
    }

    @NonNull
    public String getNumber() {
        return this.mNumber;
    }

    @Nullable
    public CharSequence getDisconnectCause() {
        return this.mDisconnectCause;
    }

    @Nullable
    public Uri getGatewayInfoOriginalAddress() {
        return this.mGatewayInfoOriginalAddress;
    }

    public long getConnectTimeMillis() {
        return this.mConnectTimeMillis;
    }

    private static String getNumber(Details callDetail) {
        String number = "";
        if (callDetail == null) {
            return number;
        } else {
            GatewayInfo gatewayInfo = callDetail.getGatewayInfo();
            if (gatewayInfo != null) {
                number = gatewayInfo.getOriginalAddress().getSchemeSpecificPart();
            } else if (callDetail.getHandle() != null) {
                number = callDetail.getHandle().getSchemeSpecificPart();
            }

            return number;
        }
    }

    @Nullable
    private static CharSequence getDisconnectCause(Details callDetail) {
        DisconnectCause cause = callDetail == null ? null : callDetail.getDisconnectCause();
        return cause == null ? null : cause.getLabel();
    }

    @Nullable
    private static Uri getGatewayInfoOriginalAddress(Details callDetail) {
        return callDetail != null && callDetail.getGatewayInfo() != null ? callDetail.getGatewayInfo().getOriginalAddress() : null;
    }

    private static long getConnectTimeMillis(Details callDetail) {
        return callDetail != null ? callDetail.getConnectTimeMillis() : 0L;
    }
}
