package ru.arturvasilov.sqlite.rx;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Callable;

import rx.Observable;

/**
 * @author Artur Vasilov
 */
public final class CursorObservable {

    private CursorObservable() {
    }

    @NonNull
    public static Observable<Cursor> create(final Context context, @NonNull final Uri uri, final @Nullable String[] projection,
                                            @Nullable final String selection, @Nullable final String[] selectionArgs,
                                            @Nullable final String sortOrder) {
        return Observable.fromCallable(new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
            }
        });
    }
}
