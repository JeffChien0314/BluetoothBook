package com.ev.bluetooth;

import android.app.Application;

import com.ev.bluetooth.phonebook.common.InMemoryPhoneBook;
import com.ev.bluetooth.btManager.BtManager;
import com.ev.bluetooth.telecom.UiCallManager;


//import androidx.multidex.MultiDex;

public class BTBookApplication extends Application {
    private final String TAG = BTBookApplication.class.getSimpleName();
    public static BTBookApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BtManager.getInstance(this);
        InMemoryPhoneBook.init(this);
        UiCallManager.init(this);
    }

}
