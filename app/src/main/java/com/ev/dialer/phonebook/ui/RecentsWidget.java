package com.ev.dialer.phonebook.ui;

import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.ev.dialer.BTBookService;
import com.ev.dialer.livedata.CallHistoryLiveData;
import com.ev.dialer.log.L;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.ui.calllog.CallHistoryViewModel;
import com.ev.dialer.phonebook.ui.common.entity.UiCallLog;
import com.ev.dialer.phonebook.utils.EVDateUtils;
import com.ev.dialer.telecom.UiCallManager;

import java.util.ArrayList;
import java.util.List;

public class RecentsWidget extends AppWidgetProvider implements ViewModelStoreOwner {

    private static final String TAG = RecentsWidget.class.getSimpleName();

    private Context mContext;
    private ViewModelStore mViewModelStore;
    private ViewModelProvider.Factory factory;

    private List<UiCallLog> mUiCallLogs = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: " + intent.getAction());
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) { //操作widget
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            switch (buttonId) {
                case R.id.widget_open:
                    Intent intentShow = new Intent();
                    intentShow.setClass(context, PhoneBookActivity.class);
                    intentShow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentShow);
                    break;
                case R.id.recent_item_layout:
                    Log.d(TAG, "onReceive: " + intent.getStringExtra("telNum"));
                    if (intent.getStringExtra("telNum") != null) {
                        UiCallManager.get().placeCall(intent.getStringExtra("telNum"));
                    }
                    initRecentsData(context);
                    break;
            }
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate: ");
        mContext = context;
        if (!BTBookService.isAlive) {
            context.startService(new Intent(context, BTBookService.class));
        }
        initRecentsData(context);
        /*for test*/
        /*List<RemoteViews> recentItemViews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RemoteViews recentItem = new RemoteViews(context.getPackageName(), R.layout.recents_item_widget_layout);
            if (i == 0) {
                recentItem.setOnClickPendingIntent(R.id.recent_item_layout, getPendingIntent(context, R.id.recent_item_layout, "aaa"));
            } else if (i == 1) {
                recentItem.setOnClickPendingIntent(R.id.recent_item_layout, getPendingIntent(context, R.id.recent_item_layout, "bbb"));
            } else {
                recentItem.setOnClickPendingIntent(R.id.recent_item_layout, getPendingIntent(context, R.id.recent_item_layout, "ccc"));
            }
            recentItem.setTextViewText(R.id.recent_name, "AAABBB");
            recentItemViews.add(recentItem);
        }
        pushUpdate(context, AppWidgetManager.getInstance(context), recentItemViews);*/
        /*for test*/
    }

    private void pushUpdate(Context context, AppWidgetManager appWidgetManager, List<RemoteViews> remoteViewsList) {
        Log.d(TAG, "pushUpdate: ");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_recents_widget);
        remoteViews.setOnClickPendingIntent(R.id.widget_open, getPendingIntent(context, R.id.widget_open, null));
        for (int i = 0; i < remoteViewsList.size(); i++) {
            remoteViews.addView(R.id.recents_list, remoteViewsList.get(i));
        }
        ComponentName componentName = new ComponentName(context, RecentsWidget.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    private PendingIntent getPendingIntent(Context context, int buttonId, String number) {
        Intent intent = new Intent();
        intent.setClass(context, RecentsWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("" + buttonId));
        intent.putExtra("telNum", number);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if ((Application) mContext.getApplicationContext() == null) {
            throw new IllegalStateException("Your activity is not yet attached to the "
                    + "Application instance. You can't request ViewModel before onCreate call...");
        }
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
        return mViewModelStore;
    }

    private void initRecentsData(Context context) {
        Application application = (Application) context.getApplicationContext();
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        CallHistoryViewModel viewModel = new ViewModelProvider(getViewModelStore(), factory).get(CallHistoryViewModel.class);
        viewModel.getCallHistory().observe(BTBookService.getMyLifecycleOwner(), uiCallLogs -> {
            if (uiCallLogs.isLoading()) {
                Log.i(TAG, "initData: 1");
                //showLoading();
            } else if (uiCallLogs.getData().isEmpty()) {
                Log.i(TAG, "initData: 2");
                //hideLoading();
            } else {
                Log.i(TAG, "initData: 3");
                //hideLoading();
                setUiCallLogs(context, uiCallLogs.getData());
            }
        });
    }

    public void setUiCallLogs(Context context, @NonNull List<UiCallLog> uiCallLogs) {
        L.d(TAG, "setUiCallLogs: %d", uiCallLogs.size());
        Log.i(TAG, "setUiCallLogs: ----------------------------------");
        for (UiCallLog log : uiCallLogs) {
            Log.i(TAG, "setUiCallLogs: getAvatarUri" + log.getAvatarUri());
            Log.i(TAG, "setUiCallLogs: getNumber" + log.getNumber());
            Log.i(TAG, "setUiCallLogs: getText" + log.getText());
            Log.i(TAG, "setUiCallLogs: getTitle" + log.getTitle());
        }
        Log.i(TAG, "setUiCallLogs: ----------------------------------");
        mUiCallLogs.clear();
        mUiCallLogs.addAll(uiCallLogs);
        int disRecentsListSize;
        List<RemoteViews> recentItemViews = new ArrayList<>();
        if (mUiCallLogs.size() >= 3) {
            disRecentsListSize = 3;
        } else {
            disRecentsListSize = mUiCallLogs.size();
        }
        for (int i = 0; i < disRecentsListSize; i++) {
            RemoteViews recentItem = new RemoteViews(context.getPackageName(), R.layout.recents_item_widget_layout);
            recentItem.setOnClickPendingIntent(R.id.recent_item_layout, getPendingIntent(context, R.id.recent_item_layout, mUiCallLogs.get(i).getNumber()));
            //UI
            recentItem.setTextViewText(R.id.recent_name, mUiCallLogs.get(i).getTitle());
            recentItem.setImageViewUri(R.id.recent_photo, mUiCallLogs.get(i).getAvatarUri());
            if (mUiCallLogs.get(i).getMostRecentCallType() == CallHistoryLiveData.CallType.MISSED_TYPE) {
                recentItem.setImageViewResource(R.id.recent_call, R.drawable.icon_call_in);
                recentItem.setTextColor(R.id.recent_name, context.getResources().getColor(R.color.color_EB5545));
            } else if (mUiCallLogs.get(i).getMostRecentCallType() == CallHistoryLiveData.CallType.INCOMING_TYPE) {
                recentItem.setImageViewResource(R.id.recent_call, R.drawable.icon_call_in);
            } else {
                recentItem.setImageViewResource(R.id.recent_call, 0);
            }
            recentItem.setTextViewText(R.id.recent_call_time, EVDateUtils.getInstance(context).dateCompareTo(mUiCallLogs.get(i).getMostRecentCallEndTimestamp()));
            recentItemViews.add(recentItem);
        }
        pushUpdate(context, AppWidgetManager.getInstance(context), recentItemViews);

    }

}
