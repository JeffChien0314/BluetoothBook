package com.ev.dialer.phonebook.ui.calllog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ev.dialer.Constants;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.ui.common.BaseLoadingFragment;
import com.ev.dialer.phonebook.ui.common.LoadingCallback;
import com.ev.dialer.phonebook.ui.common.entity.UiCallLog;
import com.ev.dialer.telecom.UiCallManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecentListFragment extends BaseLoadingFragment {
    private static final String TAG = RecentListFragment.class.getSimpleName();
    private View view;
    private RecyclerView recentListView;
    private RecentsAdapter adapter;

    public RecentListFragment(LoadingCallback callback) {
        super(callback);
    }

    public static RecentListFragment newInstance(LoadingCallback callback) {
        RecentListFragment recentListFragment = new RecentListFragment(callback);
        return recentListFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recent_list_fragment, container, false);
        recentListView = view.findViewById(R.id.recents_list);
        initData();
        addListener();
        return view;
    }

    private void initData() {
        adapter = new RecentsAdapter(getActivity());
        recentListView.setAdapter(adapter);
        adapter.setRecyclerView(recentListView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recentListView.setLayoutManager(layoutManager);
        // showLoading();
        if (!Constants.IS_DEBUG) {
            CallHistoryViewModel viewModel = ViewModelProviders.of(this).get(
                    CallHistoryViewModel.class);
            viewModel.getCallHistory().observe(getActivity(), uiCallLogs -> {
                if (uiCallLogs.isLoading()) {
                    Log.i(TAG, "initData: 1");
                    showLoading();
                } else if (uiCallLogs.getData().isEmpty()) {
                    Log.i(TAG, "initData: 2");
                    hideLoading();
                } else {
                    Log.i(TAG, "initData: 3");
                    hideLoading();
                    adapter.setUiCallLogs(uiCallLogs.getData());
                }
            });
        }
    }

    private void addListener() {
        adapter.setOnRecentItemClickListener(new RecentsAdapter.OnRecentItemClickListener() {
            @Override
            public void onRecentItemClick(int position) {
                Log.i(TAG, "onRecentItemClick: ");
//                LogUtils.i(TAG, "Name:" + vCardEntryList.get(position).getNameData().getGiven() + ",Telephone:" + vCardEntryList.get(position).getPhoneList().get(0).getNumber());
                try {
                    String phoneNumber = (((UiCallLog) adapter.getItem(position)).getNumber()).replace("-", "");

                    //?????????????????????????????????
                  /*  Uri uri= Uri.parse("tel:"+phoneNumber);
                    Intent intent=new Intent(Intent.ACTION_DIAL,uri);
                    startActivity(intent);
                    */
                    //BtManager.getInstance(getActivity()).connectAudio(phoneNumber);
                   /* Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"13954571409"));
                    startActivity(intent);*/
                    if (Constants.IS_DEBUG) {
                        UiCallManager.get().placeCall("13954571409");
                    } else
                        UiCallManager.get().placeCall(((UiCallLog) adapter.getItem(position)).getNumber());//13954571409
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //
            }

            @Override
            public void onMsgBtnClick(int position) {
                Log.i(TAG, "onMsgBtnClick: position=" + position);

            }

            @Override
            public void onDelBtnClick(int position) {
                Log.i(TAG, "onDelBtnClick: position=" + position);
            }

        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}