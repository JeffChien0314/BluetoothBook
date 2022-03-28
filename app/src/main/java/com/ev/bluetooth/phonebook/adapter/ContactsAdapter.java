package com.ev.bluetooth.phonebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ev.bluetooth.phonebook.R;
import com.ev.bluetooth.phonebook.ui.ContactsItem;
import com.ev.bluetooth.phonebook.utils.LogUtils;

import java.util.List;

public class ContactsAdapter extends BaseAdapter {
    private static final String TAG = ContactsAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private List<ContactsItem> itemList;
    private OnContactItemClickListener onContactItemClickListener;

    public ContactsAdapter(Context mContext, List<ContactsItem> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactsItem contactsItem = itemList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.contacts_item_layout, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.contact_name);
            viewHolder.catalog = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.divider = (View) convertView.findViewById(R.id.contact_divider);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取首字母作为目录catalog
        String firstLetter = contactsItem.getFirstLetter();
        LogUtils.i(TAG, "firstLetter:" + firstLetter);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(firstLetter)) {
            viewHolder.catalog.setVisibility(View.VISIBLE);
            viewHolder.catalog.setText(firstLetter.toUpperCase());
        } else {
            viewHolder.catalog.setVisibility(View.GONE);
        }

        if ((itemList.size() - 1) > position) {
            String nextLetter = itemList.get(position + 1).getFirstLetter();
            if (!firstLetter.equals(nextLetter)) {
                viewHolder.divider.setVisibility(View.GONE);
            }
        }
        LogUtils.i(TAG, "getGiven:" + contactsItem.getName());
        viewHolder.name.setText(contactsItem.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onContactItemClickListener.onContactItemClick(position);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView catalog;
        TextView name;
        View divider;
    }

    /**
     * 获取catalog首次出现位置
     */
    public int getPositionForSection(String catalog) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = itemList.get(i).getFirstLetter();
            //LogUtils.i(TAG,"catalog:"+catalog+"，sortStr:"+sortStr);
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }

    public interface OnContactItemClickListener{
        public void onContactItemClick(int position);
    }

    public void setOnContactItemClickListener(
            OnContactItemClickListener onContactItemClickListener) {
        this.onContactItemClickListener = onContactItemClickListener;
    }
}
