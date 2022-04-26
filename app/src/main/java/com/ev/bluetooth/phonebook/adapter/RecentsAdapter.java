package com.ev.bluetooth.phonebook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vcard.VCardEntry;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ev.bluetooth.phonebook.common.EVDateUtils;
import com.ev.bluetooth.phonebook.common.PhoneCallLog;
import com.ev.bluetooth.livedata.CallHistoryLiveData;
import com.ev.bluetooth.log.L;
import com.ev.bluetooth.phonebook.R;
import com.ev.bluetooth.Constants;
import com.ev.bluetooth.telecom.ui.common.entity.UiCallLog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.ViewHolder> {
    private static final String TAG = RecentsAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater inflater;
    // private ViewHolder viewHolder;
    private List<VCardEntry> vCardEntryList;

    private List<UiCallLog> mUiCallLogs = new ArrayList<>();
    private OnRecentItemClickListener onRecentItemClickListener;
    private int currentPosition = 0;
    private int lastPostion = 0;
    private RecyclerView mRecyclerView;

    public RecentsAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        if (Constants.IS_DEBUG) {
            ArrayList<PhoneCallLog.Record> records = new ArrayList<>();
            records.add(new PhoneCallLog.Record(1648999437000L, 1));
            records.add(new PhoneCallLog.Record(1648999350000l, 2));
            records.add(new PhoneCallLog.Record(1648999332000l, 2));

            mUiCallLogs.add(new UiCallLog("Tina", "我自己1", "1张婷婷", null, records));
            mUiCallLogs.add(new UiCallLog("Tina", "我自己2", "2张婷婷", null, records));
            mUiCallLogs.add(new UiCallLog("Tina", "我自己3", "3张婷婷", null, records));
            mUiCallLogs.add(new UiCallLog("Tina", "我自己4", "4张婷婷", null, records));
            mUiCallLogs.add(new UiCallLog("Tina", "我自己5", "5张婷婷", null, records));
        }

    }

    public RecentsAdapter(Context mContext, List<VCardEntry> vCardEntryList) {
        this.mContext = mContext;
        this.vCardEntryList = vCardEntryList;
        inflater = LayoutInflater.from(mContext);
    }

    public void setUiCallLogs(@NonNull List<UiCallLog> uiCallLogs) {
        L.d(TAG, "setUiCallLogs: %d", uiCallLogs.size());
        Log.i(TAG, "setUiCallLogs: ----------------------------------");
        for (UiCallLog log : uiCallLogs) {
            Log.i(TAG, "setUiCallLogs: getAvatarUri" + log.getAvatarUri());
            Log.i(TAG, "setUiCallLogs: getNumber" + log.getNumber());
            Log.i(TAG, "setUiCallLogs: getText" + log.getText());
            Log.i(TAG, "setUiCallLogs: getTitle" + log.getTitle());
        }
        Log.i(TAG, "setUiCallLogs: ----------------------------------");
        mUiCallLogs.clear();
        mUiCallLogs.addAll(uiCallLogs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recents_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UiCallLog uiCallLog = mUiCallLogs.get(position);
        /* TelecomUtils.setContactBitmapAsync(
                holder.mAvatarView.getContext(),
                holder.mAvatarView,
                uiCallLog.getAvatarUri(),
                uiCallLog.getTitle());*/
        Glide.with(mContext).load(uiCallLog.getAvatarUri()).apply((new RequestOptions()).centerCrop().error(R.drawable.icon_avatar_default)).into(holder.mAvatarView);
        holder.name.setText(uiCallLog.getTitle());

        /** 还需增加次数统计
         * */
        if (uiCallLog.getMostRecentCallType() == CallHistoryLiveData.CallType.MISSED_TYPE) {
            holder.recentCall.setBackgroundResource(R.drawable.icon_call_in);
            holder.name.setTextColor(mContext.getResources().getColor(R.color.color_EB5545));
        } else if (uiCallLog.getMostRecentCallType() == CallHistoryLiveData.CallType.INCOMING_TYPE) {
            holder.recentCall.setBackgroundResource(R.drawable.icon_call_in);
        } else {
            holder.recentCall.setBackgroundResource(0);
        }

        holder.recentCallTime.setText(EVDateUtils.getInstance(mContext).dateCompareTo(uiCallLog.getMostRecentCallEndTimestamp()));
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
                onRecentItemClickListener.onMsgBtnClick(position);
            }
        });
        holder.mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecentItemClickListener.onDelBtnClick(position);
            }
        });
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isActivated()) {
                    translate(v);
                } else
                    onRecentItemClickListener.onRecentItemClick(position);
            }
        });

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return mUiCallLogs.size();
    }

    public UiCallLog getItem(int postion) {
        return mUiCallLogs.get(postion);
    }

    private void resetUI(int currentPosition) {
        View view = mRecyclerView.getLayoutManager().findViewByPosition(currentPosition);
        if (view.isActivated())
            translate(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mLayout;
        ImageView recentCall;
        TextView name;
        View divider;
        TextView recentCallTime;
        ImageView mAvatarView;
        ImageView mMsgBtn;
        ImageView mDelBtn;
        ImageView mMore;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView;
            recentCall = (ImageView) itemView.findViewById(R.id.recent_call);
            name = (TextView) itemView.findViewById(R.id.recent_name);
            divider = (View) itemView.findViewById(R.id.recent_divider);
            recentCallTime = (TextView) itemView.findViewById(R.id.recent_call_time);
            mAvatarView = (ImageView) itemView.findViewById(R.id.recent_photo);
            mMsgBtn = (ImageView) itemView.findViewById(R.id.msg_btn);
            mDelBtn = (ImageView) itemView.findViewById(R.id.del_btn);
            mMore = (ImageView) itemView.findViewById(R.id.recent_more);
        }
    }

    public interface OnRecentItemClickListener {
        public void onRecentItemClick(int position);

        public void onMsgBtnClick(int position);

        public void onDelBtnClick(int position);

    }

    public void setOnRecentItemClickListener(
            OnRecentItemClickListener onRecentItemClickListener) {
        this.onRecentItemClickListener = onRecentItemClickListener;
    }

    public void translate(View view1) {
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

    private int getCallTypeBg() {
        return R.drawable.icon_call_in;
    }

}
