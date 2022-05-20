package com.ev.dialer;

import android.app.Application;
import android.content.Intent;

import com.ev.dialer.btManager.BtManager;
import com.ev.dialer.notification.InCallNotificationController;
import com.ev.dialer.phonebook.common.InMemoryPhoneBook;
import com.ev.dialer.telecom.UiCallManager;


//import androidx.multidex.MultiDex;

public class BTBookApplication extends Application {
    private final String TAG = BTBookApplication.class.getSimpleName();
    public static BTBookApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
		startService(new Intent(this, BTBookService.class)); //启动BTBookService
        BtManager.getInstance(this);
        InMemoryPhoneBook.init(this);
        UiCallManager.init(this);
        InCallNotificationController.init(this);
    }

}
