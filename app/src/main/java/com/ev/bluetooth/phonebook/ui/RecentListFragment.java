package com.ev.bluetooth.phonebook.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.vcard.VCardEntry;
import com.ev.bluetooth.phonebook.R;
import com.ev.bluetooth.phonebook.adapter.RecentsAdapter;
import com.ev.bluetooth.phonebook.constants.Constants;
import com.ev.bluetooth.phonebook.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class RecentListFragment extends Fragment {
    private static final String TAG = RecentListFragment.class.getSimpleName();
    private View view;
    private ListView recentListView;
    private RecentsAdapter adapter;
    private static List<VCardEntry> vCardEntryList;

    public static RecentListFragment newInstance(List<VCardEntry> vCardEntryList) {
        RecentListFragment recentListFragment = new RecentListFragment();
        RecentListFragment.vCardEntryList = vCardEntryList;
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
        adapter = new RecentsAdapter(getActivity(),vCardEntryList);
        recentListView.setAdapter(adapter);
    }

    private void addListener() {
        adapter.setOnRecentItemClickListener(new RecentsAdapter.OnRecentItemClickListener() {
            @Override
            public void onRecentItemClick(int position) {
                LogUtils.i(TAG,"Name:"+vCardEntryList.get(position).getNameData().getGiven()+",Telephone:"+vCardEntryList.get(position).getPhoneList().get(0).getNumber());
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