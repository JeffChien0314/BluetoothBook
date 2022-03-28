package com.ev.bluetooth.phonebook.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vcard.VCardEntry;
import com.ev.bluetooth.phonebook.R;
import com.ev.bluetooth.phonebook.constants.Constants;
import com.ev.bluetooth.phonebook.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RecentsAdapter extends BaseAdapter {
    private static final String TAG = RecentsAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;
    private List<VCardEntry> vCardEntryList;
    private OnRecentItemClickListener onRecentItemClickListener;

    public RecentsAdapter(Context mContext, List<VCardEntry> vCardEntryList) {
        this.mContext = mContext;
        this.vCardEntryList = vCardEntryList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return vCardEntryList.size();
    }

    @Override
    public Object getItem(int position) {
        return vCardEntryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VCardEntry vCardEntry = vCardEntryList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.recents_item_layout, parent, false);
            viewHolder.recentCall = (ImageView) convertView.findViewById(R.id.recent_call);
            viewHolder.name = (TextView) convertView.findViewById(R.id.recent_name);
            viewHolder.divider = (View) convertView.findViewById(R.id.recent_divider);
            viewHolder.recentCallTime = (TextView) convertView.findViewById(R.id.recent_call_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //LogUtils.i(TAG, "getGiven:" + vCardEntry.getNameData().getGiven());
        Collection<String> collection = vCardEntry.getCallDate().getTypeCollection();
        if(collection!=null){
            for(String category:collection){
                if(Constants.VCARD_PROPERTY_CALLTYPE_INCOMING.equals(category)){
                    viewHolder.recentCall.setBackgroundResource(R.drawable.icon_call_in);
                }else if(Constants.VCARD_PROPERTY_CALLTYPE_MISSED.equals(category)){
                    viewHolder.recentCall.setBackgroundResource(R.drawable.icon_call_in);
                    viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.color_EB5545));
                }else{
                    viewHolder.recentCall.setBackgroundResource(0);
                }
            }
        }else {
            viewHolder.recentCall.setBackgroundResource(0);
        }

        viewHolder.name.setText(vCardEntry.getDisplayName());
        viewHolder.recentCallTime.setText(dateCompareTo(parserDateFormat(vCardEntry.getCallDate().getCallDatetime())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecentItemClickListener.onRecentItemClick(position);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView recentCall;
        TextView name;
        View divider;
        TextView recentCallTime;
    }

    public interface OnRecentItemClickListener{
        public void onRecentItemClick(int position);
    }

    public void setOnRecentItemClickListener(
            OnRecentItemClickListener onRecentItemClickListener) {
        this.onRecentItemClickListener = onRecentItemClickListener;
    }

    public Date parserDateFormat(String string) {
        if (string == null) return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public String dateCompareTo(Date date) {
        if (date == null) return null;
        String dateInfo = null;

        Date todayDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayDate);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(date);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if (date != null) {
            int day = day1 - day2;
            switch (day) {
                case 0:
                    if(getTimeFormat()) {
                        dateInfo = getCurrentTime(date);
                    }else{
                        if(Calendar.AM == getTimeStyle(date)){
                            dateInfo = getCurrentTime(date)+" AM";
                        }else {
                            dateInfo = getCurrentTime(date)+" PM";
                        }
                    }
                    break;
                case 1:
                    dateInfo = "昨天";
                    break;
                default:
                    dateInfo = dateToWeek(date);
            }
        }
        return dateInfo;
    }

    private String getCurrentTime(Date date) {
        String time;
        String TIME_FORMAT = "hh:mm";//hh 12h ,HH 24h
        if(getTimeFormat()){
            TIME_FORMAT = "HH:mm";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        time = simpleDateFormat.format(date);
        return time;
    }

    private boolean getTimeFormat(){
        return DateFormat.is24HourFormat(mContext);
    }

    private int getTimeStyle(Date date) {
        int mStyle;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        mStyle = calendar.get(Calendar.AM_PM);

        return mStyle;
    }

    public String dateToWeek(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //一周的第几天
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

}
