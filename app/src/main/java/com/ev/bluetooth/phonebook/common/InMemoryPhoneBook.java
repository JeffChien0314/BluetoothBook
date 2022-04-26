package com.ev.bluetooth.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class InMemoryPhoneBook implements Observer<List<Contact>> {
    private static final String TAG = InMemoryPhoneBook.class.getName();
    private static InMemoryPhoneBook sInMemoryPhoneBook;
    private final Context mContext;
    private final AsyncQueryLiveData<List<Contact>> mContactListAsyncQueryLiveData;
    private final Map<I18nPhoneNumberWrapper, Contact> mPhoneNumberContactMap = new HashMap();
    private boolean mIsLoaded = false;

    public static InMemoryPhoneBook init(Context context) {
        if (sInMemoryPhoneBook == null) {
            sInMemoryPhoneBook = new InMemoryPhoneBook(context);
            sInMemoryPhoneBook.onInit();
        }

        return get();
    }

    public static InMemoryPhoneBook get() {
        if (sInMemoryPhoneBook != null) {
            return sInMemoryPhoneBook;
        } else {
            throw new IllegalStateException("Call init before get InMemoryPhoneBook");
        }
    }

    public static void tearDown() {
        sInMemoryPhoneBook.onTearDown();
        sInMemoryPhoneBook = null;
    }

    private InMemoryPhoneBook(Context context) {
        this.mContext = context;
        QueryParam contactListQueryParam = new QueryParam(Data.CONTENT_URI, (String[]) null, "mimetype = ?", new String[]{"vnd.android.cursor.item/phone_v2"}, "display_name ASC ");
        this.mContactListAsyncQueryLiveData = new AsyncQueryLiveData<List<Contact>>(this.mContext, QueryParam.of(contactListQueryParam)) {
            protected List<Contact> convertToEntity(Cursor cursor) {
                Log.i(TAG, "convertToEntity: cursor=" + cursor);
                return InMemoryPhoneBook.this.onCursorLoaded(cursor);
            }
        };
    }

    private void onInit() {
        this.mContactListAsyncQueryLiveData.observeForever(this);
    }

    private void onTearDown() {
        this.mContactListAsyncQueryLiveData.removeObserver(this);
    }

    public boolean isLoaded() {
        return this.mIsLoaded;
    }

    public LiveData<List<Contact>> getContactsLiveData() {
        return this.mContactListAsyncQueryLiveData;
    }

    @Nullable
    public Contact lookupContactEntry(String phoneNumber) {
        Log.v(TAG, String.format("lookupContactEntry: %s", phoneNumber));
        if (!this.isLoaded()) {
            Log.w(TAG, "looking up a contact while loading.");
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            Log.w(TAG, "looking up an empty phone number.");
            return null;
        } else {
            I18nPhoneNumberWrapper i18nPhoneNumber = I18nPhoneNumberWrapper.Factory.INSTANCE.get(this.mContext, phoneNumber);
            return (Contact) this.mPhoneNumberContactMap.get(i18nPhoneNumber);
        }
    }

    private List<Contact> onCursorLoaded(Cursor cursor) {
        Map<String, Contact> result = new LinkedHashMap();
        ArrayList contacts = new ArrayList();

        while (cursor.moveToNext()) {
            Contact contact = Contact.fromCursor(this.mContext, cursor);
            String lookupKey = contact.getLookupKey();
            if (result.containsKey(lookupKey)) {
                Contact existingContact = (Contact) result.get(lookupKey);
                existingContact.merge(contact);
            } else {
                result.put(lookupKey, contact);
            }
        }

        contacts.addAll(result.values());
        this.mPhoneNumberContactMap.clear();
        Iterator var8 = contacts.iterator();

        while (var8.hasNext()) {
            Contact contact = (Contact) var8.next();
            Iterator var10 = contact.getNumbers().iterator();

            while (var10.hasNext()) {
                PhoneNumber phoneNumber = (PhoneNumber) var10.next();
                this.mPhoneNumberContactMap.put(phoneNumber.getI18nPhoneNumberWrapper(), contact);
            }
        }

        return contacts;
    }

    public void onChanged(List<Contact> contacts) {
        for (Contact contact : contacts) {
            Log.i(TAG, "onChanged: contact=" + contact.getAltDisplayName());
            Log.i(TAG, "onChanged: getDisplayName=" + contact.getDisplayName());
            Log.i(TAG, "onChanged: getAvatarUri=" + contact.getAvatarUri());
            Log.i(TAG, "onChanged: getId=" + contact.getId());
            Log.i(TAG, "onChanged: getLookupKey=" + contact.getLookupKey());
            Log.i(TAG, "onChanged: getLookupKey=" + contact.getLookupKey());
            Log.i(TAG, "onChanged: getNumbers=" + contact.getNumbers());
            for (PhoneNumber number : contact.getNumbers()) {
                Log.i(TAG, "onChanged: number=" + number);
            }
            Log.i(TAG, "onChanged: contact=" + contact.getAltDisplayName());
        }
        Log.d(TAG, "Contacts loaded:" + (contacts == null ? 0 : contacts.size()));
        this.mIsLoaded = true;
    }
}
