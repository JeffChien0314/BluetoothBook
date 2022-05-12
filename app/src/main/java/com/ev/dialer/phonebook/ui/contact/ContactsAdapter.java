package com.ev.dialer.phonebook.ui.contact;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ev.dialer.Constants;
import com.ev.dialer.phonebook.R;
import com.ev.dialer.phonebook.common.Contact;
import com.ev.dialer.phonebook.common.I18nPhoneNumberWrapper;
import com.ev.dialer.phonebook.common.PhoneNumber;
import com.ev.dialer.phonebook.ui.ContactsItem;
import com.ev.dialer.phonebook.utils.LogUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private static final String TAG = ContactsAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater inflater;
    //private ViewHolder viewHolder;
    // private List<ContactsItem> itemList;
    private OnContactItemClickListener onContactItemClickListener;
    private final List<Contact> mContactList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private int currentPosition = 0;

    public ContactsAdapter(Context mContext) {
        this.mContext = mContext;
        if(Constants.IS_DEBUG){
            for (int i=0;i<5;i++){
                ArrayList<PhoneNumber> phonenumbers=new ArrayList<>();
                PhoneNumber number=new PhoneNumber(I18nPhoneNumberWrapper.Factory.INSTANCE.get(this.mContext, "15098672375"),
                        1,"tina",false,12342543l,"accountname","accounttype",1);
                PhoneNumber number1=new PhoneNumber(I18nPhoneNumberWrapper.Factory.INSTANCE.get(this.mContext, "15098645775"),
                        2,"tina1",false,12342543l,"accountname","accounttype",1);

                PhoneNumber number2=new PhoneNumber(I18nPhoneNumberWrapper.Factory.INSTANCE.get(this.mContext, "15098667775"),
                        3,"tina2",false,12342543l,"accountname","accounttype",1);
                phonenumbers.add(number);
                phonenumbers.add(number1);
                phonenumbers.add(number2);
                Contact contact=new Contact(12344579l,false,0,phonenumbers,"tinatina","mAltDisplay",null,null,"lookupkey","data1",false,null,"zhangtingting","z");
                mContactList.add(contact);
            }
            for (int i=0;i<5;i++){
                ArrayList<PhoneNumber> phonenumbers=new ArrayList<>();
                PhoneNumber number=new PhoneNumber(I18nPhoneNumberWrapper.Factory.INSTANCE.get(this.mContext, "15098672375"),
                        1,"tina",false,12342543l,"accountname","accounttype",1);
                PhoneNumber number1=new PhoneNumber(I18nPhoneNumberWrapper.Factory.INSTANCE.get(this.mContext, "15098645775"),
                        2,"tina1",false,12342543l,"accountname","accounttype",1);

                PhoneNumber number2=new PhoneNumber(I18nPhoneNumberWrapper.Factory.INSTANCE.get(this.mContext, "15098667775"),
                        3,"tina2",false,12342543l,"accountname","accounttype",1);
                phonenumbers.add(number);
                phonenumbers.add(number1);
                phonenumbers.add(number2);
                Contact contact=new Contact(12344579l,false,0,phonenumbers,"tinatina","mAltDisplay",null,null,"lookupkey","data1",false,null,"tingtingzhang","t");
                mContactList.add(contact);
            }
            Collections.sort(mContactList);
        }
    }

    public ContactsAdapter(Context mContext, List<ContactsItem> itemList) {
        this.mContext = mContext;
        //  this.itemList = itemList;
        inflater = LayoutInflater.from(mContext);
    }

    public void setContactList(List<Contact> contactList) {
        mContactList.clear();
        if (contactList != null) {
            mContactList.addAll(contactList);
        }
        notifyDataSetChanged();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contactsItem = mContactList.get(position);

        //根据position获取首字母作为目录catalog
        String firstLetter = contactsItem.getFirstLetter();
        LogUtils.i(TAG, "firstLetter:" + firstLetter);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(firstLetter)) {
            holder.catalog.setVisibility(View.VISIBLE);
            holder.catalog.setText(firstLetter.toUpperCase());
        } else {
            holder.catalog.setVisibility(View.GONE);
        }

        if ((mContactList.size() - 1) > position) {
            String nextLetter = mContactList.get(position + 1).getFirstLetter();
            if (!firstLetter.equals(nextLetter)) {
                holder.divider.setVisibility(View.GONE);
            }
        }
        LogUtils.i(TAG, "contactsItem.getDisplayName():" + contactsItem.getDisplayName());
        LogUtils.i(TAG, "contactsItem.getAltDisplayName():" + contactsItem.getAltDisplayName());
        holder.name.setText(contactsItem.getDisplayName());

        Log.i(TAG, "onBindViewHolder: holder.mLayout.isActivated()"+holder.mLayout.isActivated());
        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向左/→滑动动画
                Log.i(TAG, "onClick: currentPosition=" + currentPosition);
                Log.i(TAG, "onClick: position=" + position);
                translate(holder.mLayout);
                if (currentPosition != position) {
                    resetUI(currentPosition);
                    currentPosition = position;
                }
            }
        });
        holder.mMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactItemClickListener.onMsgBtnClick(position);
            }
        });
        holder.mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactItemClickListener.onDelBtnClick(position);
            }
        });
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isActivated()) {
                    translate(v);
                } else
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
        TextView catalog;
        TextView name;
        View divider;
        ImageView mMsgBtn;
        ImageView mDelBtn;
        ImageView mMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.main_layout);
            name = (TextView) itemView.findViewById(R.id.contact_name);
            catalog = (TextView) itemView.findViewById(R.id.catalog);
            divider = (View) itemView.findViewById(R.id.contact_divider);
            mMsgBtn = (ImageView) itemView.findViewById(R.id.msg_btn);
            mDelBtn = (ImageView) itemView.findViewById(R.id.del_btn);
            mMore = (ImageView) itemView.findViewById(R.id.contact_more);
        }
    }

    /**
     * 获取catalog首次出现位置
     */
    public int getPositionForSection(String catalog) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mContactList.get(i).getFirstLetter();
            //LogUtils.i(TAG,"catalog:"+catalog+"，sortStr:"+sortStr);
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }

    private void resetUI(int currentPosition) {
        View view = mRecyclerView.getLayoutManager().findViewByPosition(currentPosition).findViewById(R.id.main_layout);
        if (view.isActivated())
            translate(view);
    }

    public void translate(View view1) {
        Log.i(TAG, "translate: view1.isActivated()"+view1.isActivated());
        int l = 0;
        int t = view1.getTop();
        int r = view1.getWidth() - view1.findViewById(R.id.layout_more).getWidth() - 420;
        int b = view1.getBottom();
        if (!view1.isActivated()) {
            l = -view1.findViewById(R.id.layout_more).getWidth();
            r = view1.getWidth() + view1.findViewById(R.id.layout_more).getWidth();
        }
        Log.i(TAG, "translate: l=" + l);
        Log.i(TAG, "translate: t=" + t);
        Log.i(TAG, "translate: r=" + r);
        Log.i(TAG, "translate: b=" + b);
        view1.layout(l, t, r, b);
        view1.setActivated(!view1.isActivated());
    }

    public interface OnContactItemClickListener {
        public void onContactItemClick(int position);

        public void onMsgBtnClick(int position);

        public void onDelBtnClick(int position);
    }

    public void setOnContactItemClickListener(
            OnContactItemClickListener onContactItemClickListener) {
        this.onContactItemClickListener = onContactItemClickListener;
    }
}
