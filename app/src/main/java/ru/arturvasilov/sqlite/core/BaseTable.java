package ru.arturvasilov.sqlite.core;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author Artur Vasilov
 */
public abstract class BaseTable<T> implements Table<T> {

    @NonNull
    @Override
    public final Uri getUri() {
        return SQLiteContentProvider.getBaseUri().buildUpon().appendPath(getTableName()).build();
    }

    @Override
    public int getLastUpgradeVersion() {
        return 1;
    }

    @NonNull
    @Override
    public String getTableName() {
        return getClass().getSimpleName();
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase database, int oldVersion, int newVersion) {
        if (newVersion <= getLastUpgradeVersion() && newVersion > oldVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + getTableName());
            onCreate(database);
        }
    }
}
