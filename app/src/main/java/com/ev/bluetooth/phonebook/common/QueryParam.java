package com.ev.bluetooth.phonebook.common;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class QueryParam {
    final Uri mUri;
    final String[] mProjection;
    final String mSelection;
    final String[] mSelectionArgs;
    final String mOrderBy;

    public static QueryParam.Provider of(QueryParam queryParam) {
        return () -> {
            return queryParam;
        };
    }

    public QueryParam(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String orderBy) {
        this.mUri = uri;
        this.mProjection = projection;
        this.mSelection = selection;
        this.mSelectionArgs = selectionArgs;
        this.mOrderBy = orderBy;
    }

    public interface Provider {
        @Nullable
        QueryParam getQueryParam();
    }
}
