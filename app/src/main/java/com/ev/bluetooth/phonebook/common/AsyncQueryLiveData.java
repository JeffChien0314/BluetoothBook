package com.ev.bluetooth.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.database.Cursor;
import android.os.HandlerThread;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

public abstract class AsyncQueryLiveData<T> extends LiveData<T> {
    private static final String TAG = AsyncQueryLiveData.class.getName();
    private static HandlerThread sHandlerThread = new HandlerThread(AsyncQueryLiveData.class.getName());
    private final ObservableAsyncQuery mObservableAsyncQuery;
    private AsyncQueryLiveData<T>.CursorRunnable mCurrentCursorRunnable;

    public AsyncQueryLiveData(Context context, QueryParam.Provider provider) {
        this.mObservableAsyncQuery = new ObservableAsyncQuery(provider, context.getContentResolver(), this::onCursorLoaded);
    }

    protected void onActive() {
        super.onActive();
        this.mObservableAsyncQuery.startQuery();
    }

    protected void onInactive() {
        super.onInactive();
        if (this.mCurrentCursorRunnable != null) {
            this.mCurrentCursorRunnable.closeCursorIfNecessary();
        }

        this.mObservableAsyncQuery.stopQuery();
    }

    @WorkerThread
    protected abstract T convertToEntity(@NonNull Cursor var1);

    private void onCursorLoaded(Cursor cursor) {
        Log.d(TAG, "onCursorLoaded: " + this);
        if (this.mCurrentCursorRunnable != null) {
            this.mCurrentCursorRunnable.closeCursorIfNecessary();
        }

        this.mCurrentCursorRunnable = new AsyncQueryLiveData.CursorRunnable(cursor);
        sHandlerThread.getThreadHandler().post(this.mCurrentCursorRunnable);
    }

    static {
        sHandlerThread.start();
    }

    private class CursorRunnable implements Runnable {
        private final Cursor mCursor;
        private boolean mIsActive;

        private CursorRunnable(@Nullable Cursor cursor) {
            this.mCursor = cursor;
            this.mIsActive = true;
        }

        public void run() {
            if (this.mIsActive) {
                T entity = this.mCursor == null ? null : AsyncQueryLiveData.this.convertToEntity(this.mCursor);
                if (this.mIsActive) {
                    AsyncQueryLiveData.this.postValue(entity);
                }
            }

            this.closeCursorIfNecessary();
        }

        public synchronized void closeCursorIfNecessary() {
            if (!this.mIsActive && this.mCursor != null) {
                this.mCursor.close();
            }

            this.mIsActive = false;
        }
    }
}
