package com.ev.dialer.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import androidx.annotation.Nullable;
import java.util.Objects;

public class PhoneNumber implements Parcelable {
    private final I18nPhoneNumberWrapper mI18nPhoneNumber;
    private final int mType;
    @Nullable
    private final String mLabel;
    private boolean mIsPrimary;
    private long mId;
    private int mDataVersion;
    private String mAccountName;
    private String mAccountType;
    public static Creator<PhoneNumber> CREATOR = new Creator<PhoneNumber>() {
        public PhoneNumber createFromParcel(Parcel source) {
            int type = source.readInt();
            String label = source.readString();
            I18nPhoneNumberWrapper i18nPhoneNumberWrapper = (I18nPhoneNumberWrapper)source.readParcelable(I18nPhoneNumberWrapper.class.getClassLoader());
            boolean isPrimary = source.readBoolean();
            long id = source.readLong();
            String accountName = source.readString();
            String accountType = source.readString();
            int dataVersion = source.readInt();
            PhoneNumber phoneNumber = new PhoneNumber(i18nPhoneNumberWrapper, type, label, isPrimary, id, accountName, accountType, dataVersion);
            return phoneNumber;
        }

        public PhoneNumber[] newArray(int size) {
            return new PhoneNumber[size];
        }
    };

    static PhoneNumber fromCursor(Context context, Cursor cursor) {
        int typeColumn = cursor.getColumnIndex("data2");
        int labelColumn = cursor.getColumnIndex("data3");
        int numberColumn = cursor.getColumnIndex("data1");
        int rawDataIdColumn = cursor.getColumnIndex("_id");
        int dataVersionColumn = cursor.getColumnIndex("data_version");
        int isPrimaryColumn = cursor.getColumnIndex("is_super_primary");
        int accountNameColumn = cursor.getColumnIndex("account_name");
        int accountTypeColumn = cursor.getColumnIndex("account_type");
        return newInstance(context, cursor.getString(numberColumn), cursor.getInt(typeColumn), cursor.getString(labelColumn), cursor.getInt(isPrimaryColumn) > 0, cursor.getLong(rawDataIdColumn), cursor.getString(accountNameColumn), cursor.getString(accountTypeColumn), cursor.getInt(dataVersionColumn));
    }

    public static PhoneNumber newInstance(Context context, String rawNumber, int type, @Nullable String label, boolean isPrimary, long id, String accountName, String accountType, int dataVersion) {
        I18nPhoneNumberWrapper i18nPhoneNumber = I18nPhoneNumberWrapper.Factory.INSTANCE.get(context, rawNumber);
        return new PhoneNumber(i18nPhoneNumber, type, label, isPrimary, id, accountName, accountType, dataVersion);
    }

    public PhoneNumber(I18nPhoneNumberWrapper i18nNumber, int type, @Nullable String label, boolean isPrimary, long id, String accountName, String accountType, int dataVersion) {
        this.mI18nPhoneNumber = i18nNumber;
        this.mType = type;
        this.mLabel = label;
        this.mIsPrimary = isPrimary;
        this.mId = id;
        this.mAccountName = accountName;
        this.mAccountType = accountType;
        this.mDataVersion = dataVersion;
    }

    public boolean equals(Object obj) {
        return obj instanceof PhoneNumber && ((PhoneNumber)obj).mType == this.mType && Objects.equals(((PhoneNumber)obj).mLabel, this.mLabel) && this.mI18nPhoneNumber.equals(((PhoneNumber)obj).mI18nPhoneNumber);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.mI18nPhoneNumber, this.mType, this.mLabel});
    }

    public boolean isPrimary() {
        return this.mIsPrimary;
    }

    public CharSequence getReadableLabel(Resources res) {
        return Phone.getTypeLabel(res, this.mType, this.mLabel);
    }

    public String getNumber() {
        return this.mI18nPhoneNumber.getNumber();
    }

    public String getRawNumber() {
        return this.mI18nPhoneNumber.getRawNumber();
    }

    public I18nPhoneNumberWrapper getI18nPhoneNumberWrapper() {
        return this.mI18nPhoneNumber;
    }

    public int getType() {
        return this.mType;
    }

    public long getId() {
        return this.mId;
    }

    @Nullable
    public String getAccountName() {
        return this.mAccountName;
    }

    @Nullable
    public String getAccountType() {
        return this.mAccountType;
    }

    public PhoneNumber merge(PhoneNumber phoneNumber) {
        if (this.equals(phoneNumber) && this.mDataVersion < phoneNumber.mDataVersion) {
            this.mDataVersion = phoneNumber.mDataVersion;
            this.mId = phoneNumber.mId;
            this.mIsPrimary |= phoneNumber.mIsPrimary;
            this.mAccountName = phoneNumber.mAccountName;
            this.mAccountType = phoneNumber.mAccountType;
        }

        return this;
    }

    @Nullable
    public String getLabel() {
        return this.mLabel;
    }

    public String toString() {
        return this.getNumber() + " " + this.mLabel;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeString(this.mLabel);
        dest.writeParcelable(this.mI18nPhoneNumber, flags);
        dest.writeBoolean(this.mIsPrimary);
        dest.writeLong(this.mId);
        dest.writeString(this.mAccountName);
        dest.writeString(this.mAccountType);
        dest.writeInt(this.mDataVersion);
    }

}
