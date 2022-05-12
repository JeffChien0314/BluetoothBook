package com.ev.dialer.phonebook.ui.dialpad;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ev.dialer.Constants;
import com.ev.dialer.log.L;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.common.Contact;
import com.ev.dialer.phonebook.common.I18nPhoneNumberWrapper;
import com.ev.dialer.phonebook.common.PhoneNumber;
import com.ev.dialer.phonebook.ui.common.SpaceItemDecoration;
import com.ev.dialer.phonebook.ui.contact.ContactListViewModel;
import com.ev.dialer.phonebook.utils.TelecomUtils;
import com.ev.dialer.phonebook.utils.ViewUtils;
import com.ev.dialer.telecom.UiCallManager;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DialpadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialpadFragment extends AbstractDialpadFragment implements DialpadContactsAdapter.OnContactItemClickListener {

    private static final String TAG = "DialpadFragment";

    @VisibleForTesting
    static final int MAX_DIAL_NUMBER = 20;

    private static final int TONE_RELATIVE_VOLUME = 80;
    private static final int TONE_LENGTH_INFINITE = -1;
    private final ImmutableMap<Integer, Integer> mToneMap =
            ImmutableMap.<Integer, Integer>builder()
                    .put(KeyEvent.KEYCODE_1, ToneGenerator.TONE_DTMF_1)
                    .put(KeyEvent.KEYCODE_2, ToneGenerator.TONE_DTMF_2)
                    .put(KeyEvent.KEYCODE_3, ToneGenerator.TONE_DTMF_3)
                    .put(KeyEvent.KEYCODE_4, ToneGenerator.TONE_DTMF_4)
                    .put(KeyEvent.KEYCODE_5, ToneGenerator.TONE_DTMF_5)
                    .put(KeyEvent.KEYCODE_6, ToneGenerator.TONE_DTMF_6)
                    .put(KeyEvent.KEYCODE_7, ToneGenerator.TONE_DTMF_7)
                    .put(KeyEvent.KEYCODE_8, ToneGenerator.TONE_DTMF_8)
                    .put(KeyEvent.KEYCODE_9, ToneGenerator.TONE_DTMF_9)
                    .put(KeyEvent.KEYCODE_0, ToneGenerator.TONE_DTMF_0)
                    .put(KeyEvent.KEYCODE_STAR, ToneGenerator.TONE_DTMF_S)
                    .put(KeyEvent.KEYCODE_POUND, ToneGenerator.TONE_DTMF_P)
                    .build();

    private RecyclerView mRecyclerView;
    private RelativeLayout callingLayout;
    private TextView dialNumber;
    private ImageView callButton;
    private ImageView deleteButton;
    private TextView dialMember;
    private TextView callingType;
    private TextView callingStatus;
    private ImageButton declineButton;

    private ToneGenerator mToneGenerator;
    private DialpadContactsAdapter dialpadContactsAdapter;
    private List<Contact> mContactList = new ArrayList<>();
    private List<Contact> mSearchContactList = new ArrayList<>();
    private List<Contact> mTempSearchContactList = new ArrayList<>();

    public DialpadFragment() {
        // Required empty public constructor
    }

    public static DialpadFragment newInstance() {
        DialpadFragment dialpadFragment = new DialpadFragment();
        return dialpadFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, TONE_RELATIVE_VOLUME);
        dialpadContactsAdapter = new DialpadContactsAdapter(getContext());
        dialpadContactsAdapter.setOnContactItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dialpad, container, false);

        initView(rootView);
        initData();

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(DialpadFragment.this.getNumber().toString())) {
                    startDialNumber(DialpadFragment.this.getNumber().toString());
                } /*else {
                    DialpadFragment.this.setDialedNumber(CallLog.Calls.getLastOutgoingCall(DialpadFragment.this.getContext()));
                }*/
            }
        });

        callButton.addOnUnhandledKeyEventListener((v, event) -> {
            if (event.getKeyCode() == KeyEvent.KEYCODE_CALL) {
                // Use onKeyDown/Up instead of performClick() because it animates the ripple
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    callButton.onKeyDown(KeyEvent.KEYCODE_ENTER, event);
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    callButton.onKeyUp(KeyEvent.KEYCODE_ENTER, event);
                }
                return true;
            } else {
                return false;
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTempSearchContactList.clear();
                mTempSearchContactList.addAll(mContactList);
                DialpadFragment.this.removeLastDigit();
            }
        });
        deleteButton.setOnLongClickListener(v -> {
            mTempSearchContactList.clear();
            mTempSearchContactList.addAll(mContactList);
            clearDialedNumber();
            return true;
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.setVisible(callingLayout, false);
                dialNumber.setGravity(Gravity.CENTER);
                dialNumber.setText("");
            }
        });

        return rootView;
    }

    private void initView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.contact_list);
        callingLayout = (RelativeLayout) rootView.findViewById(R.id.calling_layout);
        dialNumber = (TextView) rootView.findViewById(R.id.dial_number);
        callButton = (ImageView) rootView.findViewById(R.id.call_button);
        deleteButton = (ImageView) rootView.findViewById(R.id.delete_button);

        dialMember = (TextView) rootView.findViewById(R.id.dial_member);
        callingType = (TextView) rootView.findViewById(R.id.calling_type);
        callingStatus = (TextView) rootView.findViewById(R.id.calling_status);
        declineButton = (ImageButton) rootView.findViewById(R.id.decline_button);

        mRecyclerView.setAdapter(dialpadContactsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.dialpad_contact_list_divider));
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    private void initData() {
        if (Constants.IS_DEBUG) {
            ArrayList<PhoneNumber> phonenumbers = new ArrayList<>();
            PhoneNumber number = new PhoneNumber(I18nPhoneNumberWrapper.Factory.INSTANCE.get(getContext(), "406 556-0175"),
                    0, "Person", false, 0, null, null, 0);

            phonenumbers.add(number);
            Contact contact1 = new Contact(0, false, 0, phonenumbers, "Person", null, null, null, null, "406 556-0175", false, null, "Person", "P");
            Contact contact2 = new Contact(0, false, 0, phonenumbers, "Person", null, null, null, null, "407 556-0175", false, null, "Person", "P");
            Contact contact3 = new Contact(0, false, 0, phonenumbers, "Person", null, null, null, null, "406 566-0175", false, null, "Person", "P");
            Contact contact4 = new Contact(0, false, 0, phonenumbers, "Person", null, null, null, null, "406 656-0175", false, null, "Person", "P");

            mContactList.add(contact1);
            mContactList.add(contact2);
            mContactList.add(contact3);
            mContactList.add(contact4);


            Collections.sort(mContactList);
        } else {
            ContactListViewModel contactListViewModel = ViewModelProviders.of(this).get(
                    ContactListViewModel.class);
            contactListViewModel.getAllContacts().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contactList) {
                    mContactList = contactList;
                }
            });
        }

        mTempSearchContactList.addAll(mContactList);
    }

    @Override
    public void onKeypadKeyLongPressed(@KeypadFragment.DialKeyCode int keycode) {
        switch (keycode) {
            case KeyEvent.KEYCODE_0:
                removeLastDigit();
                appendDialedNumber("+");
                break;
            case KeyEvent.KEYCODE_STAR:
                removeLastDigit();
                appendDialedNumber(",");
                break;
            /*case KeyEvent.KEYCODE_1:
                UiCallManager.get().callVoicemail();
                break;*/
            default:
                break;
        }
    }

    @Override
    void playTone(int keycode) {
        mToneGenerator.startTone(mToneMap.get(keycode), TONE_LENGTH_INFINITE);
    }

    @Override
    void stopAllTones() {
        mToneGenerator.stopTone();
    }

    @Override
    void presentDialedNumber(@NonNull StringBuffer number) {
        if (getView() == null) {
            return;
        }

        if (number.length() == 0) {
            dialNumber.setGravity(Gravity.CENTER);
            dialNumber.setText("");
            ViewUtils.setVisible(deleteButton, false);
            ViewUtils.setVisible(mRecyclerView, false);
        } else {
            dialNumber.setGravity(Gravity.CENTER);
            if (number.length() <= MAX_DIAL_NUMBER) {
                dialNumber.setText(
                        TelecomUtils.getFormattedNumber(getContext(), number.toString()).replaceAll("\\(", "").replaceAll("\\)", ""));
            } else {
                dialNumber.setText(number.substring(number.length() - MAX_DIAL_NUMBER));
            }
            ViewUtils.setVisible(deleteButton, true);
            if (View.VISIBLE != callingLayout.getVisibility()) {
                ViewUtils.setVisible(mRecyclerView, true);
            }

            mSearchContactList.clear();
            if (dialNumber.getText().toString().length() > 1) {
                for (Contact contact : mTempSearchContactList) {
                    if (stringReplace(contact.getPhoneNumber()).contains(stringReplace(dialNumber.getText().toString()))) {
                        mSearchContactList.add(contact);
                    }
                }
                mTempSearchContactList.clear();
                mTempSearchContactList.addAll(mSearchContactList);
            }
            dialpadContactsAdapter.setContactList(mSearchContactList, dialNumber.getText().toString());

        }

    }

    private String stringReplace(String string) {
        if (!TextUtils.isEmpty(string)) {
            return string.replaceAll("[^0-9]", "");
        }
        return null;
    }

    @Override
    public void onContactItemClick(int position) {
        startDialNumber(dialpadContactsAdapter.getItem(position).getPhoneNumber());

        L.i(TAG, "Name:" + dialpadContactsAdapter.getItem(position).getDisplayName() + ",Telephone:" + dialpadContactsAdapter.getItem(position).getPhoneNumber());
    }

    private void startDialNumber(String number) {
        UiCallManager.get().placeCall(number);
        ViewUtils.setVisible(callingLayout, true);
        ViewUtils.setVisible(mRecyclerView, false);
        ViewUtils.setVisible(deleteButton, false);
        dialMember.setText(number);
        dialNumber.setGravity(Gravity.CENTER);
        dialNumber.setText("");
        DialpadFragment.this.getNumber().setLength(0);
    }

}