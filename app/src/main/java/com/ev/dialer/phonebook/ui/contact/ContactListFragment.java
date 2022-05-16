package com.ev.dialer.phonebook.ui.contact;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.vcard.VCardEntry;
import com.ev.dialer.Constants;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.common.Contact;
import com.ev.dialer.phonebook.ui.common.BaseFragment;
import com.ev.dialer.phonebook.ui.common.LetterListView;
import com.ev.dialer.phonebook.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.ev.dialer.phonebook.ui.calllog.LetterListView;

public class ContactListFragment extends BaseFragment {
    private static final String TAG = ContactListFragment.class.getSimpleName();
    private View view;
    private RecyclerView contactListView;
    private ContactsAdapter adapter;
    private LetterListView letterView;
    private static List<VCardEntry> vCardEntryList = new ArrayList<>();
    //  private List<ContactsItem> itemList = new ArrayList<>();


    public static ContactListFragment newInstance() {
        ContactListFragment contactListFragment = new ContactListFragment();
        return contactListFragment;
    }

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
        adapter = new ContactsAdapter(getActivity());
        contactListView.setAdapter(adapter);
        adapter.setRecyclerView(contactListView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        contactListView.setLayoutManager(layoutManager);

        if (!Constants.IS_DEBUG) {
            ContactListViewModel contactListViewModel = ViewModelProviders.of(this).get(
                    ContactListViewModel.class);
          //  contactListViewModel.getAllContacts().observe(getViewLifecycleOwner(), adapter::setContactList);

            contactListViewModel.getAllContacts().observe(getViewLifecycleOwner(), contacts -> {
                if (contacts.isLoading()) {
                    showLoading();
                } else if (contacts.getData() == null) {
                    hideLoading();
                  /*  showEmpty(Constants.INVALID_RES_ID, R.string.contact_list_empty,
                            R.string.available_after_sync);*/
                } else {
                    hideLoading();
                    adapter.setContactList((List<Contact>) contacts.getData());
                //    showContent();

                }
            });
        }


/*
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

        Collections.sort(itemList);*/


    }

    private void addListener() {
        letterView.setOnLetterChangedListener(new LetterListView.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                LogUtils.i(TAG, "onLetterChanged:" + letter);
                for (int i = 0; i < adapter.getContactList().size(); i++) {
                    if (letter.equalsIgnoreCase(adapter.getContactList().get(i).getFirstLetter())) {
                        contactListView.scrollToPosition(i);
                     //   contactListView.setSelection(i); // 选择到首字母出现的位置
                        return;
                    }
                }
            }
        });
        adapter.setOnContactItemClickListener(new ContactsAdapter.OnContactItemClickListener() {
            @Override
            public void onContactItemClick(int position) {
                Log.i(TAG, "onContactItemClick: position=" + position);
                //   LogUtils.i(TAG, "Name:" + adapter.getItem(position).getDisplayName() + ",Telephone:" +adapter.getItem(position).getNumbers().get(0));
            }

            @Override
            public void onMsgBtnClick(int position) {
                Log.i(TAG, "onMsgBtnClick: postion=" + position);
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