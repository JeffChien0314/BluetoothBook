package com.ev.dialer.phonebook.ui;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.WindowManager;

import com.ev.dialer.log.L;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.telecom.UiCallManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PhoneBookActivity extends AppCompatActivity {
    final String TAG = PhoneBookActivity.class.getName();
    private static final int REQUEST_ID = 1;
    private TabCategoryView mTabCategoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllpower();
        setRoleHolderAsUser(this);
        setContentView(R.layout.activity_phone_book);
        mTabCategoryView = new TabCategoryView(this);
        mTabCategoryView.registerReceiver();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //tabCategoryView.changeFocusBtn();
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        setIntent(i);
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent != null ? intent.getAction() : null;
        L.d(TAG, "handleIntent, intent: %s, action: %s", intent, action);
        if (action == null || action.length() == 0) {
            return;
        }
        String number;
        switch (action) {
            case Intent.ACTION_DIAL:
                number = PhoneNumberUtils.getNumberFromIntent(intent, this);
                /*if (TelecomActivityViewModel.DialerAppState.BLUETOOTH_ERROR
                        != mDialerAppStateLiveData.getValue()) {
                    showDialPadFragment(number);
                }*/
                break;
            case Intent.ACTION_CALL:
                number = PhoneNumberUtils.getNumberFromIntent(intent, this);
                UiCallManager.get().placeCall(number);
                break;
            default:
                break;
        }
        setIntent(null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabCategoryView.unRegisterReceiver();
    }

    private void requestAllpower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.READ_CONTACTS
                            , Manifest.permission.WRITE_CONTACTS
                            , Manifest.permission.WRITE_CALL_LOG
                            , Manifest.permission.READ_CALL_LOG

                            // ,Manifest.permission.CALL_PRIVILEGED
                            , Manifest.permission.PROCESS_OUTGOING_CALLS}, 1);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Log.d(TAG, "ask alert window permission");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent);
                return;
            } else {
                Log.d(TAG, "alertwindow permitted");
            }
        }
    }

    public void setRoleHolderAsUser(Context context) {
        if (!isDefaultPhoneCallApp(context)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                RoleManager roleManager = context.getSystemService(RoleManager.class);
                Intent roleRequestIntent = null;
                roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                startActivityForResult(roleRequestIntent, REQUEST_ID);
            }

        }
        Log.i("PhoneBookActivity", "onCreate: isDefaultPhoneCallApp=" + isDefaultPhoneCallApp(this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID) {

            if (resultCode == Activity.RESULT_OK) {
                // Your app is now the default dialer app
                Log.i(TAG, "onActivityResult: Your app is now the default dialer app");
            } else {
                Log.i(TAG, "onActivityResult: Your app is not the default dialer app");
                // Your app is not the default dialer app
            }
        }
    }

    public static boolean isDefaultPhoneCallApp(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TelecomManager manger = (TelecomManager) context.getSystemService(TELECOM_SERVICE);
            if (manger != null) {
                String name = manger.getDefaultDialerPackage();
                try {
                    return name.equals(context.getPackageName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return false;
    }
}