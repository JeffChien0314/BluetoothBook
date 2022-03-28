package com.ev.bluetooth.phonebook.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import com.ev.bluetooth.phonebook.R;

public class PhoneBookActivity extends AppCompatActivity {
    private TabCategoryView tabCategoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_book);

        tabCategoryView = new TabCategoryView(this);
        tabCategoryView.registerReceiver();
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
}