package com.ev.dialer.telecom.dial;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ev.dialer.phonebook.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.content.Context.WINDOW_SERVICE;

public class MyConnection extends Connection {
    final String TAG = MyConnection.class.getName();
    private Context mContext;
    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams params, params_delete;
    private View mView;

    int initialX, initialY, screenWidth, screenHeight, last_x, last_y;
    float initialTouchX, initialTouchY;


    public MyConnection(Context context) {
        super();
        mContext = context;

    }

    public MyConnection(Context context, boolean isOutgoing) {
        super();
        mContext = context;
       // initView(context, isOutgoing);
        // Intent intent=new Intent();

        // checkOVERLAY_PERMISSION();
    }

    private void initView(Context context, boolean isOutgoing) {
        if (null == mWindowManager) {
            mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            Display display = mWindowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
            Log.d(TAG, "width:" + screenWidth + ", height:" + screenHeight);
            mView = LayoutInflater.from(mContext).inflate(R.layout.popup_layout, null, false);
        }
        if (isOutgoing) {
            onShowOutgoingCallUi();
        }

    }

    private void onShowOutgoingCallUi() {
      //  mWindowManager.addView(mView,);
    }

    private void HideUI() {

    }

    @Override
    public void onShowIncomingCallUi() {
        super.onShowIncomingCallUi();
        Log.i("", "onShowIncomingCallUi: ");
        Toast.makeText(mContext, "incomingcall", Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestBluetoothAudio(@NonNull BluetoothDevice bluetoothDevice) {
        super.requestBluetoothAudio(bluetoothDevice);
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState state) {
        super.onCallAudioStateChanged(state);
    }

    @Override
    public void onStateChanged(int state) {
        super.onStateChanged(state);
        Log.i(TAG, "onStateChanged: state=" + state);
    }

    @Override
    public void onPlayDtmfTone(char c) {
        super.onPlayDtmfTone(c);
    }

    @Override
    public void onStopDtmfTone() {
        super.onStopDtmfTone();
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
    }

    @Override
    public void onSeparate() {
        super.onSeparate();
    }

    @Override
    public void onAbort() {
        super.onAbort();
    }

    @Override
    public void onHold() {
        super.onHold();
    }

    @Override
    public void onUnhold() {
        super.onUnhold();
    }

    @Override
    public void onAnswer(int videoState) {
        super.onAnswer(videoState);
    }

    @Override
    public void onAnswer() {
        super.onAnswer();
    }

    @Override
    public void onDeflect(Uri address) {
        super.onDeflect(address);
    }

    @Override
    public void onReject() {
        super.onReject();
    }

    @Override
    public void onReject(String replyMessage) {
        super.onReject(replyMessage);
    }

    @Override
    public void onSilence() {
        super.onSilence();
    }

    @Override
    public void onPostDialContinue(boolean proceed) {
        super.onPostDialContinue(proceed);
    }

    @Override
    public void onPullExternalCall() {
        super.onPullExternalCall();
    }

    @Override
    public void onCallEvent(String event, Bundle extras) {
        Log.i(TAG, "onCallEvent: ");
        super.onCallEvent(event, extras);
    }

    @Override
    public void onHandoverComplete() {
        super.onHandoverComplete();
    }

    @Override
    public void onExtrasChanged(Bundle extras) {
        super.onExtrasChanged(extras);
    }

    @Override
    public void onStartRtt(@NonNull RttTextStream rttTextStream) {
        super.onStartRtt(rttTextStream);
    }

    @Override
    public void onStopRtt() {
        super.onStopRtt();
    }

    @Override
    public void handleRttUpgradeResponse(@Nullable RttTextStream rttTextStream) {
        super.handleRttUpgradeResponse(rttTextStream);
    }

    @Override
    public void sendConnectionEvent(String event, Bundle extras) {
        super.sendConnectionEvent(event, extras);
    }
}
