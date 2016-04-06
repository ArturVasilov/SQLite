package ru.arturvasilov.sqlite.action;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.arturvasilov.sqlite.table.Table;

/**
 * @author Artur Vasilov
 */
public class DeleteActionImpl<T> implements DeleteAction<T> {

    private final Context mContext;
    private final Table<T> mTable;

    private String mWhere;
    private String[] mArgs;

    public DeleteActionImpl(Context context, @NonNull Table<T> table) {
        mContext = context;
        mTable = table;
    }

    @NonNull
    @Override
    public DeleteAction<T> where(@Nullable String where) {
        mWhere = where;
        return this;
    }

    @NonNull
    @Override
    public DeleteAction<T> whereArgs(@Nullable String[] args) {
        mArgs = args;
        return this;
    }

    @Override
    public int execute() {
        return mContext.getContentResolver().delete(mTable.getUri(), mWhere, mArgs);
    }
}
