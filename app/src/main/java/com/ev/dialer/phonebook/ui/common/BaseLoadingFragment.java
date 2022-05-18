package com.ev.dialer.phonebook.ui.common;

import android.util.Log;

import androidx.fragment.app.Fragment;


public class BaseLoadingFragment extends Fragment {
    protected LoadingCallback mLoadingcallback;

    public BaseLoadingFragment(LoadingCallback callback) {
        mLoadingcallback = callback;
    }

    protected void hideLoading() {
        mLoadingcallback.hideloading();
    }

    protected void showLoading() {
        mLoadingcallback.showloading();
    }

}
