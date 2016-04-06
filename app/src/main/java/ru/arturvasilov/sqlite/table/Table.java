package ru.arturvasilov.sqlite.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author Artur Vasilov
 */
public interface Table<T> {

    @NonNull
    Uri getUri();

    @NonNull
    String getTableName();

    void onCreate(@NonNull SQLiteDatabase database);

    void onUpgrade(@NonNull SQLiteDatabase database, int oldVersion, int newVersion);

    int getLastUpgradeVersion();

    @NonNull
    ContentValues toValues(@NonNull T object);

    @NonNull
    T fromCursor(@NonNull Cursor cursor);

}
