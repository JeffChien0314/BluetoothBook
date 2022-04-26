package com.ev.bluetooth.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import android.content.Context;
import android.database.Cursor;
import android.icu.text.Collator;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract.Contacts;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import com.ev.bluetooth.phonebook.utils.TelecomUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Contact implements Parcelable, Comparable<Contact> {
    private static final String TAG = "CD.Contact";
    private static final int TYPE_LETTER = 1;
    private static final int TYPE_DIGIT = 2;
    private static final int TYPE_OTHER = 3;
    private long mId;
    private boolean mIsStarred;
    private int mPinnedPosition;
    private List<PhoneNumber> mPhoneNumbers = new ArrayList();
    private String mDisplayName;
    private String mAltDisplayName;
    private Uri mAvatarThumbnailUri;
    private Uri mAvatarUri;
    private String mLookupKey;
    private boolean mIsVoiceMail;
    private PhoneNumber mPrimaryPhoneNumber;
    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        public Contact createFromParcel(Parcel source) {
            return Contact.fromParcel(source);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public Contact() {
    }

    public static Contact fromCursor(Context context, Cursor cursor) {
        int contactIdColumn = cursor.getColumnIndex("contact_id");
        int starredColumn = cursor.getColumnIndex("starred");
        int pinnedColumn = cursor.getColumnIndex("pinned");
        int displayNameColumn = cursor.getColumnIndex("display_name");
        int altDisplayNameColumn = cursor.getColumnIndex("display_name_alt");
        int avatarUriColumn = cursor.getColumnIndex("photo_uri");
        int avatarThumbnailColumn = cursor.getColumnIndex("photo_thumb_uri");
        int lookupKeyColumn = cursor.getColumnIndex("lookup");
        Contact contact = new Contact();
        contact.mId = cursor.getLong(contactIdColumn);
        contact.mDisplayName = cursor.getString(displayNameColumn);
        contact.mAltDisplayName = cursor.getString(altDisplayNameColumn);
        PhoneNumber number = PhoneNumber.fromCursor(context, cursor);
        contact.mPhoneNumbers.add(number);
        if (number.isPrimary()) {
            contact.mPrimaryPhoneNumber = number;
        }

        contact.mIsStarred = cursor.getInt(starredColumn) > 0;
        contact.mPinnedPosition = cursor.getInt(pinnedColumn);
        contact.mIsVoiceMail = TelecomUtils.isVoicemailNumber(context, number.getNumber());
        String avatarUriStr = cursor.getString(avatarUriColumn);
        contact.mAvatarUri = avatarUriStr == null ? null : Uri.parse(avatarUriStr);
        String avatarThumbnailStringUri = cursor.getString(avatarThumbnailColumn);
        contact.mAvatarThumbnailUri = avatarThumbnailStringUri == null ? null : Uri.parse(avatarThumbnailStringUri);
        String lookUpKey = cursor.getString(lookupKeyColumn);
        if (lookUpKey != null) {
            contact.mLookupKey = lookUpKey;
        } else {
            Log.w("CD.Contact", "Look up key is null. Fallback to use display name");
            contact.mLookupKey = contact.mDisplayName;
        }
        Log.i(TAG, "onChanged: contact=" + contact.getAltDisplayName());
        Log.i(TAG, "onChanged: getDisplayName=" + contact.getDisplayName());
        Log.i(TAG, "onChanged: getAvatarUri=" + contact.getAvatarUri());
        Log.i(TAG, "onChanged: getId=" + contact.getId());
        Log.i(TAG, "onChanged: getLookupKey=" + contact.getLookupKey());
        Log.i(TAG, "onChanged: getLookupKey=" + contact.getLookupKey());
        Log.i(TAG, "onChanged: getNumbers=" + contact.getNumbers());
        for (PhoneNumber number1 : contact.getNumbers()) {
            Log.i(TAG, "onChanged: number=" + number1);
        }
        Log.i(TAG, "onChanged: contact=" + contact.getAltDisplayName());
        return contact;
    }

    public boolean equals(Object obj) {
        return obj instanceof Contact && this.mLookupKey.equals(((Contact)obj).mLookupKey);
    }

    public int hashCode() {
        return this.mLookupKey.hashCode();
    }

    public String toString() {
        return this.mDisplayName + this.mPhoneNumbers;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public String getAltDisplayName() {
        return this.mAltDisplayName;
    }

    public boolean isVoicemail() {
        return this.mIsVoiceMail;
    }

    @Nullable
    public Uri getAvatarUri() {
        return this.mAvatarThumbnailUri != null ? this.mAvatarThumbnailUri : this.mAvatarUri;
    }

    public String getLookupKey() {
        return this.mLookupKey;
    }

    public Uri getLookupUri() {
        return Contacts.getLookupUri(this.mId, this.mLookupKey);
    }

    public List<PhoneNumber> getNumbers() {
        return this.mPhoneNumbers;
    }

    public long getId() {
        return this.mId;
    }

    public boolean isStarred() {
        return this.mIsStarred;
    }

    public int getPinnedPosition() {
        return this.mPinnedPosition;
    }

    public Contact merge(Contact contact) {
        if (this.equals(contact)) {
            Iterator var2 = contact.mPhoneNumbers.iterator();

            while(var2.hasNext()) {
                PhoneNumber phoneNumber = (PhoneNumber)var2.next();
                int indexOfPhoneNumber = this.mPhoneNumbers.indexOf(phoneNumber);
                if (indexOfPhoneNumber < 0) {
                    this.mPhoneNumbers.add(phoneNumber);
                } else {
                    PhoneNumber existingPhoneNumber = (PhoneNumber)this.mPhoneNumbers.get(indexOfPhoneNumber);
                    existingPhoneNumber.merge(phoneNumber);
                }
            }

            if (contact.mPrimaryPhoneNumber != null) {
                this.mPrimaryPhoneNumber = contact.mPrimaryPhoneNumber.merge(this.mPrimaryPhoneNumber);
            }
        }

        return this;
    }

    @Nullable
    public PhoneNumber getPhoneNumber(String number) {
        Iterator var2 = this.mPhoneNumbers.iterator();

        PhoneNumber phoneNumber;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            phoneNumber = (PhoneNumber)var2.next();
        } while(!PhoneNumberUtils.compare(phoneNumber.getNumber(), number));

        return phoneNumber;
    }

    public PhoneNumber getPrimaryPhoneNumber() {
        return this.mPrimaryPhoneNumber;
    }

    public boolean hasPrimaryPhoneNumber() {
        return this.mPrimaryPhoneNumber != null;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeBoolean(this.mIsStarred);
        dest.writeInt(this.mPinnedPosition);
        dest.writeInt(this.mPhoneNumbers.size());
        Iterator var3 = this.mPhoneNumbers.iterator();

        while(var3.hasNext()) {
            PhoneNumber phoneNumber = (PhoneNumber)var3.next();
            dest.writeParcelable(phoneNumber, flags);
        }

        dest.writeString(this.mDisplayName);
        dest.writeString(this.mAltDisplayName);
        dest.writeParcelable(this.mAvatarThumbnailUri, 0);
        dest.writeParcelable(this.mAvatarUri, 0);
        dest.writeString(this.mLookupKey);
        dest.writeBoolean(this.mIsVoiceMail);
    }

    private static Contact fromParcel(Parcel source) {
        Contact contact = new Contact();
        contact.mId = source.readLong();
        contact.mIsStarred = source.readBoolean();
        contact.mPinnedPosition = source.readInt();
        int phoneNumberListLength = source.readInt();
        contact.mPhoneNumbers = new ArrayList();

        for(int i = 0; i < phoneNumberListLength; ++i) {
            PhoneNumber phoneNumber = (PhoneNumber)source.readParcelable(PhoneNumber.class.getClassLoader());
            contact.mPhoneNumbers.add(phoneNumber);
            if (phoneNumber.isPrimary()) {
                contact.mPrimaryPhoneNumber = phoneNumber;
            }
        }

        contact.mDisplayName = source.readString();
        contact.mAltDisplayName = source.readString();
        contact.mAvatarThumbnailUri = (Uri)source.readParcelable(Uri.class.getClassLoader());
        contact.mAvatarUri = (Uri)source.readParcelable(Uri.class.getClassLoader());
        contact.mLookupKey = source.readString();
        contact.mIsVoiceMail = source.readBoolean();
        return contact;
    }

    public int compareTo(Contact otherContact) {
        return this.compareByDisplayName(otherContact);
    }

    public int compareByDisplayName(@NonNull Contact otherContact) {
        return this.compareNames(this.mDisplayName, otherContact.getDisplayName());
    }

    public int compareByAltDisplayName(@NonNull Contact otherContact) {
        return this.compareNames(this.mAltDisplayName, otherContact.getAltDisplayName());
    }

    private int compareNames(String name, String otherName) {
        int type = getNameType(name);
        int otherType = getNameType(otherName);
        if (type != otherType) {
            return Integer.compare(type, otherType);
        } else {
            Collator collator = Collator.getInstance();
            return collator.compare(name == null ? "" : name, otherName == null ? "" : otherName);
        }
    }

    private static int getNameType(String displayName) {
        if (!TextUtils.isEmpty(displayName)) {
            if (Character.isLetter(displayName.charAt(0))) {
                return 1;
            }

            if (Character.isDigit(displayName.charAt(0))) {
                return 2;
            }
        }

        return 3;
    }
}
