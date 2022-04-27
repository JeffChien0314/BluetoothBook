package com.ev.dialer.phonebook.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.ev.dialer.phonebook.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PhoneBookActivity extends AppCompatActivity {
    private TabCategoryView tabCategoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllpower();
        setContentView(R.layout.activity_phone_book);

        tabCategoryView = new TabCategoryView(this);
        tabCategoryView.registerReceiver();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //tabCategoryView.changeFocusBtn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabCategoryView.unRegisterReceiver();
    }

    private void requestAllpower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "requestAllpower: ");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.READ_CONTACTS
                            , Manifest.permission.WRITE_CONTACTS
                            , Manifest.permission.WRITE_CALL_LOG
                            , Manifest.permission.READ_CALL_LOG
                            /*  ,Manifest.permission.MODIFY_PHONE_STATE
                             */
                            // ,Manifest.permission.CALL_PRIVILEGED
                            , Manifest.permission.PROCESS_OUTGOING_CALLS}, 1);
        }
    }
}