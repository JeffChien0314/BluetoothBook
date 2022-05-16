package com.ev.dialer.phonebook.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.vcard.VCardEntry;
import com.ev.dialer.Constants;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.service.BluetoothContactsService;
import com.ev.dialer.phonebook.service.BluetoothRecentsService;
import com.ev.dialer.phonebook.ui.calllog.RecentListFragment;
import com.ev.dialer.phonebook.ui.contact.ContactListFragment;
import com.ev.dialer.phonebook.ui.dialpad.DialpadFragment;
import com.ev.dialer.phonebook.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static android.security.KeyStore.getApplicationContext;

public class TabCategoryView implements View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = TabCategoryView.class.getSimpleName();
    private AppCompatActivity mActivity;
    private Button mRecents, mContacts, mMessages, mKeypad;
    public BluetoothRecentsService mRecentsService;
    public BluetoothContactsService mContactsService;
    private List<VCardEntry> vCardEntryList;
    private static final String fragmentTag = "FragmentTag";
    private Dialog dialog;
    //  private HashMap<String, List<VCardEntry>> listHashMap = new HashMap<>();
    public static final int MESSAGE_SHOWDIALOG = 0;
    public static final int MESSAGE_HIDEDIALOG = 1;

    public final int MESSAGE_SHOWRECENTLIST = 2;
    public final int MESSAGE_SHOWCONTACTLIST = 3;
    public static final int MESSAGE_HIDE_FRAGMENT = 4;
    public final int MESSAGE_SYNC_TIMEOUT = 5 * 1000;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SHOWDIALOG:
                    removeMessages(MESSAGE_HIDEDIALOG);
                  //  sendEmptyMessageDelayed(MESSAGE_HIDEDIALOG, MESSAGE_SYNC_TIMEOUT);
                    break;
                case MESSAGE_HIDEDIALOG:
                    hideProcessDialog();
                    break;
                case MESSAGE_SHOWRECENTLIST:
                   // showProcessDialog();
                    showRecentsList();
                    break;
                case MESSAGE_SHOWCONTACTLIST:
                    showContactsList();
                    break;
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d(TAG, "onReceive, action:" + action);
            switch (action) {
                /*case Constants.PBAP_RECENTS_READY:
                    vCardEntryList = mRecentsService.getVCardEntryList();
                    listHashMap.put("RECENTS", vCardEntryList);
                    mHandler.sendEmptyMessage(MESSAGE_SHOWRECENTLIST);
                    mHandler.sendEmptyMessage(MESSAGE_HIDEDIALOG);
                    break;
                case Constants.PBAP_CONTACTS_READY:
                    vCardEntryList = mContactsService.getVCardEntryList();
                    listHashMap.put("CONTACTS", vCardEntryList);
                    mHandler.sendEmptyMessage(MESSAGE_SHOWCONTACTLIST);
                    mHandler.sendEmptyMessage(MESSAGE_HIDEDIALOG);
                    break;*/
                case TelephonyManager.ACTION_PHONE_STATE_CHANGED:
                    Toast.makeText(getApplicationContext(), "received", Toast.LENGTH_SHORT);
                    // TelephoneManager.ACTION_PHONE_STATE_CHANGED
                    String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                    String inNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);// 来电号码
                    Log.i(TAG, "onReceive: inNumber=" + inNumber);
                    if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {// 电话正在响铃
                        Toast.makeText(context, "动态广播，电话正在振铃", Toast.LENGTH_SHORT).show();
                    } else if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {// 挂断
                        Toast.makeText(context, "动态广播，电话已挂断", Toast.LENGTH_SHORT).show();
                    } else if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {// 摘机，通话状态
                        Toast.makeText(context, "动态广播，电话正在通话", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "动态广播，通话状态改变，没有TelephonyManager.EXTRA_STATE", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;

    public TabCategoryView(AppCompatActivity mActivity) {
     /*   Log.i(TAG, "TabCategoryView: getRelativeTime(1650628074000L)=" + getRelativeTime(1650507974000L));
        Log.i(TAG, "TabCategoryView: getString2Date=" + EVDateUtils.getInstance(mActivity).getDate2String(1650507974000L, "yyyy/MM/dd HH:mm:ss"));
        Log.i(TAG, "TabCategoryView: DateUtils.isToday(1650507974000L)=" + DateUtils.isToday(1650690027767L));
        Log.i(TAG, "TabCategoryView:  EVDateUtils.getInstance(mActivity).dateCompareTo(new Date(System.currentTimeMillis()))" +  EVDateUtils.getInstance(mActivity).dateCompareTo(new Date(1650628084000L-WEEK_IN_MILLIS)));
       // EVDateUtils.getInstance(mActivity).dateCompareTo(new Date(System.currentTimeMillis()));
*/
        this.mActivity = mActivity;
        initLeftView();
        requestRecentsData();
        setListener();
        if (Constants.IS_DEBUG) showRecentsList();
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

    public void sendMessage(int what) {
        mHandler.removeMessages(what);
        mHandler.sendEmptyMessage(what);
    }

    private void requestRecentsData() {
        if (Constants.IS_DEBUG) return;
      //  mHandler.sendEmptyMessage(MESSAGE_SHOWDIALOG);
        sendMessage(MESSAGE_SHOWRECENTLIST);
        //  getDataList();
        /*Intent serviceIntent = new Intent(mActivity, BluetoothRecentsService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.d(TAG, "BluetoothRecentsService, init the Service");
                mRecentsService = ((BluetoothRecentsService.BluetoothServiceBinder) service).getService();

                if (mRecentsService.connectPbapClient()) {
                    mHandler.sendEmptyMessage(MESSAGE_SHOWDIALOG);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRecentsService = null;
            }
        };
        mActivity.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);*/
    }

    private void requestContactsData() {
        if (Constants.IS_DEBUG) return;
        mHandler.sendEmptyMessage(MESSAGE_SHOWDIALOG);
        /*Intent serviceIntent = new Intent(mActivity, BluetoothContactsService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtils.d(TAG, "BluetoothContactsService, init the Service");
                mContactsService = ((BluetoothContactsService.BluetoothServiceBinder) service).getService();
                if (mContactsService.connectPbapClient()) {
                    mHandler.sendEmptyMessage(MESSAGE_SHOWDIALOG);
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRecentsService = null;
            }
        };
        mActivity.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);*/
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.PBAP_RECENTS_READY);
        filter.addAction(Constants.PBAP_CONTACTS_READY);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
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
                sendMessage(MESSAGE_HIDE_FRAGMENT);
                sendMessage(MESSAGE_SHOWRECENTLIST);
                /*if (listHashMap.containsKey("RECENTS")) {
                    vCardEntryList = listHashMap.get("RECENTS");
                    showRecentsList();
                } else {
                    requestRecentsData();
                }*/
                break;
            case R.id.contacts:
                clearSelectedView();
                mContacts.setSelected(true);
            //    hideFragment();
                sendMessage(MESSAGE_HIDE_FRAGMENT);
                sendMessage(MESSAGE_SHOWCONTACTLIST);
                /*  showContactsList();
              if (listHashMap.containsKey("CONTACTS")) {
                    vCardEntryList = listHashMap.get("CONTACTS");
                    showContactsList();
                } else {
                    requestContactsData();
                }*/
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

        if (Constants.IS_DEBUG) {
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
        RecentListFragment recentListFragment = RecentListFragment.newInstance();
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
        ContactListFragment contactListFragment = ContactListFragment.newInstance();
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
        FragmentManager fManager = mActivity.getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        Fragment fragment = fManager.findFragmentByTag(fragmentTag);
        DialpadFragment dialpadFragment = DialpadFragment.newInstance();
        if (fragment == null) {
            fTransaction.add(R.id.content_layout, dialpadFragment, fragmentTag);
        } else {
            fTransaction.replace(R.id.content_layout, dialpadFragment, fragmentTag);
        }
        fTransaction.commitAllowingStateLoss();
    }

    private void hideFragment() {
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
        mHandler.removeMessages(MESSAGE_HIDEDIALOG);
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
