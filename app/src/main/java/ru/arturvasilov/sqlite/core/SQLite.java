package ru.arturvasilov.sqlite.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.sqlite.utils.SQLiteUtils;

/**
 * @author Artur Vasilov
 */
public class SQLite {

    private static SQLite sSQLite;

    private final Context mContext;

    private final Observers mObservers;

    private SQLite(@NonNull Context context) {
        mContext = context;
        mObservers = new Observers();
    }

    @NonNull
    public static SQLite initialize(@NonNull Context context) {
        SQLite sqLite = sSQLite;
        if (sqLite == null) {
            synchronized (SQLite.class) {
                sqLite = sSQLite;
                if (sqLite == null) {
                    sqLite = sSQLite = new SQLite(context.getApplicationContext());
                }
            }
        }
        return sqLite;
    }

    @NonNull
    public static SQLite get() {
        if (sSQLite == null) {
            throw new IllegalStateException("You should call initialize(Context) first, to initialize the database");
        }
        return sSQLite;
    }

    @NonNull
    public <T> List<T> query(@NonNull Table<T> table, @NonNull Where where) {
        List<T> list = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(table.getUri(), null, where.where(), where.whereArgs(), null);
        try {
            if (SQLiteUtils.isEmptyCursor(cursor)) {
                return list;
            }
            do {
                T t = table.fromCursor(cursor);
                list.add(t);
            } while (cursor.moveToNext());
            return list;
        } finally {
            SQLiteUtils.safeCloseCursor(cursor);
        }
    }

    @Nullable
    public <T> T queryObject(@NonNull Table<T> table, @NonNull Where where) {
        Cursor cursor = mContext.getContentResolver().query(table.getUri(), null, where.where(), where.whereArgs(), null);
        try {
            if (SQLiteUtils.isEmptyCursor(cursor)) {
                return null;
            }
            return table.fromCursor(cursor);
        } finally {
            SQLiteUtils.safeCloseCursor(cursor);
        }
    }

    @Nullable
    public <T> Uri insert(@NonNull Table<T> table, @NonNull T object) {
        Uri uri = mContext.getContentResolver().insert(table.getUri(), table.toValues(object));
        if (uri != null) {
            notifyTableChanged(table);
        }
        return uri;
    }

    public <T> int insert(@NonNull Table<T> table, @NonNull List<T> objects) {
        ContentValues[] values = new ContentValues[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            values[i] = table.toValues(objects.get(i));
        }
        int count = mContext.getContentResolver().bulkInsert(table.getUri(), values);
        if (count > 0) {
            notifyTableChanged(table);
        }
        return count;
    }

    public <T> int delete(@NonNull Table<T> table, @NonNull Where where) {
        int count = mContext.getContentResolver().delete(table.getUri(), where.where(), where.whereArgs());
        if (count > 0) {
            notifyTableChanged(table);
        }
        return count;
    }

    public <T> int update(@NonNull Table<T> table, @NonNull Where where, @NonNull T newObject) {
        int count = mContext.getContentResolver().update(table.getUri(), table.toValues(newObject),
                where.where(), where.whereArgs());
        if (count > 0) {
            notifyTableChanged(table);
        }
        return count;
    }

    public <T> void registerObserver(@NonNull Table<T> table, @NonNull final BasicTableObserver observer) {
        mObservers.registerObserver(mContext, table, observer);
    }

    public <T> void registerObserver(@NonNull Table<T> table, @NonNull final ContentTableObserver<T> observer) {
        mObservers.registerObserver(mContext, table, observer);
    }

    public void unregisterObserver(@NonNull BasicTableObserver observer) {
        mObservers.unregisterObserver(mContext, observer);
    }

    public <T> void unregisterObserver(@NonNull ContentTableObserver<T> observer) {
        mObservers.unregisterObserver(mContext, observer);
    }

    private <T> void notifyTableChanged(@NonNull Table<T> table) {
        mContext.getContentResolver().notifyChange(table.getUri(), null);
    }
}
