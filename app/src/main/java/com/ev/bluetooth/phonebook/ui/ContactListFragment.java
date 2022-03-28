package com.ev.bluetooth.phonebook.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.vcard.VCardEntry;
import com.ev.bluetooth.phonebook.R;
import com.ev.bluetooth.phonebook.adapter.ContactsAdapter;
import com.ev.bluetooth.phonebook.constants.Constants;
import com.ev.bluetooth.phonebook.utils.LogUtils;
import com.ev.bluetooth.phonebook.view.LetterListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactListFragment extends Fragment {
    private static final String TAG = ContactListFragment.class.getSimpleName();
    private View view;
    private ListView contactListView;
    private ContactsAdapter adapter;
    private LetterListView letterView;
    private static List<VCardEntry> vCardEntryList = new ArrayList<>();
    private List<ContactsItem> itemList = new ArrayList<>();

    public static ContactListFragment newInstance(List<VCardEntry> vCardEntryList) {
        ContactListFragment contactListFragment = new ContactListFragment();
        ContactListFragment.vCardEntryList = vCardEntryList;
        return contactListFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contact_list_fragment, container, false);
        contactListView = view.findViewById(R.id.contacts_list);
        letterView = view.findViewById(R.id.letter_list);

        initData();
        addListener();
        return view;
    }

    private void initData() {
        /*if(Constants.IS_DEBUG) {
            for (int i = 0; i < 2; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("A-person-" + i);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 2; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("C-person-" + i);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 2; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("B-person-" + i);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 2; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("12-person-" + i);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 2; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("#-person-" + i);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 2; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("B-person-" + i);
                vCardEntryList.add(vCardEntry);
            }
            for (int i = 0; i < 2; i++) {
                VCardEntry vCardEntry = new VCardEntry();
                vCardEntry.getNameData().setGiven("A-person-" + i);
                vCardEntryList.add(vCardEntry);
            }

            for (VCardEntry vCardEntry : vCardEntryList) {
                itemList.add(new ContactsItem(vCardEntry.getNameData().getGiven(), vCardEntry.getNameData().getGiven() + ":158"));
            }
        }*/
        for (VCardEntry vCardEntry : vCardEntryList) {
            String number = "";
            String name = vCardEntry.getDisplayName();
            if(vCardEntry.getPhoneList()!=null && !vCardEntry.getPhoneList().isEmpty()){
                number = vCardEntry.getPhoneList().get(0).getNumber();
            }
            if(TextUtils.isEmpty(name)){
                continue;
            }
            //LogUtils.i(TAG,"getDisplayName:"+vCardEntry.getDisplayName()+",number:"+number);
            ContactsItem contactsItem = new ContactsItem(vCardEntry.getDisplayName(),number);
            itemList.add(contactsItem);
        }

        Collections.sort(itemList);
        adapter = new ContactsAdapter(getActivity(),itemList);
        contactListView.setAdapter(adapter);
    }

    private void addListener() {
        letterView.setOnLetterChangedListener(new LetterListView.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                LogUtils.i(TAG,"onLetterChanged:"+letter);
                for (int i = 0; i < itemList.size(); i++) {
                    if (letter.equalsIgnoreCase(itemList.get(i).getFirstLetter())) {
                        contactListView.setSelection(i); // 选择到首字母出现的位置
                        return;
                    }
                }
            }
        });
        adapter.setOnContactItemClickListener(new ContactsAdapter.OnContactItemClickListener() {
            @Override
            public void onContactItemClick(int position) {
                LogUtils.i(TAG,"Name:"+itemList.get(position).getName()+",Telephone:"+itemList.get(position).getTelephone());
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