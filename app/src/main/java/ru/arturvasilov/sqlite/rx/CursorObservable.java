package ru.arturvasilov.sqlite.rx;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.arturvasilov.sqlite.SQLiteUtils;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Artur Vasilov
 */
public class CursorObservable extends Observable<Cursor> {

    public CursorObservable(final Context context, @NonNull final Uri uri, final @Nullable String[] projection,
                            @Nullable final String selection, @Nullable final String[] selectionArgs,
                            @Nullable final String sortOrder) {
        super(new OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
                subscriber.onNext(cursor);
                subscriber.onCompleted();
                SQLiteUtils.safeCloseCursor(cursor);
            }
        });
    }
}
