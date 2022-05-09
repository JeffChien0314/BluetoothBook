package com.ev.dialer.telecom.ui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ev.dialer.Constants;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.common.PhoneCallManager;
import com.ev.dialer.telecom.InCallServiceImpl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WindowService extends Service implements View.OnClickListener {
    private final static String TAG = "WindowService";
    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mParams;
    private View mView, mkeypadView;
    private ImageView mAnswerBtn, mCancelBtn, mDialpad;
    private TextView mName, mState, mType, mInputNumber;
    private boolean isFullviewShowing = false, isKeypadShowing = false;

    int initialX, initialY, screenWidth, screenHeight, last_x, last_y;
    float initialTouchX, initialTouchY;
    private InCallServiceImpl.CallType mCallType = InCallServiceImpl.CallType.CALL_IN;
    private String mPhoneNumber = "";
    private PhoneCallManager mPhoneCallManager;
    private final int MSG_ANSWER_CALL = 0;
    private final int MSG_UPDATE_DURATION = 1;
    private static final int ANSWER_CALL_TIME_OUT = 5 * 1000;
    private static final int CALLING_TIMER_INTERVAL = 1000;
    private int mCallingDuration = 0;
    private final static String CALL_END_ACTION = "com.action.CallEnd";
    private BroadcastReceiver myReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CALL_END_ACTION)) {
                WindowService.this.stopSelf();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ANSWER_CALL:
                    mPhoneCallManager.answer();
                    mAnswerBtn.setVisibility(View.INVISIBLE);
                    setDuration(-1);
                    break;
                case MSG_UPDATE_DURATION:
                    setDuration(mCallingDuration);
                    break;
                // case
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        initWindow();
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CALL_END_ACTION);
        registerReceiver(myReciver, filter);
    }

    private void initWindow() {
        mPhoneCallManager = new PhoneCallManager(this);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        Log.d(TAG, "width:" + screenWidth + ", height:" + screenHeight);
        mView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null, false);
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP;
        mParams.x = 0;
        mParams.y = 80;
        mAnswerBtn = mView.findViewById(R.id.btn_answer);
        mCancelBtn = mView.findViewById(R.id.btn_decline);
        mDialpad = mView.findViewById(R.id.btn_dialpad);
        mState = mView.findViewById(R.id.text_state);
        mName = mView.findViewById(R.id.text_name);
        mType = mView.findViewById(R.id.text_type);
        mkeypadView = mView.findViewById(R.id.keypad_layout);
        mInputNumber = mView.findViewById(R.id.input_number);
        setListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        initData(intent);
        initView();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initView() {
        onshow();
        if (mCallType == InCallServiceImpl.CallType.CALL_IN) {// 打进的电话
            mHandler.sendEmptyMessageDelayed(MSG_ANSWER_CALL, ANSWER_CALL_TIME_OUT);
        } else if (mCallType == InCallServiceImpl.CallType.CALL_OUT) {// 打出的电话
            mPhoneCallManager.openSpeaker();
        }
    }

    private void onshow() {
        mName.setText(TextUtils.isEmpty(mPhoneNumber) ? "unknown number" : mPhoneNumber);
        mType.setText(getCallType());
        //  mInputNumber.setText("input");
        if (mCallType == InCallServiceImpl.CallType.CALL_OUT) {
            mAnswerBtn.setVisibility(View.INVISIBLE);
        }
        if (!isFullviewShowing) {
            mWindowManager.addView(mView, mParams);
            isFullviewShowing = true;
        }

    }

    private String getCallType() {

        return "Mobile";
    }

    private void hide() {
        if (isFullviewShowing) {
            mWindowManager.removeView(mView);
            isFullviewShowing = false;
        }
    }

    private boolean isShowing() {
        return isFullviewShowing;
    }

    private void setListener() {
        mAnswerBtn.setOnClickListener(this::onClick);
        mCancelBtn.setOnClickListener(this::onClick);
        mDialpad.setOnClickListener(this::onClick);
    }

    private void initData(Intent intent) {
        if (Constants.IS_DEBUG) return;
        if (null != intent) {
            mCallType = (InCallServiceImpl.CallType) intent.getSerializableExtra(Intent.EXTRA_MIME_TYPES);
            mPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        } else {
            Log.i(TAG, "initData: intent=null");
            stopSelf();
        }
    }

    private void setDuration(int duration) {
        mCallingDuration = duration;
        mCallingDuration++;
        int sec = mCallingDuration % 60;
        int minutes = mCallingDuration / 60;
        int min = minutes % 60;
        int hour = minutes / 60;
        String text = (hour > 0 ? ((hour < 10 ? ("0" + hour) : hour)) + ":" : "")
                + (min < 10 ? ("0" + min) : min) + ":"
                + (sec < 10 ? ("0" + sec) : sec);
        mState.setText(text);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_DURATION, CALLING_TIMER_INTERVAL);
    }

    @Override
    public void onDestroy() {
        hide();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_answer:
                mPhoneCallManager.answer();
                break;
            case R.id.btn_decline:
                mPhoneCallManager.disconnect();
                stopSelf();
                break;
            case R.id.btn_dialpad:
                showDialpad();
                break;
        }
    }

    private void showDialpad() {
        mkeypadView.setVisibility(isKeypadShowing ? View.GONE : View.VISIBLE);
        isKeypadShowing = !isKeypadShowing;
    }
}
