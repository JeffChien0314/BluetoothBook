package com.ev.dialer.phonebook.ui.dialpad;

import static com.ev.dialer.telecom.Constants.Action.CALL_END_ACTION;
import static com.ev.dialer.telecom.Constants.Action.CALL_STATE_CHANGE_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import android.os.Handler;
import android.os.Message;
import android.telecom.Call;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
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
import com.ev.dialer.livedata.FutureData;
import com.ev.dialer.log.L;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.common.Contact;
import com.ev.dialer.phonebook.common.I18nPhoneNumberWrapper;
import com.ev.dialer.phonebook.common.PhoneCallManager;
import com.ev.dialer.phonebook.common.PhoneNumber;
import com.ev.dialer.phonebook.ui.common.KeypadView;
import com.ev.dialer.phonebook.ui.common.SpaceItemDecoration;
import com.ev.dialer.phonebook.ui.contact.ContactListViewModel;
import com.ev.dialer.phonebook.utils.TelecomUtils;
import com.ev.dialer.phonebook.utils.ToneUtil;
import com.ev.dialer.phonebook.utils.ViewUtils;
import com.ev.dialer.telecom.InCallServiceImpl;
import com.ev.dialer.telecom.UiCallManager;
import com.ev.dialer.telecom.ui.WindowService;
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
    private PhoneCallManager mPhoneCallManager;
    private final int MSG_ANSWER_CALL = 0;
    private final int MSG_UPDATE_DURATION = 1;
    private int mCallingDuration = 0;
    private static final int CALLING_TIMER_INTERVAL = 1000;

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
    private FutureData<List<Contact>>  mContactList;
    private List<Contact> mSearchContactList = new ArrayList<>();
    private List<Contact> mTempSearchContactList = new ArrayList<>();

    private BroadcastReceiver myReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CALL_END_ACTION:
                    endCalling();
                    break;
                case TelephonyManager.ACTION_PHONE_STATE_CHANGED:

                    break;
                case CALL_STATE_CHANGE_ACTION:
                    int state = intent.getIntExtra("state", -1);
                    if (PhoneCallManager.getCallType() == InCallServiceImpl.CallType.CALL_OUT &&
                            state == Call.STATE_ACTIVE) {
                        mHandler.sendEmptyMessage(MSG_ANSWER_CALL);
                    }
                    break;

            }

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ANSWER_CALL:
                    mPhoneCallManager.answer();
                    setDuration(-1);
                    break;
                case MSG_UPDATE_DURATION:
                    setDuration(mCallingDuration);
                    break;
                // case
            }
        }
    };

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
        mPhoneCallManager = new PhoneCallManager(getContext());
        registerReceiver();
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
                    ViewUtils.setText(callingStatus,getContext().getResources().getString(R.string.calling));
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
                mTempSearchContactList.addAll(mContactList.getData());
                DialpadFragment.this.removeLastDigit();
            }
        });
        deleteButton.setOnLongClickListener(v -> {
            mTempSearchContactList.clear();
            mTempSearchContactList.addAll(mContactList.getData());
            clearDialedNumber();
            return true;
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCalling();
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

            mContactList=new FutureData<>(true, new ArrayList<>());
            mContactList.getData().add(contact1);
            mContactList.getData().add(contact2);
            mContactList.getData().add(contact3);
            mContactList.getData().add(contact4);


            Collections.sort(mContactList.getData());
             mTempSearchContactList.addAll(mContactList.getData());
        } else {
            ContactListViewModel contactListViewModel = ViewModelProviders.of(this).get(
                    ContactListViewModel.class);
            contactListViewModel.getAllContacts().observe(getViewLifecycleOwner(), new Observer<FutureData< List<Contact>>>() {
                @Override
                public void onChanged(FutureData<List<Contact>> listFutureData) {
                    mContactList = listFutureData;
                    mTempSearchContactList.clear();
                    if(null!=mContactList.getData())
                    mTempSearchContactList.addAll(mContactList.getData());
                }
            });
        }


    }

    @Override
    public void onKeypadKeyLongPressed(@KeypadView.DialKeyCode int keycode) {
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
      //  mToneGenerator.startTone(mToneMap.get(keycode), TONE_LENGTH_INFINITE);
        ToneUtil.getInstance().playTone(keycode);
    }

    @Override
    void stopAllTones() {
     //   mToneGenerator.stopTone();
        ToneUtil.getInstance().stopAllTones();
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
            }else{
                ViewUtils.setVisible(deleteButton, false);
            }

            mSearchContactList.clear();
            if (dialNumber.getText().toString().length() > 1) {
                for (Contact contact : mContactList.getData()) {
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
        mPhoneCallManager.openSpeaker();
        UiCallManager.get().placeCall(number);
        ViewUtils.setVisible(callingLayout, true);
        ViewUtils.setVisible(mRecyclerView, false);
        ViewUtils.setVisible(deleteButton, false);
        ViewUtils.setVisible(callButton, false);
        dialMember.setText(number);
        dialNumber.setGravity(Gravity.CENTER);
        //dialNumber.setText("");
        //DialpadFragment.this.getNumber().setLength(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPhoneCallManager.destroy();
        getContext().unregisterReceiver(myReciver);
    }

    private void endCalling(){
        ViewUtils.setVisible(callingLayout, false);
        ViewUtils.setVisible(callButton, true);
        ViewUtils.setVisible(deleteButton, true);
        mPhoneCallManager.disconnect();
        mHandler.removeMessages(MSG_UPDATE_DURATION);
        mCallingDuration = 0;
        //dialNumber.setGravity(Gravity.CENTER);
        //dialNumber.setText("");
    }

    private void setDuration(int duration) {
        mCallingDuration = duration;
        mCallingDuration++;
        int sec = mCallingDuration % 60;
        int minutes = mCallingDuration / 60;
        int min = minutes % 60;
        int hour = minutes / 60;
        String text = (hour > 0 ? ((hour < 10 ? ("0" + hour) : hour)) + ":" : "")
                + (min < 10 ? ("0" + min) : min) + ":"
                + (sec < 10 ? ("0" + sec) : sec);
        callingStatus.setText(text);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_DURATION, CALLING_TIMER_INTERVAL);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CALL_END_ACTION);
        filter.addAction(CALL_STATE_CHANGE_ACTION);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        getContext().registerReceiver(myReciver, filter);
    }
}