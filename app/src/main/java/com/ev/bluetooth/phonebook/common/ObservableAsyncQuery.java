package com.ev.bluetooth.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.util.Log;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ObservableAsyncQuery {
    private static final String TAG = ObservableAsyncQuery.class.getName();
    private AsyncQueryHandler mAsyncQueryHandler;
    private QueryParam.Provider mQueryParamProvider;
    private Cursor mCurrentCursor;
    private ObservableAsyncQuery.OnQueryFinishedListener mOnQueryFinishedListener;
    private ContentObserver mContentObserver;
    private boolean mIsActive = false;
    private int mToken;

    public ObservableAsyncQuery(@NonNull QueryParam.Provider queryParamProvider, @NonNull ContentResolver cr, @NonNull ObservableAsyncQuery.OnQueryFinishedListener listener) {
        this.mAsyncQueryHandler = new ObservableAsyncQuery.AsyncQueryHandlerImpl(this, cr);
        this.mContentObserver = new ContentObserver(this.mAsyncQueryHandler) {
            public void onChange(boolean selfChange) {
                ObservableAsyncQuery.this.startQuery();
            }
        };
        this.mQueryParamProvider = queryParamProvider;
        this.mOnQueryFinishedListener = listener;
        this.mToken = 0;
    }

    @MainThread
    public void startQuery() {
        Log.d(TAG, "startQuery");
        this.mAsyncQueryHandler.cancelOperation(this.mToken);
        ++this.mToken;
        QueryParam queryParam = this.mQueryParamProvider.getQueryParam();
        if (queryParam != null) {
            this.mAsyncQueryHandler.startQuery(this.mToken, (Object)null, queryParam.mUri, queryParam.mProjection, queryParam.mSelection, queryParam.mSelectionArgs, queryParam.mOrderBy);
        } else {
            this.mOnQueryFinishedListener.onQueryFinished((Cursor)null);
        }

        this.mIsActive = true;
    }

    @MainThread
    public void stopQuery() {
        Log.d(TAG, "stopQuery");
        this.mIsActive = false;
        this.cleanupCursorIfNecessary();
        this.mAsyncQueryHandler.cancelOperation(this.mToken);
    }

    private void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (this.mIsActive) {
            Log.d(TAG, "onQueryComplete");
            this.cleanupCursorIfNecessary();
            if (cursor != null) {
                cursor.registerContentObserver(this.mContentObserver);
                this.mCurrentCursor = cursor;
            }

            if (this.mOnQueryFinishedListener != null) {
                this.mOnQueryFinishedListener.onQueryFinished(cursor);
            }

        }
    }

    protected void cleanupCursorIfNecessary() {
        if (this.mCurrentCursor != null) {
            this.mCurrentCursor.unregisterContentObserver(this.mContentObserver);
        }

        this.mCurrentCursor = null;
    }

    private static class AsyncQueryHandlerImpl extends AsyncQueryHandler {
        private ObservableAsyncQuery mQuery;

        AsyncQueryHandlerImpl(ObservableAsyncQuery query, ContentResolver cr) {
            super(cr);
            this.mQuery = query;
        }

        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == this.mQuery.mToken) {
                this.mQuery.onQueryComplete(token, cookie, cursor);
            } else {
                cursor.close();
            }

        }
    }

    public interface OnQueryFinishedListener {
        @MainThread
        void onQueryFinished(@Nullable Cursor var1);
    }
}
