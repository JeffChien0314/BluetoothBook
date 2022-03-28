package com.ev.bluetooth.phonebook.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.vcard.VCardEntry;
import com.ev.bluetooth.phonebook.BTBookApplication;
import com.ev.bluetooth.phonebook.R;
import com.ev.bluetooth.phonebook.constants.Constants;
import com.ev.bluetooth.phonebook.service.BluetoothContactsService;
import com.ev.bluetooth.phonebook.service.BluetoothRecentsService;
import com.ev.bluetooth.phonebook.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TabCategoryView implements View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = TabCategoryView.class.getSimpleName();
    private AppCompatActivity mActivity;
    private Button mRecents, mContacts, mMessages, mKeypad;
    public BluetoothRecentsService mRecentsService;
    public BluetoothContactsService mContactsService;
    private List<VCardEntry> vCardEntryList;
    private static final String fragmentTag = "FragmentTag";
    private Dialog dialog;
    private HashMap<String,List<VCardEntry>> listHashMap = new HashMap<>();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d(TAG, "onReceive, action:" + action);
            switch (action) {
                case Constants.PBAP_RECENTS_READY:
                    vCardEntryList = mRecentsService.getVCardEntryList();
                    listHashMap.put("RECENTS",vCardEntryList);
                    showRecentsList();
                    hideProcessDialog();
                    break;
                case Constants.PBAP_CONTACTS_READY:
                    vCardEntryList = mContactsService.getVCardEntryList();
                    listHashMap.put("CONTACTS",vCardEntryList);
                    showContactsList();
                    hideProcessDialog();
                    break;
            }
        }
    };

    public TabCategoryView(AppCompatActivity mActivity) {
        this.mActivity = mActivity;

        initLeftView();
        requestRecentsData();
        setListener();
        if(Constants.IS_DEBUG) showRecentsList();
    }

    private void initLeftView() {
        mRecents = mActivity.findViewById(R.id.recents);
        mRecents.setSelected(true);
        mContacts = mActivity.findViewById(R.id.contacts);
        mKeypad = mActivity.findViewById(R.id.keypad);
        mMessages = mActivity.findViewById(R.id.messages);

        setDrawables(mRecents, R.drawable.selector_icon_tab_recents_btn);
        setDrawables(mContacts, R.drawable.selector_icon_tab_contacts_btn);
        setDrawables(mKeypad, R.drawable.selector_icon_tab_keypad_btn);
        setDrawables(mMessages, R.drawable.selector_icon_tab_messages_btn);
    }

    private void setDrawables(Button button, int drawableID) {
        Drawable drawable = mActivity.getResources().getDrawable(drawableID);
        drawable.setBounds(0, 0, 36, 36);//第一0是距左边距离，第二0是距上边距离，36、36分别是长宽
        button.setCompoundDrawables(drawable, null, null, null);
    }

    private void setListener() {
        mRecents.setOnClickListener(this);
        mRecents.setOnFocusChangeListener(this);
        mContacts.setOnClickListener(this);
        mContacts.setOnFocusChangeListener(this);
        mMessages.setOnClickListener(this);
        mMessages.setOnFocusChangeListener(this);
        mKeypad.setOnClickListener(this);
        mKeypad.setOnFocusChangeListener(this);
    }

    private void requestRecentsData() {
        if(Constants.IS_DEBUG) return;
        Intent serviceIntent = new Intent(mActivity, BluetoothRecentsService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.d(TAG, "BluetoothRecentsService, init the Service");
                mRecentsService = ((BluetoothRecentsService.BluetoothServiceBinder) service).getService();
                mRecentsService.connectPbapClient(BTBookApplication.deviceMac);
                showProcessDialog();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRecentsService = null;
            }
        };
        mActivity.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private void requestContactsData() {
        if(Constants.IS_DEBUG) return;
        Intent serviceIntent = new Intent(mActivity, BluetoothContactsService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.d(TAG, "BluetoothContactsService, init the Service");
                mContactsService = ((BluetoothContactsService.BluetoothServiceBinder) service).getService();
                mContactsService.connectPbapClient(BTBookApplication.deviceMac);
                showProcessDialog();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRecentsService = null;
            }
        };
        mActivity.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.PBAP_RECENTS_READY);
        filter.addAction(Constants.PBAP_CONTACTS_READY);
        mActivity.registerReceiver(broadcastReceiver, filter);
    }

    public void unRegisterReceiver() {
        if (broadcastReceiver != null) {
            mActivity.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.recents:
                if (hasFocus) {
                    clearSelectedView();
                    mRecents.setSelected(true);
                }
                break;
            case R.id.contacts:
                if (hasFocus) {
                    clearSelectedView();
                    mContacts.setSelected(true);
                }
                break;
            case R.id.messages:
                if (hasFocus) {
                    clearSelectedView();
                    mMessages.setSelected(true);
                }
                break;
            case R.id.keypad:
                if (hasFocus) {
                    clearSelectedView();
                    mKeypad.setSelected(true);
                }
                break;
        }
    }

    private void clearSelectedView() {
        mRecents.setSelected(false);
        mContacts.setSelected(false);
        mMessages.setSelected(false);
        mKeypad.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recents:
                clearSelectedView();
                mRecents.setSelected(true);
                hideFragment();
                if(listHashMap.containsKey("RECENTS")){
                    vCardEntryList = listHashMap.get("RECENTS");
                    showRecentsList();
                }else{
                    requestRecentsData();
                }
                break;
            case R.id.contacts:
                clearSelectedView();
                mContacts.setSelected(true);
                hideFragment();
                if(listHashMap.containsKey("CONTACTS")){
                    vCardEntryList = listHashMap.get("CONTACTS");
                    showContactsList();
                }else{
                    requestContactsData();
                }
                break;
            case R.id.messages:
                clearSelectedView();
                mMessages.setSelected(true);
                showMessagesList();
                break;
            case R.id.keypad:
                clearSelectedView();
                mKeypad.setSelected(true);
                showKeypadList();
                break;
        }
    }

    private void showRecentsList() {
        if(Constants.IS_DEBUG) {
            vCardEntryList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("A-person");
                vCardEntry.getCallDate().setCallDatetime("20220215T150000");
                Collection<String> collection = new ArrayList<>();
                collection.add("RECEIVED");
                vCardEntry.getCallDate().setTypeCollection(collection);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 3; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("B-person");
                vCardEntry.getCallDate().setCallDatetime("20220215T100000");
                Collection<String> collection = new ArrayList<>();
                collection.add("DIALED");
                vCardEntry.getCallDate().setTypeCollection(collection);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 3; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("C-person-" + i);
                vCardEntry.getCallDate().setCallDatetime("20220214T140000");
                Collection<String> collection = new ArrayList<>();
                collection.add("MISSED");
                vCardEntry.getCallDate().setTypeCollection(collection);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 4; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("D-person");
                vCardEntry.getCallDate().setCallDatetime("20220215T100000");
                Collection<String> collection = new ArrayList<>();
                collection.add("DIALED");
                vCardEntry.getCallDate().setTypeCollection(collection);
                vCardEntryList.add(vCardEntry);
            }
        }
        FragmentManager fManager = mActivity.getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        Fragment fragment = fManager.findFragmentByTag(fragmentTag);
        RecentListFragment recentListFragment = RecentListFragment.newInstance(vCardEntryList);
        if (fragment == null) {
            fTransaction.add(R.id.content_layout, recentListFragment, fragmentTag);
        } else {
            fTransaction.replace(R.id.content_layout, recentListFragment, fragmentTag);
        }
        fTransaction.commitAllowingStateLoss();
    }

    private void showContactsList() {
        FragmentManager fManager = mActivity.getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        Fragment fragment = fManager.findFragmentByTag(fragmentTag);
        ContactListFragment contactListFragment = ContactListFragment.newInstance(vCardEntryList);
        if (fragment == null) {
            fTransaction.add(R.id.content_layout, contactListFragment, fragmentTag);
        } else {
            fTransaction.replace(R.id.content_layout, contactListFragment, fragmentTag);
        }
        fTransaction.commitAllowingStateLoss();
    }

    private void showMessagesList() {

    }

    private void showKeypadList() {

    }

    private void hideFragment(){
        FragmentManager fManager = mActivity.getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        Fragment fragment = fManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fTransaction.hide(fragment);
            fTransaction.commitAllowingStateLoss();
        }
    }

    //show the progress dialog
    public void showProcessDialog() {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new Dialog(mActivity, R.style.TransparentDialog);
            dialog.setContentView(R.layout.layout_data_loading_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            if (mActivity != null && !mActivity.isFinishing()) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        }
    }

    //hide the progress dialog
    public void hideProcessDialog() {
        if (mActivity != null && !mActivity.isFinishing()) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }
}
