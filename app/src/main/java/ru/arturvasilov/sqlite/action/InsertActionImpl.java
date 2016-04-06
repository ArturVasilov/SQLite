package ru.arturvasilov.sqlite.action;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import ru.arturvasilov.sqlite.table.Table;

/**
 * @author Artur Vasilov
 */
public class InsertActionImpl<T> implements InsertAction<T> {

    private final Table<T> mTable;
    private final Context mContext;

    public InsertActionImpl(@NonNull Table<T> table, Context context) {
        mTable = table;
        mContext = context;
    }

    @Override
    public Uri insert(@NonNull T object) {
        return mContext.getContentResolver().insert(mTable.getUri(), mTable.toValues(object));
    }

    @Override
    public int insert(@NonNull List<T> objects) {
        ContentValues[] values = new ContentValues[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            values[i] = mTable.toValues(objects.get(i));
        }
        return mContext.getContentResolver().bulkInsert(mTable.getUri(), values);
    }
}
