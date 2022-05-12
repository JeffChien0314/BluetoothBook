package com.ev.dialer.phonebook.ui.dialpad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ev.dialer.Constants;
import com.ev.dialer.log.L;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.common.Contact;
import com.ev.dialer.phonebook.common.I18nPhoneNumberWrapper;
import com.ev.dialer.phonebook.common.PhoneNumber;
import com.ev.dialer.phonebook.ui.ContactsItem;
import com.ev.dialer.phonebook.utils.LogUtils;
import com.ev.dialer.phonebook.utils.TelecomUtils;
import com.ev.dialer.phonebook.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialpadContactsAdapter extends RecyclerView.Adapter<DialpadContactsAdapter.ViewHolder> {
    private static final String TAG = DialpadContactsAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater inflater;
    private OnContactItemClickListener onContactItemClickListener;
    private final List<Contact> mContactList = new ArrayList<>();
    private int currentPosition = 0;
    private String textPhoneNumber;

    public DialpadContactsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public DialpadContactsAdapter(Context mContext, List<ContactsItem> itemList) {
        this.mContext = mContext;
        //  this.itemList = itemList;
        inflater = LayoutInflater.from(mContext);
    }

    public void setContactList(List<Contact> contactList,String textPhoneNumber) {
        mContactList.clear();
        if (contactList != null) {
            mContactList.addAll(contactList);
        }
        this.textPhoneNumber = textPhoneNumber;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialpad_contacts_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contactsItem = mContactList.get(position);

        ViewUtils.setText(holder.contactName, contactsItem.getDisplayName());
        ViewUtils.setText(holder.contactNumber, contactsItem.getPhoneNumber());
        TelecomUtils.setContactBitmapAsync(mContext, holder.contactPhoto, contactsItem,null);

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactItemClickListener.onContactItemClick(position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    public Contact getItem(int position) {
        return mContactList.get(position);
    }

    public List<Contact> getContactList(){
        return mContactList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mLayout;
        TextView contactName;
        TextView contactNumber;
        ImageView contactPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.dialpad_contact_layout);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            contactNumber = (TextView) itemView.findViewById(R.id.contact_number);
            contactPhoto = (ImageView) itemView.findViewById(R.id.contact_photo);
        }
    }

    public interface OnContactItemClickListener {
        public void onContactItemClick(int position);
    }

    public void setOnContactItemClickListener(
            OnContactItemClickListener onContactItemClickListener) {
        this.onContactItemClickListener = onContactItemClickListener;
    }
}
