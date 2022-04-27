package com.ev.dialer.phonebook.ui.calllog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ev.dialer.phonebook.R;
import com.ev.dialer.btManager.BtManager;
import com.ev.dialer.Constants;
import com.ev.dialer.phonebook.ui.common.entity.UiCallLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecentListFragment extends Fragment {
    private static final String TAG = RecentListFragment.class.getSimpleName();
    private View view;
    private RecyclerView recentListView;
    private RecentsAdapter adapter;

    public static RecentListFragment newInstance() {
        RecentListFragment recentListFragment = new RecentListFragment();
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
        if (!Constants.IS_DEBUG) {
            CallHistoryViewModel viewModel = ViewModelProviders.of(this).get(
                    CallHistoryViewModel.class);
            viewModel.getCallHistory().observe(getActivity(), adapter::setUiCallLogs);
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

                    //调用拨号器拨打电话号码
                  /*  Uri uri= Uri.parse("tel:"+phoneNumber);
                    Intent intent=new Intent(Intent.ACTION_DIAL,uri);
                    startActivity(intent);
                    */
                    BtManager.getInstance(getActivity()).connectAudio(phoneNumber);
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