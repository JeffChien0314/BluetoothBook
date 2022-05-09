package com.ev.dialer.telecom.dial;

import android.content.Intent;
import android.net.Uri;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.telecom.RemoteConference;
import android.telecom.RemoteConnection;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

public class MyConnectionService extends ConnectionService {
    String TAG = MyConnectionService.class.getName();

    public MyConnectionService() {
        super();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
       // super.onCreateIncomingConnection(connectionManagerPhoneAccount, request);
        MyConnection conn = new MyConnection(getApplicationContext());
        conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        conn.setCallerDisplayName("Telecom", TelecomManager.PRESENTATION_ALLOWED);
        conn.setAddress(Uri.parse("tel:" + "10086"), TelecomManager.PRESENTATION_ALLOWED);
        conn.setRinging();
        conn.setInitializing();
        conn.setActive();
        return conn;
    }

    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.i(TAG, "onCreateIncomingConnectionFailed: ");
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
        Toast.makeText(getApplicationContext(), "onCreateOutgoingConnectionFailed", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onCreateOutgoingConnectionFailed: ");
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Log.i(TAG, "onCreateOutgoingConnection: ");
         super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);

      /*  Intent it = new Intent(this,WindowService.class);
        startService(it);*/
        MyConnection conn = new MyConnection(getApplicationContext(),true);
        conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        conn.setCallerDisplayName("Telecom", TelecomManager.PRESENTATION_ALLOWED);
    //    conn.setAddress(Uri.parse("tel:" + "10086"), TelecomManager.PRESENTATION_ALLOWED);
        conn.setRinging();
        conn.setInitializing();
        conn.setActive();
        return conn;
    }

    @Override
    public Connection onCreateOutgoingHandoverConnection(PhoneAccountHandle fromPhoneAccountHandle, ConnectionRequest request) {
        Log.i(TAG, "onCreateOutgoingHandoverConnection: ");
        return super.onCreateOutgoingHandoverConnection(fromPhoneAccountHandle, request);
    }

    @Override
    public Connection onCreateIncomingHandoverConnection(PhoneAccountHandle fromPhoneAccountHandle, ConnectionRequest request) {
        Log.i(TAG, "onCreateIncomingHandoverConnection: ");
        return super.onCreateIncomingHandoverConnection(fromPhoneAccountHandle, request);
    }

    @Override
    public void onHandoverFailed(ConnectionRequest request, int error) {
        Log.i(TAG, "onHandoverFailed: ");
        super.onHandoverFailed(request, error);
    }

    @Override
    public void onConference(Connection connection1, Connection connection2) {
        Log.i(TAG, "onConference: ");
        super.onConference(connection1, connection2);
    }

    @Override
    public void onRemoteConferenceAdded(RemoteConference conference) {
        Log.i(TAG, "onRemoteConferenceAdded: ");
        super.onRemoteConferenceAdded(conference);
    }

    @Override
    public void onRemoteExistingConnectionAdded(RemoteConnection connection) {
        Log.i(TAG, "onRemoteExistingConnectionAdded: ");
        super.onRemoteExistingConnectionAdded(connection);
    }

    @Override
    public void onConnectionServiceFocusLost() {
        Log.i(TAG, "onConnectionServiceFocusLost: ");
        super.onConnectionServiceFocusLost();
    }

    @Override
    public void onConnectionServiceFocusGained() {
        Log.i(TAG, "onConnectionServiceFocusGained: ");
        super.onConnectionServiceFocusGained();
    }
}
