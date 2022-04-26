package com.ev.bluetooth.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ev.bluetooth.phonebook.utils.TelecomUtils;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.MatchType;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class I18nPhoneNumberWrapper implements Parcelable {
    private final PhoneNumber mI18nPhoneNumber;
    private final String mRawNumber;
    private final String mNumber;
    public static Creator<I18nPhoneNumberWrapper> CREATOR = new Creator<I18nPhoneNumberWrapper>() {
        public I18nPhoneNumberWrapper createFromParcel(Parcel source) {
            String rawNumber = source.readString();
            PhoneNumber i18nPhoneNumber = (PhoneNumber)source.readSerializable();
            return new I18nPhoneNumberWrapper(rawNumber, i18nPhoneNumber);
        }

        public I18nPhoneNumberWrapper[] newArray(int size) {
            return new I18nPhoneNumberWrapper[size];
        }
    };

    private I18nPhoneNumberWrapper(String rawNumber, @Nullable PhoneNumber i18nPhoneNumber) {
        this.mI18nPhoneNumber = i18nPhoneNumber;
        this.mRawNumber = rawNumber;
        this.mNumber = i18nPhoneNumber == null ? rawNumber : PhoneNumberUtil.getInstance().format(i18nPhoneNumber, PhoneNumberFormat.INTERNATIONAL);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof I18nPhoneNumberWrapper)) {
            return false;
        } else {
            I18nPhoneNumberWrapper other = (I18nPhoneNumberWrapper)obj;
            if (this.mI18nPhoneNumber != null && other.mI18nPhoneNumber != null) {
                MatchType matchType = PhoneNumberUtil.getInstance().isNumberMatch(this.mI18nPhoneNumber, other.mI18nPhoneNumber);
                return matchType == MatchType.EXACT_MATCH || matchType == MatchType.NSN_MATCH;
            } else {
                return this.mI18nPhoneNumber == null && other.mI18nPhoneNumber == null ? this.mRawNumber.equals(other.mRawNumber) : false;
            }
        }
    }

    public int hashCode() {
        return this.mI18nPhoneNumber == null ? Objects.hashCode(this.mRawNumber) : Objects.hash(new Object[]{this.mI18nPhoneNumber});
    }

    public String getRawNumber() {
        return this.mRawNumber;
    }

    public String getNumber() {
        return this.mNumber;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mRawNumber);
        dest.writeSerializable(this.mI18nPhoneNumber);
    }

    public static enum Factory {
        INSTANCE;

        private final Map<String, WeakReference<I18nPhoneNumberWrapper>> mRecycledPool = new ConcurrentHashMap();

        private Factory() {
        }

        public I18nPhoneNumberWrapper get(@NonNull Context context, @NonNull String rawNumber) {
            Iterator var3 = this.mRecycledPool.keySet().iterator();

            while(var3.hasNext()) {
                String key = (String)var3.next();
                if (((WeakReference)this.mRecycledPool.get(key)).get() == null) {
                    this.mRecycledPool.remove(key);
                }
            }

            WeakReference<I18nPhoneNumberWrapper> existingReference = (WeakReference)this.mRecycledPool.get(rawNumber);
            I18nPhoneNumberWrapper i18nPhoneNumberWrapper = existingReference == null ? null : (I18nPhoneNumberWrapper)existingReference.get();
            if (i18nPhoneNumberWrapper == null) {
                i18nPhoneNumberWrapper = this.create(context, rawNumber);
                this.mRecycledPool.put(rawNumber, new WeakReference(i18nPhoneNumberWrapper));
                return i18nPhoneNumberWrapper;
            } else {
                return i18nPhoneNumberWrapper;
            }
        }

        private I18nPhoneNumberWrapper create(@NonNull Context context, @NonNull String rawNumber) {
            PhoneNumber i18nPhoneNumber = TelecomUtils.createI18nPhoneNumber(context, rawNumber);
            return new I18nPhoneNumberWrapper(rawNumber, i18nPhoneNumber);
        }
    }
}
