package com.ev.dialer.phonebook.ui.dialpad;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CallLog;
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

import com.android.vcard.VCardEntry;
import com.ev.dialer.log.L;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.common.Contact;
import com.ev.dialer.phonebook.common.InMemoryPhoneBook;
import com.ev.dialer.phonebook.common.PhoneNumber;
import com.ev.dialer.phonebook.ui.contact.ContactListFragment;
import com.ev.dialer.phonebook.utils.TelecomUtils;
import com.ev.dialer.phonebook.utils.ViewUtils;
import com.ev.dialer.telecom.UiCallManager;
import com.google.common.collect.ImmutableMap;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DialpadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialpadFragment extends AbstractDialpadFragment {

    private static final String TAG = "DialpadFragment";

    private static final String DIALPAD_MODE_KEY = "DIALPAD_MODE_KEY";
    private static final int MODE_DIAL = 1;
    private static final int MODE_EMERGENCY = 2;

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
    //private final TypeDownResultsAdapter mAdapter = new TypeDownResultsAdapter();

    //private TypeDownResultsViewModel mTypeDownResultsViewModel;
    private RecyclerView mRecyclerView;
    private RelativeLayout callingLayout;
    private TextView dialNumber;
    private ImageView callButton;
    private ImageView deleteButton;
    private int mMode;

    private ToneGenerator mToneGenerator;

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

        /*mTypeDownResultsViewModel = ViewModelProviders.of(this).get(
                TypeDownResultsViewModel.class);
        mTypeDownResultsViewModel.getContactSearchResults().observe(this,
                new Observer<List<ContactResultsLiveData.ContactResultListItem>>() {
                    @Override
                    public void onChanged(List<ContactResultsLiveData.ContactResultListItem> contactResults) {
                        mAdapter.setData(contactResults);
                    }
                });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dialpad, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.contact_list);
        callingLayout = (RelativeLayout) rootView.findViewById(R.id.calling_layout);
        dialNumber = (TextView) rootView.findViewById(R.id.dial_number);
        callButton = (ImageView) rootView.findViewById(R.id.call_button);
        deleteButton = (ImageView) rootView.findViewById(R.id.delete_button);

        /*
        mRecyclerView.setAdapter(mAdapter);
        mLabel = rootView.findViewById(R.id.label);
        mAvatar = rootView.findViewById(R.id.dialpad_contact_avatar);
        if (mAvatar != null) {
            mAvatar.setOutlineProvider(ContactAvatarOutputlineProvider.get());
        }*/

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(DialpadFragment.this.getNumber().toString())) {
                    UiCallManager.get().placeCall(DialpadFragment.this.getNumber().toString());
                    // Update dialed number UI later in onResume() when in call intent is handled.
                    DialpadFragment.this.getNumber().setLength(0);
                } else {
                    DialpadFragment.this.setDialedNumber(CallLog.Calls.getLastOutgoingCall(DialpadFragment.this.getContext()));
                }
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

        deleteButton.setOnClickListener(v -> removeLastDigit());
        deleteButton.setOnLongClickListener(v -> {
            clearDialedNumber();
            return true;
        });

        return rootView;
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
            case KeyEvent.KEYCODE_1:
                UiCallManager.get().callVoicemail();
                break;
            default:
                break;
        }
    }

    @Override
    void playTone(int keycode) {
        L.d(TAG, "start key pressed tone for %s", keycode);
        mToneGenerator.startTone(mToneMap.get(keycode), TONE_LENGTH_INFINITE);
    }

    @Override
    void stopAllTones() {
        L.d(TAG, "stop key pressed tone");
        mToneGenerator.stopTone();
    }

    @Override
    void presentDialedNumber(@NonNull StringBuffer number) {
        if (getView() == null) {
            return;
        }

        if (number.length() == 0) {
            dialNumber.setGravity(Gravity.CENTER);
            /*dialNumber.setText(
                    mMode == MODE_DIAL ? R.string.dial_a_number
                            : R.string.emergency_call_description);*/
            dialNumber.setText("");
            ViewUtils.setVisible(deleteButton, false);
        } else {
            dialNumber.setGravity(Gravity.CENTER);
            if (number.length() <= MAX_DIAL_NUMBER) {
                dialNumber.setText(
                        TelecomUtils.getFormattedNumber(getContext(), number.toString()));
            } else {
                dialNumber.setText(number.substring(number.length() - MAX_DIAL_NUMBER));
            }
            ViewUtils.setVisible(deleteButton, true);
        }

        /*if (getResources().getBoolean(R.bool.config_show_type_down_list_on_dialpad)) {
            resetContactInfo();
            ViewUtils.setVisible(mRecyclerView, true);
            mTypeDownResultsViewModel.setSearchQuery(number.toString());
        } else {
            presentContactInfo(number.toString());
        }*/
    }

    /*private void presentContactInfo(@NonNull String number) {
        Contact contact = InMemoryPhoneBook.get().lookupContactEntry(number);
        ViewUtils.setText(mDisplayName, contact == null ? "" : contact.getDisplayName());
        if (contact != null && getResources().getBoolean(
                R.bool.config_show_detailed_user_profile_on_dialpad)) {
            presentContactDetail(contact, number);
        } else {
            resetContactInfo();
        }
    }*/

    /*private void presentContactDetail(@Nullable Contact contact, @NonNull String number) {
        PhoneNumber phoneNumber = contact.getPhoneNumber( number);
        CharSequence readableLabel = phoneNumber.getReadableLabel(
                getContext().getResources());
        ViewUtils.setText(mLabel, phoneNumber.isPrimary() ? getContext().getString(
                R.string.primary_number_description, readableLabel) : readableLabel);
        ViewUtils.setVisible(mLabel, true);

        //TelecomUtils.setContactBitmapAsync(getContext(), mAvatar, contact);
        ViewUtils.setVisible(mAvatar, true);
    }

    private void resetContactInfo() {
        ViewUtils.setVisible(mLabel, false);
        ViewUtils.setVisible(mAvatar, false);
    }*/
}