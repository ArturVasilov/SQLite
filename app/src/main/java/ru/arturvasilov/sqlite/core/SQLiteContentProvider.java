package ru.arturvasilov.sqlite.core;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import ru.arturvasilov.sqlite.SQLiteException;

/**
 * @author Artur Vasilov
 */
public abstract class SQLiteContentProvider extends ContentProvider {

    private Schema mSchema;

    private SQLiteOpenHelper mSQLiteHelper;

    private static String sContentAuthority;
    private static Uri sBaseUri;

    protected abstract void prepareConfig(@NonNull SQLiteConfig config);

    protected abstract void prepareSchema(@NonNull Schema schema);

    @Override
    public boolean onCreate() {
        SQLiteConfig config = new SQLiteConfig(getContext());
        prepareConfig(config);

        sContentAuthority = config.getAuthority();
        sBaseUri = Uri.parse("content://" + sContentAuthority);

        mSchema = new Schema();
        prepareSchema(mSchema);

        mSQLiteHelper = new SQLiteHelper(getContext(), config, mSchema);
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return mSchema.findTable(uri);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mSQLiteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new SQLiteException("No such table to query");
        } else {
            return database.query(table,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
        }
    }

    @NonNull
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase database = mSQLiteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new SQLiteException("No such table to insert");
        } else {
            long id = database.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return ContentUris.withAppendedId(uri, id);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase database = mSQLiteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new SQLiteException("No such table to insert");
        } else {
            int numInserted = 0;
            database.beginTransaction();
            try {
                for (ContentValues contentValues : values) {
                    long id = database.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                    if (id > 0) {
                        numInserted++;
                    }
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
            return numInserted;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mSQLiteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new SQLiteException("No such table to delete");
        } else {
            return database.delete(table, selection, selectionArgs);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        SQLiteDatabase database = mSQLiteHelper.getWritableDatabase();
        String table = getType(uri);
        if (TextUtils.isEmpty(table)) {
            throw new SQLiteException("No such table to update");
        } else {
            return database.update(table, values, selection, selectionArgs);
        }
    }

    @NonNull
    public static String getContentAuthority() {
        return sContentAuthority;
    }

    @NonNull
    public static Uri getBaseUri() {
        return sBaseUri;
    }
}
