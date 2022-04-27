package com.ev.dialer.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.database.Cursor;
import android.telecom.Log;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PhoneCallLog {
    private static final String TAG = "CD.PhoneCallLog";
    private long mId;
    private String mPhoneNumberString;
    private I18nPhoneNumberWrapper mI18nPhoneNumberWrapper;
    private List<PhoneCallLog.Record> mCallRecords = new ArrayList();

    public PhoneCallLog() {
    }

    public static PhoneCallLog fromCursor(Context context, Cursor cursor) {
        int idColumn = cursor.getColumnIndex("_id");
        int numberColumn = cursor.getColumnIndex("number");
        int dateColumn = cursor.getColumnIndex("date");
        int callTypeColumn = cursor.getColumnIndex("type");
        PhoneCallLog phoneCallLog = new PhoneCallLog();
        phoneCallLog.mId = cursor.getLong(idColumn);
        phoneCallLog.mPhoneNumberString = cursor.getString(numberColumn);
        phoneCallLog.mI18nPhoneNumberWrapper = I18nPhoneNumberWrapper.Factory.INSTANCE.get(context, phoneCallLog.mPhoneNumberString);
        PhoneCallLog.Record record = new PhoneCallLog.Record(cursor.getLong(dateColumn), cursor.getInt(callTypeColumn));
        phoneCallLog.mCallRecords.add(record);
        return phoneCallLog;
    }

    public String getPhoneNumberString() {
        return this.mPhoneNumberString;
    }

    public long getPhoneLogId() {
        return this.mId;
    }

    public long getLastCallEndTimestamp() {
        return !this.mCallRecords.isEmpty() ? ((PhoneCallLog.Record)this.mCallRecords.get(0)).getCallEndTimestamp() : -1L;
    }

    public List<PhoneCallLog.Record> getAllCallRecords() {
        return new ArrayList(this.mCallRecords);
    }

    public boolean merge(@NonNull PhoneCallLog phoneCallLog) {
        if (this.equals(phoneCallLog)) {
            this.mCallRecords.addAll(phoneCallLog.mCallRecords);
            Collections.sort(this.mCallRecords);
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(Object object) {
        if (object instanceof PhoneCallLog) {
            if (TextUtils.isEmpty(this.mPhoneNumberString)) {
                return this.mId == ((PhoneCallLog)object).mId;
            } else {
                return this.mI18nPhoneNumberWrapper.equals(((PhoneCallLog)object).mI18nPhoneNumberWrapper);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return TextUtils.isEmpty(this.mPhoneNumberString) ? Long.hashCode(this.mId) : Objects.hashCode(this.mI18nPhoneNumberWrapper);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PhoneNumber: ");
        sb.append(Log.pii(this.mPhoneNumberString));
        sb.append(" CallLog: ");
        sb.append(this.mCallRecords.size());
        return sb.toString();
    }

    public static class Record implements Comparable<PhoneCallLog.Record> {
        private final long mCallEndTimestamp;
        private final int mCallType;

        public Record(long callEndTimestamp, int callType) {
            this.mCallEndTimestamp = callEndTimestamp;
            this.mCallType = callType;
        }

        public long getCallEndTimestamp() {
            return this.mCallEndTimestamp;
        }

        public int getCallType() {
            return this.mCallType;
        }

        public int compareTo(PhoneCallLog.Record otherRecord) {
            return (int)(otherRecord.mCallEndTimestamp - this.mCallEndTimestamp);
        }
    }
}
